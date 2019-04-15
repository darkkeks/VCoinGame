package ru.darkkeks.vcoin.game.vk.keyboard;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Keyboard {

    @SerializedName("one_time")
    private boolean oneTime;

    private List<List<KeyboardButton>> buttons;

    private Keyboard() {
        oneTime = false;
        buttons = new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Keyboard keyboard;

        public Builder() {
            this.keyboard = new Keyboard();
        }

        public Builder oneTime(boolean oneTime) {
            keyboard.oneTime = oneTime;
            return this;
        }

        public Builder newRow() {
            keyboard.buttons.add(new ArrayList<>());
            return this;
        }

        public Builder addButton(KeyboardButton button) {
            keyboard.buttons.get(keyboard.buttons.size() - 1).add(button);
            return this;
        }

        public Keyboard build() {
            return keyboard;
        }
    }
}
