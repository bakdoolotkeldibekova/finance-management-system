package com.example.fms.gson.serialize;

import com.example.fms.entity.Journal;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;

public class JournalSerializer implements JsonSerializer<Journal> {
    @Override
    public JsonElement serialize(Journal journal, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();

        result.addProperty("ID", journal.getId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        result.addProperty("DateCreated", journal.getDateCreated().format(formatter));
        if (journal.getDateUpdated() != null)
            result.addProperty("DateUpdated", journal.getDateUpdated().format(formatter));

        String user = journal.getUser().getName() + journal.getUser().getSurname();
        result.addProperty("User", user);
        result.addProperty("Table", journal.getTable());
        result.addProperty("Action", journal.getAction());
        result.addProperty("Deleted", journal.isDeleted());
        return result;
    }
}
