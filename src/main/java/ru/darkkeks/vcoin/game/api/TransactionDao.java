package ru.darkkeks.vcoin.game.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TransactionDao {

    private Set<Integer> ids = new HashSet<>();

    public List<Transaction> filter(List<Transaction> transactions) {
        List<Transaction> result = new ArrayList<>();
        transactions.forEach(tr -> {
            if(!ids.contains(tr.getId())) {
                result.add(tr);
                ids.add(tr.getId());
            }
        });
        return result;
    }

}
