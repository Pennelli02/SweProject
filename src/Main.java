import BusinessLogic.AdminController;
import BusinessLogic.UserController;
import DAO.UserDAO;
import DomainModel.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
                    rearchAccommodation();
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

    private static void rearchAccommodation() {
        Object [] filter = setFilterArray();
        Scanner scanner = new Scanner(System.in);
        boolean dativalidi = false;
        int choice;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //todo ma all categories e cateogy non si potrebbe unire in uno solo lasciando solo category e vedere quali sono importanti avere sicuramente
        do{
            do {
                System.out.println("MENU FILTER FOR THE RESEARCH ACCOMMODATION: " +
                        "\nPLEASE, ENTER THE FILTERS FOR THE RESEARCH ACCOMMODATION"
                        + "\n1*. Place"
                        + "\n2*. dateOfCheckIn"
                        + "\n3*. dateOfCheckOut"
                        + "\n4*. howMuchRooms"
                        + "\n5*. howMuchPeople"
                        + "\n6. category"
                        + "\n7. allCategories"
                        + "\n8. maxPrice"
                        + "\n9. minAccommodationRating"
                        + "\n10. specificAccommodationRating"
                        + "\n11. isRefundable"
                        + "\n12. haveFreeWifi"
                        + "\n13. canISmoke"
                        + "\n14. haveParking"
                        + "\n15. haveCoffeeMachine"
                        + "\n16. haveRoomService"
                        + "\n17. haveCleaningService"
                        + "\n18. haveSpa"
                        + "\n19. goodForKids"
                        + "\n20. canHaveAnimal"
                        + "\n21. Stop insert filter"
                        //+ "\nThose with * are mandatory to enter to carry out the search."
                        + "\nEnter your choice: ");

                choice = scanner.nextInt();

                switch (choice) {
                    case 1:{
                        System.out.println("Enter the place where you want to go: ");
                        filter[0] = scanner.nextLine().split(" ");
                        break;
                    }
                    case 2:{
                        System.out.println("Enter a check-in date in format 'yyyy-MM-dd': ");
                        String input = scanner.nextLine();
                        try {
                            filter[1] = LocalDateTime.parse(input, formatter);
                        } catch (Exception e) {
                            System.out.println("Error: the format is not correct. Try again with the format 'yyyy-MM-dd'.");
                        }
                        break;
                    }
                    case 3:{
                        System.out.println("Enter a check-out date in format 'yyyy-MM-dd': ");
                        String input = scanner.nextLine();
                        try {
                            filter[2] = LocalDateTime.parse(input, formatter);
                        } catch (Exception e) {
                            System.out.println("Error: the format is not correct. Try again with the format 'yyyy-MM-dd'.");
                        }
                        break;
                    }
                    case 4:{
                        System.out.println("Enter as many rooms as you need");
                        filter[3] = scanner.nextInt();
                        break;
                    }
                    case 5:{
                        System.out.println("Enter as many people as you need");
                        filter[4] = scanner.nextInt();
                    }
                    //todo decidere per il category o all category
                    case 6:{
                        break;
                    }
                    case 7:{
                        break;
                    }
                    case 8:{
                        System.out.println("Enter the maximum price you would like to spend: ");
                        filter[7] = scanner.nextInt();
                        break;
                    }
                    case 9:{
                        System.out.println("Please enter the lowest accommodation rating number to search: ");
                        filter[8] = scanner.nextInt();
                        break;
                    }
                    case 10:{
                        System.out.println("Enter a specific rating of the accommodations you want to search for: ");
                        filter[9] = scanner.nextInt();
                        break;
                    }
                    case 11:{
                        filter[10] = true;
                        break;
                    }
                    case 12:{
                        filter[11] = true;
                        break;
                    }
                    case 13:{
                        filter[12]= true;
                        break;
                    }
                    case 14:{
                        filter[13]= true;
                        break;
                    }
                    case 15:{
                        filter[14]= true;
                        break;
                    }
                    case 16:{
                        filter[15]= true;
                        break;
                    }
                    case 17:{
                        filter[16]= true;
                        break;
                    }
                    case 18:{
                        filter[17] = true;
                        break;
                    }
                    case 19:{
                        filter[18]= true;
                        break;
                    }
                    case 20:{
                        filter[19] = true;
                        break;
                    }
                    default: {
                        System.out.println("Please enter a valid choice");
                    }
                }
            }while (choice!=21);
            //todo controllo se i dati obbligatori sono stati inseriti e magari se i dati sono sensati(ovviamente se al prof interessa)
        }while (!dativalidi);
        //todo fare il builder dei parametri
    }

    private static Object[] setFilterArray() {
        //todo gestione del accomodationrating se nessuno vuole inserirlo
        Object[] array = new Object[20];
        for (int i = 0; i < array.length; i++) {
            if (i<3){
                array[i] = null;
            }else if(i == 3 || i==4 || i == 7){
                array[i] = 0;
            }else if (i == 8 || i == 9){

            }else{
                array[i] = false;
            }
        }
        return array;
    }

    private static void profileMenu(RegisterUser registerUser) throws SQLException, ClassNotFoundException {
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
                    changePersonalInformation(registerUser);
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

    private static void changePersonalInformation(RegisterUser registerUser) throws SQLException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        int choice;
        UserDAO userDAO = new UserDAO();

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
                   registerUser.setName(name);
                   userDAO.updateName(registerUser.getId(),name);
                   break;
               }
               case 2:{
                   System.out.println("Enter your new Surname: ");
                   String surname = scanner.nextLine();
                   registerUser.setSurname(surname);
                   userDAO.updateSurname(registerUser.getId(),surname);
                   break;
               }
               case 3:{
                   System.out.println("Enter your new Email: ");
                   String email = scanner.nextLine();
                   registerUser.setEmail(email);
                   userDAO.updateEmail(registerUser.getId(),email);
                   break;
               }
               case 4:{
                   System.out.println("Enter your new Password: ");
                   String password = scanner.nextLine();
                   registerUser.setPassword(password);
                   userDAO.updatePassword(registerUser.getId(),password);
                   break;
               }
               case 5:{
                   System.out.println("Enter your new UserName: ");
                   String username = scanner.nextLine();
                   registerUser.setUsername(username);
                   userDAO.updateUsername(registerUser.getId(),username);
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
                   registerUser.setFavouriteLocations(nfl);
                   userDAO.updateFavouriteLocations(registerUser.getId(),nfl);
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