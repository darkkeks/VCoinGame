package ru.darkkeks.vcoin.game.hangman;

public class HangmanState {

    private long coins;
    private String word;
    private String guessedLetters;

    public HangmanState() {
        this(Hangman.BASE_BET, null, null);
    }

    public HangmanState(long coins, String word, String guessedLetters) {
        this.coins = coins;
        this.word = word;
        this.guessedLetters = guessedLetters;
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
}
