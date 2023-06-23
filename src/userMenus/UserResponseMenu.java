package userMenus;

import models.Product;
import services.ServicesManager;

import java.util.List;
import java.util.Scanner;


public class UserResponseMenu {
    private String role = null;

    public void runMenu() {
        System.out.println("Welcome to Shopping System!");
        System.out.println("In order to continue, please choose your way:");
        while (true) {
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");

            Scanner reader = new Scanner(System.in);
            int chosen = reader.nextInt();

            if (chosen == 1) {
                String role = register();
                if (isUserTypeValid(role)) {
                    runMenuByUserType(role);
                }
            } else if (chosen == 2) {
                String role = login();
                if (isUserTypeValid(role)) {
                    runMenuByUserType(role);
                }
            } else if (chosen == 3) {
                System.out.println("Bye Bye!");
                break;
            } else {
                System.out.println("Invalid input, try again.");
            }
        }
    }

    private String login() {
        System.out.println("Enter your email:");
        Scanner reader = new Scanner(System.in);
        String email = reader.nextLine();
        System.out.println("Enter your password:");
        String password = reader.nextLine();
        try {
            role = ServicesManager.getInstance().getUserService().login(email, password);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return role;
    }

    private String register() {
        System.out.println("Enter your details:");
        System.out.println("Enter your name:");
        Scanner reader = new Scanner(System.in);
        String name = reader.nextLine();
        System.out.println("Enter your email:");
        String email = reader.nextLine();
        System.out.println("Enter your password:");
        String password = reader.nextLine();
        try {
            role = ServicesManager.getInstance().getUserService().register(name, email, password);
            System.out.println("You have successfully registered!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return role;
    }

    public void searchByPriceRange() {
        System.out.println("Please enter the min price:");
        Scanner reader = new Scanner(System.in);
        double minPrice = reader.nextDouble();
        System.out.println("Please enter the max price:");
        double maxPrice = reader.nextDouble();
        try {
            List<Product> products = ServicesManager.getInstance().getProductService().searchByPriceRange(minPrice, maxPrice);
            if (products.size() == 0) {
                System.out.println("No products found.");
            } else {
                showProductsList(products);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void searchByName() {
        System.out.println("Please enter the keyword to search:");
        Scanner reader = new Scanner(System.in);
        String keyword = reader.nextLine();
        try {
            List<Product> products = ServicesManager.getInstance().getProductService().searchByName(keyword);
            if (products.size() == 0) {
                System.out.println("No products found.");
            } else {
                showProductsList(products);
            }
            showProductsList(products);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    void showProductsList(List<Product> products) {
        System.out.println("");
        System.out.println("Products list:");
        for (Product product : products) {
            System.out.println(product);
        }
        System.out.println("");
    }

    private void runMenuByUserType(String userType) {
        if (userType.equals("Admin")) {
            AdminMenu adminMenu = new AdminMenu();
            adminMenu.runMenu();
        } else if (userType.equals("Customer")) {
            CostumerMenu costumerMenu = new CostumerMenu();
            costumerMenu.runMenu();
        }
    }

    private boolean isUserTypeValid(String userType) {
        return userType.equals("Admin") || userType.equals("Customer");
    }
}