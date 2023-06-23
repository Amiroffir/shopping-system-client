package userMenus;

import models.Order;
import models.OrderItem;
import models.Product;
import services.ServicesManager;

import java.util.List;
import java.util.Scanner;

public class CostumerMenu extends UserResponseMenu {
    public void runMenu() {
        boolean running = true;
        while (running) {
            System.out.println("User Menu:");
            System.out.println("1. Explore products");
            System.out.println("2. View Cart");
            System.out.println("3. Checkout");
            System.out.println("4. Logout");

            Scanner reader = new Scanner(System.in);
            int choice = reader.nextInt();

            switch (choice) {
                case 1:
                    exploreProducts();
                    break;
                case 2:
                    viewCart();
                    break;
                case 3:
                    checkout();
                    break;
                case 4:
                    System.out.println("Go back");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice, please try again");
                    break;
            }
        }
    }

    private void exploreProducts() {
        ServicesManager.getInstance().getProductService().getAllProducts();
        showProductsList(ServicesManager.getInstance().getProductsList());
        boolean running = true;
        while (running) {
            System.out.println("What would you like to do?");
            System.out.println("1. Search by name");
            System.out.println("2. Search by price range");
            System.out.println("3. Add to cart");
            System.out.println("4. Go back");

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
    private void viewCart() {
        try {
            List<OrderItem> cart = ServicesManager.getInstance().getOrderItemService().viewCart();
            System.out.println("Your cart:");
            int i = 1;
            for (OrderItem orderItem : cart) {
                System.out.print(i++ + ". ");
                System.out.println(orderItem);
            }
            System.out.println("");
            editCart();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void editCart() {
        boolean running = true;
        while (running) {
            System.out.println(" Please select what you want to do:");
            System.out.println("1. Update product quantity");
            System.out.println("2. Remove product from cart");
            System.out.println("3. Go back");
            Scanner reader = new Scanner(System.in);
            int choice = reader.nextInt();

            switch (choice) {
                case 1:
                    updateItemQuantity();
                    break;
                case 2:
                    removeItemFromCart();
                    break;
                case 3:
                    System.out.println("Go back");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice, please try again");
                    break;
            }
        }
    }

    private void removeItemFromCart() {
        try {
            System.out.println("Please enter the cart item to remove:");
            Scanner reader = new Scanner(System.in);
            int cartItem = reader.nextInt();
            ServicesManager.getInstance().getOrderItemService().removeItemFromCart(cartItem);
            System.out.println("Item removed successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateItemQuantity() {
        try {
            System.out.println("Please enter the product number to update:");
            Scanner reader = new Scanner(System.in);
            int productNumber = reader.nextInt();
            System.out.println("Please enter the new quantity:");
            int quantity = reader.nextInt();
            ServicesManager.getInstance().getOrderItemService().updateItemQuantity(productNumber, quantity);
            System.out.println("Cart updated successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private void checkout() {
        try {
            Order completedOrder = ServicesManager.getInstance().getOrderService().completeOrder();
            if (completedOrder != null) {
                System.out.println("Order completed successfully!");
                System.out.println("Your order number is: " + completedOrder.getOrderId());
            } else {
                System.out.println("Order failed");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}