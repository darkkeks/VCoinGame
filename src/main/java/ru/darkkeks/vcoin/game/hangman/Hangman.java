package ru.darkkeks.vcoin.game.hangman;

import com.vk.api.sdk.objects.messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.darkkeks.vcoin.game.*;
import ru.darkkeks.vcoin.game.api.Transaction;
import ru.darkkeks.vcoin.game.vk.keyboard.ButtonType;
import ru.darkkeks.vcoin.game.vk.keyboard.Keyboard;
import ru.darkkeks.vcoin.game.vk.keyboard.KeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Hangman extends Game<HangmanSession> {

    private static final Logger logger = LoggerFactory.getLogger(Hangman.class);

    private static final int BASE_BET = 10_000_000;
    private static final int REWARD = 20_000_000;
    private static final int WRONG_ATTEMPTS = 5;

    private static final String ALPHABET = "ёйцукенгшщзхъфывапролджэячсмитьбю";

    private AppContext context;

    private HangmanDao hangmanDao;

    private Keyboard mainKeyboard;
    private Keyboard gameKeyboard;

    private Screen<HangmanSession> mainScreen;
    private Screen<HangmanSession> gameScreen;
    private Screen<HangmanSession> withdrawScreen;

    private WordGenerator generator;

    public Hangman(AppContext context) {
        this.context = context;
        this.hangmanDao = new HangmanDao();
        this.generator = new WordGenerator();

        mainKeyboard = Keyboard.builder()
                .newRow()
                .addButton(new KeyboardButton(HangmanMessages.PLAY, ButtonType.POSITIVE))
                .addButton(new KeyboardButton(HangmanMessages.RULES, ButtonType.PRIMARY))
                .newRow()
                .addButton(new KeyboardButton(HangmanMessages.DEPOSIT, ButtonType.POSITIVE))
                .addButton(new KeyboardButton(HangmanMessages.BALANCE, ButtonType.DEFAULT))
                .addButton(new KeyboardButton(HangmanMessages.WITHDRAW, ButtonType.NEGATIVE))
                .build();

        mainScreen = new Screen<>(mainKeyboard);

        mainScreen.addHandler(Handlers.exactMatch(HangmanMessages.PLAY, session -> {
            if (session.getState().getCoins() >= BASE_BET) {
                session.getState().addCoins(-BASE_BET);
                startGame(session);
            } else {
                session.sendMessage(String.format(HangmanMessages.NOT_ENOUGH_TO_PLAY, BASE_BET), mainKeyboard);
            }
        }));

        mainScreen.addHandler(Handlers.exactMatch(HangmanMessages.RULES, session -> {
            session.sendMessage(HangmanMessages.RULES_MESSAGE, mainKeyboard);
        }));

        mainScreen.addHandler(Handlers.exactMatch(HangmanMessages.DEPOSIT, this::handleDeposit));
        mainScreen.addHandler(Handlers.exactMatch(HangmanMessages.BALANCE, this::handleBalance));
        mainScreen.addHandler(Handlers.exactMatch(HangmanMessages.WITHDRAW, this::handleWithdraw));

        mainScreen.fallback(Handlers.any((message, session) -> {
            session.sendMessage(HangmanMessages.COMMANDS_MESSAGE, mainKeyboard);
        }));

        withdrawScreen = new Screen<>();

        withdrawScreen.addHandler(Handlers.regexp("^\\d+[.,]?\\d*$", ((matcher, session) -> {
            double floatAmount = Double.valueOf(matcher.group());
            long amount = Math.round(floatAmount * 1000);
            logger.info("Withdraw (id = {}, amount = {})", session.getChatId(), amount);

            Screen<HangmanSession> screen = getScreen(session.getState());
            session.setScreen(screen);

            if(session.getState().getCoins() >= amount) {
                session.getState().addCoins(-amount);
                hangmanDao.saveState(session.getChatId(), session.getState());
                context.getVCoinApi().transfer(session.getChatId(), amount)
                        .whenComplete((transferResult, throwable) -> {
                            if(throwable == null) {
                                session.sendMessage(HangmanMessages.SUCCESS_WITHDRAW_MESSAGE, screen.getKeyboard());
                            } else {
                                session.getState().addCoins(amount);
                                hangmanDao.saveState(session.getChatId(), session.getState());
                                session.sendMessage(HangmanMessages.SOMETHING_WENT_WRONG, screen.getKeyboard());
                                logger.error("Error while trying to transfer", throwable);
                            }
                        });
            } else {
                session.sendMessage(HangmanMessages.NOT_ENOUGH_WITHDRAW_MESSAGE, screen.getKeyboard());
            }
        })));

        withdrawScreen.fallback(Handlers.any((message, session) -> {
            Screen<HangmanSession> screen = getScreen(session.getState());
            session.setScreen(screen);
            screen.acceptMessage(message, session);
        }));

        gameKeyboard = Keyboard.builder()
                .newRow()
                .addButton(new KeyboardButton(HangmanMessages.GIVE_UP, ButtonType.NEGATIVE))
                .newRow()
                .addButton(new KeyboardButton(HangmanMessages.DEPOSIT, ButtonType.POSITIVE))
                .addButton(new KeyboardButton(HangmanMessages.BALANCE, ButtonType.DEFAULT))
                .addButton(new KeyboardButton(HangmanMessages.WITHDRAW, ButtonType.NEGATIVE))
                .build();

        gameScreen = new Screen<>(gameKeyboard);

        gameScreen.addHandler(Handlers.exactMatch(HangmanMessages.GIVE_UP, session -> {
            endGame(session, false, -1);
        }));

        gameScreen.addHandler(new MessageHandler<>(message -> extractLetter(message) != null, (message, session) -> {
            String letter = extractLetter(message);
            assert letter != null;
            if(session.getState().getGuessedLetters().contains(letter)) {
                session.sendMessage(HangmanMessages.LETTER_USED_ALREADY, gameKeyboard);
            } else {
                session.getState().setGuessedLetters(session.getState().getGuessedLetters() + letter);
                sendGameMessage(session);
            }
        }));

        gameScreen.addHandler(Handlers.exactMatch(HangmanMessages.DEPOSIT, this::handleDeposit));
        gameScreen.addHandler(Handlers.exactMatch(HangmanMessages.BALANCE, this::handleBalance));
        gameScreen.addHandler(Handlers.exactMatch(HangmanMessages.WITHDRAW, this::handleWithdraw));

        gameScreen.fallback(Handlers.any((message, session) -> sendGameMessage(session)));
    }

    private void startGame(HangmanSession session) {
        String word = generator.getWord();

        session.getState().setWord(word);
        session.getState().setGuessedLetters("");

        sendGameMessage(session);
    }

    private void sendGameMessage(HangmanSession session) {
        String word = session.getState().getWord();
        char[] result = word.toCharArray();
        Arrays.fill(result, '*');

        List<Integer> wrongAttempts = new ArrayList<>();
        session.getState().getGuessedLetters().chars().forEach(ch -> {
            boolean used = false;
            for (int i = 0; i < word.length(); ++i) {
                if (word.charAt(i) == ch) {
                    result[i] = word.charAt(i);
                    used = true;
                }
            }
            if (!used) {
                wrongAttempts.add(ch);
            }
        });

        if (wrongAttempts.size() > WRONG_ATTEMPTS) {
            endGame(session, false, wrongAttempts.size());
        } else {
            hangmanDao.saveState(session.getChatId(), session.getState());

            String masked = new String(result);
            String wrong = wrongAttempts.stream().map(x -> "" + (char)(int)x).collect(Collectors.joining(", "));
            if (masked.equals(word)) {
                endGame(session, true, wrongAttempts.size());
            } else {
                session.sendMessage(String.format(HangmanMessages.GAME_STATUS_MESSAGE, masked, wrong),
                        HangmanMessages.IMAGES[wrongAttempts.size()], gameKeyboard);
                session.setScreen(gameScreen);
            }
        }
    }

    private void endGame(HangmanSession session, boolean win, int wrong) {
        HangmanState state = session.getState();
        if(win) {
            state.addCoins(REWARD);

            session.sendMessage(HangmanMessages.WIN_MESSAGE + "\n\n" +
                    HangmanMessages.WORD_MESSAGE + state.getWord(), HangmanMessages.IMAGES[wrong], mainKeyboard);
        } else if(wrong != -1) {
            session.sendMessage(HangmanMessages.LOSE_MESSAGE + "\n\n" +
                    HangmanMessages.WORD_MESSAGE + state.getWord(), HangmanMessages.IMAGES[wrong], mainKeyboard);
        } else {
            session.sendMessage(HangmanMessages.LOSE_MESSAGE + "\n\n" +
                    HangmanMessages.WORD_MESSAGE + state.getWord(), mainKeyboard);
        }

        session.setScreen(mainScreen);

        state.setWord(null);
        state.setGuessedLetters(null);

        hangmanDao.saveState(session.getChatId(), state);
    }

    private String extractLetter(Message message) {
        if(message.getText() == null) return null;
        String text = message.getText().trim().toLowerCase();
        if(text.length() != 1) return null;
        if(!ALPHABET.contains(text)) return null;
        return text;
    }

    private void handleDeposit(HangmanSession session) {
        session.sendMessage(String.format(HangmanMessages.DEPOSIT_MESSAGE,
                context.getVCoinApi().getPaymentLink(1000)), session.getScreen().getKeyboard());
    }

    private void handleWithdraw(HangmanSession session) {
        session.sendMessage(HangmanMessages.WITHDRAW_MESSAGE, session.getScreen().getKeyboard());
        session.setScreen(withdrawScreen);
    }

    private void handleBalance(HangmanSession session) {
        session.sendMessage(String.format(HangmanMessages.BALANCE_MESSAGE,
                session.getState().getCoins() / 1000.0), session.getScreen().getKeyboard());
    }

    @Override
    public Consumer<Transaction> getTransferConsumer() {
        return transaction -> {
            if(transaction.getTo() == context.getVCoinApi().getUserId()) {
                getSession(transaction.getFrom()).acceptPayment(transaction);
            }
        };
    }

    public Screen<HangmanSession> getScreen(HangmanState state) {
        return state.inGame() ? gameScreen : mainScreen;
    }

    @Override
    protected HangmanSession createSession(int chatId) {
        return new HangmanSession(context, this, chatId);
    }

    public HangmanDao getDao() {
        return hangmanDao;
    }
}
