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
                    if(registerUser != null) {
                        userMenu(registerUser);
                    }else{
                        System.out.println("Invalid email or password, try again");
                    }
                    break;
                }
                case 2:{
                    Scanner in2 = new Scanner(System.in);

                    System.out.println("Enter your password: ");
                    String passwordAdmin = in2.nextLine();

                    if(ac.loginAdmin(passwordAdmin)){
                        adminMenu();
                    }else{
                        System.out.println("Invalid password, try again");
                    }
                    break;
                }
                case 3:{
                    RegisterUser registerUser = registerModule(uc);

                    if(registerUser != null){
                        userMenu(registerUser);
                    }else{
                        System.out.println("Something went wrong, try again");
                    }
                    break;
                }
                case 4:{
                    System.exit(0);
                    break;
                }
                default: {
                    System.out.println("Please enter a valid choice");
                    break;
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
                    reSearchAccommodation();
                }
                case 3:{
                    //todo
                }
                default: {
                    System.out.println("Please enter a valid choice");
                    break;
                }
            }
        }while(choice != 4);
    }

    private static void reSearchAccommodation() {
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
                    "\n5. DELETE A REVIEW" + //Fixme io metterei vedi le mie recensioni e da lì decidi se eliminare o no
                    "\n6. DELETE FAVOURITE LOCATION" + //fixme stessa cosa delle recensioni
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
                    break;
                }
            }
        }while (choice!=7);
    }

    private static void changePersonalInformation() {
        //TODO Pensare se è meglio fargli fare gli aggiornamenti dei dati uno alla volta o tutti insieme
        Scanner scanner = new Scanner(System.in);
        int choice;

        do{
           System.out.println("What information do you want to change: "
           + "\n1. Name"
           + "\n2. Surname"
           + "\n3. Email"
           + "\n4. Password"
           + "\n5. Username"
           + "\n6. Favourite Location"
           + "\n7. Exit");

           choice = scanner.nextInt();

           switch(choice) {
               case 1:{
                   System.out.println("Enter your new Name: ");
                   String name = scanner.nextLine();
                   break;
               }
               case 2:{
                   System.out.println("Enter your new Surname: ");
                   String surname = scanner.nextLine();
                   break;
               }
               case 3:{
                   System.out.println("Enter your new Email: ");
                   String email = scanner.nextLine();
                   break;
               }
               case 4:{
                   System.out.println("Enter your new Password: ");
                   String password = scanner.nextLine();
                   break;
               }
               case 5:{
                   System.out.println("Enter your new UserName: ");
                   String username = scanner.nextLine();
                   break;
               }
               case 6:{
                   Location nfl = Location.Nothing;
                   int choice2;
                   do{
                       System.out.println("Enter your new favourite location: " +
                               "\n1. Sea"+
                               "\n2. Mountain"+
                               "\n3. ArtCity"
                               + "\n4. Nothing");

                       choice2 = scanner.nextInt();

                       switch(choice2) {
                           case 1: {
                               nfl = Location.Sea;
                               break;
                           }
                           case 2: {
                               nfl = Location.Mountain;
                               break;
                           }
                           case 3: {
                               nfl = Location.ArtCity;
                               break;
                           }
                           default: {
                               System.out.println("Please enter a valid choice");
                               break;
                           }
                       }
                   }while (choice2!=4);
                   break;
               }
               default: {
                   System.out.println("Please enter a valid choice");
                   break;
               }
           }
        }while(choice!=7);
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
        int choice;
        Location favouriteLocations = Location.Nothing;
        do {
            System.out.println("Enter your favourite location: " +
                    "\n1. Sea" +
                    "\n2. Mountain" +
                    "\n3. ArtCity"
                    + "\n4. Nothing");
            choice = in.nextInt();
            switch (choice) {
                case 1: {
                    favouriteLocations = Location.Sea;
                    break;
                }
                case 2: {
                    favouriteLocations = Location.Mountain;
                    break;
                }
                case 3: {
                    favouriteLocations = Location.ArtCity;
                    break;
                }
                default: {
                    System.out.println("Please enter a valid choice");
                    break;
                }
            }
        } while (choice != 4);
        return uc.register(stringAttributes.get(0), stringAttributes.get(1),
                stringAttributes.get(2), stringAttributes.get(3), stringAttributes.get(4), favouriteLocations);
    }
}