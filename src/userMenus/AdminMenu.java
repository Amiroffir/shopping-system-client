package userMenus;

import models.Product;
import models.User;
import services.ServicesManager;

import java.util.List;
import java.util.Scanner;

public class AdminMenu extends UserResponseMenu {
    public void runMenu() {
        boolean running = true;
        while (running) {
            System.out.println("Press number as you like: ");
            System.out.println("1. View all users");
            System.out.println("2. Products management");
            System.out.println("3. Go back");

            Scanner reader = new Scanner(System.in);
            int choice = reader.nextInt();

            switch (choice) {
                case 1:
                    viewUsers();
                    break;
                case 2:
                    manageProducts();
                    break;
                case 3:
                    System.out.println("Go back");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice");
                    break;
            }
        }
    }

    private void manageProducts() {
        ServicesManager.getInstance().getProductService().getAllProducts();
        showProductsList(ServicesManager.getInstance().getProductsList());
        boolean running = true;
        while (running) {
            System.out.println("What would you like to do?");
            System.out.println("1. Search by name");
            System.out.println("2. Search by price range");
            System.out.println("3. Add new product");
            System.out.println("4. Update product details");
            System.out.println("5. Delete product");
            System.out.println("6. Go back");

            Scanner reader = new Scanner(System.in);
            int choice = reader.nextInt();

            switch (choice) {
                case 1:
                    searchByName();
                    break;
                case 2:
                    searchByPriceRange();
                    break;
                case 3:
                    addNewProduct();
                    break;
                case 4:
                    updateProductDetails();
                    break;
                case 5:
                    deleteProduct();
                    break;
                case 6:
                    System.out.println("Logout");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice");
                    break;
            }
        }
    }

    private void deleteProduct() {
        System.out.println("Please enter the product number to delete:");
        Scanner reader = new Scanner(System.in);
        int productId = reader.nextInt();
        try {
            ServicesManager.getInstance().getProductService().deleteProduct(productId);
            System.out.println("Product deleted successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private Product updateProductDetails() {
        System.out.println("Please enter the product number to update:");
        Scanner reader = new Scanner(System.in);
        int productId = reader.nextInt();
        reader.nextLine();
        System.out.println("Please enter the product name:");
        String name = reader.nextLine();
        System.out.println("Please enter the product description:");
        String description = reader.nextLine();
        System.out.println("Please enter the product price:");
        double price = reader.nextDouble();
        System.out.println("Please enter the product quantity:");
        int quantity = reader.nextInt();
        try {
            Product product = ServicesManager.getInstance().getProductService().updateProductDetails(productId, name, description, price, quantity);
            System.out.println("Product updated successfully");
            return product;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private Product addNewProduct() {
        System.out.println("Please enter the product name:");
        Scanner reader = new Scanner(System.in);
        String name = reader.nextLine();
        System.out.println("Please enter the product description:");
        String description = reader.nextLine();
        System.out.println("Please enter the product price:");
        double price = reader.nextDouble();
        System.out.println("Please enter the product quantity:");
        int quantity = reader.nextInt();
        try {
            Product product = ServicesManager.getInstance().getProductService().addNewProduct(name, description, price, quantity);
            System.out.println("Product added successfully");
            return product;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private void viewUsers() {
        try {
            List<User> usersList = ServicesManager.getInstance().getUserService().getAllUsers();
            System.out.println("Users list:");
            for (User user : usersList) {
                System.out.println(user);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}