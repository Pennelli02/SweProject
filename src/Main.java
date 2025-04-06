import BusinessLogic.AdminController;
import BusinessLogic.UserController;
import DomainModel.Location;
import DomainModel.RegisterUser;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        logInMenu();
    }

    public static void logInMenu() throws SQLException, ClassNotFoundException {
        Scanner in = new Scanner(System.in);
        UserController uc = new UserController();
        AdminController ac = new AdminController();
        String choice;
        do{
            System.out.println("MENU LOGIN APARTMENT: "
                            + "\n1. LOGIN USER"
                            + "\n2. LOGIN ADMIN"
                            + "\n3. SIGN IN"
                            + "\n4. EXIT");

            choice = in.nextLine();

            switch(choice) {
                case "1":{

                    Scanner in1 = new Scanner(System.in);

                    System.out.println("Enter your email: ");
                    String email = in1.nextLine();
                    System.out.println("Enter your password: ");
                    String password = in1.nextLine();

                    RegisterUser registerUser = uc.login(email,password);

                    if(registerUser != null) {
                        userMenu();
                    }
                    break;
                }
                case "2":{
                    Scanner in2 = new Scanner(System.in);

                    System.out.println("Enter your password: ");
                    String passwordAdmin = in2.nextLine();

                    if(ac.loginAdmin(passwordAdmin)){
                        adminMenu();
                    }
                    break;
                }
                case "3":{
                    RegisterUser registerUser = registerModule(uc);

                    if(registerUser != null){
                        userMenu();
                    }
                    break;
                }
                case "4":{
                    System.exit(0);
                    break;
                }
                default: {
                    System.out.println("Please enter a valid choice");
                }
            }

        }while (true);
    }

    public static void userMenu() throws SQLException, ClassNotFoundException {

    }

    public static void adminMenu() throws SQLException, ClassNotFoundException {

    }

    public static RegisterUser registerModule(UserController uc) throws SQLException, ClassNotFoundException {
        ArrayList<String> stringAttributes = new ArrayList<String>();
        Scanner in = new Scanner(System.in);
        System.out.println("MENU REGISTRATION APARTMENT: ");
        System.out.println("Enter your email: ");
        stringAttributes.add(in.nextLine());
        System.out.println("Enter your password: ");
        stringAttributes.add(in.nextLine());
        System.out.println("Enter your username: ");
        stringAttributes.add(in.nextLine());
        System.out.println("Enter your name: ");
        stringAttributes.add(in.nextLine());
        System.out.println("Enter your surname: ");
        stringAttributes.add(in.nextLine());
        System.out.println("Enter your location: " +
                            "\n1. Sea"+
                            "\n2. Mountain"+
                            "\n3. ArtCity"
                            + "\n4. Nothing");
        int choice = in.nextInt();
        Location favouriteLocations;
        switch(choice) {
            case 1:{
                favouriteLocations = Location.Sea;
            }
            case 2:{
                favouriteLocations = Location.Mountain;
            }
            case 3:{
                favouriteLocations = Location.ArtCity;
            }
            default:{
                favouriteLocations = Location.Nothing;
            }
        }
        return uc.register(stringAttributes.get(0), stringAttributes.get(1),
                stringAttributes.get(2),stringAttributes.get(3),stringAttributes.get(4),favouriteLocations);
    }
}