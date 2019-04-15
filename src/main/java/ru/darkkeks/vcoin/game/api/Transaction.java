package ru.darkkeks.vcoin.game.api;

import com.google.gson.JsonObject;

public class Transaction {
    private int id;
    private int from;
    private int to;
    private long amount;
    private int type;
    private int payload;

    public Transaction(JsonObject object) {
        this.id = object.get("id").getAsInt();
        this.from = object.get("from_id").getAsInt();
        this.to = object.get("to_id").getAsInt();
        this.amount = object.get("amount").getAsLong();
        this.type = object.get("type").getAsInt();
        this.payload = object.get("payload").getAsInt();
    }

    public int getId() {
        return id;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public long getAmount() {
        return amount;
    }

    public int getType() {
        return type;
    }

    public int getPayload() {
        return payload;
    }
}
