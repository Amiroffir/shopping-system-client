package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.Product;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;

public class ProductService {

    public List<Product> getAllProducts() {
        ServicesManager.getInstance().getProductsList().clear(); // Clear the local products list
        List<Product> products;
        HttpURLConnection connection = null;
        try {
            connection = ServicesManager.getInstance().createConnection("/products/get", "GET", false);

            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                ObjectMapper mapper = new ObjectMapper();
                products = mapper.readValue(reader, mapper.getTypeFactory()
                        .constructCollectionType(List.class, Product.class));

                ServicesManager.getInstance().setProductsList(products);
            } else {
                throw new RuntimeException(connection.getResponseMessage());
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
                    .createConnection("/products/filter?searchBy=name&name=" + name, "GET", false);

            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                ObjectMapper mapper = new ObjectMapper();
                filterProducts = mapper.readValue(reader, mapper.getTypeFactory()
                        .constructCollectionType(List.class, Product.class));
            } else {
                throw new RuntimeException(connection.getResponseMessage());
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
                                      "GET", false);

            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                ObjectMapper mapper = new ObjectMapper();
                filterProducts = mapper.readValue(reader, mapper.getTypeFactory()
                        .constructCollectionType(List.class, Product.class));
            } else {
                throw new RuntimeException(connection.getResponseMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect(); // Disconnect and close the connection
        }
        return filterProducts;
    }


    public Product addNewProduct(String name, String description, double price, int quantity) {
        Product newProduct = new Product(name, description, price, quantity);
        Product addedProduct;
        HttpURLConnection connection = null;
        try {
            connection = ServicesManager.getInstance().createConnection("/products/add", "POST", true);

            try (OutputStream os = connection.getOutputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(os, newProduct);
            }

            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                ObjectMapper mapper = new ObjectMapper();
                addedProduct = mapper.readValue(reader, Product.class);
            } else {
                throw new RuntimeException(connection.getResponseMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect(); // Disconnect and close the connection
        }
        return addedProduct;
    }

    public Product updateProductDetails(int productId, String name, String description, double price, int quantity) {
        Product updatedProduct;
        HttpURLConnection connection = null;
        try {
            connection = ServicesManager.getInstance().createConnection("/products/update", "PUT", true);

            try (OutputStream os = connection.getOutputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(os, new Product(productId, name, description, price, quantity));
            }

            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                ObjectMapper mapper = new ObjectMapper();
                updatedProduct = mapper.readValue(reader, Product.class);

                // Find the product in the local list and update it
                updateProductInLocalList(updatedProduct);
            } else {
                throw new RuntimeException(connection.getResponseMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect(); // Disconnect and close the connection
        }
        return updatedProduct;
    }

    public void deleteProduct(int productId) {
        HttpURLConnection connection = null;
        try {
            connection =
                    ServicesManager.getInstance().createConnection("/products/delete/" + productId, "DELETE", false);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                // Remove the product from the local list
                ServicesManager.getInstance().getProductsList()
                        .removeIf(product -> product.getProductId() == productId);
            } else {
                throw new RuntimeException(connection.getResponseMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect();
        }
    }

    private void updateProductInLocalList(Product updatedProduct) {
        // Find the product in the local list and update it
        ServicesManager.getInstance().getProductsList().stream()
                .filter(product -> product.getProductId() == updatedProduct.getProductId())
                .findFirst().ifPresent(product -> {
                    product.setName(updatedProduct.getName());
                    product.setDescription(updatedProduct.getDescription());
                    product.setPrice(updatedProduct.getPrice());
                    product.setQuantity(updatedProduct.getQuantity());
                });
    }
}