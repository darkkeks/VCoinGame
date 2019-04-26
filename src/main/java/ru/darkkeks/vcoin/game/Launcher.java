package ru.darkkeks.vcoin.game;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.enums.MessagesFilter;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.responses.GetConversationsResponse;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.darkkeks.vcoin.game.api.TransactionDao;
import ru.darkkeks.vcoin.game.api.TransactionWatcher;
import ru.darkkeks.vcoin.game.api.VCoinApi;
import ru.darkkeks.vcoin.game.hangman.Hangman;
import ru.darkkeks.vcoin.game.hangman.HangmanSession;
import ru.darkkeks.vcoin.game.vk.FollowerManager;
import ru.darkkeks.vcoin.game.vk.MessageBatcher;

import java.util.Optional;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Launcher {

    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

    private static final int THREADS = 16;

    private static final int VCOIN_ID = Integer.valueOf(getEnv("VCOIN_ID"));
    private static final String VCOIN_KEY = getEnv("VCOIN_KEY");
    private static final int VCOIN_PAYLOAD = Integer.valueOf(getEnv("VCOIN_PAYLOAD"));

    private static final int GROUP_ID = Integer.valueOf(getEnv("GROUP_ID"));
    private static final String GROUP_TOKEN = getEnv("GROUP_TOKEN");

    private static final String DATABASE_URL = getEnv("DATABASE_URL");
    private static final String DATABASE_USERNAME = getEnv("DATABASE_USERNAME");
    private static final String DATABASE_PASSWORD = getEnv("DATABASE_PASSWORD");

    private AppContext context;
    private Hangman hangman;

    private void start() {
        context = new AppContext();

        context.setExecutorService(new ScheduledThreadPoolExecutor(THREADS));
        context.setTransportClient(new HttpTransportClient());
        context.setVk(new VkApiClient(context.getTransportClient()));
        context.setActor(new GroupActor(GROUP_ID, GROUP_TOKEN));
        context.setVCoinApi(new VCoinApi(VCOIN_ID, VCOIN_KEY, VCOIN_PAYLOAD, context));
        context.setDataSource(createDataSource());
        context.setMessageBatcher(new MessageBatcher(context));
        context.setFollowerManager(new FollowerManager(context));

        context.getFollowerManager().start();

        hangman = new Hangman(context);

        TransactionWatcher watcher = new TransactionWatcher(
                context.getVCoinApi(), new TransactionDao(context.getDataSource()), hangman.getTransferConsumer());
        context.getExecutorService().scheduleAtFixedRate(watcher, 0, 2, TimeUnit.SECONDS);


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down");
            try {
                context.getExecutorService().shutdown();
                context.getVCoinApi().getTransferExecutor().shutdown();
                context.getExecutorService().awaitTermination(10, TimeUnit.SECONDS);
                context.getVCoinApi().getTransferExecutor().awaitTermination(10, TimeUnit.SECONDS);
                context.getDataSource().close();
            } catch (InterruptedException ignored) {}
        }));

        startLongPoll();
    }

    private void startLongPoll() {
        GameBotLongPoll<HangmanSession> game = new GameBotLongPoll<>(context, hangman);

        //noinspection InfiniteLoopStatement
        while(true) {
            processUnread(message -> game.messageNew(context.getActor().getId(), message));
            try {
                game.run();
            } catch (ClientException | ApiException e) {
                logger.error("Long polling exception", e);
            } catch (Exception e) {
                logger.error("Unknown exception", e);
            }
        }
    }

    private void processUnread(Consumer<Message> consumer) {
        try {
            for(int offset = 0; ; offset += 200) {
                GetConversationsResponse unread = context.getVk().messages().getConversations(context.getActor())
                        .count(200)
                        .offset(offset)
                        .filter(MessagesFilter.UNREAD)
                        .execute();

                context.getExecutorService().submit(() -> {
                    unread.getItems().forEach(conversation -> {
                        Message message = conversation.getLastMessage();
                        consumer.accept(message);
                    });
                });

                if(unread.getCount() < offset + 200) {
                    break;
                }
            }
        } catch (ApiException | ClientException e) {
            logger.error("Can't get unread conversations", e);
        } catch (Exception e) {
            logger.error("Unknown exception", e);
        }
    }

    private static HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(DATABASE_URL);
        config.setUsername(DATABASE_USERNAME);
        config.setPassword(DATABASE_PASSWORD);

        return new HikariDataSource(config);
    }

    private static String getEnv(String name) {
        return Optional.ofNullable(System.getenv(name)).orElseThrow(() -> new IllegalStateException("Env " + name));
    }

    public static void main(String[] args) {
        new Launcher().start();
    }
}
