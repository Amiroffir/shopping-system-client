package userTypes;

import java.util.Scanner;

public class AdminMenu {
    public void runMenu() {
        showMenu();
    }


    public void showMenu(){
        boolean running = true;
        while (running){
        System.out.println("Press number as you like: ");
        System.out.println("1. View all users");
        System.out.println("2. View all products");
        System.out.println("3. Logout");

        // Reading data using readLine
        Scanner reader = new Scanner(System.in);
        int choice = reader.nextInt();

            switch (choice) {
                case 1:
                    viewUsers();
                    break;
                case 2:
                    showProducts();
                    break;
                case 3:
                    System.out.println("Logout");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice");
                    break;
            }
        }
    }

    private void showProducts(){

    }


    private void viewUsers() {

    }
}