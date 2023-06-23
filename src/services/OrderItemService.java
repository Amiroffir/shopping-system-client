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

    public void addToCart(int productId) {
        Product productToAdd = getProductFromLocalList(productId);
        if (productToAdd == null) {
            throw new RuntimeException("Product not found");
        }
        OrderItem cartItem = createOrderItemFromRequest(productToAdd);

        HttpURLConnection connection = null;
        try {
            connection = ServicesManager.getInstance().createConnection("/order-items/add", "POST", true);
            if (ServicesManager.getInstance().getSessionToken() != null) {
                // Include session ID in the request headers
                connection.setRequestProperty("Cookie", ServicesManager.getInstance().getSessionToken());
            }

            try (OutputStream os = connection.getOutputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(os, cartItem);
            }

            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                if (ServicesManager.getInstance().getSessionToken() == null) {
                    // In case that the session ID is not set in the "authentication" phase, get it from the response header
                    // as it needed for next cart operations
                    ServicesManager.getInstance().setSessionToken(connection.getHeaderField("Set-Cookie"));
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                ObjectMapper mapper = new ObjectMapper();
                List<OrderItem> cartItems = mapper.readValue(reader, mapper.getTypeFactory()
                        .constructCollectionType(List.class, OrderItem.class));
                ServicesManager.getInstance().setCart(cartItems); // Update the local cart
            } else {
                throw new RuntimeException(connection.getResponseMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect(); // Disconnect and close the connection
        }
    }

    public List<OrderItem> viewCart() {
        List<OrderItem> cartItems;
        HttpURLConnection connection = null;
        try {
            connection = ServicesManager.getInstance().createConnection("/order-items/view", "GET", false);
            if (ServicesManager.getInstance().getSessionToken() != null) {
                connection.setRequestProperty("Cookie", ServicesManager.getInstance().getSessionToken());
            }

            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                if (ServicesManager.getInstance().getSessionToken() == null) {
                    // In case that the session ID is not set in the "authentication" phase, get it from the response header
                    // as it needed for next cart operations
                    ServicesManager.getInstance().setSessionToken(connection.getHeaderField("Set-Cookie"));
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                ObjectMapper mapper = new ObjectMapper();
                cartItems = mapper.readValue(reader, mapper.getTypeFactory()
                        .constructCollectionType(List.class, OrderItem.class));
            } else {
                throw new RuntimeException(connection.getResponseMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect(); // Disconnect and close the connection
        }
        return cartItems;
    }

    public void updateItemQuantity(int cartProductNumber, int quantity) {
        if(cartProductNumber < 1 || cartProductNumber > ServicesManager.getInstance().getCart().size()) {
            throw new RuntimeException("Item not found");
        }
        OrderItem cartItem = ServicesManager.getInstance().getCart().get(cartProductNumber - 1);
        int oldQuantity = cartItem.getQuantity();   // Enable to roll back if the update fails
        // Update the cart item quantity and amount in the local cart first
        cartItem.setQuantity(quantity);
        cartItem.setItemAmount(cartItem.getProduct().getPrice() * cartItem.getQuantity());

        HttpURLConnection connection = null;
        try {
            connection = ServicesManager.getInstance().createConnection("/order-items/update", "PUT", true);
            if (ServicesManager.getInstance().getSessionToken() != null) {
                connection.setRequestProperty("Cookie", ServicesManager.getInstance().getSessionToken());
            }
            try (OutputStream os = connection.getOutputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(os, cartItem);
            }

            connection.connect();
            int responseCode = connection.getResponseCode();
            if (!(responseCode == HttpURLConnection.HTTP_OK)) {
                // Rollback the local cart and throw an exception
                cartItem.setQuantity(oldQuantity);
                cartItem.setItemAmount(cartItem.getProduct().getPrice() * cartItem.getQuantity());
                throw new RuntimeException(connection.getResponseMessage());
            }
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("Product not found");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                connection.disconnect(); // Disconnect and close the connection
            }
        }
    }

    public void removeItemFromCart(int cartItem) {
        if( cartItem < 1 ||  cartItem > ServicesManager.getInstance().getCart().size()) {
            throw new RuntimeException("Item not found");
        }
        OrderItem itemToRemove = ServicesManager.getInstance().getCart().get(cartItem - 1);
        if (itemToRemove == null) {
            throw new RuntimeException("Product not found");
        }

        HttpURLConnection connection = null;
        try {
            connection = ServicesManager.getInstance().createConnection("/order-items/remove", "DELETE", true);
            if (ServicesManager.getInstance().getSessionToken() != null) {
                connection.setRequestProperty("Cookie", ServicesManager.getInstance().getSessionToken());
            }
            try (OutputStream os = connection.getOutputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(os, itemToRemove);
            }

            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                ServicesManager.getInstance().getCart().remove(cartItem - 1);
            } else {
                throw new RuntimeException(connection.getResponseMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private OrderItem createOrderItemFromRequest(Product product) {
        OrderItem cartItem = new OrderItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(1);
        cartItem.setItemAmount(product.getPrice() * cartItem.getQuantity());
        return cartItem;
    }

    private Product getProductFromLocalList(int productId) {
        return ServicesManager.getInstance().getProductsList().stream()
                .filter(product -> product.getProductId() == productId).findFirst().orElse(null);
    }
}