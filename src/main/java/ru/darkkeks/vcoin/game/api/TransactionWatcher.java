package ru.darkkeks.vcoin.game.api;

import java.util.function.Consumer;

public class TransactionWatcher implements Runnable {

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
        });

        api.getTransactions(VCoinApi.RequestType.USER).thenAccept(transactions -> {
            transactionDao.filter(transactions).forEach(handler);
        });
    }
}
