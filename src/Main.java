import BusinessLogic.AdminController;
import BusinessLogic.ProfileUserController;
import BusinessLogic.ResearchController;
import BusinessLogic.UserController;
import DomainModel.*;

import javax.management.InvalidAttributeValueException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        logInMenu();
    }
    private static SearchParameters mySearchParameters;

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
                        if (registerUser.getId() == -1) {
                            // Correct email but wrong password
                            System.out.println("The email is correct but the password is wrong.");
                            System.out.println("Choose an option:");
                            System.out.println("1. Try again");
                            System.out.println("2. Retrieve password");

                            int recoveryChoice = in1.nextInt();
                            in1.nextLine(); // consume the newline

                            if (recoveryChoice == 1) {
                                // Let them try again
                                System.out.println("Enter your password again: ");
                                String newPassword = in1.nextLine();
                                registerUser = uc.login(email, newPassword);
                                if (registerUser != null && registerUser.getId() != -1) {
                                    userMenu(registerUser);
                                } else {
                                    System.out.println("Still incorrect password.");
                                }
                            } else if (recoveryChoice == 2) {
                                // Retrieve and display password
                                String retrievedPassword = uc.getForgottenPassword(registerUser.getEmail());
                                if (retrievedPassword != null) {
                                    System.out.println("Your password is: " + retrievedPassword);

                                } else {
                                    System.out.println("Could not retrieve password.");
                                }
                            }
                        } else {
                            userMenu(registerUser);
                        }
                    }
                        break;
                }
                case 2:{
                    Scanner in2 = new Scanner(System.in);

                    System.out.println("Enter your password: ");
                    String passwordAdmin = in2.nextLine();

                    if(ac.loginAdmin(passwordAdmin)){
                        adminMenu(ac);
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
        ArrayList<Accommodation> accommodations;
        ProfileUserController puc= new ProfileUserController(registerUser);
        int choice;
        do{
            System.out.println("MENU USER: " +
                    "\n1. Manage Profile" +
                    "\n2. Research: do an apartment search " +
                    "\n3. Log out ");

            choice = scanner.nextInt();

            switch(choice) {
                case 1:{
                    profileMenu(registerUser,puc);
                    break;
                }
                case 2:{
                    ResearchController rc= new ResearchController(registerUser);
                    accommodations = researchAccommodation(rc);
                    if(accommodations == null) {
                        System.out.println("Something went wrong, try again");

                    }else if (accommodations.isEmpty()){
                        System.out.println("No accommodations found, try again");
                    }else{
                        System.out.println("Accommodations found, you can do some operations");
                        operationSearchedAccommodations(rc, accommodations);
                    }
                    break;
                }
                case 3:{
                    System.out.println("successful logout.");
                    break;
                }
                default: {
                    System.out.println("Please enter a valid choice");
                    break;
                }
            }
        }while(choice != 3);
    }

    private static void operationSearchedAccommodations(ResearchController rc, ArrayList<Accommodation> accommodations) {
        Scanner scanner = new Scanner(System.in);
        int choice;
        do{
            System.out.println("MENU OPERATION ON SEARCHED ACCOMMODATION: " +
                    "\n 1. See all sought-after accommodations" +
                    "\n 2. Add accommodation to your preferred accommodation (the choice of accommodation is based on the list of accommodations searched)" +
                    "\n 3. Enter a review for an accommodation (the choice of accommodation is based on the list of accommodations searched)" +
                    "\n 4. Booking a sought-after accommodation (the choice of accommodation is based on the list of accommodations searched)" +
                    "\n 5. View reviews of a specific accommodation (the choice of accommodation is based on the list of accommodations searched)" +
                    "\n 6. See more information for a specific accommodation" +
                    "\n 7. Exit"
            );

            choice = scanner.nextInt();

            switch(choice) {
                case 1:{
                    System.out.println("----ALL ACCOMMODATIONS---------");
                    for (Accommodation accommodation : accommodations) {
                        System.out.println(accommodation.toString());
                    }
                    break;
                }
                case 2:{
                    try{
                        Scanner s2 = new Scanner(System.in);
                        int choice2;
                        System.out.println("Enter which accommodation you want to bookmark(start to 1): ");
                        choice2 = s2.nextInt();
                        choice2 = choice2 - 1;
                        rc.saveAccommodation(accommodations.get(choice2));
                        System.out.println("Preference successfully entered");
                    }catch (IndexOutOfBoundsException e){
                        System.out.println("Added an index that goes beyond the length of the list of searched accommodations, Try again");
                    }
                    break;
                }
                case 3:{
                    try{
                        Scanner s2 = new Scanner(System.in);
                        Scanner s3 = new Scanner(System.in);
                        int choice2;
                        int choice3;
                        String description;
                        AccommodationRating rate = null;
                        System.out.println("Enter the accommodation where you want to write a review(start to 1): ");
                        choice2 = s2.nextInt();
                        choice2 = choice2 - 1;
                        if(choice2 < 0 || choice2>accommodations.size()){
                            throw new IndexOutOfBoundsException("Added an index that goes beyond the length of the list of searched accommodations, Try again");
                        }
                        System.out.println("Enter the description of the accommodation where you want to write a review: ");
                        description = s3.nextLine();
                        do{
                            System.out.println("Enter the rating for the accommodation: "
                                    + "\n 1. OneStar"
                                    + "\n 2. TwoStar"
                                    + "\n 3. ThreeStar"
                                    + "\n 4. FourStar"
                                    + "\n 5. FiveStar"
                            );
                            choice3 = s2.nextInt();
                            switch(choice3) {
                                case 1:{
                                    rate = AccommodationRating.OneStar;
                                    break;
                                }
                                case 2:{
                                    rate = AccommodationRating.TwoStar;
                                    break;
                                }
                                case 3:{
                                    rate = AccommodationRating.ThreeStar;
                                    break;
                                }
                                case 4:{
                                    rate = AccommodationRating.FourStar;
                                    break;
                                }
                                case 5:{
                                    rate = AccommodationRating.FiveStar;
                                    break;
                                }
                                default: {
                                    System.out.println("Please enter a valid choice");
                                }
                            }
                        }while(choice3 < 1 || choice3 > 5);
                        rc.writeReview(accommodations.get(choice2),description,rate);
                        System.out.println("Review successfully entered");
                    }catch (IndexOutOfBoundsException e){
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case 4:{ //FIXME richiesta LOre scegli tra i due metodi proposti nell'audio
                    try{
                        Scanner s2 = new Scanner(System.in);
                        int choice2;
                        System.out.println("Enter the accommodation that you want to book(start to 1): ");
                        choice2 = s2.nextInt();
                        choice2 = choice2 - 1;
                        LocalDateTime checkin;
                        LocalDateTime checkout;
                        if(mySearchParameters.getDateOfCheckIn() == null && mySearchParameters.getDateOfCheckOut() == null) {
                             checkin = getDate("Enter the check-in date");
                             checkout = getDate("Enter the check-out date");
                            validateDates(checkin,checkout, accommodations.get(choice2));
                        }else{
                             checkin= mySearchParameters.getDateOfCheckIn();
                             checkout = mySearchParameters.getDateOfCheckOut();
                        }
                        float daysBetween = ChronoUnit.DAYS.between(checkin.toLocalDate(), checkout.toLocalDate());
                        float rateprice = accommodations.get(choice2).getRatePrice();
                        int price = (int)(rateprice * daysBetween);
                        int numPersone;
                        if (mySearchParameters.getHowMuchPeople()==0) {
                            System.out.println("Enter the number of people");
                            numPersone = s2.nextInt();
                            if (numPersone < 1 || numPersone > accommodations.get(choice2).getMaxNumberOfPeople()) {
                                throw new InvalidAttributeValueException("Number of people entered is negative or 0 or or more than the maximum number of people in the accommodation");
                            }
                        }else{
                            numPersone=mySearchParameters.getHowMuchPeople();
                        }
                        boolean applydiscont = rc.applyDiscount(price);
                        System.out.println(applydiscont);
                        rc.booking(accommodations.get(choice2),checkin,checkout,numPersone,price,applydiscont);
                        System.out.println("Booking successfully entered");
                    }catch (IndexOutOfBoundsException| InvalidAttributeValueException | IllegalArgumentException e){
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case 5:{
                    try{
                        Scanner s2 = new Scanner(System.in);
                        int choice2;
                        System.out.println("Enter the accommodation you want to see reviews for(start to 1): ");
                        choice2 = s2.nextInt();
                        choice2 = choice2 - 1;
                        ArrayList<Review> reviews = rc.getReviews(accommodations.get(choice2));
                        if(reviews.isEmpty()){
                            System.out.println("No reviews found for this accommodation");
                        }else{
                            System.out.println("Reviews found for the accommodation: "+accommodations.get(choice2).getName());
                            for (Review review : reviews) {
                                System.out.println(review.toStringAccommodation());
                            }
                        }
                    }catch (IndexOutOfBoundsException e){
                        System.out.println("Added an index that goes beyond the length of the list of searched accommodations, Try again");
                    }
                    break;
                }
                case 6:{
                    try{
                        Scanner s2 = new Scanner(System.in);
                        int choice2;
                        System.out.println("Enter the accommodation you want to see the overall information for(start to 1): ");
                        choice2 = s2.nextInt();
                        choice2 = choice2 - 1;
                        System.out.println(accommodations.get(choice2).toStringSpecific());
                    }catch (IndexOutOfBoundsException e){
                        System.out.println("Added an index that goes beyond the length of the list of searched accommodations, Try again");
                    }
                    break;
                }
                case 7:{
                    break;
                }
                default: {
                    System.out.println("Please enter a valid choice");
                }
            }
        }while (choice != 7);
    }

    private static void validateDates(LocalDateTime checkin, LocalDateTime checkout, Accommodation accommodation) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime checkinAcc = accommodation.getAvailableFrom();
        LocalDateTime checkoutAcc = accommodation.getAvailableEnd();

        if (checkin != null && checkout != null) {
            // Verifica che le date non siano nel passato
            if (checkin.isBefore(now)) {
                throw new IllegalArgumentException("Check-in date cannot be in the past.");
            }

            if (checkout.isBefore(now)) {
                throw new IllegalArgumentException("Check-out date cannot be in the past.");
            }

            // Verifica che la data di check-out sia dopo il check-in
            if (!checkout.isAfter(checkin)) {
                throw new IllegalArgumentException("Check-out date must be after check-in date.");
            }

            if(checkin.isBefore(checkinAcc)) {
                throw new IllegalArgumentException("Check-in date cannot be before check-in date of accommodation.");
            }

            if(checkout.isAfter(checkoutAcc)) {
                throw new IllegalArgumentException("Check-out date cannot be after check-out date of accommodation.");
            }

        } else if (checkin != null || checkout != null) {
            // Se solo una delle due date è specificata
            throw new IllegalArgumentException("Both check-in and check-out dates must be provided or both must be null.");
        }
    }


    private static LocalDateTime getDate(String incipit) {
        Scanner s2 = new Scanner(System.in);
        System.out.println(incipit);
        System.out.print("Year (e.g., 2025): ");
        int year = s2.nextInt();
        System.out.print("Month (1-12): ");
        int month = s2.nextInt();
        System.out.print("Day (1-31): ");
        int day = s2.nextInt();
        return LocalDateTime.of(year, month, day, 0, 0);
    }
    // alternativa all'attuale ricerca con uso di colori per indicare ciò che si è scelto più un menu indicativo di cosa manca e cosa si è messo modulare e leggermente userfriendly
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";

    private static ArrayList<Accommodation> researchAccommodation(ResearchController rc) {
        Scanner scanner = new Scanner(System.in);
        Object[] filter = setFilterArray();
        int choice;

        do {
            printMenu(filter);
            choice = scanner.nextInt();
            handleUserChoice(choice, filter);
            if (filter[0] == null || ((String) filter[0]).trim().isEmpty()) {
                System.out.println("\n" + RED + "You must specify a place to perform a search." + RESET + "\n");
            }
        } while (choice != 21 || filter[0] == null || ((String) filter[0]).trim().isEmpty());

        try {
            SearchParameters sp = SearchParametersBuilder.newBuilder((String) filter[0])
                    .setDateOfCheckIn((LocalDateTime) filter[1])
                    .setDateOfCheckOut((LocalDateTime) filter[2])
                    .setHowMuchRooms((int) filter[3])
                    .setHowMuchPeople((int) filter[4])
                    .setCategory((AccommodationType) filter[5])
                    .setAllCategories((boolean) filter[6])
                    .setMaxPrice((float) filter[7])
                    .setMinRatingStars((AccommodationRating) filter[8])
                    .setSpecificRatingStars((AccommodationRating) filter[9])
                    .setRefundable((boolean) filter[10])
                    .setHaveFreeWifi((boolean) filter[11])
                    .setCanISmoke((boolean) filter[12])
                    .setHaveParking((boolean) filter[13])
                    .setHaveCoffeeMachine((boolean) filter[14])
                    .setHaveRoomService((boolean) filter[15])
                    .setHaveCleaningService((boolean) filter[16])
                    .setHaveSpa((boolean) filter[17])
                    .setGoodForKids((boolean) filter[18])
                    .setCanHaveAnimal((boolean) filter[19])
                    .build();
            mySearchParameters =sp;
            return rc.doResearch(sp);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }


    private static void printMenu(Object[] filter) {
        System.out.println("\n=== FILTER MENU ===");
        for (int i = 1; i <= 22; i++) {
            String label = getMenuLabel(i);
            boolean isSet = isFilterSet(filter, i);
            String coloredLabel = isSet ? GREEN + label + RESET : label;
            System.out.println(i + ". " + coloredLabel);
        }
        System.out.print("\nChoose an option: ");
    }


    private static String getMenuLabel(int option) {
        return switch (option) {
            case 1 -> "Place";
            case 2 -> "Date of Check-In";
            case 3 -> "Date of Check-Out";
            case 4 -> "How Many Rooms";
            case 5 -> "How Many People";
            case 6 -> "Category";
            case 7 -> "All Categories";
            case 8 -> "Max Price";
            case 9 -> "Min Accommodation Rating";
            case 10 -> "Specific Accommodation Rating";
            case 11 -> "Is Refundable";
            case 12 -> "Have Free Wifi";
            case 13 -> "Can I Smoke";
            case 14 -> "Have Parking";
            case 15 -> "Have Coffee Machine";
            case 16 -> "Have Room Service";
            case 17 -> "Have Cleaning Service";
            case 18 -> "Have Spa";
            case 19 -> "Good for Kids";
            case 20 -> "Can Have Animal";
            case 21 -> "Search";
            case 22 -> "Show Current Filters";
            default -> "Invalid";
        };
    }

    private static boolean isFilterSet(Object[] filter, int option) {
        return switch (option) {
            case 1 -> filter[0] != null && !filter[0].toString().trim().isEmpty();
            case 2 -> filter[1] != null;
            case 3 -> filter[2] != null;
            case 4 -> (int) filter[3] > 0;
            case 5 -> (int) filter[4] > 0;
            case 6 -> filter[5] != null;
            case 7 -> (boolean) filter[6];
            case 8 -> (float) filter[7] > 0;
            case 9 -> filter[8] != null;
            case 10 -> filter[9] != null;
            case 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 -> (boolean) filter[option - 1];
            default -> false;
        };
    }

    private static void handleUserChoice(int choice, Object[] filter) {
        Scanner scanner = new Scanner(System.in);
        switch (choice) {
            case 1 -> {
                System.out.print("Enter place: ");
                String place = scanner.nextLine();
                filter[0] = place;
                System.out.println("Place set to: " + GREEN + place + RESET);
            }
            case 2, 3 -> {
                System.out.println((choice == 2 ? "Check-In" : "Check-Out") + " Date:");
                System.out.print("Year (e.g., 2025): ");
                int year = scanner.nextInt();
                System.out.print("Month (1-12): ");
                int month = scanner.nextInt();
                System.out.print("Day (1-31): ");
                int day = scanner.nextInt();
                try {
                    LocalDateTime date = LocalDateTime.of(year, month, day, 0, 0);
                    filter[choice - 1] = date;
                    System.out.println((choice == 2 ? "Check-In" : "Check-Out") + " set to: " + GREEN + date.toLocalDate() + RESET);
                } catch (Exception e) {
                    System.out.println(RED + "Invalid date." + RESET);
                }
            }
            case 4, 5 -> {
                System.out.print("Enter number: ");
                int num = scanner.nextInt();
                filter[choice - 1] = num;
                System.out.println(getMenuLabel(choice) + " set to: " + GREEN + num + RESET);
            }
            case 6 -> {
                System.out.println("Select category: 1. Hotel, 2. B&B, 3. Apartment");
                int type = scanner.nextInt();
                AccommodationType selected = switch (type) {
                    case 1 -> AccommodationType.Hotel;
                    case 2 -> AccommodationType.BnB;
                    case 3 -> AccommodationType.Apartment;
                    default -> null;
                };
                filter[5] = selected;
                if (selected != null)
                    System.out.println("Category set to: " + GREEN + selected + RESET);
                else
                    System.out.println(RED + "Invalid category selected." + RESET);
            }
            case 7 -> {
                filter[6] = true;
                System.out.println("Set: " + GREEN + "All Categories = true" + RESET);
            }
            case 8 -> {
                System.out.print("Max Price: ");
                float price = scanner.nextFloat();
                filter[7] = price;
                System.out.println("Max Price set to: " + GREEN + price + RESET);
            }
            case 9, 10 -> {
                System.out.print("Rating (1-5): ");
                int r = scanner.nextInt();
                AccommodationRating rating = switch (r) {
                    case 1 -> AccommodationRating.OneStar;
                    case 2 -> AccommodationRating.TwoStar;
                    case 3 -> AccommodationRating.ThreeStar;
                    case 4 -> AccommodationRating.FourStar;
                    case 5 -> AccommodationRating.FiveStar;
                    default -> null;
                };
                filter[choice - 1] = rating;
                if (rating != null)
                    System.out.println(getMenuLabel(choice) + " set to: " + GREEN + rating + RESET);
                else
                    System.out.println(RED + "Invalid rating." + RESET);
            }
            case 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 -> {
                filter[choice - 1] = true;
                System.out.println(getMenuLabel(choice) + " set to: " + GREEN + "true" + RESET);
            }
            case 21 -> System.out.println("Searching...");
            case 22 -> showCurrentFilters(filter);
            default -> System.out.println(RED + "Invalid choice." + RESET);
        }
    }

    private static void showCurrentFilters(Object[] filter) {
        System.out.println("\n--- CURRENT FILTERS ---");
        for (int i = 1; i <= 20; i++) {
            String label = getMenuLabel(i);
            Object val = filter[i - 1];
            String value;
            if (val == null || (val instanceof Number num && num.doubleValue() == 0.0) || (val instanceof Boolean b && !b) || (val instanceof String && ((String) val).trim().isEmpty())) {
                value = RED + "Not set" + RESET;
            } else {
                value = GREEN + val.toString() + RESET;
            }
            System.out.println(label + ": " + value);
        }
        System.out.println();
    }

    private static Object[] setFilterArray() {
        Object[] array = new Object[20];
        for (int i = 0; i < array.length; i++) {
            if (i < 3 || i == 5 || i == 8 || i == 9) {
                array[i] = null;
            } else if (i == 3 || i == 4) {
                array[i] = 0;
            } else if (i == 7) {
                array[i] = 0.0f;
            } else {
                array[i] = false;
            }
        }
        return array;
    }

    private static void profileMenu(RegisterUser registerUser, ProfileUserController puc) throws SQLException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        int choice;
        boolean tag=true;
        do{
            System.out.println("MENU PROFILE USER: " +
                    "\n1. SEE PERSONAL INFORMATION" +
                    "\n2. SEE ALL FAVOURITE LOCATION" +
                    "\n3. SEE ALL BOOKINGS" +
                    "\n4. SEE ALL YOUR REVIEWS"+
                    "\n5. CHANGE PERSONAL INFORMATION"+
                    "\n6. DELETE A REVIEW" +
                    "\n7. DELETE FAVOURITE LOCATION" +
                    "\n8. EXIT"+
                    "\n9."+ RED+ " REMOVE ACCOUNT"+RESET
            );


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
                    registerUser.showMyReviews(puc.getReviewsByUser());
                    break;
                }
                case 5:{
                    changePersonalInformation(puc);
                    break;
                }
                case 6:{
                    try {
                        ArrayList<Review> reviews = puc.getReviewsByUser();
                        Scanner sc = new Scanner(System.in);
                        System.out.println("Enter the review you want to delete from the list of reviews written by you (start from 1)");
                        int choice2 = sc.nextInt();
                        choice2 = choice2 - 1;
                        if(choice2<0 || choice2>reviews.size()) {
                            throw new IndexOutOfBoundsException("The index cannot be less than zero or the index is beyond the size of the review list ");
                        }
                        puc.removeReview(reviews.get(choice2));
                        System.out.println("You have successfully deleted the review");
                    }catch (IndexOutOfBoundsException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case 7:{
                    try {
                        ArrayList<Accommodation> myPreferences = registerUser.getMyPreferences();
                        Scanner sc = new Scanner(System.in);
                        System.out.println("Enter the review you want to delete from the list of favourite accommodation (start from 1)");
                        int choice2 = sc.nextInt();
                        choice2 = choice2 - 1;
                        if(choice2<0 || choice2>myPreferences.size()) {
                            throw new IndexOutOfBoundsException("The index cannot be less than zero or the index is beyond the size of the review list ");
                        }
                        puc.unSaveAccommodation(myPreferences.get(choice2));
                        registerUser.removePreference(myPreferences.get(choice2));
                        System.out.println("You have successfully deleted the favourite accommodation");
                    }catch (IndexOutOfBoundsException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case 8:{
                    System.out.println("successful exit.");
                     tag = false;
                    break;
                }
                case 9: {
                    System.out.println("Are you sure you want to remove your account? You will lose everything");
                    System.out.println("\n1. Yes" +
                            "\n2. No");
                    choice = scanner.nextInt();
                    switch (choice) {
                        case 1: {
                            removeAccount(puc);
                            return;
                        }
                        case 2: {
                            break;
                        }
                        default:
                            System.out.println(RED + "Invalid choice." + RESET);
                            break;
                    }
                    break;
                }
                default: {
                    System.out.println("Please enter a valid choice");
                    break;
                }
            }
        }while (tag);
    }

    private static void removeAccount(ProfileUserController pc) throws SQLException, ClassNotFoundException {
        pc.unRegister();
        pc.exit();
        logInMenu();
    }

    private static void changePersonalInformation(ProfileUserController puc) throws SQLException, ClassNotFoundException {
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
        puc.updateProfile(name,surname,email,password,username,nfl);
    }

    public static void adminMenu(AdminController ac) throws SQLException, ClassNotFoundException {
        //todo
    }

    public static RegisterUser registerModule(UserController uc) throws SQLException, ClassNotFoundException {
        ArrayList<String> stringAttributes = new ArrayList<>();
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