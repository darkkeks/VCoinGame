package ru.darkkeks.vcoin.game;

import ru.darkkeks.vcoin.game.api.Transaction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public abstract class Game<T extends GameSession> {

    private Map<Integer, T> sessions;

    protected Game() {
        this.sessions = new ConcurrentHashMap<>();
    }

    public T getSession(int chatId) {
        return sessions.computeIfAbsent(chatId, this::createSession);
    }

    protected Map<Integer, T> getSessions() {
        return sessions;
    }

    protected abstract T createSession(int chatId);
    protected abstract Consumer<Transaction> getTransferConsumer();
}
