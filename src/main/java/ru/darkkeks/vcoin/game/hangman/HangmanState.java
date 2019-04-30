package ru.darkkeks.vcoin.game.hangman;

public class HangmanState {

    private long coins;
    private long bet;
    private String word;
    private String guessedLetters;
    private long profit;
    private int wins;

    private HangmanSettings settings;

    public HangmanState() {
        this(Hangman.BASE_BET, 0, null, null, 0, 0, new HangmanSettings());
    }

    public HangmanState(long coins, long bet, String word, String guessedLetters, long profit, int wins,
                        HangmanSettings settings) {
        this.coins = coins;
        this.bet = bet;
        this.word = word;
        this.guessedLetters = guessedLetters;
        this.profit = profit;
        this.wins = wins;
        this.settings = settings;
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

    public long getBet() {
        return bet;
    }

    public void setBet(long bet) {
        this.bet = bet;
    }

    public void addProfit(long profit) {
        this.profit += profit;
    }

    public long getProfit() {
        return profit;
    }

    public HangmanSettings getSettings() {
        return settings;
    }

    public void incrementWins() {
        wins++;
    }

    public int getWins() {
        return wins;
    }

    public void resetWins() {
        wins = 0;
    }
}
