package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.Product;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;

public class ProductService {

    public List<Product> getAllProducts() {
        ServicesManager.getInstance().getProductsList().clear();
        List<Product> products;
        HttpURLConnection connection = null;
        try {
            connection = ServicesManager.getInstance().createConnection("/products/get", "GET");
            connection.connect();
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                ObjectMapper mapper = new ObjectMapper();
                products = mapper.readValue(reader, mapper.getTypeFactory()
                        .constructCollectionType(List.class, Product.class));

                ServicesManager.getInstance().setProductsList(products);
            } else {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect(); // Disconnect and close the connection
        }
        return products;
    }

    public List<Product> searchByName(String name) {
        List<Product> filterProducts;
        HttpURLConnection connection = null;
        try {
            connection = ServicesManager.getInstance()
                    .createConnection("/products/filter?searchBy=name&name=" + name, "GET");
            connection.connect();
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                ObjectMapper mapper = new ObjectMapper();
                filterProducts = mapper.readValue(reader, mapper.getTypeFactory()
                        .constructCollectionType(List.class, Product.class));
            } else {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect(); // Disconnect and close the connection
        }
        return filterProducts;
    }

    public List<Product> searchByPriceRange(double minPrice, double maxPrice) {
        List<Product> filterProducts;
        HttpURLConnection connection = null;
        try {
            connection = ServicesManager.getInstance()
                    .createConnection("/products/filter?searchBy=price&minPrice=" + minPrice + "&maxPrice=" + maxPrice,
                                      "GET");
            connection.connect();
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                ObjectMapper mapper = new ObjectMapper();
                filterProducts = mapper.readValue(reader, mapper.getTypeFactory()
                        .constructCollectionType(List.class, Product.class));
            } else {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect(); // Disconnect and close the connection
        }
        return filterProducts;
    }


}