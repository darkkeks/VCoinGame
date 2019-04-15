package ru.darkkeks.vcoin.game.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class TransactionWatcher implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TransactionWatcher.class);

    private VCoinApi api;
    private Consumer<Transaction> handler;
    private TransactionDao transactionDao;

    public TransactionWatcher(VCoinApi api, TransactionDao transactionDao, Consumer<Transaction> handler) {
        this.api = api;
        this.handler = handler;
        this.transactionDao = transactionDao;
    }

    @Override
    public void run() {
        api.getTransactions(VCoinApi.RequestType.MERCHANT).thenAccept(transactions -> {
            transactionDao.filter(transactions).forEach(handler);
        }).whenComplete((aVoid, throwable) -> {
            if(throwable != null) {
                logger.error("Error while handling transactions", throwable);
            }
        });
    }
}
