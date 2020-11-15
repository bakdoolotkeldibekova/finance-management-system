package com.example.fms.gson.serialize;

import com.example.fms.entity.Transaction;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class TransactionSerializer implements JsonSerializer<Transaction> {
    @Override
    public JsonElement serialize(Transaction transaction, Type type, JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        result.addProperty("Action", transaction.getAction());
        result.add("FromAccount", context.serialize(transaction.getFromAccount()));
        result.add("Category", context.serialize(transaction.getCategory()));
        result.add("ToAccount", context.serialize(transaction.getToAccount()));
        result.addProperty("Balance", transaction.getBalance());
        result.add("User", context.serialize(transaction.getUser()));
        result.add("Project", context.serialize(transaction.getProject()));
        result.add("Counterparty", context.serialize(transaction.getCounterparty()));
        result.addProperty("Description", transaction.getDescription());
        result.addProperty("Deleted", transaction.isDeleted());

        return result;
    }
}
