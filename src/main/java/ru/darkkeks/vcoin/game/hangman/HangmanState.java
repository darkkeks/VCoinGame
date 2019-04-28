package ru.darkkeks.vcoin.game.hangman;

public class HangmanState {

    private long coins;
    private long bet;
    private String word;
    private String guessedLetters;

    private boolean showGiveUp;
    private boolean showImage;
    private boolean freeGame;
    private boolean definition;
    private boolean english;

    private long profit;

    public HangmanState() {
        this(Hangman.BASE_BET, 0, null, null, false, true, false, false, 0, false);
    }

    public HangmanState(long coins, long bet, String word, String guessedLetters,
                        boolean showGiveUp, boolean showImage, boolean freeGame, boolean definition,
                        long profit, boolean english) {
        this.coins = coins;
        this.bet = bet;
        this.word = word;
        this.guessedLetters = guessedLetters;
        this.showGiveUp = showGiveUp;
        this.showImage = showImage;
        this.freeGame = freeGame;
        this.definition = definition;
        this.profit = profit;
        this.english = english;
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

    public boolean isFreeGame() {
        return freeGame;
    }

    public void toggleFreeGame() {
        freeGame = !freeGame;
    }

    public long getBet() {
        return bet;
    }

    public void setBet(long bet) {
        this.bet = bet;
    }

    public boolean isDefinition() {
        return definition;
    }

    public void toggleDefinition() {
        definition = !definition;
    }

    public long getProfit() {
        return profit;
    }

    public void addProfit(long profit) {
        this.profit += profit;
    }

    public boolean isEnglish() {
        return english;
    }

    public void toggleEnglish() {
        this.english = !english;
    }
}
