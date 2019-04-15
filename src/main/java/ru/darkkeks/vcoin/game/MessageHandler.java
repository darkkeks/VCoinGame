package ru.darkkeks.vcoin.game;

import com.vk.api.sdk.objects.messages.Message;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class MessageHandler<T extends GameSession> {

    private Predicate<Message> predicate;
    private BiConsumer<Message, T> handler;

    public MessageHandler(Predicate<Message> predicate, BiConsumer<Message, T> handler) {
        this.predicate = predicate;
        this.handler = handler;
    }

    public boolean test(Message message) {
        return predicate.test(message);
    }

    public void accept(Message message, T session) {
        handler.accept(message, session);
    }
}
