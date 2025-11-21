package org.daylight.museumapp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.daylight.museumapp.dto.*;
import org.daylight.museumapp.model.StatCard;
import org.daylight.museumapp.util.Icons;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class ApiService {
    private static ApiService instance;
    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private final Gson gson;
    private final String baseUrl = "http://localhost:8042"; // Замените на ваш URL

    private ApiService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
        this.mapper = new ObjectMapper();
    }

    public static ApiService getInstance() {
        if (instance == null) {
            instance = new ApiService();
        }
        return instance;
    }

    public List<StatCard> getStats() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/public/general_stats"))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return parseStatsFromJson(response.body());
            } else {
                System.err.println("API error: " + response.statusCode());
                return getFallbackStats();
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to fetch stats: " + e.getMessage());
            return getFallbackStats();
        }
    }

    private List<StatCard> parseStatsFromJson(String json) {
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        return Arrays.asList(
                new StatCard("Экспонаты",
                        String.valueOf(jsonObject.get("totalItems").getAsInt()),
                        "/exhibits",
                        Icons.EXTERNAL_LINK),
                new StatCard("Коллекции",
                        String.valueOf(jsonObject.get("totalCollections").getAsInt()),
                        "/collections",
                        Icons.FOLDER_OPEN),
                new StatCard("Залы",
                        String.valueOf(jsonObject.get("totalHalls").getAsInt()),
                        "/halls",
                        Icons.DOOR_OPEN),
                new StatCard("Авторы",
                        String.valueOf(jsonObject.get("totalAuthors").getAsInt()),
                        "/authors",
                        Icons.USERS)
        );
    }

    private List<StatCard> getFallbackStats() {
        // Заглушка на случай недоступности API
        return Arrays.asList(
                new StatCard("Экспонаты", "-", "/exhibits", Icons.EXTERNAL_LINK),
                new StatCard("Коллекции", "-", "/collections", Icons.FOLDER_OPEN),
                new StatCard("Залы", "-", "/halls", Icons.DOOR_OPEN),
                new StatCard("Авторы", "-", "/authors", Icons.USERS)
        );
    }

    public ApiResult<Void> register(String username, String password, String fullName) {
        try {
            String json = mapper.writeValueAsString(
                    new RegisterRequestData(username, password, fullName)
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/auth/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return ApiResult.success(null);
            }

            try {
                ApiError error = gson.fromJson(response.body(), ApiError.class);
                return ApiResult.error(error.getMessage());
            } catch (Exception parseError) {
                return ApiResult.error("Ошибка регистрации: " + response.statusCode());
            }

        } catch (IOException e) {
            return ApiResult.error("Ошибка сериализации запроса");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ApiResult.error("Запрос был прерван");
        } catch (Exception e) {
            return ApiResult.error("Сетевая ошибка: " + e.getMessage());
        }
    }

    public ApiResult<UserData> login(String username, String password) {
        try {
            String json = mapper.writeValueAsString(new LoginRequestData(username, password));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                UserData user = gson.fromJson(response.body(), UserData.class);
                return ApiResult.success(user);
            }

            ApiError error = gson.fromJson(response.body(), ApiError.class);
            return ApiResult.error(error.getMessage());

        } catch (Exception e) {
            return ApiResult.error("Сетевая ошибка");
        }
    }
}
