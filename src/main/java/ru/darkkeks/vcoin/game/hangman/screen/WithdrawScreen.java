package ru.darkkeks.vcoin.game.hangman.screen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.darkkeks.vcoin.game.Handlers;
import ru.darkkeks.vcoin.game.Screen;
import ru.darkkeks.vcoin.game.hangman.Hangman;
import ru.darkkeks.vcoin.game.hangman.HangmanMessages;
import ru.darkkeks.vcoin.game.hangman.HangmanSession;

import java.util.regex.Matcher;

public class WithdrawScreen extends Screen<HangmanSession> {

    private static final Logger logger = LoggerFactory.getLogger(WithdrawScreen.class);

    private Hangman hangman;

    public WithdrawScreen(Hangman hangman) {
        this.hangman = hangman;

        addHandler(Handlers.regexp("^\\d+[.,]?\\d*$", this::handleAmount));

        fallback(Handlers.any((message, session) -> {
            Screen<HangmanSession> screen = session.getPreviousScreen();
            session.setScreen(screen);
            screen.acceptMessage(message, session);
        }));
    }


    private void handleAmount(Matcher matcher, HangmanSession session) {
        double floatAmount = Double.valueOf(matcher.group());
        long amount = Math.round(floatAmount * 1000);

        Screen<HangmanSession> screen = session.getPreviousScreen();
        session.setScreen(screen);

        if (amount <= 0) {
            session.sendMessage(HangmanMessages.AMOUNT_HAS_TO_BE_POSITIVE, screen.getKeyboard(session));
        } else if (session.getState().getCoins() < amount) {
            session.sendMessage(HangmanMessages.NOT_ENOUGH_WITHDRAW_MESSAGE, screen.getKeyboard(session));
        } else {
            logger.info("Withdraw (id = {}, amount = {})", session.getChatId(), amount);

            session.getState().addCoins(-amount);
            hangman.getDao().saveState(session.getChatId(), session.getState());
            hangman.getContext().getVCoinApi().transfer(session.getChatId(), amount)
                    .whenComplete((transferResult, throwable) -> {
                        if (throwable == null) {
                            session.sendMessage(String.format(HangmanMessages.SUCCESS_WITHDRAW_MESSAGE,
                                    amount / 1e3), screen.getKeyboard(session));
                        } else {
                            session.getState().addCoins(amount);
                            hangman.getDao().saveState(session.getChatId(), session.getState());
                            session.sendMessage(HangmanMessages.SOMETHING_WENT_WRONG, screen.getKeyboard(session));
                            logger.error("Error while trying to transfer", throwable);
                        }
                    });
        }
    }
}
