package ru.darkkeks.vcoin.game;

import ru.darkkeks.vcoin.game.vk.keyboard.Keyboard;

import java.util.function.Function;

public class StaticKeyboard<T extends GameSession> implements Function<T, Keyboard> {

    private Keyboard keyboard;

    public StaticKeyboard(Keyboard keyboard) {
        this.keyboard = keyboard;
    }

    @Override
    public Keyboard apply(T t) {
        return keyboard;
    }
}
