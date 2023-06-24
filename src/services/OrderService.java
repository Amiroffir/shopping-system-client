package services;

import models.Order;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OrderService {
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public Order completeOrder() {
        Order order;
        try {
            HttpRequest request =
                    ServicesManager.getInstance().getHttpService().createRequest("/orders/checkout", "POST", true);

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            order = ServicesManager.getInstance().getHttpService()
                    .parseResponse(response, Order.class, HttpURLConnection.HTTP_OK);

            ServicesManager.getInstance().getCart().clear(); // Clear the cart
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return order;
    }
}