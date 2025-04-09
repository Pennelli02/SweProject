import BusinessLogic.AdminController;
import BusinessLogic.UserController;
import DomainModel.Accommodation;
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
    //TODO gestire l'eventualità che l'utente inserisca l'email giusta ma la password sbagliata 2 opzioni fargliela rimettere o richiedere la password.
    public static void logInMenu() throws SQLException, ClassNotFoundException {
        Scanner in = new Scanner(System.in);
        UserController uc = new UserController();
        AdminController ac = new AdminController();
        int choice;
        do{
            System.out.println("MENU LOGIN APARTMENT: "
                            + "\n1. LOGIN USER"
                            + "\n2. LOGIN ADMIN"
                            + "\n3. SIGN IN"
                            + "\n4. EXIT");

            choice = in.nextInt();

            switch(choice) {
                case 1:{

                    Scanner in1 = new Scanner(System.in);

                    System.out.println("Enter your email: ");
                    String email = in1.nextLine();
                    System.out.println("Enter your password: ");
                    String password = in1.nextLine();

                    RegisterUser registerUser = uc.login(email,password);
                    //TODO gestione caso in cui ci sono degli errori
                    if(registerUser != null) {
                        userMenu(registerUser);
                    }
                    break;
                }
                case 2:{
                    Scanner in2 = new Scanner(System.in);

                    System.out.println("Enter your password: ");
                    String passwordAdmin = in2.nextLine();

                    if(ac.loginAdmin(passwordAdmin)){
                        adminMenu();
                    }
                    break;
                }
                case 3:{
                    RegisterUser registerUser = registerModule(uc);

                    if(registerUser != null){
                        userMenu(registerUser);
                    }
                    break;
                }
                case 4:{
                    System.exit(0);
                    break;
                }
                default: {
                    System.out.println("Please enter a valid choice");
                }
            }

        }while (true);
    }

    public static void userMenu(RegisterUser registerUser) throws SQLException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Accommodation> accommodations = new ArrayList<Accommodation>();
        int choice;
        do{
            System.out.println("MENU USER: " +
                                "\n1. Manage Profile" +
                                "\n2. Research: do an apartment search " +
                                "\n3. Manage operations on the searched accommodations."+
                                "\n4. Log out ");

            choice = scanner.nextInt();

            switch(choice) {
                case 1:{
                    profileMenu(registerUser);
                }
                case 2:{
                    rearchAccommodation();
                }
                case 3:{
                    //todo
                }
                default: {
                    System.out.println("Please enter a valid choice");
                }
            }
        }while(choice != 4);
    }

    private static void rearchAccommodation() {
        //todo
    }

    private static void profileMenu(RegisterUser registerUser) {
        Scanner scanner = new Scanner(System.in);

        int choice;

        do{
            System.out.println("MENU PROFILE USER: " +
                    "\n1. SEE PERSONAL INFORMATION" +
                    "\n2. SEE ALL FAVOURITE LOCATION" +
                    "\n3. SEE ALL BOOKINGS" +
                    "\n4. CHANGE PERSONAL INFORMATION"+
                    "\n5. DELETE A REVIEW" +
                    "\n6. DELETE FAVOURITE LOCATION" +
                    "\n7. EXIT");

            choice = scanner.nextInt();

            switch(choice) {
                case 1:{
                    registerUser.showMyPersonalInfo();
                    break;
                }
                case 2:{
                    registerUser.showMyPreferences();
                    break;
                }
                case 3:{
                    registerUser.showMyBookings();
                    break;
                }
                case 4:{
                    changePersonalInformation();
                    break;
                }
                case 5:{
                    //todo
                    break;
                }
                case 6:{
                    //todo
                    break;
                }
                default: {
                    System.out.println("Please enter a valid choice");
                }
            }
        }while (choice!=7);
    }

    private static void changePersonalInformation() {
        //TODO
    }

    public static void adminMenu() throws SQLException, ClassNotFoundException {
        //todo
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