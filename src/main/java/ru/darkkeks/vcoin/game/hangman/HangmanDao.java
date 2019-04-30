package ru.darkkeks.vcoin.game.hangman;

import com.google.gson.Gson;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.darkkeks.vcoin.game.StateDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HangmanDao implements StateDao<Integer, HangmanState> {

    private static final Logger logger = LoggerFactory.getLogger(HangmanDao.class);

    private static final String RESET_STATS = "UPDATE hangman SET profit = 0, wins = 0 WHERE profit != 0 or wins != 0";

    private static final String TOP_PROFIT = "SELECT user_id, profit, RANK() over (ORDER BY profit DESC) " +
            "FROM hangman LIMIT 10";

    private static final String TOP_WIN = "SELECT user_id, wins, RANK() over (ORDER BY wins DESC) " +
            "FROM hangman LIMIT 10";

    private static final String TOP_BUY = "SELECT coin_sum.user_id, coin_sum.sum / 1000, " +
            "RANK() OVER (ORDER BY sum desc) " +
            "FROM (" +
            "     SELECT user_id, SUM(coins) AS sum" +
            "     FROM used_codes" +
            "     GROUP BY user_id" +
            ") as coin_sum LIMIT 10";

    private static final String SELECT = "SELECT * FROM hangman WHERE user_id = ?";

    private static final String UPDATE =
            "INSERT INTO hangman(user_id, coins, bet, word, letters, profit, wins, settings) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?::json) " +
            "ON CONFLICT (user_id) DO UPDATE SET " +
            "coins = excluded.coins, " +
            "bet = excluded.bet, " +
            "word = excluded.word, " +
            "letters = excluded.letters, " +
            "profit = excluded.profit, " +
            "wins = excluded.wins, " +
            "settings = excluded.settings";

    private HikariDataSource dataSource;
    private Gson gson;

    public HangmanDao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
        this.gson = new Gson();
    }

    @Override
    public HangmanState getState(Integer key) {
        logger.info("Loading state (id={})", key);
        try(Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(SELECT)) {
            statement.setInt(1, key);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return new HangmanState(
                        resultSet.getLong("coins"),
                        resultSet.getLong("bet"),
                        resultSet.getString("word"),
                        resultSet.getString("letters"),
                        resultSet.getLong("profit"),
                        resultSet.getInt("wins"),
                        gson.fromJson(resultSet.getString("settings"), HangmanSettings.class));
            }

            return new HangmanState();
        } catch (SQLException e) {
            logger.error("Cant get hangman state", e);
            return null;
        }
    }

    @Override
    public void saveState(Integer key, HangmanState state) {
        logger.info("Saved state (id={}, coins={}, bet={}, word={}, letters={}, profit={})", key, state.getCoins(),
                state.getBet(), state.getWord(), state.getGuessedLetters(), state.getProfit());

        try(Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            statement.setInt(1, key);
            statement.setLong(2, state.getCoins());
            statement.setLong(3, state.getBet());
            statement.setString(4, state.getWord());
            statement.setString(5, state.getGuessedLetters());
            statement.setLong(6, state.getProfit());
            statement.setInt(7, state.getWins());
            statement.setString(8, gson.toJson(state.getSettings()));
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Cant save hangman state", e);
        }
    }

    public void resetStats() {
        logger.info("Resetting stats");

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(RESET_STATS)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Cant reset profit", e);
        }
    }

    private List<TopEntry> getTop(String query) {
        List<TopEntry> top = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet result = statement.executeQuery();
            for(; result.next(); ) {
                top.add(new TopEntry(result.getInt(1), result.getLong(2)));
            }
        } catch (SQLException e) {
            logger.error("Cant get top", e);
        }

        return top;
    }

    public List<TopEntry> getProfitTop() {
        return getTop(TOP_PROFIT);
    }

    public List<TopEntry> getWinTop() {
        return getTop(TOP_WIN);
    }

    public List<TopEntry> getBuyTop() {
        return getTop(TOP_BUY);
    }

    public static class TopEntry {
        private int userId;
        private long value;

        public TopEntry(int userId, long value) {
            this.userId = userId;
            this.value = value;
        }

        public int getUserId() {
            return userId;
        }

        public long getValue() {
            return value;
        }
    }
}
