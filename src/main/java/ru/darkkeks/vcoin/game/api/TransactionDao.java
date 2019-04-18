package ru.darkkeks.vcoin.game.api;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TransactionDao {

    private static final Logger logger = LoggerFactory.getLogger(TransactionDao.class);

    private static final String SELECT = "SELECT tid FROM hangman_transactions ORDER BY tid DESC LIMIT 1000";
    private static final String INSERT = "INSERT INTO hangman_transactions " +
            "(from_id, to_id, amount, created_at, tid) VALUES (?, ?, ?, ?, ?)";

    private HikariDataSource dataSource;

    public TransactionDao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Transaction> filter(List<Transaction> transactions) {
        List<Transaction> filtered = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
            PreparedStatement getTransactions = connection.prepareStatement(SELECT);
            PreparedStatement insert = connection.prepareStatement(INSERT)) {

            ResultSet currentTransactions = getTransactions.executeQuery();

            Set<Integer> ids = new HashSet<>();
            while(currentTransactions.next()) {
                ids.add(currentTransactions.getInt("tid"));
            }

            getTransactions.executeBatch();
            for(Transaction transaction : transactions) {
                if(!ids.contains(transaction.getId())) {
                    filtered.add(transaction);
                    insert.setInt(1, transaction.getFrom());
                    insert.setInt(2, transaction.getTo());
                    insert.setLong(3, transaction.getAmount());
                    insert.setTimestamp(4, new Timestamp(transaction.getCreatedAt()));
                    insert.setInt(5, transaction.getId());
                    insert.addBatch();
                }
            }

            insert.executeBatch();
        } catch (SQLException e) {
            logger.error("Can't filter transactions", e);
        }
        return filtered;
    }
}
