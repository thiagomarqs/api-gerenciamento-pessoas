package integration.junit.utils;

import com.github.thiagomarqs.gerenciamentopessoas.config.gson.GsonConfig;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;

public class GsonClient {

    private static final com.google.gson.Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, GsonConfig.getLocalDateTypeAdapter())
            .create();

    public static com.google.gson.Gson get() {
        return gson;
    }
}