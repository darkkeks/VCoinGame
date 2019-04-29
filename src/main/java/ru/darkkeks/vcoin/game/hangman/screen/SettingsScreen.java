package ru.darkkeks.vcoin.game.hangman.screen;

import ru.darkkeks.vcoin.game.Handlers;
import ru.darkkeks.vcoin.game.Screen;
import ru.darkkeks.vcoin.game.hangman.Hangman;
import ru.darkkeks.vcoin.game.hangman.HangmanMessages;
import ru.darkkeks.vcoin.game.hangman.HangmanSession;
import ru.darkkeks.vcoin.game.hangman.HangmanSettings;
import ru.darkkeks.vcoin.game.vk.keyboard.ButtonType;
import ru.darkkeks.vcoin.game.vk.keyboard.Keyboard;
import ru.darkkeks.vcoin.game.vk.keyboard.KeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class SettingsScreen extends Screen<HangmanSession> {

    private static final String LANGUAGE = "\"language\"";

    private Hangman hangman;

    private static List<List<Setting>> keyboard;

    public SettingsScreen(Hangman hangman) {
        super(SettingsScreen::createKeyboard);

        this.hangman = hangman;
        keyboard = new ArrayList<>();

        keyboard.add(Arrays.asList(
                new Setting(s -> s.getState().getSettings().getShowGiveUp(), HangmanMessages.TOGGLE_GIVE_UP,
                        HangmanMessages.ENABLED_GIVE_UP_BUTTON, HangmanMessages.DISABLED_GIVE_UP_BUTTON),
                new Setting(s -> s.getState().getSettings().getShowImage(), HangmanMessages.TOGGLE_IMAGE,
                        HangmanMessages.ENABLED_IMAGE, HangmanMessages.DISABLED_IMAGE)
        ));

        keyboard.add(Arrays.asList(
                new Setting(s -> s.getState().getSettings().getFreeGame(), HangmanMessages.FREE_GAME,
                        HangmanMessages.ENABLED_FREE_GAME, HangmanMessages.DISABLED_FREE_GAME),
                new Setting(s -> s.getState().getSettings().getDefinition(), HangmanMessages.DEFINITION,
                        HangmanMessages.ENABLED_DEFINITION, HangmanMessages.DISABLED_DEFINITION)
        ));

        keyboard.add(Collections.singletonList(
                new Setting(s -> s.getState().getSettings().getEnglish(),
                        HangmanMessages.LANGUAGE_ENGLISH, HangmanMessages.ENABLED_ENGLISH_LANGUAGE, ButtonType.PRIMARY,
                        HangmanMessages.LANGUAGE_RUSSIAN, HangmanMessages.ENABLED_RUSSIAN_LANGUAGE, ButtonType.PRIMARY,
                        LANGUAGE)
        ));

        keyboard.forEach(x -> x.forEach(this::addSetting));

        addHandler(Handlers.exactMatch(HangmanMessages.GO_BACK, session -> {
            session.setScreen(session.getPreviousScreen());
            session.sendMessage(HangmanMessages.GO_BACK_MESSAGE, session.getScreen().getKeyboard(session));
        }));

        fallback(Handlers.any((message, session) -> session.sendMessage(HangmanMessages.COMMANDS_MESSAGE,
                getKeyboard(session))));
    }

    private void addSetting(Setting setting) {
        addHandler(Handlers.payload(setting.getPayload(), session -> {
            HangmanSettings.BooleanSetting value = setting.get(session);
            value.toggle();
            if(value.get()) {
                session.sendMessage(setting.getEnabledMessage(), getKeyboard(session));
            } else {
                session.sendMessage(setting.getDisabledMessage(), getKeyboard(session));
            }

            hangman.getDao().saveState(session.getChatId(), session.getState());
        }));
    }

    private static Keyboard createKeyboard(HangmanSession session) {
        Keyboard.Builder builder = Keyboard.builder();

        keyboard.forEach(row -> {
            builder.newRow();

            row.forEach(setting -> {
                if(setting.get(session).get()) {
                    builder.addButton(new KeyboardButton(setting.getEnabledButton(),
                            setting.getPayload(), setting.getEnabledType()));
                } else {
                    builder.addButton(new KeyboardButton(setting.getDisabledButton(),
                            setting.getPayload(), setting.getDisabledType()));
                }
            });
        });

        builder.newRow();
        builder.addButton(new KeyboardButton(HangmanMessages.GO_BACK, ButtonType.DEFAULT));

        return builder.build();
    }

    private static class Setting {
        private Function<HangmanSession, HangmanSettings.BooleanSetting> settingExtractor;

        private String enabledButton;
        private String enabledMessage;
        private ButtonType enabledType;

        private String disabledButton;
        private String disabledMessage;
        private ButtonType disabledType;

        private String payload;

        public Setting(Function<HangmanSession, HangmanSettings.BooleanSetting> settingExtractor,
                       String button, String enabledMessage, String disabledMessage) {
            this(settingExtractor, button, enabledMessage, ButtonType.POSITIVE, button, disabledMessage,
                    ButtonType.NEGATIVE, button);
        }

        public Setting(Function<HangmanSession, HangmanSettings.BooleanSetting> settingExtractor,
                       String enabledButton, String enabledMessage, ButtonType enabledType, String disabledButton,
                       String disabledMessage, ButtonType disabledType, String payload) {
            this.settingExtractor = settingExtractor;
            this.enabledButton = enabledButton;
            this.enabledMessage = enabledMessage;
            this.enabledType = enabledType;
            this.disabledButton = disabledButton;
            this.disabledMessage = disabledMessage;
            this.disabledType = disabledType;
            this.payload = String.format("\"%s\"", payload.replace("\"", "\\\""));
        }

        public String getEnabledButton() {
            return enabledButton;
        }

        public String getEnabledMessage() {
            return enabledMessage;
        }

        public ButtonType getEnabledType() {
            return enabledType;
        }

        public String getDisabledButton() {
            return disabledButton;
        }

        public String getDisabledMessage() {
            return disabledMessage;
        }

        public ButtonType getDisabledType() {
            return disabledType;
        }

        public String getPayload() {
            return payload;
        }

        public HangmanSettings.BooleanSetting get(HangmanSession session) {
            return settingExtractor.apply(session);
        }
    }

}
