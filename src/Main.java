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
                    // magari inserire un'eccezione che gestisce il caso in cui uno prova ad entrare da qui come admin.
                    RegisterUser registerUser=null;
                    try {
                         registerUser = uc.login(email, password);
                    }catch(RuntimeException e){
                        System.err.println(e.getMessage());
                    }
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
            System.out.println("----ALL ACCOMMODATIONS---------");
            for (int i=0; i<accommodations.size(); i++) {
                System.out.println((i+1)+") "+accommodations.get(i).toString());
            }
            System.out.println("MENU OPERATION ON SEARCHED ACCOMMODATION: " +
                    "\n 1. Add accommodation to your preferred accommodation (the choice of accommodation is based on the list of accommodations searched)" +
                    "\n 2. Enter a review for an accommodation (the choice of accommodation is based on the list of accommodations searched)" +
                    "\n 3. Booking a sought-after accommodation (the choice of accommodation is based on the list of accommodations searched)" +
                    "\n 4. View reviews of a specific accommodation (the choice of accommodation is based on the list of accommodations searched)" +
                    "\n 5. See more information for a specific accommodation" +
                    "\n 6. Exit"
            );

            choice = scanner.nextInt();

            switch(choice) {
                case 1:{
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
                case 2:{
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
                case 3:{
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
                        rc.booking(accommodations.get(choice2),checkin,checkout,numPersone,price,applydiscont);
                        System.out.println("Booking successfully entered");
                    }catch (IndexOutOfBoundsException| InvalidAttributeValueException | IllegalArgumentException e){
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case 4:{
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
                case 5:{
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
                case 6:{
                    System.out.println("Go back to Menu User");
                    break;
                }
                default: {
                    System.out.println("Please enter a valid choice");
                }
            }
        }while (choice != 6);
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
      try{
        Scanner s2 = new Scanner(System.in);
        System.out.println(incipit);
        System.out.print("Year (e.g., 2025): ");
        int year = s2.nextInt();
        System.out.print("Month (1-12): ");
        int month = s2.nextInt();
        System.out.print("Day (1-31): ");
        int day = s2.nextInt();
        return LocalDateTime.of(year, month, day, 0, 0);
    }catch (IndexOutOfBoundsException e){
      System.out.println(e.getMessage());
      return null;
      }
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
                    "\n2. MANAGE YOUR FAVOURITE ACCOMMODATIONS" +
                    "\n3. MANAGE YOUR BOOKINGS" +
                    "\n4. MANAGE YOUR REVIEWS"+
                    "\n5. CHANGE PERSONAL INFORMATION"+
                    "\n6. EXIT"+
                    "\n7."+ RED+ " REMOVE ACCOUNT"+RESET
            );

            choice = scanner.nextInt();

            switch(choice) {
                case 1:{
                    puc.seeProfile();
                    break;
                }
                case 2:{
                    Scanner sc = new Scanner(System.in);
                    registerUser.showMyPreferences();
                    int choice3;
                    do{
                        System.out.println("Want to delete a favourite accommodation? (1 yes,2 no)");
                        choice3 = scanner.nextInt();
                        switch(choice3) {
                            case 1:{
                                try {
                                    ArrayList<Accommodation> myPreferences = registerUser.getMyPreferences();
                                    if(!myPreferences.isEmpty()) {
                                        System.out.println("Enter the number you want to delete from the list of favourite accommodation (start from 1)");
                                        int choice2 = sc.nextInt();
                                        choice2 = choice2 - 1;
                                        if(choice2<0 || choice2>myPreferences.size()) {
                                            throw new IndexOutOfBoundsException("The index cannot be less than zero or the index is beyond the size of the review list ");
                                        }
                                        puc.unSaveAccommodation(myPreferences.get(choice2));
                                        registerUser.removePreference(myPreferences.get(choice2));
                                        System.out.println("You have successfully deleted the favourite accommodation");
                                    }else{
                                        System.out.println("No favourite accommodation");
                                    }
                                }catch (IndexOutOfBoundsException e) {
                                    System.out.println(e.getMessage());
                                }
                                break;
                            }
                            case 2:{
                                break;
                            }
                            default:{
                                System.out.println("Invalid choice.");
                                break;
                            }
                        }
                    }while(choice3 < 1 || choice3 > 2);
                    break;
                }
                case 3: {
                    Scanner sc = new Scanner(System.in);
                    ArrayList<Booking> bookings = puc.viewMyBookings();

                    if (bookings.isEmpty()) {
                        System.out.println("You have no bookings.");
                        break;
                    }

                    registerUser.showMyBookings();

                    // Prima parte: Rimozione di prenotazioni concluse o cancellate
                    System.out.println("\nDo you want to remove a concluded/cancelled booking from your list?");
                    System.out.println("1. Yes\n2. No");
                    int removeChoice = sc.nextInt();

                    if (removeChoice == 1) {
                        int indexToRemove = chooseBookingIndex(sc, bookings, "remove");
                        if (indexToRemove != -1) {
                            Booking selected = bookings.get(indexToRemove);
                            puc.removeBooking(selected);
                            System.out.println("Booking successfully removed.");
                            break;
                        }
                    }
                    // Seconda parte: Cancellazione di prenotazioni attive
                    System.out.println("\nDo you want to cancel an active booking?");
                    System.out.println("1. Yes\n2. No");
                    int cancelChoice = sc.nextInt();

                    if (cancelChoice == 1) {
                        int indexToCancel = chooseBookingIndex(sc, bookings, "cancel");
                        if (indexToCancel != -1) {
                            Booking selected = bookings.get(indexToCancel);

                            puc.cancelABooking(selected);
                            System.out.println("Booking successfully cancelled.");
                            System.out.println(" Remember! Only confirmed bookings can be cancelled.");
                        }
                    }
                    break;
                }

                case 4:{
                    Scanner sc = new Scanner(System.in);
                    registerUser.showMyReviews(puc.getReviewsByUser());
                    int choice3;
                    do{
                        System.out.println("Want to delete a review? (1 yes,2 no)");
                        choice3 = sc.nextInt();
                        switch(choice3) {
                            case 1:{
                                try {
                                    ArrayList<Review> reviews = puc.getReviewsByUser();
                                    if(!reviews.isEmpty()) {
                                        System.out.println("Enter the review you want to delete from the list of reviews written by you (start from 1)");
                                        int choice2 = sc.nextInt();
                                        choice2 = choice2 - 1;
                                        if(choice2<0 || choice2>reviews.size()) {
                                            throw new IndexOutOfBoundsException("The index cannot be less than zero or the index is beyond the size of the review list ");
                                        }
                                        puc.removeReview(reviews.get(choice2));
                                        System.out.println("You have successfully deleted the review");
                                    }else{
                                     System.out.println("No reviews");
                                    }
                                }catch (IndexOutOfBoundsException e) {
                                    System.out.println(e.getMessage());
                                }
                                break;
                            }
                            case 2:{
                                break;
                            }
                            default:{
                                System.out.println("Invalid choice.");
                                break;
                            }
                        }
                    }while (choice3<1 || choice3>2);
                    break;
                }
                case 5:{
                    changePersonalInformation(puc);
                    break;
                }
                case 6:{
                    System.out.println("successful exit.");
                     tag = false;
                    break;
                }
                case 7: {
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

    private static int chooseBookingIndex(Scanner sc, ArrayList<Booking> bookings, String action) {
        System.out.println("Enter the booking number you want to " + action + " (start from 1): ");
        int choice = sc.nextInt();
        int index = choice - 1;

        if (index < 0 || index >= bookings.size()) {
            System.out.println("Invalid booking number.");
            return -1;
        }

        return index;
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
           + "\n7. " + GREEN +"Confirm Update"+ RESET
           + "\n8. "+ RED+ "Go Back"+ RESET);

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
               case 7:
                   break;

               case 8:{
                   return;
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
        Scanner sc = new Scanner(System.in);
        int choice;
        do{
            System.out.println("Welcome to Admin Menu"
                    + "\n1. Add a new accommodation"
                    + "\n2. Update an accommodation"
                    + "\n3. Delete an accommodation"
                    + "\n4. Remove an User by his id"
                    + "\n5. Search an User by his id"
                    + "\n6. Get All accommodations"
                    + "\n7. See information specific of an accommodation"
                    + "\n8. Get All users"
                    + "\n9. Get all reviews from a user"
                    + "\n10. Get all reviews of an accommodation"
                    + "\n11. Get accommodation with the id"
                    + "\n12. Change Password"
                    + "\n13. Removing a review with its id"
                    + "\n14. Logout");

            choice = sc.nextInt();

            switch(choice) {
                case 1:{
                    addAccommodation(ac);
                    break;
                }
                case 2:{
                    updateAccommodation(ac);
                    break;
                }
                case 3:{
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("Enter the id of the accommodation that you want to delete: ");
                    int choise2 = scanner.nextInt();
                    try{
                       ac.deleteAccommodation(choise2);
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case 4:{
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("Enter the id of the user that you want to remove: ");
                    int choise2 = scanner.nextInt();
                    try{
                        ac.removeUser(choise2);
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case 5:{
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("Enter the id of the user that you want to search: ");
                    int choise2 = scanner.nextInt();
                    try{
                        RegisterUser searched=ac.searchUser(choise2);
                        if(searched!=null){
                            searched.showMyPersonalInfo();
                        }else{
                            System.out.println("User with id: "+choise2+" does not exist");
                        }
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case 6:{
                    ArrayList<Accommodation> accommodations = ac.getAllAccommodation();
                    System.out.println("----ALL ACCOMMODATION----");
                    if(!accommodations.isEmpty()){
                        for (Accommodation accommodation : accommodations) {
                            System.out.println(accommodation.toString());
                        }
                    }else{
                        System.out.println("There is no accommodation");
                    }
                    break;
                }
                case 7:{
                    Scanner scanner = new Scanner(System.in);
                    int choise2;
                    try {
                        System.out.println("Enter the id of the accommodation that you want to see in detail: ");
                        choise2 = scanner.nextInt();
                        Accommodation acc = ac.getAccommodationById(choise2);
                        if(acc!=null) {
                            System.out.println(acc.toStringSpecific());
                        }else{
                            System.out.println("There is an error");
                        }
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case 8:{
                    ArrayList<RegisterUser> registerUsers = ac.getAllUser();
                    System.out.println("----ALL USERS----");
                    if(!registerUsers.isEmpty()){
                        for (RegisterUser ru : registerUsers) {
                            ru.showMyPersonalInfoAdmin();
                        }
                    }else{
                        System.out.println("There is no user");
                    }
                    break;
                }
                case 9:{
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("Enter the ID of the user whose reviews you want to see written by him: ");
                    int choise2 = scanner.nextInt();
                    try{
                                RegisterUser searched=ac.searchUser(choise2);
                                if(searched!=null) {
                                    ArrayList<Review> reviews = ac.getReviewByUser(searched);
                                    System.out.println("ALL REVIEWS BY " + searched.getUsername() + " :");
                                    if (!reviews.isEmpty()) {
                                        for (Review review : reviews) {
                                            System.out.println(review.toStringUser());
                                        }
                                    } else {
                                        System.out.println("There is no review");
                                    }
                                }else {
                                    System.out.println("There is no user with given ID");
                                }
                                break;

                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case 10:{
                    Scanner scanner = new Scanner(System.in);
                    int choise2;
                    try {
                        System.out.println("Enter the ID of the accommodation you want to see reviews for: ");
                        choise2 = scanner.nextInt();
                        Accommodation acc = ac.getAccommodationById(choise2);
                        if(acc!=null) {
                            ArrayList<Review> reviews = ac.getReviewByAccommodation(acc);
                            System.out.println("ALL REVIEWS OF " + acc.getName() + " :");
                            if (!reviews.isEmpty()) {
                                for (Review review : reviews) {
                                    System.out.println(review.toStringUser());
                                }
                            } else {
                                System.out.println("There is no review");
                            }
                        }else{
                            System.out.println("There is no accommodation with given ID");
                        }
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case 11:{
                    try {
                        Scanner scanner = new Scanner(System.in);
                        System.out.println("Enter the id of the accommodation that you want to search: ");
                        int choise2 = scanner.nextInt();
                        Accommodation a = ac.getAccommodationById(choise2);
                        if(a!=null){
                            System.out.println(a.toString());
                        }else{
                            System.out.println("There is with this id: "+choise2+ "no accommodation");
                        }
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case 12:{
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("Enter your new password: ");
                    String pass = scanner.nextLine();
                    ac.changePassword(pass);
                    break;
                }
                case 13:{
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("Enter the id of the review that you want to remove: ");
                    int choise2 = scanner.nextInt();
                    try{
                        ac.removeReview(choise2);
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case 14:{
                    ac.exit();
                    System.out.println("Successfully logout");
                    break;
                }
                default:{
                    System.out.println("Please enter a valid choice");
                }
            }
        }while (choice!=14);
    }

    private static void updateAccommodation(AdminController ac) {
        Scanner scanner = new Scanner(System.in);
        Object[] filter = setFilterArrayAccommodation();
        int choice;
        ArrayList<Accommodation> acc = ac.getAllAccommodation();
        System.out.println("Enter the id of accommodation that you want to update: ");
        choice = scanner.nextInt();
        boolean control=false;
        Accommodation accommodation = null;
        try{
            for (Accommodation a : acc) {
                if (a.getId() == choice) {
                    control = true;
                    accommodation = a;
                    break;
                }
            }
            if(control) {
                do {
                    printMenuAccommodationUpdate(accommodation,"Update Accommodation");
                    choice = scanner.nextInt();
                    accommodation = handleUserChoiceAccommodationUpdate(accommodation, choice, "Changing your accommodation information......");
                } while (choice != 22);
                checkDataAccommodationUpdate(accommodation);
                ac.updateAccommodation(accommodation);
                System.out.println("Successfully updated");
            }else {
                throw new IndexOutOfBoundsException("There is no accommodation with that ID");
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void checkDataAccommodationUpdate(Accommodation accommodation) {
        if (accommodation.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("\n" + RED + "You must specify a name of place to perform the insert." + RESET + "\n");
        }else if(accommodation.getAddress().trim().isEmpty()){
            throw new IllegalArgumentException("\n" + RED + "You must specify an address of place to perform the insert." + RESET + "\n");
        }else if(accommodation.getPlace().trim().isEmpty()){
            throw new IllegalArgumentException("\n" + RED + "You must specify a place to perform the insert." + RESET + "\n");
        }else if(accommodation.getType() == null){
            throw new IllegalArgumentException("\n" + RED + "You must specify a type of accommodation to perform the insert." + RESET + "\n");
        }else if(accommodation.getRatePrice() < 1){
            throw new IllegalArgumentException("\n" + RED + "You must specify a rateprice to perform the insert." + RESET + "\n");
        }else if(accommodation.getAvailableFrom() == null){
            throw new IllegalArgumentException("\n" + RED + "You must specify the available from date to perform the insert." + RESET + "\n");
        }else if(accommodation.getAvailableEnd() == null){
            throw new IllegalArgumentException("\n" + RED + "You must specify the available end date to perform the insert." + RESET + "\n");
        }else if(accommodation.getNumberOfRoom() < 1){
            throw new IllegalArgumentException("\n" + RED + "You must specify the max number of rooms to perform the insert." + RESET + "\n");
        }else if(accommodation.getMaxNumberOfPeople() < 1){
            throw new IllegalArgumentException("\n" + RED + "You must specify the max number of people to perform the insert." + RESET + "\n");
        }
        LocalDateTime now = LocalDateTime.now();
        if (accommodation.getAvailableFrom().isBefore(now)) {
            throw new IllegalArgumentException("Check-in date cannot be in the past.");
        }

        if (accommodation.getAvailableEnd().isBefore(now)) {
            throw new IllegalArgumentException("Check-out date cannot be in the past.");
        }

        // Verifica che la data di check-out sia dopo il check-in
        if (!accommodation.getAvailableEnd().isAfter(accommodation.getAvailableFrom())){
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }
    }

    private static Accommodation handleUserChoiceAccommodationUpdate(Accommodation acc, int choice, String s) {
        Scanner scanner = new Scanner(System.in);
        switch (choice) {
            case 1 -> {
                System.out.print("Enter name: ");
                String name = scanner.nextLine();
                acc.setName(name);
                System.out.println("Name set to: " + GREEN + name + RESET);
            }
            case 2 -> {
                System.out.print("Enter address: ");
                String address = scanner.nextLine();
                acc.setAddress(address);
                System.out.println("address set to: " + GREEN + address + RESET);
            }
            case 3 -> {
                System.out.print("Enter place: ");
                String place = scanner.nextLine();
                acc.setPlace(place);
                System.out.println("place set to: " + GREEN + place + RESET);
            }
            case 4 -> {
                System.out.println("Select category: 1. Hotel, 2. B&B, 3. Apartment");
                int type = scanner.nextInt();
                AccommodationType selected = switch (type) {
                    case 1 -> AccommodationType.Hotel;
                    case 2 -> AccommodationType.BnB;
                    case 3 -> AccommodationType.Apartment;
                    default -> null;
                };
                acc.setType(selected);
                if (selected != null) {
                    System.out.println("Category set to: " + GREEN + selected + RESET);
                }else
                    System.out.println(RED + "Invalid category selected." + RESET);
            }
            case 5 -> {
                System.out.print("Max Price: ");
                float price = scanner.nextFloat();
                acc.setRatePrice(price);
                System.out.println("Max Price set to: " + GREEN + price + RESET);
            }
            case 6 -> {
                System.out.println("Description");
                String description = scanner.nextLine();
                acc.setDescription(description);
                System.out.println("Description set to: " + GREEN + description + RESET);
            }
            case 7 -> {
                System.out.println("Available-From" + " Date:");
                System.out.print("Year (e.g., 2025): ");
                int year = scanner.nextInt();
                System.out.print("Month (1-12): ");
                int month = scanner.nextInt();
                System.out.print("Day (1-31): ");
                int day = scanner.nextInt();
                try {
                    LocalDateTime date = LocalDateTime.of(year, month, day, 0, 0);
                    acc.setAvailableFrom(date);
                    System.out.println("Check-In" + " set to: " + GREEN + date.toLocalDate() + RESET);
                } catch (Exception e) {
                    System.out.println(RED + "Invalid date." + RESET);
                }
            }
            case 8 -> {
                System.out.println("Available-End" + " Date:");
                System.out.print("Year (e.g., 2025): ");
                int year = scanner.nextInt();
                System.out.print("Month (1-12): ");
                int month = scanner.nextInt();
                System.out.print("Day (1-31): ");
                int day = scanner.nextInt();
                try {
                    LocalDateTime date = LocalDateTime.of(year, month, day, 0, 0);
                    acc.setAvailableEnd(date);
                    System.out.println("Check-Out" + " set to: " + GREEN + date.toLocalDate() + RESET);
                } catch (Exception e) {
                    System.out.println(RED + "Invalid date." + RESET);
                }
            }
            case 9 ->{
                acc.setCoffeMachine(!acc.isCoffeMachine());
                System.out.println(getMenuLabelAccommodation(choice, "Update Accommodation") + " set to: " + GREEN + acc.isCoffeMachine() + RESET);
            }
            case 10 -> {
                acc.setRoomService(!acc.isRoomService());
                System.out.println(getMenuLabelAccommodation(choice, "Update Accommodation") + " set to: " + GREEN + acc.isRoomService() + RESET);
            }
            case 11 -> {
                acc.setWelcomeAnimal(!acc.isWelcomeAnimal());
                System.out.println(getMenuLabelAccommodation(choice, "Update Accommodation") + " set to: " + GREEN + acc.isWelcomeAnimal() + RESET);
            }
            case 12 -> {
                System.out.print("Enter number of rooms: ");
                int numberrooms = scanner.nextInt();
                acc.setNumberOfRoom(numberrooms);
                System.out.println(getMenuLabelAccommodation(choice, "Update Accommodation") + " set to: " + GREEN + numberrooms + RESET);
            }
            case 13 -> {
                acc.setGoodForKids(!acc.isGoodForKids());
                System.out.println(getMenuLabelAccommodation(choice, "Update Accommodation") + " set to: " + GREEN + acc.isGoodForKids() + RESET);
            }
            case 14 -> {
                acc.setHaveSpa(!acc.isHaveSpa());
                System.out.println(getMenuLabelAccommodation(choice, "Update Accommodation") + " set to: " + GREEN + acc.isHaveSpa() + RESET);
            }
            case 15 -> {
                acc.setCleaningService(!acc.isCleaningService());
                System.out.println(getMenuLabelAccommodation(choice, "Update Accommodation") + " set to: " + GREEN + acc.isCleaningService() + RESET);
            }
            case 16 -> {
                acc.setRefundable(!acc.isRefundable());
                System.out.println(getMenuLabelAccommodation(choice, "Update Accommodation") + " set to: " + GREEN + acc.isRefundable() + RESET);
            }
            case 17 -> {
                acc.setFreewifi(!acc.isFreewifi());
                System.out.println(getMenuLabelAccommodation(choice, "Update Accommodation") + " set to: " + GREEN + acc.isFreewifi() + RESET);
            }
            case 18 -> {
                acc.setHaveSmokingArea(!acc.isHaveSmokingArea());
                System.out.println(getMenuLabelAccommodation(choice, "Update Accommodation") + " set to: " + GREEN + acc.isHaveSmokingArea() + RESET);
            }
            case 19 -> {
                acc.setHaveParking(!acc.isHaveParking());
                System.out.println(getMenuLabelAccommodation(choice, "Update Accommodation") + " set to: " + GREEN + acc.isHaveParking() + RESET);
            }
            case 20 -> {
                System.out.print("Enter number of people: ");
                int numberpeople = scanner.nextInt();
                acc.setMaxNumberOfPeople(numberpeople);
                System.out.println(getMenuLabel(choice) + " set to: " + GREEN + numberpeople + RESET);
            }
            case 21 -> System.out.println(s);
            case 22 -> showCurrentFiltersAccommodationUpdate(acc);
            default -> System.out.println(RED + "Invalid choice." + RESET);
        }
        return acc;
    }

    private static void showCurrentFiltersAccommodationUpdate(Accommodation acc) {
        System.out.println("\n--- CURRENT FILTERS ---");
        for (int i = 1; i < 22; i++) {
            String label = getMenuLabelAccommodation(i,"Insert Accommodation");
            Object val = filterAccommodation(acc,i);
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

    private static Object filterAccommodation(Accommodation a,int i) {
        return switch (i) {
            case 1 -> a.getName();
            case 2 -> a.getAddress();
            case 3 -> a.getPlace();
            case 4 -> a.getType();
            case 5 -> a.getRatePrice();
            case 6 -> a.getDescription();
            case 7 -> a.getAvailableFrom();
            case 8 -> a.getAvailableEnd();
            case 9 -> a.isCoffeMachine();
            case 10 -> a.isRoomService();
            case 11 -> a.isWelcomeAnimal();
            case 12 -> a.getNumberOfRoom();
            case 13 -> a.isGoodForKids();
            case 14 -> a.isHaveSpa();
            case 15 -> a.isCleaningService();
            case 16 -> a.isRefundable();
            case 17 -> a.isFreewifi();
            case 18 -> a.isHaveSmokingArea();
            case 19 -> a.isHaveParking();
            case 20 -> a.getMaxNumberOfPeople();
            default -> false;
        };
    }

    private static void printMenuAccommodationUpdate(Accommodation a,String updateAccommodation) {
        System.out.println("\n=== FILTER MENU ===");
        for (int i = 1; i < 24; i++) {
            String label = getMenuLabelAccommodation(i,updateAccommodation);
            boolean isSet = isFilterSetAccommodationUpdate(a,i);
            String coloredLabel = isSet ? GREEN + label + RESET : label;
            System.out.println(i + ". " + coloredLabel);
        }
        System.out.print("\nChoose an option: ");
    }

    private static boolean isFilterSetAccommodationUpdate(Accommodation a,int i) {
        return switch (i) {
            case 1 -> a.isFieldModified("name");
            case 2 -> a.isFieldModified("address");
            case 3 -> a.isFieldModified("place");
            case 4 -> a.isFieldModified("type");
            case 5 -> a.isFieldModified("ratePrice");
            case 6 -> a.isFieldModified("description");
            case 7 -> a.isFieldModified("availableFrom");
            case 8 -> a.isFieldModified("availableEnd");
            case 9 -> a.isFieldModified("coffeMachine");
            case 10 -> a.isFieldModified("roomService");
            case 11 -> a.isFieldModified("welcomeAnimal");
            case 12 -> a.isFieldModified("numberOfRoom");
            case 13 -> a.isFieldModified("goodForKids");
            case 14 -> a.isFieldModified("haveSpa");
            case 15 -> a.isFieldModified("cleaningService");
            case 16 -> a.isFieldModified("refundable");
            case 17 -> a.isFieldModified("freewifi");
            case 18 -> a.isFieldModified("haveSmokingArea");
            case 19 -> a.isFieldModified("haveParking");
            case 20 -> a.isFieldModified("maxNumberOfPeople");
            default -> false;
        };
    }

    private static void addAccommodation (AdminController ac) {
        Scanner scanner = new Scanner(System.in);
        Object[] filter = setFilterArrayAccommodation();
        int choice;

        do {
            printMenuAccommodation(filter,"Insert Accommodation");
            choice = scanner.nextInt();
            handleUserChoiceAccommodation(choice, filter, "Adding Accommodation.....");
        } while (choice != 22);

        try {
            checkDataAccommodation(filter);
            ac.addAccommodation((String) filter[0],(String) filter[1], (String) filter[2],(int) filter[3],(AccommodationType) filter[4],(float) filter[5],(String) filter[6],(LocalDateTime) filter[7],(LocalDateTime) filter[8],
                    (boolean) filter[9],(boolean) filter[10],(boolean) filter[11],(int) filter[12],(boolean) filter[13],(boolean) filter[14],(boolean) filter[15],(boolean) filter[16],(boolean) filter[17],(boolean) filter[18],
                    (boolean) filter[19],(int) filter[20]);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void checkDataAccommodation(Object[] filter) {
        if (filter[0] == null || ((String) filter[0]).trim().isEmpty()) {
            throw new IllegalArgumentException("\n" + RED + "You must specify a name of place to perform the insert." + RESET + "\n");
        }else if(filter[1] == null || ((String) filter[1]).trim().isEmpty()){
            throw new IllegalArgumentException("\n" + RED + "You must specify an address of place to perform the insert." + RESET + "\n");
        }else if(filter[2] == null || ((String) filter[2]).trim().isEmpty()){
            throw new IllegalArgumentException("\n" + RED + "You must specify a place to perform the insert." + RESET + "\n");
        }else if((int)filter[3] < 1){
            throw new IllegalArgumentException("\n" + RED + "You must specify the disponibilty to perform the insert." + RESET + "\n");
        }else if(filter[4] == null){
            throw new IllegalArgumentException("\n" + RED + "You must specify a type of accommodation to perform the insert." + RESET + "\n");
        }else if((float)filter[5] < 1){
            throw new IllegalArgumentException("\n" + RED + "You must specify a rateprice to perform the insert." + RESET + "\n");
        }else if(filter[7] == null){
            throw new IllegalArgumentException("\n" + RED + "You must specify the available from date to perform the insert." + RESET + "\n");
        }else if(filter[8] == null){
            throw new IllegalArgumentException("\n" + RED + "You must specify the available end date to perform the insert." + RESET + "\n");
        }else if((int)filter[12] < 1){
            throw new IllegalArgumentException("\n" + RED + "You must specify the max number of rooms to perform the insert." + RESET + "\n");
        }else if((int)filter[20] < 1){
            throw new IllegalArgumentException("\n" + RED + "You must specify the max number of people to perform the insert." + RESET + "\n");
        }
        LocalDateTime now = LocalDateTime.now();
        if (((LocalDateTime) filter[7]).isBefore(now)) {
            throw new IllegalArgumentException("Check-in date cannot be in the past.");
        }

        if (((LocalDateTime) filter[8]).isBefore(now)) {
            throw new IllegalArgumentException("Check-out date cannot be in the past.");
        }

        // Verifica che la data di check-out sia dopo il check-in
        if (!((LocalDateTime) filter[8]).isAfter((LocalDateTime) filter[7])) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }


    }

    private static void printMenuAccommodation(Object[] filter, String app) {
        System.out.println("\n=== FILTER MENU ===");
        for (int i = 1; i < 24; i++) {
            String label = getMenuLabelAccommodation(i,app);
            boolean isSet = isFilterSetAccommodation(filter, i);
            String coloredLabel = isSet ? GREEN + label + RESET : label;
            System.out.println(i + ". " + coloredLabel);
        }
        System.out.print("\nChoose an option: ");
    }

    private static String getMenuLabelAccommodation(int option,String app) {
        return switch (option) {
            case 1 -> "Name";
            case 2 -> "Address";
            case 3 -> "Place";
            case 4 -> "disponibility";
            case 5 -> "type";
            case 6 -> "rateprice";
            case 7 -> "description";
            case 8 -> "availableFrom";
            case 9 -> "availableEnd";
            case 10 -> "coffeemachine";
            case 11 -> "roomservice";
            case 12 -> "welcomeanimal";
            case 13 -> "numberofroom";
            case 14 -> "goodforkids";
            case 15 -> "havespa";
            case 16 -> "cleaningservice";
            case 17 -> "refundable";
            case 18 -> "freewifi";
            case 19 -> "havesmockingarea";
            case 20 -> "haveparking";
            case 21 -> "maxpeople";
            case 22 -> app;
            case 23 -> "Show Current Data";
            default -> "Invalid";
        };
    }

    private static boolean isFilterSetAccommodation(Object[] filter, int option) {
        return switch (option) {
            case 1 -> filter[0] != null && !filter[0].toString().trim().isEmpty();
            case 2 -> filter[1] != null && !filter[1].toString().trim().isEmpty();
            case 3 -> filter[2] != null && !filter[2].toString().trim().isEmpty();
            case 4 -> (int) filter[3] > 0;
            case 5 -> filter[4] != null && !filter[4].toString().trim().isEmpty();
            case 6 -> (float) filter[5] > 0;
            case 7,8,9 -> filter[option - 1] != null && !filter[option - 1].toString().trim().isEmpty();
            case 13 -> (int) filter[12] > 0;
            case 10, 11, 12, 14, 15, 16, 17, 18, 19, 20 -> (boolean) filter[option - 1];
            case 21 -> (int)filter[20] > 0;
            default -> false;
        };
    }

    private static void handleUserChoiceAccommodation(int choice, Object[] filter, String stringUsefull) {
        Scanner scanner = new Scanner(System.in);
        switch (choice) {
            case 1 -> {
                System.out.print("Enter name: ");
                String name = scanner.nextLine();
                filter[0] = name;
                System.out.println("Name set to: " + GREEN + name + RESET);
            }
            case 2 -> {
                System.out.print("Enter address: ");
                String address = scanner.nextLine();
                filter[1] = address;
                System.out.println("address set to: " + GREEN + address + RESET);
            }
            case 3 -> {
                System.out.print("Enter place: ");
                String place = scanner.nextLine();
                filter[2] = place;
                System.out.println("place set to: " + GREEN + place + RESET);
            }
            case 4 -> {
                System.out.print("Enter disponibility: ");
                int disponibility = scanner.nextInt();
                filter[3] = disponibility;
                System.out.println(getMenuLabel(choice) + " set to: " + GREEN + disponibility + RESET);
            }
            case 5 -> {
                System.out.println("Select category: 1. Hotel, 2. B&B, 3. Apartment");
                int type = scanner.nextInt();
                AccommodationType selected = switch (type) {
                    case 1 -> AccommodationType.Hotel;
                    case 2 -> AccommodationType.BnB;
                    case 3 -> AccommodationType.Apartment;
                    default -> null;
                };
                filter[4] = selected;
                if (selected != null) {
                    System.out.println("Category set to: " + GREEN + selected + RESET);
                }else
                    System.out.println(RED + "Invalid category selected." + RESET);
            }
            case 6 -> {
                System.out.print("Max Price: ");
                float price = scanner.nextFloat();
                filter[5] = price;
                System.out.println("Max Price set to: " + GREEN + price + RESET);
            }
            case 7 -> {
                System.out.println("Description");
                String description = scanner.nextLine();
                filter[6] = description;
                System.out.println("Description set to: " + GREEN + description + RESET);
            }
            case 8, 9 -> {
                System.out.println((choice == 2 ? "Available-From" : "Available-End") + " Date:");
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
            case 13 -> {
                System.out.print("Enter number of rooms: ");
                int numberrooms = scanner.nextInt();
                filter[12] = numberrooms;
                System.out.println(getMenuLabel(choice) + " set to: " + GREEN + numberrooms + RESET);
            }
            case 10, 11, 12, 14, 15, 16, 17, 18, 19, 20 -> {
                filter[choice - 1] = true;
                System.out.println(getMenuLabelAccommodation(choice, "Insert Accommodation") + " set to: " + GREEN + "true" + RESET);
            }
            case 21 -> {
                System.out.print("Enter number of people: ");
                int numberpeople = scanner.nextInt();
                filter[20] = numberpeople;
                System.out.println(getMenuLabel(choice) + " set to: " + GREEN + numberpeople + RESET);
            }
            case 22 -> System.out.println(stringUsefull);
            case 23 -> showCurrentFiltersAccommodation(filter);
            default -> System.out.println(RED + "Invalid choice." + RESET);
        }
    }

    private static void showCurrentFiltersAccommodation(Object[] filter) {
        System.out.println("\n--- CURRENT FILTERS ---");
        for (int i = 1; i < 22; i++) {
            String label = getMenuLabelAccommodation(i,"Insert Accommodation");
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

    private static Object[] setFilterArrayAccommodation() {
        Object[] array = new Object[21];
        for (int i = 0; i < array.length; i++) {
            if (i < 3 || i == 4 || (i > 5 && i < 9) ) {
                array[i] = null;
            } else if (i == 3 || i == 20 || i==12) {
                array[i] = 0;
            } else if (i == 5) {
                array[i] = 0.0f;
            } else {
                array[i] = false;
            }
        }
        return array;
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