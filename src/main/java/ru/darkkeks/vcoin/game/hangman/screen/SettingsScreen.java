package ru.darkkeks.vcoin.game.hangman.screen;

import ru.darkkeks.vcoin.game.Handlers;
import ru.darkkeks.vcoin.game.Screen;
import ru.darkkeks.vcoin.game.hangman.Hangman;
import ru.darkkeks.vcoin.game.hangman.HangmanMessages;
import ru.darkkeks.vcoin.game.hangman.HangmanSession;
import ru.darkkeks.vcoin.game.hangman.HangmanState;
import ru.darkkeks.vcoin.game.vk.keyboard.ButtonType;
import ru.darkkeks.vcoin.game.vk.keyboard.Keyboard;
import ru.darkkeks.vcoin.game.vk.keyboard.KeyboardButton;

public class SettingsScreen extends Screen<HangmanSession> {

    public SettingsScreen(Hangman hangman) {
        super(SettingsScreen::createKeyboard);

        addHandler(Handlers.exactMatch(HangmanMessages.TOGGLE_GIVE_UP, session -> {
            session.getState().toggleShowGiveUp();
            if(session.getState().isShowGiveUp()) {
                session.sendMessage(HangmanMessages.ENABLED_GIVE_UP_BUTTON, getKeyboard(session));
            } else {
                session.sendMessage(HangmanMessages.DISABLED_GIVE_UP_BUTTON, getKeyboard(session));
            }

            hangman.getDao().saveState(session.getChatId(), session.getState());
        }));

        addHandler(Handlers.exactMatch(HangmanMessages.TOGGLE_IMAGE, session -> {
            session.getState().toggleShowImage();
            if(session.getState().isShowImage()) {
                session.sendMessage(HangmanMessages.ENABLED_IMAGE, getKeyboard(session));
            } else {
                session.sendMessage(HangmanMessages.DISABLED_IMAGE, getKeyboard(session));
            }

            hangman.getDao().saveState(session.getChatId(), session.getState());
        }));

        addHandler(Handlers.exactMatch(HangmanMessages.FREE_GAME, session -> {
            session.getState().toggleFreeGame();
            if(session.getState().isFreeGame()) {
                session.sendMessage(HangmanMessages.ENABLED_FREE_GAME, getKeyboard(session));
            } else {
                session.sendMessage(HangmanMessages.DISABLED_FREE_GAME, getKeyboard(session));
            }

            hangman.getDao().saveState(session.getChatId(), session.getState());
        }));

        addHandler(Handlers.exactMatch(HangmanMessages.DEFINITION, session -> {
            session.getState().toggleDefinition();
            if(session.getState().isDefinition()) {
                session.sendMessage(HangmanMessages.ENABLED_DEFINITION, getKeyboard(session));
            } else {
                session.sendMessage(HangmanMessages.DISABLED_DEFINITION, getKeyboard(session));
            }

            hangman.getDao().saveState(session.getChatId(), session.getState());
        }));

        addHandler(Handlers.exactMatch(HangmanMessages.GO_BACK, session -> {
            session.setScreen(session.getPreviousScreen());
            session.sendMessage(HangmanMessages.GO_BACK_MESSAGE, session.getScreen().getKeyboard(session));
        }));

        fallback(Handlers.any((message, session) -> session.sendMessage(HangmanMessages.COMMANDS_MESSAGE)));
    }

    private static Keyboard createKeyboard(HangmanSession session) {
        Keyboard.Builder builder = Keyboard.builder();

        builder.newRow();

        HangmanState state = session.getState();
        if(state.isShowGiveUp()) {
            builder.addButton(new KeyboardButton(HangmanMessages.TOGGLE_GIVE_UP, ButtonType.POSITIVE));
        } else {
            builder.addButton(new KeyboardButton(HangmanMessages.TOGGLE_GIVE_UP, ButtonType.NEGATIVE));
        }

        if(state.isShowImage()) {
            builder.addButton(new KeyboardButton(HangmanMessages.TOGGLE_IMAGE, ButtonType.POSITIVE));
        } else {
            builder.addButton(new KeyboardButton(HangmanMessages.TOGGLE_IMAGE, ButtonType.NEGATIVE));
        }

        builder.newRow();

        if (state.isFreeGame()) {
            builder.addButton(new KeyboardButton(HangmanMessages.FREE_GAME, ButtonType.POSITIVE));
        } else {
            builder.addButton(new KeyboardButton(HangmanMessages.FREE_GAME, ButtonType.NEGATIVE));
        }

        if (state.isDefinition()) {
            builder.addButton(new KeyboardButton(HangmanMessages.DEFINITION, ButtonType.POSITIVE));
        } else {
            builder.addButton(new KeyboardButton(HangmanMessages.DEFINITION, ButtonType.NEGATIVE));
        }

        builder.newRow();
        builder.addButton(new KeyboardButton(HangmanMessages.GO_BACK, ButtonType.DEFAULT));

        return builder.build();
    }

}
