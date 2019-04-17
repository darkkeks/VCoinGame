package ru.darkkeks.vcoin.game;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.darkkeks.vcoin.game.api.TransactionDao;
import ru.darkkeks.vcoin.game.api.TransactionWatcher;
import ru.darkkeks.vcoin.game.api.VCoinApi;
import ru.darkkeks.vcoin.game.hangman.Hangman;

import java.util.Optional;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Launcher {

    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

    private static final int THREADS = 32;

    private static final int VCOIN_ID = Integer.valueOf(getEnv("VCOIN_ID"));
    private static final String VCOIN_KEY = getEnv("VCOIN_KEY");

    private static final int GROUP_ID = Integer.valueOf(getEnv("GROUP_ID"));
    private static final String GROUP_TOKEN = getEnv("GROUP_TOKEN");

    private static final String DATABASE_URL = getEnv("DATABASE_URL");
    private static final String DATABASE_USERNAME = getEnv("DATABASE_USERNAME");
    private static final String DATABASE_PASSWORD = getEnv("DATABASE_PASSWORD");

    public static void main(String[] args) {
        AppContext context = new AppContext();

        context.setExecutorService(new ScheduledThreadPoolExecutor(THREADS));
        context.setTransportClient(new HttpTransportClient());
        context.setVk(new VkApiClient(context.getTransportClient()));
        context.setActor(new GroupActor(GROUP_ID, GROUP_TOKEN));
        context.setVCoinApi(new VCoinApi(VCOIN_ID, VCOIN_KEY, context));
        context.setDataSource(createDataSource());

        Hangman hangman = new Hangman(context);

        TransactionWatcher watcher = new TransactionWatcher(
                context.getVCoinApi(), new TransactionDao(context.getDataSource()), hangman.getTransferConsumer());
        context.getExecutorService().scheduleAtFixedRate(watcher, 0, 2, TimeUnit.SECONDS);

        //noinspection InfiniteLoopStatement
        while(true) {
            try {
                new GameBot<>(context, hangman).run();
            } catch (ClientException | ApiException e) {
                logger.error("Long polling exception", e);
            }
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

}
