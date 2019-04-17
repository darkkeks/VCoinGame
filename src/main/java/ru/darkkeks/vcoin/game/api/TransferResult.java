package ru.darkkeks.vcoin.game.api;

import com.google.gson.JsonObject;

public class TransferResult {
    private int id;
    private long amount;
    private long current;

    public TransferResult(JsonObject object) {
        id = object.get("id").getAsInt();
        amount = object.get("amount").getAsLong();
        current = object.get("current").getAsLong();
    }

    public int getId() {
        return id;
    }

    public long getAmount() {
        return amount;
    }

    public long getCurrent() {
        return current;
    }
}
