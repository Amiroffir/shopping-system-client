package services;

import models.OrderItem;
import models.Product;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ServicesManager {
    public static final String BASE_URL = "http://localhost:8080";
    private static ServicesManager instance;

    private ProductService productService;
    private OrderItemService orderItemService;
    private UserService userService;
    private OrderService orderService;
    private List<OrderItem> cart;
    private List<Product> productsList;

    private String sessionToken = null;


    // Private constructor to prevent instantiation
    private ServicesManager() {
        // Initialize services and lists
        instance = this;
        productService = new ProductService();
        orderItemService = new OrderItemService();
        userService = new UserService();
        orderService = new OrderService();
        cart = new ArrayList<>();
        productsList = new ArrayList<>();
    }

    public static ServicesManager getInstance() {
        if (instance == null) {
            instance = new ServicesManager();
        }
        return instance;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public ProductService getProductService() {
        return productService;
    }

    public OrderItemService getOrderItemService() {
        return orderItemService;
    }

    public List<OrderItem> getCart() {
        return cart;
    }

    public void setCart(List<OrderItem> cart) {
        this.cart = cart;
    }

    public List<Product> getProductsList() {
        return productsList;
    }

    public void setProductsList(List<Product> productsList) {
        this.productsList = productsList;
    }

    public UserService getUserService() {
        return userService;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public HttpURLConnection createConnection(String url, String method, boolean isOutput) {
        try {
            URI uri = new URI(BASE_URL + url);
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod(method);
            if (isOutput) {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
            }
            return connection;
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}