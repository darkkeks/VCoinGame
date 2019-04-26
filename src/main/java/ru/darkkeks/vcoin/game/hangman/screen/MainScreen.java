package ru.darkkeks.vcoin.game.hangman.screen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.darkkeks.vcoin.game.Handlers;
import ru.darkkeks.vcoin.game.Screen;
import ru.darkkeks.vcoin.game.StaticKeyboard;
import ru.darkkeks.vcoin.game.hangman.Hangman;
import ru.darkkeks.vcoin.game.hangman.HangmanMessages;
import ru.darkkeks.vcoin.game.hangman.HangmanSession;
import ru.darkkeks.vcoin.game.vk.VkUtil;
import ru.darkkeks.vcoin.game.vk.keyboard.ButtonType;
import ru.darkkeks.vcoin.game.vk.keyboard.Keyboard;
import ru.darkkeks.vcoin.game.vk.keyboard.KeyboardButton;

import java.io.IOException;
import java.net.URISyntaxException;

public class MainScreen extends Screen<HangmanSession> {

    private static final Logger logger = LoggerFactory.getLogger(MainScreen.class);

    private static final String MERCHANT_URL = "https://www.digiseller.market/asp2/pay_wm.asp?id_d=2629111&lang=ru-RU&" +
            "referrer=bank&vk_id=%d";

    public MainScreen(Hangman hangman) {
        super(new StaticKeyboard<>(Keyboard.builder()
                .newRow()
                .addButton(new KeyboardButton(HangmanMessages.PLAY, ButtonType.POSITIVE))
                .addButton(new KeyboardButton(HangmanMessages.SETTINGS, ButtonType.POSITIVE))
                .newRow()
                .addButton(new KeyboardButton(HangmanMessages.RULES, ButtonType.PRIMARY))
                .addButton(new KeyboardButton(HangmanMessages.BUY, ButtonType.NEGATIVE))
                .newRow()
                .addButton(new KeyboardButton(HangmanMessages.DEPOSIT, ButtonType.DEFAULT))
                .addButton(new KeyboardButton(HangmanMessages.BALANCE, ButtonType.DEFAULT))
                .addButton(new KeyboardButton(HangmanMessages.WITHDRAW, ButtonType.DEFAULT))
                .build()));

        addHandler(Handlers.exactMatch(HangmanMessages.PLAY, hangman::startGame));

        addHandler(Handlers.exactMatch(HangmanMessages.RULES, session -> {
            session.sendMessage(HangmanMessages.RULES_MESSAGE, getKeyboard(session));
        }));

        addHandler(Handlers.exactMatch(HangmanMessages.BUY, session -> {
            String url = String.format(MERCHANT_URL, session.getChatId());
            try {
                url = VkUtil.shortenUrl(hangman.getContext().getTransportClient(), url);
                session.sendMessage(String.format(HangmanMessages.BUY_MESSAGE, url), getKeyboard(session));
            } catch (IOException | URISyntaxException e) {
                logger.error("Can't shorten url", e);
                session.sendMessage(HangmanMessages.SOMETHING_WENT_WRONG);
            }
        }));

        addHandler(Handlers.exactMatch(HangmanMessages.SETTINGS, session -> {
            SettingsScreen screen = hangman.getSettingsScreen();
            session.setScreen(screen);
            session.sendMessage(HangmanMessages.SETTINGS, screen.getKeyboard(session));
        }));

        addHandler(Handlers.exactMatch(HangmanMessages.START, session -> {
            session.sendMessage(HangmanMessages.RULES_MESSAGE, getKeyboard(session));
        }));

        addHandler(Handlers.exactMatch(HangmanMessages.DEPOSIT, hangman::handleDeposit));
        addHandler(Handlers.exactMatch(HangmanMessages.BALANCE, hangman::handleBalance));
        addHandler(Handlers.exactMatch(HangmanMessages.WITHDRAW, hangman::handleWithdraw));

        fallback(Handlers.any((message, session) -> {
            session.sendMessage(HangmanMessages.COMMANDS_MESSAGE, getKeyboard(session));
        }));
    }
}
