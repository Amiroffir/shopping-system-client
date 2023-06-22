package userTypes;

import models.OrderItem;
import models.Product;
import services.ProductService;
import services.ServicesManager;

import java.util.List;
import java.util.Scanner;

public class CostumerMenu {
    private final ProductService productService = new ProductService();


    public void runMenu() {
        showMenu();
    }

    public void showMenu() {
        boolean running = true;
        while (running) {
            System.out.println("User Menu:");
            System.out.println("1. View Products");
            System.out.println("2. View Cart");
            System.out.println("3. Checkout");
            System.out.println("4. Logout");

            // Reading data using readLine
            Scanner reader = new Scanner(System.in);
            int choice = reader.nextInt();

            switch (choice) {
                case 1:
                    viewProducts();
                    break;
                case 2:
                    viewCart();
                    break;
                case 3:
                    System.out.println("Checkout");
                    System.out.println("Call the service to checkout");
                    break;
                case 4:
                    System.out.println("Logout");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice");
                    break;
            }
        }
    }

    public void viewCart() {
        List<OrderItem> cart = ServicesManager.getInstance().getOrderItemService().viewCart();
        System.out.println("Your cart:");
        for (OrderItem orderItem : cart) {
            System.out.println(orderItem);
        }
    }

    public void viewProducts() {
        ServicesManager.getInstance().getProductService().getAllProducts();
        showProductsList(ServicesManager.getInstance().getProductsList());
        boolean running = true;
        while (running) {
            System.out.println("What would you like to do?");
            System.out.println("1. Search by name");
            System.out.println("2. Search by price range");
            System.out.println("3. Add to cart");
            System.out.println("4. Logout");
            // Reading data using readLine
            Scanner reader = new Scanner(System.in);
            int choice = reader.nextInt();

            // Switch statement
            switch (choice) {
                case 1:
                    searchByName();
                    break;
                case 2:
                    searchByPriceRange();
                    break;
                case 3:
                    addToCart();
                    break;
                case 4:
                    System.out.println("Logout");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice");
                    break;
            }
        }
    }

    private void addToCart() {
        System.out.println("Please enter the product number to add to cart:");
        Scanner reader = new Scanner(System.in);
        int productId = reader.nextInt();
        try {
            ServicesManager.getInstance().getOrderItemService().addToCart(productId);
            System.out.println("Product added to cart successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void showProductsList() {
        System.out.println("");
        System.out.println("Products list:");
        System.out.println("Call the service to show products list");
    }

    private void showProductsList(List<Product> products) {
        System.out.println("");
        System.out.println("Products list:");
        for (Product product : products) {
            System.out.println(product);
        }
        System.out.println("");
    }

    private void searchByName() {
        System.out.println("Please enter the keyword to search:");
        Scanner reader = new Scanner(System.in);
        String keyword = reader.nextLine();
        try {
            List<Product> products = productService.searchByName(keyword);
            showProductsList(products);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void searchByPriceRange() {
        System.out.println("Please enter the min price:");
        Scanner reader = new Scanner(System.in);
        double minPrice = reader.nextDouble();
        System.out.println("Please enter the max price:");
        double maxPrice = reader.nextDouble();
        try {
            List<Product> products = productService.searchByPriceRange(minPrice, maxPrice);
            showProductsList(products);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}