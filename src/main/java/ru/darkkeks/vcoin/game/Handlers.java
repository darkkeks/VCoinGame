package ru.darkkeks.vcoin.game;

import com.vk.api.sdk.objects.messages.Message;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Handlers {

    public static <T extends GameSession> MessageHandler<T> exactMatch(String text, Consumer<T> handler) {
        return new MessageHandler<>((message) -> text.equalsIgnoreCase(message.getText()),
                (message, session) -> handler.accept(session));
    }

    public static <T extends GameSession> MessageHandler<T> any(BiConsumer<Message, T> handler) {
        return new MessageHandler<>(message -> true, handler);
    }

    public static <T extends GameSession> MessageHandler<T> regexp(String regexp, BiConsumer<Matcher, T> handler) {
        Pattern pattern = Pattern.compile(regexp);
        return new MessageHandler<>(message -> {
            return message.getText() != null && pattern.matcher(message.getText()).find();
        }, (message, session) -> {
            Matcher matcher = pattern.matcher(message.getText());
            //noinspection ResultOfMethodCallIgnored
            matcher.find();
            handler.accept(matcher, session);
        });
    }
}
