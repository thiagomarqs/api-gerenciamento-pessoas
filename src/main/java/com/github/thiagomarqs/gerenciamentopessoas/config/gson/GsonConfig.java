package com.github.thiagomarqs.gerenciamentopessoas.config.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GsonConfig {

    public static TypeAdapter<LocalDate> getLocalDateTypeAdapter() {

        return new TypeAdapter<>() {
            @Override
            public void write(JsonWriter jsonWriter, LocalDate localDate) throws IOException {
                if(localDate == null) {
                    jsonWriter.value("");
                    return;
                }
                jsonWriter.value(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            }

            @Override
            public LocalDate read(JsonReader jsonReader) throws IOException {
                return LocalDate.parse(jsonReader.nextString(), DateTimeFormatter.ISO_LOCAL_DATE);
            }
        };

    }
}
