package ru.darkkeks.vcoin.game.api;

import com.google.gson.*;
import ru.darkkeks.vcoin.game.AppContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class VCoinApi {

    private static final String GET_TRANSACTIONS = "https://coin-without-bugs.vkforms.ru/merchant/tx/";
    private static final String TRANSFER = "https://coin-without-bugs.vkforms.ru/merchant/send/";
    private static final String PAY_LINK = "https://vk.com/coin#x%d_%d_%d_1";
    private static final String FIXED_PAY_LINK = "https://vk.com/coin#x%d_%d_%d";

    private static final JsonParser jsonParser = new JsonParser();

    private AppContext context;

    private ExecutorService transferExecutor;

    private int userId;
    private String apiKey;

    public VCoinApi(int userId, String apiKey, AppContext context) {
        this.userId = userId;
        this.apiKey = apiKey;
        this.context = context;

        // Has to be one thread, so we never do concurrent transfers
        this.transferExecutor = new ScheduledThreadPoolExecutor(1);
    }

    private String doRequest(String url, String data) {
        try {
            return context.getTransportClient().post(url, data).getContent();
        } catch (IOException e) {
            return null;
        }
    }

    public CompletableFuture<List<Transaction>> getTransactions(RequestType type) {
        return CompletableFuture.supplyAsync(() -> {
            JsonObject data = new JsonObject();
            data.add("merchantId", new JsonPrimitive(userId));
            data.add("key", new JsonPrimitive(apiKey));
            data.add("tx", type.toJson());
            return doRequest(GET_TRANSACTIONS, data.toString());
        }, context.getExecutorService()).thenApply(message -> {
            JsonObject response = jsonParser.parse(message).getAsJsonObject();
            List<Transaction> transactions = new ArrayList<>();

            response.get("response").getAsJsonArray().forEach(element -> {
                transactions.add(new Transaction(element.getAsJsonObject()));
            });

            return transactions;
        });
    }

    public CompletableFuture<TransferResult> transfer(int to, long amount) {
        return CompletableFuture.supplyAsync(() -> {
            JsonObject data = new JsonObject();
            data.add("merchantId", new JsonPrimitive(userId));
            data.add("key", new JsonPrimitive(apiKey));
            data.add("toId", new JsonPrimitive(to));
            data.add("amount", new JsonPrimitive(amount));
            return doRequest(TRANSFER, data.toString());
        }, transferExecutor)
                .thenApply(message -> {
                    JsonObject result = jsonParser.parse(message).getAsJsonObject();
                    if(result.has("error")) {
                        throw new IllegalStateException(result.get("error").getAsJsonObject()
                                .get("message").getAsString());
                    }
                    return new TransferResult(result);
                });
    }

    public String getPaymentLink(long amount) {
        return String.format(PAY_LINK, userId, amount, 0);
    }

    public String getFixedPaymentLink(long amount) {
        return String.format(FIXED_PAY_LINK, userId, amount, 0);
    }

    public int getUserId() {
        return userId;
    }

    public enum RequestType {
        MERCHANT(1),
        USER(2);

        int type;

        RequestType(int type) {
            this.type = type;
        }

        protected JsonElement toJson() {
            JsonArray result = new JsonArray();
            result.add(type);
            return result;
        }
    }
}
