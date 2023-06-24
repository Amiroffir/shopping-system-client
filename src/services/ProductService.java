package services;

import models.Product;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ProductService {
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public List<Product> getAllProducts() {
        ServicesManager.getInstance().getProductsList().clear(); // Clear the local products list
        List<Product> products;
        try {
            // Create a request to get all products
            HttpRequest request = ServicesManager.getInstance().getHttpService().createRequest("/products/get", "GET", false);
            // Send the request and get the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // Parse the response to a list of products
            products = ServicesManager.getInstance().getHttpService()
                    .parseResponseToList(response, Product.class, HttpURLConnection.HTTP_OK);
            // Set the local products list
            ServicesManager.getInstance().setProductsList(products);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    public List<Product> searchByName(String name) {
        List<Product> filterProducts;
        try {
            HttpRequest request = ServicesManager.getInstance().getHttpService()
                    .createRequest("/products/filter?searchBy=name&name=" + name, "GET", false);

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            filterProducts = ServicesManager.getInstance().getHttpService()
                    .parseResponseToList(response, Product.class, HttpURLConnection.HTTP_OK);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return filterProducts;
    }


    public List<Product> searchByPriceRange(double minPrice, double maxPrice) {
        List<Product> filterProducts;
        try {
            HttpRequest request = ServicesManager.getInstance().getHttpService()
                    .createRequest("/products/filter?searchBy=price&minPrice=" + minPrice + "&maxPrice=" + maxPrice,
                                   "GET", false);

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            filterProducts = ServicesManager.getInstance().getHttpService()
                    .parseResponseToList(response, Product.class, HttpURLConnection.HTTP_OK);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return filterProducts;
    }


    public Product addNewProduct(String name, String description, double price, int quantity) {
        Product newProduct = new Product(name, description, price, quantity);
        Product addedProduct;
        try {
            HttpRequest request =
                    ServicesManager.getInstance().getHttpService().createRequest("/products/add", "POST", false);
            // Set the request body to the new product and set the content type to JSON
            request = ServicesManager.getInstance().getHttpService().setRequestToOutput(request, newProduct, "POST");

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            addedProduct = ServicesManager.getInstance().getHttpService()
                    .parseResponse(response, Product.class, HttpURLConnection.HTTP_CREATED);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return addedProduct;
    }

    public Product updateProductDetails(int productId, String name, String description, double price, int quantity) {
        Product updatedProduct;
        try {
            HttpRequest request = ServicesManager.getInstance().getHttpService()
                    .createRequest("/products/update", "PUT", false);
           request = ServicesManager.getInstance().getHttpService()
                    .setRequestToOutput(request, new Product(productId, name, description, price, quantity), "PUT");

           HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

           updatedProduct = ServicesManager.getInstance().getHttpService()
                    .parseResponse(response, Product.class, HttpURLConnection.HTTP_OK);

           updateProductInLocalList(updatedProduct);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return updatedProduct;
    }

    public void deleteProduct(int productId) {
        try {
            HttpRequest request = ServicesManager.getInstance().getHttpService()
                    .createRequest("/products/delete/" + productId, "DELETE", false);

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                // Remove the product from the local list
                ServicesManager.getInstance().getProductsList()
                        .removeIf(product -> product.getProductId() == productId);
            } else {
                throw new RuntimeException("Failed to delete product with id: " + productId);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
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