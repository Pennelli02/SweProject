import BusinessLogic.AdminController;
import BusinessLogic.ProfileUserController;
import BusinessLogic.ResearchController;
import BusinessLogic.UserController;
import DomainModel.*;

import java.sql.SQLException;
import java.time.LocalDate;
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
                    break;
                }
                case 2:{
                    accommodations = rearchAccommodation(registerUser);
                    if(accommodations != null) {
                        for (Accommodation accommodation : accommodations) {
                            System.out.println(accommodation.toString());
                        }
                    }else{
                        System.out.println("Something went wrong, try again");
                    }
                    break;
                }
                case 3:{
                    operationSearchedAccommodations(accommodations);
                    break;
                }
                case 4:{
                    System.out.println("successful logout.");
                }
                default: {
                    System.out.println("Please enter a valid choice");
                    break;
                }
            }
        }while(choice != 4);
    }

    private static void operationSearchedAccommodations(ArrayList<Accommodation> accommodations) {

    }

    private static ArrayList<Accommodation> rearchAccommodation(RegisterUser registerUser){
        Scanner scanner = new Scanner(System.in);
        Object [] filter = setFilterArray();
        boolean dativalidi = false;
        int choice;
        ResearchController rc = new ResearchController(registerUser);
        do{
            do{
                System.out.println("MENU FILTER RESEARCH ACCOMODATION:\n"
                        + "PLEASE ENTER YOUR FILTER TO RESEARCH ACCOMODATIONS:\n"
                        + "1. Place\n"
                        + "2. Date of Check-In\n"
                        + "3. Date of Check-Out\n"
                        + "4. How Many Rooms\n"
                        + "5. How Many People\n"
                        + "6. Category\n"
                        + "7. All Categories\n"
                        + "8. Max Price\n"
                        + "9. Min Accommodation Rating\n"
                        + "10. Specific Accommodation Rating\n"
                        + "11. Is Refundable\n"
                        + "12. Have Free Wifi\n"
                        + "13. Can I Smoke\n"
                        + "14. Have Parking\n"
                        + "15. Have Coffee Machine\n"
                        + "16. Have Room Service\n"
                        + "17. Have Cleaning Service\n"
                        + "18. Have Spa\n"
                        + "19. Good for Kids\n"
                        + "20. Can Have Animal\n"
                        + "21. Exit\n"
                        + "Please enter your choice: ");
                choice = scanner.nextInt();
                switch(choice) {
                    case 1:{
                        System.out.println("Enter the place where you want to search for accommodations: ");
                        Scanner sc2 = new Scanner(System.in);
                        filter[0] = sc2.nextLine();
                        break;
                    }
                    case 2:{
                        Scanner sc2 = new Scanner(System.in);
                        System.out.println("Enter the check-in in the format 'yyyy-MM-dd':");
                        System.out.print("Inserisci l'anno (es. 2025): ");
                        int anno = sc2.nextInt();
                        System.out.print("Inserisci il mese (1-12): ");
                        int mese = sc2.nextInt();
                        System.out.print("Inserisci il giorno (1-31): ");
                        int giorno = sc2.nextInt();

                        // Creazione della data locale con ora impostata alle 00:00:00
                        try {
                            LocalDateTime dateTime = LocalDateTime.of(anno, mese, giorno, 0, 0, 0);
                            filter[1] = dateTime;
                        } catch (Exception e) {
                            System.out.println("Error: date not valid");
                        }
                        break;
                    }
                    case 3:{
                        Scanner sc2 = new Scanner(System.in);
                        System.out.println("Enter the check-out in the format 'yyyy-MM-dd':");
                        System.out.print("Inserisci l'anno (es. 2025): ");
                        int anno = sc2.nextInt();
                        System.out.print("Inserisci il mese (1-12): ");
                        int mese = sc2.nextInt();
                        System.out.print("Inserisci il giorno (1-31): ");
                        int giorno = sc2.nextInt();

                        // Creazione della data locale con ora impostata alle 00:00:00
                        try {
                            LocalDateTime dateTime = LocalDateTime.of(anno, mese, giorno, 0, 0, 0);
                            filter[2] = dateTime;
                        } catch (Exception e) {
                            System.out.println("Error: date not valid");
                        }
                        break;
                    }
                    case 4:{
                        System.out.println("Enter the how Many Rooms: ");
                        Scanner sc2 = new Scanner(System.in);
                        filter[3] = sc2.nextInt();
                        break;
                    }
                    case 5:{
                        System.out.println("Enter the how Many People: ");
                        Scanner sc2 = new Scanner(System.in);
                        filter[4] = sc2.nextInt();
                        break;
                    }
                    case 6:{
                        boolean app = (boolean) filter[6];
                        if(!app) {
                            Scanner sc2 = new Scanner(System.in);
                            int choice2;
                            do{
                                System.out.println("Please enter a category of accommodation \n"
                                        + "1. Hotel\n"
                                        + "2. B&B\n"
                                        + "3. Apartment");
                                choice2 = sc2.nextInt();
                                switch(choice2) {
                                    case 1:{
                                        filter[5] = AccommodationType.Hotel;
                                        break;
                                    }
                                    case 2:{
                                        filter[5] = AccommodationType.BeB;
                                        break;
                                    }
                                    case 3:{
                                        filter[5] = AccommodationType.Apartment;
                                        break;
                                    }
                                    default:{
                                        System.out.println("Please enter a valid choice");
                                    }
                                }
                            }while (choice2 < 1 || choice2 > 4);
                        }else{
                            System.out.println("All categories are already included");
                        }
                        break;
                    }
                    case 7:{
                        filter[6] = true;
                        System.out.println("All categories are set to search");
                        break;
                    }
                    case 8:{
                        System.out.println("Enter the maximum price for the accommodation search: ");
                        Scanner sc2 = new Scanner(System.in);
                        filter[7] = sc2.nextFloat();
                        break;
                    }
                    case 9:{
                        if(filter[9] == null){
                            Scanner sc2 = new Scanner(System.in);
                            int choice2;
                            do{
                                System.out.println("Please enter the minimum accommodation rating\n"
                                                + "1. OneStar\n"
                                                + "2. TwoStar\n"
                                                + "3. ThreeStar\n"
                                                + "4. FourStar\n"
                                                + "5. FiveStar");

                                choice2 = sc2.nextInt();
                                switch(choice2) {
                                    case 1:{
                                        filter[8] = AccommodationRating.OneStar;
                                        break;
                                    }
                                    case 2:{
                                        filter[8] = AccommodationRating.TwoStar;
                                        break;
                                    }
                                    case 3:{
                                        filter[8] = AccommodationRating.ThreeStar;
                                        break;
                                    }
                                    case 4:{
                                        filter[8] = AccommodationRating.FourStar;
                                        break;
                                    }
                                    case 5:{
                                        filter[8] = AccommodationRating.FiveStar;
                                        break;
                                    }
                                    default:{
                                        System.out.println("Please enter a valid choice");
                                    }
                                }
                            }while (choice2 < 1 || choice2 > 6);
                        }else{
                            System.out.println("You have already set up your specific rating");
                        }
                        break;
                    }
                    case 10:{
                        if(filter[8] == null){
                            Scanner sc2 = new Scanner(System.in);
                            int choice2;
                            do{
                                System.out.println("Please enter the specific accommodation rating\n"
                                        + "1. OneStar\n"
                                        + "2. TwoStar\n"
                                        + "3. ThreeStar\n"
                                        + "4. FourStar\n"
                                        + "5. FiveStar");

                                choice2 = sc2.nextInt();
                                switch(choice2) {
                                    case 1:{
                                        filter[9] = AccommodationRating.OneStar;
                                        break;
                                    }
                                    case 2:{
                                        filter[9] = AccommodationRating.TwoStar;
                                        break;
                                    }
                                    case 3:{
                                        filter[9] = AccommodationRating.ThreeStar;
                                        break;
                                    }
                                    case 4:{
                                        filter[9] = AccommodationRating.FourStar;
                                        break;
                                    }
                                    case 5:{
                                        filter[9] = AccommodationRating.FiveStar;
                                        break;
                                    }
                                    default:{
                                        System.out.println("Please enter a valid choice");
                                    }
                                }
                            }while (choice2 < 1 || choice2 > 6);
                        }else{
                            System.out.println("You have already set the minimum rating");
                        }
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
                        filter[12] = true;
                        break;
                    }
                    case 14:{
                        filter[13] = true;
                        break;
                    }
                    case 15:{
                        filter[14] = true;
                        break;
                    }
                    case 16:{
                        filter[15] = true;
                        break;
                    }
                    case 17:{
                        filter[16] = true;
                        break;
                    }
                    case 18:{
                        filter[17] = true;
                        break;
                    }
                    case 19:{
                        filter[18] = true;
                        break;
                    }
                    case 20:{
                        filter[19] = true;
                        break;
                    }
                    case 21:{
                        break;
                    }
                    default: {
                        System.out.println("Please enter a valid choice");
                    }
                }
            }while (choice!=21);
            if(filter[0] != null){
                dativalidi = true;
            }else{
                System.out.println("Please, its important a place where you want to search for accommodations");
            }
        }while(!dativalidi);
        try {
            SearchParameters sp = SearchParametersBuilder.newBuilder((String) filter[0]).setDateOfCheckIn((LocalDateTime) filter[1])
                    .setDateOfCheckOut((LocalDateTime) filter[2]).setHowMuchRooms((Integer) filter[3]).setHowMuchPeople((Integer) filter[4])
                    .setCategory((AccommodationType) filter[5]).setAllCategories((boolean) filter[6]).setMaxPrice(((float) filter[7]))
                    .setMinRatingStars((AccommodationRating) filter[8]).setSpecificRatingStars((AccommodationRating) filter[9])
                    .setRefundable((boolean) filter[10]).setHaveFreeWifi((boolean) filter[11]).setCanISmoke((boolean) filter[12])
                    .setHaveParking((boolean) filter[13]).setHaveCoffeeMachine((boolean) filter[14]).setHaveRoomService((boolean) filter[15])
                    .setHaveCleaningService((boolean) filter[16]).setHaveSpa((boolean) filter[17]).setGoodForKids((boolean) filter[18])
                    .setCanHaveAnimal((boolean) filter[19]).build();
            return rc.doResearch(sp);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    private static Object[] setFilterArray() {
        Object[] array = new Object[20];
        for (int i = 0; i < array.length; i++) {
            if(i < 3){
                array[i] = null;
            }else if(i == 3 || i==4) {
                array[i] = null;
            }else if(i == 7){
                array[i]=0.0f;
            }else if(i == 5 || i==8 || i==9){
                array[i] = null;
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
                case 7:{
                    System.out.println("successful exit.");
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
        ProfileUserController profileUserController = new ProfileUserController(registerUser);
        int choice;
        String name = null;
        String surname = null;
        String email = null;
        String username = null;
        String password = null;
        Location nfl = null;
        Scanner scanner = new Scanner(System.in);

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
                   Scanner sc = new Scanner(System.in);
                   System.out.println("Enter your new Name: ");
                   name = sc.nextLine();
                   break;
               }
               case 2:{
                   Scanner sc = new Scanner(System.in);
                   System.out.println("Enter your new Surname: ");
                   surname = sc.nextLine();
                   break;
               }
               case 3:{
                   Scanner sc = new Scanner(System.in);
                   System.out.println("Enter your new Email: ");
                   email = sc.nextLine();
                   break;
               }
               case 4:{
                   Scanner sc = new Scanner(System.in);
                   System.out.println("Enter your new Password: ");
                   password = sc.nextLine();
                   break;
               }
               case 5:{
                   Scanner sc = new Scanner(System.in);
                   System.out.println("Enter your new UserName: ");
                   username = sc.nextLine();
                   break;
               }
               case 6:{
                   Scanner sc = new Scanner(System.in);
                   nfl = Location.Nothing;
                   int choice2;
                   do{
                       System.out.println("Enter your new favourite location: " +
                               "\n1. Sea"+
                               "\n2. Mountain"+
                               "\n3. ArtCity"
                               + "\n4. Nothing");

                       choice2 = sc.nextInt();

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
                           case 4: {
                               break;
                           }
                           default:{
                               System.out.println("Please enter a valid choice");
                               break;
                           }
                       }
                   }while (choice2<1 || choice2>4);
                   break;
               }

               default: {
                   System.out.println("Please enter a valid choice");
                   break;
               }
           }
        }while(choice!=7);
        profileUserController.updateProfile(name,surname,email,password,username,nfl);
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
                case 4:{
                    favouriteLocations = Location.Nothing;
                }
                default: {
                    System.out.println("Please enter a valid choice");
                    break;
                }
            }
        } while (choice < 1 || choice > 4);
        return uc.register(stringAttributes.get(0), stringAttributes.get(1),
                stringAttributes.get(2), stringAttributes.get(3), stringAttributes.get(4), favouriteLocations);
    }
}