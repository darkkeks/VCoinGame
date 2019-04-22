package ru.darkkeks.vcoin.game.hangman;

import ru.darkkeks.vcoin.game.hangman.screen.GameScreen;

public class HangmanState {

    private long coins;
    private String word;
    private String guessedLetters;

    private boolean showGiveUp;
    private boolean showImage;

    public HangmanState() {
        this(GameScreen.BASE_BET, null, null, false, true);
    }

    public HangmanState(long coins, String word, String guessedLetters, boolean showGiveUp, boolean showImage) {
        this.coins = coins;
        this.word = word;
        this.guessedLetters = guessedLetters;
        this.showGiveUp = showGiveUp;
        this.showImage = showImage;
    }

    public boolean inGame() {
        return word != null;
    }

    public long getCoins() {
        return coins;
    }

    public String getWord() {
        return word;
    }

    public String getGuessedLetters() {
        return guessedLetters;
    }

    public void addCoins(long coins) {
        this.coins += coins;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setGuessedLetters(String guessedLetters) {
        this.guessedLetters = guessedLetters;
    }

    public boolean isShowGiveUp() {
        return showGiveUp;
    }

    public void toggleShowGiveUp() {
        showGiveUp = !showGiveUp;
    }

    public boolean isShowImage() {
        return showImage;
    }

    public void toggleShowImage() {
        showImage = !showImage;
    }
}
