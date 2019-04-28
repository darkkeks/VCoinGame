package ru.darkkeks.vcoin.game.hangman.screen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.darkkeks.vcoin.game.Handlers;
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

    private static final int WRONG_ATTEMPTS = 6;

    private static final String GUESS_PATTERN = "^[ёЁа-яА-Я]+$";
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
            endGame(session, false, 0);
        }));

        addHandler(Handlers.exactMatch(HangmanMessages.DEPOSIT, hangman::handleDeposit));
        addHandler(Handlers.exactMatch(HangmanMessages.BALANCE, hangman::handleBalance));
        addHandler(Handlers.exactMatch(HangmanMessages.WITHDRAW, hangman::handleWithdraw));

        addHandler(Handlers.regexp(GUESS_PATTERN, (match, session) -> {
            HangmanState state = session.getState();
            String guess = match.group().toLowerCase();

            for (Map.Entry<String, String> entry : CHAR_REPLACE.entrySet()) {
                String from = entry.getKey();
                String to = entry.getValue();

                guess = guess.replace(from, to);
                if (state.getWord().contains(from)) {
                    state.setWord(state.getWord().replace(from, to));
                }
                if (state.getGuessedLetters().contains(from)) {
                    state.setGuessedLetters(state.getGuessedLetters().replace(from, to));
                }
            }

            List<Character> newChars = guess.chars().mapToObj(x -> (char)x)
                    .filter(x -> state.getGuessedLetters().indexOf(x) == -1).collect(Collectors.toList());

            if(newChars.size() == 0) {
                session.sendMessage(HangmanMessages.LETTER_USED_ALREADY, getKeyboard(session));
            } else {
                StringBuilder previousGuesses = new StringBuilder(state.getGuessedLetters());
                newChars.forEach(previousGuesses::append);
                state.setGuessedLetters(previousGuesses.toString());

                sendGameMessage(session);
            }
        }));

        fallback(Handlers.any((message, session) -> sendGameMessage(session)));
    }

    public void sendGameMessage(HangmanSession session) {
        HangmanState state = session.getState();

        Set<Character> wrongAttempts = state.getGuessedLetters().chars().mapToObj(x -> (char)x)
                .filter(x -> state.getWord().indexOf(x) == -1).collect(Collectors.toSet());

        String maskedWord = state.getWord().chars().mapToObj(x -> (char)x).map(x -> {
            if(state.getGuessedLetters().indexOf(x) == -1) {
                return "*";
            }
            return Character.toString(x);
        }).collect(Collectors.joining());

        int wrongAttemptsCount = Math.min(wrongAttempts.size(), WRONG_ATTEMPTS);
        if (wrongAttemptsCount >= WRONG_ATTEMPTS) {
            endGame(session, false, wrongAttemptsCount);
        } else {
            String wrong = wrongAttempts.stream().map(x -> Character.toString(x))
                    .collect(Collectors.joining(", "));

            if (maskedWord.equals(state.getWord())) {
                endGame(session, true, wrongAttemptsCount);
            } else {
                hangman.getDao().saveState(session.getChatId(), session.getState());

                String message = String.format(HangmanMessages.GAME_STATUS_MESSAGE, maskedWord, wrong);

                if(session.getState().isDefinition()) {
                    String definition = hangman.getDescGenerator().getDefinition(state.getWord());
                    message = String.format(HangmanMessages.WORD_DEFINITION, definition) + message;
                }

                session.setScreen(this);
                sendMessageWithHealth(message, wrongAttemptsCount, session);
            }
        }
    }

    private void endGame(HangmanSession session, boolean win, int wrong) {
        HangmanState state = session.getState();

        logger.info("GameEnd(user = {}, win = {}, wrong = {})", session.getChatId(), win, wrong);

        MainScreen screen = hangman.getMainScreen();
        session.setScreen(screen);

        String message = (win ? HangmanMessages.WIN_MESSAGE : HangmanMessages.LOSE_MESSAGE);
        message += "\n\n";
        message += String.format(HangmanMessages.WORD_MESSAGE, state.getWord());

        sendMessageWithHealth(message, wrong, session);

        if(win) {
            state.addCoins(state.getBet() * 2);
            state.addProfit(state.getBet() * 2);
        }

        state.setBet(0);
        state.setWord(null);
        state.setGuessedLetters(null);

        hangman.getDao().saveState(session.getChatId(), state);
    }

    private void sendMessageWithHealth(String message, int wrong, HangmanSession session) {
        if(session.getState().isShowImage()) {
            session.sendMessage(message, HangmanMessages.IMAGES[wrong], session.getScreen().getKeyboard(session));
        } else {
            message += String.format(HangmanMessages.HEALTH_MESSAGE,
                    HangmanMessages.HEALTH[wrong]);
            session.sendMessage(message, session.getScreen().getKeyboard(session));
        }
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
