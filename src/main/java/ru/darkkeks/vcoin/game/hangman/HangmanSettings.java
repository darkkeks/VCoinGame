package ru.darkkeks.vcoin.game.hangman;

public class HangmanSettings {

    private BooleanSetting showGiveUp;
    private BooleanSetting showImage;
    private BooleanSetting freeGame;
    private BooleanSetting definition;
    private BooleanSetting english;

    public HangmanSettings() {
        showGiveUp = new BooleanSetting(false);
        showImage = new BooleanSetting(true);
        freeGame = new BooleanSetting(false);
        definition = new BooleanSetting(true);
        english = new BooleanSetting(false);
    }

    public BooleanSetting getShowGiveUp() {
        return showGiveUp;
    }

    public BooleanSetting getShowImage() {
        return showImage;
    }

    public BooleanSetting getFreeGame() {
        return freeGame;
    }

    public BooleanSetting getDefinition() {
        return definition;
    }

    public BooleanSetting getEnglish() {
        return english;
    }

    public static class BooleanSetting {
        private boolean value;

        public BooleanSetting(boolean value) {
            this.value = value;
        }

        public boolean get() {
            return value;
        }

        public void toggle() {
            value = !value;
        }
    }
}

