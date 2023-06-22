import userTypes.AdminMenu;
import userTypes.CostumerMenu;

import java.util.Scanner;


public class ClientOperations {


    public static void welcome() {
        System.out.println("Welcome to Shopping System!");
        System.out.println("In order to continue, please choose your way:");
        while (true) {
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");

            // Reading data using readLine
            Scanner reader = new Scanner(System.in);
            int chosen = reader.nextInt();

            if (chosen == 1) {
                String role = ClientOperations.register();
                runMenuByUserType(role);
            } else if (chosen == 2) {
                String role = ClientOperations.login();
                runMenuByUserType(role);
            } else if (chosen == 3) {
                System.out.println("Bye Bye!");
                break;
            } else {
                ClientOperations.invalidInput();
            }
        }
    }


    public static void invalidInput() {
        System.out.println("Invalid input, please try again");
    }

    public static String login() {
        return "Costumer";
    }

    public static String register() {
        return null;
    }

    private static void runMenuByUserType(String userType) {
        if (userType.equals("Admin")) {
            AdminMenu adminMenu = new AdminMenu();
            adminMenu.runMenu();
        } else if (userType.equals("Costumer")) {
            CostumerMenu costumerMenu = new CostumerMenu();
            costumerMenu.runMenu();
        }
    }

}