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

    private static final String SELECT = "SELECT * FROM hangman WHERE user_id = ?";

    private static final String UPDATE =
            "INSERT INTO hangman(user_id, coins, word, letters) VALUES (?, ?, ?, ?) " +
            "ON CONFLICT (user_id) " +
            "DO UPDATE SET " +
            "coins = excluded.coins, " +
            "word = excluded.word, " +
            "letters = excluded.letters";

    private HikariDataSource dataSource;

    public HangmanDao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }
    @Override
    public HangmanState getState(Integer key) {
        try(Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(SELECT)) {
            statement.setInt(1, key);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return new HangmanState(
                        resultSet.getLong("coins"),
                        resultSet.getString("word"),
                        resultSet.getString("letters"));
            }

            return new HangmanState();
        } catch (SQLException e) {
            logger.error("Cant get hangman state", e);
            return null;
        }
    }

    @Override
    public void saveState(Integer key, HangmanState state) {
        logger.info("Saved state (id={}, coins={}, word={}, letters={}", key, state.getCoins(),
                state.getWord(), state.getGuessedLetters());

        try(Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            statement.setInt(1, key);
            statement.setLong(2, state.getCoins());
            statement.setString(3, state.getWord());
            statement.setString(4, state.getGuessedLetters());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Cant save hangman state", e);
        }
    }
}
