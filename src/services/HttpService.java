package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import models.Product;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class HttpService {

    private ObjectMapper objectMapper;

    public HttpService() {
        this.objectMapper = new ObjectMapper();
    }

    public HttpRequest createRequest(String urlPath, String method, boolean requiresSession) {
        String baseUrl = ServicesManager.BASE_URL;
        String requestUrl = baseUrl + urlPath;

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .method(method, HttpRequest.BodyPublishers.noBody());

        if (requiresSession) {
            String sessionToken = ServicesManager.getInstance().getSessionToken();
            requestBuilder = requestBuilder.header("Cookie", sessionToken);
        }
        return requestBuilder.build();
    }

    public <T> HttpRequest setRequestToOutput(HttpRequest request, T requestBody, String method) throws
            JsonProcessingException {
        String requestBodyJson = objectMapper.writeValueAsString(requestBody);
        // Copy the request from the original request
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(request.uri());
        Map<String, List<String>> headers = request.headers().map();
        for (String header : headers.keySet()) {
            requestBuilder = requestBuilder.header(header, headers.get(header).get(0));
        }
        // Set the body and content type
        requestBuilder = requestBuilder.method(method, HttpRequest.BodyPublishers.ofString(requestBodyJson))
                .header("Content-Type", "application/json");
        return requestBuilder.build();
    }

    public <T> T parseResponse(HttpResponse<String> response, Class<T> valueType, int expectedResponse) throws
            IOException {
        int statusCode = response.statusCode();
        if (statusCode == expectedResponse) {
            // Convert the response body to the desired type
            return objectMapper.readValue(response.body(), valueType);
        } else {
            throw new RuntimeException("HTTP Error: " + statusCode + " - " + response.body());
        }
    }

    public <T> List<T> parseResponseToList(HttpResponse<String> response,
            Class<T> type, int expectedResponse) throws IOException {
        int statusCode = response.statusCode();
        if (statusCode == expectedResponse) {
            // Convert the response body to the desired type (List of T)
            CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, type);
            return objectMapper.readValue(response.body(), collectionType);
        } else {
            throw new RuntimeException("HTTP Error: " + statusCode + " - " + response.body());
        }
    }
}