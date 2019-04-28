package ru.darkkeks.vcoin.game.vk;

import com.google.gson.JsonElement;
import com.vk.api.sdk.client.AbstractQueryBuilder;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.queries.messages.MessagesSendQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.darkkeks.vcoin.game.AppContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MessageBatcher {

    private static final Logger logger = LoggerFactory.getLogger(MessageBatcher.class);

    private static final int PERIOD_MS = 75; // should be >= 50
    private static final int MAX_BATCH_SIZE = 25;

    private AppContext appContext;
    private BlockingQueue<MessagesSendQuery> messageQueue;

    public MessageBatcher(AppContext appContext) {
        this.appContext = appContext;
        this.messageQueue = new LinkedBlockingQueue<>();

        ScheduledExecutorService executor = appContext.getExecutorService();
        executor.scheduleAtFixedRate(() -> {
            executor.submit(this::flush);
        }, 0, PERIOD_MS, TimeUnit.MILLISECONDS);
    }

    private void flush() {
        List<AbstractQueryBuilder> queries = new ArrayList<>();
        while(queries.size() < MAX_BATCH_SIZE) {
            MessagesSendQuery message = messageQueue.poll();
            if(message != null) {
                queries.add(message);
            } else {
                break;
            }
        }

        if(queries.size() == MAX_BATCH_SIZE) {
            logger.warn("Message backlog, {} messages still in queue", messageQueue.size());
        }

        if(!queries.isEmpty()) {
            logger.info("Sending batch of size {}", queries.size());
            try {
                appContext.getVk().execute().batch(appContext.getActor(), queries).execute();
            } catch (ApiException | ClientException e) {
                logger.error("Api error", e);
            }
        }
    }

    public void sendMessage(MessagesSendQuery query) {
        messageQueue.add(query);
    }

}
