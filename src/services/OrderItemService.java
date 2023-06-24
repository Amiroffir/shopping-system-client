package services;

import models.OrderItem;
import models.Product;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class OrderItemService {
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public void addToCart(int productId) {
        Product productToAdd = getProductFromLocalList(productId);
        if (productToAdd == null) {
            throw new RuntimeException("Product not found");
        }
        OrderItem cartItem = createOrderItemFromRequest(productToAdd);

        try {
            HttpRequest request = ServicesManager.getInstance().getHttpService()
                    .createRequest("/order-items/add", "POST", true);
            request = ServicesManager.getInstance().getHttpService().setRequestToOutput(request, cartItem, "POST");

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            List<OrderItem> cartItems = ServicesManager.getInstance().getHttpService()
                    .parseResponseToList(response, OrderItem.class, HttpURLConnection.HTTP_OK);

            ServicesManager.getInstance().setCart(cartItems); // Update the local cart
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<OrderItem> viewCart() {
        List<OrderItem> cartItems;
        try {
            HttpRequest request = ServicesManager.getInstance().getHttpService()
                    .createRequest("/order-items/view", "GET", true);

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            cartItems = ServicesManager.getInstance().getHttpService()
                    .parseResponseToList(response, OrderItem.class, HttpURLConnection.HTTP_OK);

            ServicesManager.getInstance().setCart(cartItems); // Update the local cart
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return cartItems;
    }

    public void updateItemQuantity(int cartProductNumber, int quantity) {
        if (cartProductNumber < 1 || cartProductNumber > ServicesManager.getInstance().getCart().size()) {
            throw new RuntimeException("Item not found");
        }
        OrderItem cartItem = ServicesManager.getInstance().getCart().get(cartProductNumber - 1);
        int oldQuantity = cartItem.getQuantity();   // Enable to roll back if the update fails
        // Update the cart item quantity and amount in the local cart first
        cartItem.setQuantity(quantity);
        cartItem.setItemAmount(cartItem.getProduct().getPrice() * cartItem.getQuantity());

        try {
            HttpRequest request = ServicesManager.getInstance().getHttpService()
                    .createRequest("/order-items/update", "PUT", true);
            request = ServicesManager.getInstance().getHttpService().setRequestToOutput(request, cartItem, "PUT");

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            OrderItem updatedCartItem = ServicesManager.getInstance().getHttpService()
                    .parseResponse(response, OrderItem.class, HttpURLConnection.HTTP_OK);
        } catch (IOException | InterruptedException e) {
            // Roll back the local cart if the update fails
            cartItem.setQuantity(oldQuantity);
            cartItem.setItemAmount(cartItem.getProduct().getPrice() * cartItem.getQuantity());
            throw new RuntimeException(e);
        }
    }

    public void removeItemFromCart(int cartItem) {
        if (cartItem < 1 || cartItem > ServicesManager.getInstance().getCart().size()) {
            throw new RuntimeException("Item not found");
        }
        OrderItem itemToRemove = ServicesManager.getInstance().getCart().get(cartItem - 1);
        if (itemToRemove == null) {
            throw new RuntimeException("Product not found");
        }
        try {
            HttpRequest request = ServicesManager.getInstance().getHttpService()
                    .createRequest("/order-items/remove", "DELETE", true);
            request = ServicesManager.getInstance().getHttpService().setRequestToOutput(request, itemToRemove, "DELETE");

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                // Remove the item from the local cart
                ServicesManager.getInstance().getCart().remove(cartItem - 1);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
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