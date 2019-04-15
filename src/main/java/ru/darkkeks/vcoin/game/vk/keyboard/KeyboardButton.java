package ru.darkkeks.vcoin.game.vk.keyboard;

public class KeyboardButton {

    private ButtonType color;
    private ButtonAction action;

    public KeyboardButton(String text, String payload, ButtonType color) {
        this.color = color;
        this.action = new ButtonAction(payload, text);
    }

    public KeyboardButton(String text, ButtonType color) {
        this(text, "", color);
    }

    static class ButtonAction {
        private String type;
        private String payload;
        private String label;

        protected ButtonAction(String payload, String label) {
            this.type = "text";
            this.payload = payload;
            this.label = label;
        }
    }
}
