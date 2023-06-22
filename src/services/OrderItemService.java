package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.OrderItem;
import models.Product;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;

public class OrderItemService {

    private String sessionId = null;

    public void addToCart(int productId) {
        Product productToAdd = getProductFromLocalList(productId);
        if (productToAdd == null) {
            throw new RuntimeException("Product not found");
        }
        OrderItem cartItem = new OrderItem();
        cartItem.setProduct(productToAdd);
        cartItem.setQuantity(1);
        cartItem.setItemAmount(productToAdd.getPrice() * cartItem.getQuantity());

        HttpURLConnection connection = null;
        try {
            connection = ServicesManager.getInstance().createConnection("/order-items/add", "POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            if (sessionId != null) {
                connection.setRequestProperty("Cookie", sessionId); // Include session ID in the request headers
            }
            try (OutputStream os = connection.getOutputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(os, cartItem);
            }


            connection.connect();
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                if (sessionId == null) {
                    sessionId = connection.getHeaderField("Set-Cookie"); // Get session ID from the response header
                }
                ServicesManager.getInstance().getCart().add(cartItem);
            } else {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect(); // Disconnect and close the connection
        }
    }


    private Product getProductFromLocalList(int productId) {
        for (Product product : ServicesManager.getInstance().getProductsList()) {
            if (product.getProductId() == productId) {
                return product;
            }
        }
        return null;
    }

    public List<OrderItem> viewCart() {
        List<OrderItem> cartItems;
        HttpURLConnection connection = null;
        try {
            connection = ServicesManager.getInstance().createConnection("/order-items/view", "GET");
            if (sessionId != null) {
                connection.setRequestProperty("Cookie", sessionId); // Include session ID in the request headers
            }
            connection.connect();
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                ObjectMapper mapper = new ObjectMapper();
                cartItems = mapper.readValue(reader, mapper.getTypeFactory()
                        .constructCollectionType(List.class, OrderItem.class));
            } else {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect(); // Disconnect and close the connection
        }
        return cartItems;
    }
}