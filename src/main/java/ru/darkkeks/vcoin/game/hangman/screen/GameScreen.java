package ru.darkkeks.vcoin.game.hangman.screen;

import com.vk.api.sdk.objects.messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.darkkeks.vcoin.game.Handlers;
import ru.darkkeks.vcoin.game.MessageHandler;
import ru.darkkeks.vcoin.game.Screen;
import ru.darkkeks.vcoin.game.hangman.Hangman;
import ru.darkkeks.vcoin.game.hangman.HangmanMessages;
import ru.darkkeks.vcoin.game.hangman.HangmanSession;
import ru.darkkeks.vcoin.game.hangman.HangmanState;
import ru.darkkeks.vcoin.game.vk.keyboard.ButtonType;
import ru.darkkeks.vcoin.game.vk.keyboard.Keyboard;
import ru.darkkeks.vcoin.game.vk.keyboard.KeyboardButton;

import java.util.*;
import java.util.stream.Collectors;

public class GameScreen extends Screen<HangmanSession> {

    private static final Logger logger = LoggerFactory.getLogger(GameScreen.class);

    private static final int WRONG_ATTEMPTS = 5;

    private static final String ALPHABET = "ёйцукенгшщзхъфывапролджэячсмитьбю";
    private static Map<String, String> CHAR_REPLACE;

    static {
        CHAR_REPLACE = new HashMap<>();
        CHAR_REPLACE.put("ё", "е");
    }

    private Hangman hangman;

    public GameScreen(Hangman hangman) {
        super(GameScreen::createKeyboard);
        this.hangman = hangman;

        addHandler(Handlers.exactMatch(HangmanMessages.GIVE_UP, session -> {
            endGame(session, false, -1);
        }));

        addHandler(new MessageHandler<>(message -> extractLetter(message) != null, this::handleLetter));

        addHandler(Handlers.exactMatch(HangmanMessages.DEPOSIT, hangman::handleDeposit));
        addHandler(Handlers.exactMatch(HangmanMessages.BALANCE, hangman::handleBalance));
        addHandler(Handlers.exactMatch(HangmanMessages.WITHDRAW, hangman::handleWithdraw));

        fallback(Handlers.any((message, session) -> sendGameMessage(session)));
    }

    public void sendGameMessage(HangmanSession session) {
        String word = session.getState().getWord();
        char[] result = word.toCharArray();
        Arrays.fill(result, '*');

        Set<Integer> wrongAttempts = new HashSet<>();
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
            hangman.getDao().saveState(session.getChatId(), session.getState());

            String masked = new String(result);
            String wrong = wrongAttempts.stream().map(x -> "" + (char)(int)x).collect(Collectors.joining(", "));
            if (masked.equals(word)) {
                endGame(session, true, wrongAttempts.size());
            } else {
                String message = String.format(HangmanMessages.GAME_STATUS_MESSAGE, masked, wrong);

                if(session.getState().isDefinition()) {
                    String definition = hangman.getDescGenerator().getDefinition(word);
                    message = String.format(HangmanMessages.WORD_DEFINITION, definition) + message;
                }

                if(session.getState().isShowImage()) {
                    session.sendMessage(message, HangmanMessages.IMAGES[wrongAttempts.size()], getKeyboard(session));
                } else {
                    message += String.format(HangmanMessages.HEALTH_MESSAGE,
                            HangmanMessages.HEALTH[wrongAttempts.size()]);
                    session.sendMessage(message, getKeyboard(session));
                }
                session.setScreen(this);
            }
        }
    }

    private void handleLetter(Message message, HangmanSession session) {
        String letter = extractLetter(message);
        assert letter != null;
        CHAR_REPLACE.forEach((from, to) -> {
            HangmanState state = session.getState();
            if (state.getWord().contains(from)) {
                state.setWord(state.getWord().replace(from, to));
            }
            if (state.getGuessedLetters().contains(from)) {
                state.setGuessedLetters(state.getGuessedLetters().replace(from, to));
            }
        });
        if (session.getState().getGuessedLetters().contains(letter)) {
            session.sendMessage(HangmanMessages.LETTER_USED_ALREADY, getKeyboard(session));
        } else {
            session.getState().setGuessedLetters(session.getState().getGuessedLetters() + letter);
            sendGameMessage(session);
        }
    }

    private void endGame(HangmanSession session, boolean win, int wrong) {
        HangmanState state = session.getState();

        logger.info("GameEnd(user = {}, win = {}, wrong = {})", session.getChatId(), win, wrong);

        String message = (win ? HangmanMessages.WIN_MESSAGE : HangmanMessages.LOSE_MESSAGE);
        message += "\n\n";
        message += String.format(HangmanMessages.WORD_MESSAGE, state.getWord());

        MainScreen screen = hangman.getMainScreen();
        session.setScreen(screen);

        if(win) {
            state.addCoins(state.getBet() * 2);
            state.addProfit(state.getBet());
            session.sendMessage(message, HangmanMessages.IMAGES[wrong], screen.getKeyboard(session));
        } else if(wrong != -1) {
            session.sendMessage(message, HangmanMessages.IMAGES[wrong], screen.getKeyboard(session));
        } else {
            session.sendMessage(message, screen.getKeyboard(session));
        }

        state.setBet(0);
        state.setWord(null);
        state.setGuessedLetters(null);

        hangman.getDao().saveState(session.getChatId(), state);
    }

    private String extractLetter(Message message) {
        if(message.getText() == null) return null;
        String text = message.getText().trim().toLowerCase();
        if(text.length() != 1) return null;
        if(!ALPHABET.contains(text)) return null;
        if(CHAR_REPLACE.containsKey(text)) return CHAR_REPLACE.get(text);
        return text;
    }

    private static Keyboard createKeyboard(HangmanSession session) {
        Keyboard.Builder builder = Keyboard.builder();

        if (session.getState().isShowGiveUp()) {
            builder.newRow();
            builder.addButton(new KeyboardButton(HangmanMessages.GIVE_UP, ButtonType.NEGATIVE));
        }

        builder.newRow();
        builder.addButton(new KeyboardButton(HangmanMessages.DEPOSIT, ButtonType.DEFAULT));
        builder.addButton(new KeyboardButton(HangmanMessages.BALANCE, ButtonType.DEFAULT));
        builder.addButton(new KeyboardButton(HangmanMessages.WITHDRAW, ButtonType.DEFAULT));

        return builder.build();
    }
}
