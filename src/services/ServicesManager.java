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
    private List<OrderItem> cart;
    private List<Product> productsList;


    // Private constructor to prevent instantiation
    private ServicesManager() {
        // Initialize variables or perform other setup operations
        instance = this;
        productService = new ProductService();
        orderItemService = new OrderItemService();
        cart = new ArrayList<>();
        productsList = new ArrayList<>();
    }

    public static ServicesManager getInstance() {
        if (instance == null) {
            instance = new ServicesManager();
        }
        return instance;
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

    public HttpURLConnection createConnection(String url, String method) {
        try {
            URI uri = new URI(BASE_URL + url);
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod(method);
            return connection;
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}