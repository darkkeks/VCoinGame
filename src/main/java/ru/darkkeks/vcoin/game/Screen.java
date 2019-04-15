package ru.darkkeks.vcoin.game;

import com.vk.api.sdk.objects.messages.Message;
import ru.darkkeks.vcoin.game.vk.keyboard.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class Screen<T extends GameSession> {

    private Keyboard keyboard;
    private MessageHandler<T> fallback;
    private List<MessageHandler<T>> handlers;

    public Screen() {
        this(null);
    }

    public Screen(Keyboard keyboard) {
        this.handlers = new ArrayList<>();
        this.keyboard = keyboard;
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }

    public void addHandler(MessageHandler<T> handler) {
        handlers.add(handler);
    }

    public void fallback(MessageHandler<T> handler) {
        fallback = handler;
    }

    public void acceptMessage(Message message, T session) {
        for (MessageHandler<T> handler : handlers) {
            if(handler.test(message)) {
                handler.accept(message, session);
                return;
            }
        }
        if(fallback != null) {
            fallback.accept(message, session);
        }
    }
}
