package ru.darkkeks.vcoin.game;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import ru.darkkeks.vcoin.game.api.Transaction;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public abstract class Game<T extends GameSession> {

    private LoadingCache<Integer, T> sessions;

    protected Game() {
        this.sessions = CacheBuilder.newBuilder().weakValues().build(new CacheLoader<Integer, T>() {
            @Override
            public T load(Integer key) {
                return createSession(key);
            }
        });
    }

    public T getSession(int chatId) {
        try {
            return sessions.get(chatId);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected LoadingCache<Integer, T> getSessions() {
        return sessions;
    }

    protected abstract T createSession(int chatId);
    protected abstract Consumer<Transaction> getTransferConsumer();
}
