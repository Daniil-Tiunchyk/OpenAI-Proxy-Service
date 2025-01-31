package com.example.proxyapi.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Утилита для отправки HTTP-запросов к ProxyAPI.
 */
public class ProxyApiHttpClient {

    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    private ProxyApiHttpClient() {
    }

    /**
     * Отправить GET-запрос.
     *
     * @param url    URL запроса
     * @param apiKey Ключ API для авторизации
     * @return Тело ответа как строка
     * @throws IOException
     * @throws InterruptedException
     */
    public static String sendGet(String url, String apiKey) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + apiKey)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new IOException("GET request failed with status code: " + response.statusCode() +
                    " and body: " + response.body());
        }
    }

    /**
     * Отправить POST-запрос с JSON-телом.
     *
     * @param url      URL запроса
     * @param jsonBody JSON-тело запроса
     * @param apiKey   Ключ API для авторизации
     * @return Тело ответа как строка
     * @throws IOException
     * @throws InterruptedException
     */
    public static String sendPost(String url, String jsonBody, String apiKey) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new IOException("POST request failed with status code: " + response.statusCode() +
                    " and body: " + response.body());
        }
    }

    /**
     * Отправить POST-запрос с JSON-телом и получить бинарный ответ.
     *
     * @param url      URL запроса
     * @param jsonBody JSON-тело запроса
     * @param apiKey   Ключ API для авторизации
     * @return Бинарные данные ответа
     * @throws IOException
     * @throws InterruptedException
     */
    public static byte[] sendPostForBytes(String url, String jsonBody, String apiKey) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new IOException("POST request failed with status code: " + response.statusCode() +
                    " and body: " + new String(response.body()));
        }
    }

    /**
     * Отправить POST-запрос с multipart/form-data телом.
     *
     * @param url       URL запроса
     * @param fields    Поля формы (ключ - имя поля, значение - значение поля)
     * @param fileParts Список файлов для загрузки
     * @param apiKey    Ключ API для авторизации
     * @return Тело ответа как строка
     * @throws IOException
     * @throws InterruptedException
     */
    public static String sendMultipartPost(String url, Map<String, String> fields, List<FilePart> fileParts, String apiKey) throws IOException, InterruptedException {
        String boundary = "Boundary-" + UUID.randomUUID();
        var byteArrayBuilder = new ByteArrayOutputStream();

        // Добавление текстовых полей
        for (Map.Entry<String, String> field : fields.entrySet()) {
            byteArrayBuilder.write(("--" + boundary + "\r\n").getBytes());
            byteArrayBuilder.write(("Content-Disposition: form-data; name=\"" + field.getKey() + "\"\r\n\r\n").getBytes());
            byteArrayBuilder.write((field.getValue() + "\r\n").getBytes());
        }

        // Добавление файлов
        for (FilePart filePart : fileParts) {
            byteArrayBuilder.write(("--" + boundary + "\r\n").getBytes());
            byteArrayBuilder.write(("Content-Disposition: form-data; name=\"" + filePart.getFieldName() + "\"; filename=\"" + filePart.getFileName() + "\"\r\n").getBytes());
            byteArrayBuilder.write(("Content-Type: " + filePart.getContentType() + "\r\n\r\n").getBytes());
            byteArrayBuilder.write(filePart.getContent());
            byteArrayBuilder.write("\r\n".getBytes());
        }

        // Завершение границы
        byteArrayBuilder.write(("--" + boundary + "--\r\n").getBytes());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(byteArrayBuilder.toByteArray()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new IOException("Multipart POST request failed with status code: " + response.statusCode() +
                    " and body: " + response.body());
        }
    }
}
