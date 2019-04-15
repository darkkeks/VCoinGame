package ru.darkkeks.vcoin.game;

public interface StateDao<K, T> {
    T getState(K key);
    void saveState(K key, T state);
}
