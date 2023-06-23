package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.Order;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class OrderService {

    public Order completeOrder() {
        Order order;
        HttpURLConnection connection = null;
        try {
            connection = ServicesManager.getInstance().createConnection("/orders/checkout", "POST", true);
            connection.setRequestProperty("Cookie", ServicesManager.getInstance().getSessionToken());

            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                ObjectMapper mapper = new ObjectMapper();
                order = mapper.readValue(reader, Order.class);
                ServicesManager.getInstance().getCart().clear(); // Clear the cart
            } else {
                throw new RuntimeException(connection.getResponseMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect();
        }
        return order;
    }
}