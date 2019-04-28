package ru.darkkeks.vcoin.game;

import com.vk.api.sdk.objects.messages.Message;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class MessageHandler<T extends GameSession> {

    private BiPredicate<Message, T> predicate;
    private BiConsumer<Message, T> handler;

    public MessageHandler(Predicate<Message> predicate, BiConsumer<Message, T> handler) {
        this((message, session) -> predicate.test(message), handler);
    }

    public MessageHandler(BiPredicate<Message, T> predicate, BiConsumer<Message, T> handler) {
        this.predicate = predicate;
        this.handler = handler;
    }

    public boolean test(Message message, T session) {
        return predicate.test(message, session);
    }

    public void accept(Message message, T session) {
        handler.accept(message, session);
    }
}
