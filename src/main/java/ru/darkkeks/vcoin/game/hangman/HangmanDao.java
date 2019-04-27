package ru.darkkeks.vcoin.game.hangman;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.darkkeks.vcoin.game.StateDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HangmanDao implements StateDao<Integer, HangmanState> {

    private static final Logger logger = LoggerFactory.getLogger(HangmanDao.class);

    private static final String RESET_PROFIT = "UPDATE hangman SET profit = 0 WHERE profit != 0";

    private static final String SELECT = "SELECT * FROM hangman WHERE user_id = ?";

    private static final String UPDATE =
            "INSERT INTO hangman(user_id, coins, bet, word, letters, " +
                    "showGiveUp, showImage, freeGame, definition, profit) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
            "ON CONFLICT (user_id) DO UPDATE SET " +
            "coins = excluded.coins, " +
            "bet = excluded.bet, " +
            "word = excluded.word, " +
            "letters = excluded.letters, " +
            "showGiveUp = excluded.showGiveUp, " +
            "showImage = excluded.showImage, " +
            "freeGame = excluded.freeGame, " +
            "definition = excluded.definition," +
            "profit = excluded.profit";

    private HikariDataSource dataSource;

    public HangmanDao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
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
                        resultSet.getBoolean("showGiveUp"),
                        resultSet.getBoolean("showImage"),
                        resultSet.getBoolean("freeGame"),
                        resultSet.getBoolean("definition"),
                        resultSet.getLong("profit"));
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
            statement.setBoolean(6, state.isShowGiveUp());
            statement.setBoolean(7, state.isShowImage());
            statement.setBoolean(8, state.isFreeGame());
            statement.setBoolean(9, state.isDefinition());
            statement.setLong(10, state.getProfit());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Cant save hangman state", e);
        }
    }

    public void resetProfit() {
        logger.info("Resetting profit");

        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(RESET_PROFIT)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Cant reset profit");
        }
    }
}
