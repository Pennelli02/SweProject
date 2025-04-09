package BusinessLogic;

import DAO.AccommodationDAO;
import DAO.ReviewDAO;
import DAO.UserDAO;
import DomainModel.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class AdminController {
    private boolean isLoggedIn;

    public AdminController() {
        isLoggedIn = false;
    }

    public void deleteAccomodation(int idAccomodation){
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        accommodationDAO.deleteAccommodation(idAccomodation);
    }

    //TODO scegliere quale soluzione tenere in considerazione (richiesta a Lore)
    //TODO da implemtare tale funzione uso il dirty Flag
    public void updateAccommodation(Accommodation accommodation){
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        accommodationDAO.updateAccommodationDirty(accommodation);
        accommodation.clearModifiedFields();
    }
    //TODO da implementare tale funzione seguendo la logica del UML
    public void updateAccommodation(Accommodation accommodation,  String newName, String newAddress, String place, AccommodationType type, float ratePrice, LocalDateTime availableFrom, LocalDateTime availableEnd, String description, boolean freewifi, boolean haveSmokingArea,boolean haveParking, boolean coffeMachine, boolean roomService, boolean cleaningService, boolean haveSpa, boolean goodForKids, int numberOfRoom, boolean welcomeAnimal, int maxNumberOfPeople) throws SQLException {
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        int idAcc=accommodation.getId();
        if (!Objects.equals(accommodation.getName(), newName)){
            accommodationDAO.updateName(idAcc,newName);
            accommodation.setName(newName);
        }
        if (!Objects.equals(accommodation.getAddress(), newAddress)) {
            accommodationDAO.updateAddress(idAcc, newAddress);
            accommodation.setAddress(newAddress);
        }

        if (!Objects.equals(accommodation.getPlace(), place)) {
            accommodationDAO.updatePlace(idAcc, place);
            accommodation.setPlace(place);
        }

        if (!Objects.equals(accommodation.getType(), type)) {
            accommodationDAO.updateType(idAcc, type);
            accommodation.setType(type);
        }

        if (accommodation.getRatePrice() != ratePrice) {
            accommodationDAO.updateRatePrice(idAcc, ratePrice);
            accommodation.setRatePrice(ratePrice);
        }

        if (!Objects.equals(accommodation.getAvailableFrom(), availableFrom)) {
            accommodationDAO.updateAvailableFrom(idAcc, availableFrom);
            accommodation.setAvailableFrom(availableFrom);
        }

        if (!Objects.equals(accommodation.getAvailableEnd(), availableEnd)) {
            accommodationDAO.updateAvailableEnd(idAcc, availableEnd);
            accommodation.setAvailableEnd(availableEnd);
        }

        if (!Objects.equals(accommodation.getDescription(), description)) {
            accommodationDAO.updateDescription(idAcc, description);
            accommodation.setDescription(description);
        }

        if (accommodation.isFreewifi() != freewifi) {
            accommodationDAO.updateFreewifi(idAcc, freewifi);
            accommodation.setFreewifi(freewifi);
        }

        if (accommodation.isHaveSmokingArea() != haveSmokingArea) {
            accommodationDAO.updateHaveSmokingArea(idAcc, haveSmokingArea);
            accommodation.setHaveSmokingArea(haveSmokingArea);
        }

        if (accommodation.isHaveParking() != haveParking) {
            accommodationDAO.updateHaveParking(idAcc, haveParking);
            accommodation.setHaveParking(haveParking);
        }

        if (accommodation.isCoffeMachine() != coffeMachine) {
            accommodationDAO.updateCoffeMachine(idAcc, coffeMachine);
            accommodation.setCoffeMachine(coffeMachine);
        }

        if (accommodation.isRoomService() != roomService) {
            accommodationDAO.updateRoomService(idAcc, roomService);
            accommodation.setRoomService(roomService);
        }

        if (accommodation.isCleaningService() != cleaningService) {
            accommodationDAO.updateCleaningService(idAcc, cleaningService);
            accommodation.setCleaningService(cleaningService);
        }

        if (accommodation.isHaveSpa() != haveSpa) {
            accommodationDAO.updateHaveSpa(idAcc, haveSpa);
            accommodation.setHaveSpa(haveSpa);
        }

        if (accommodation.isGoodForKids() != goodForKids) {
            accommodationDAO.updateGoodForKids(idAcc, goodForKids);
            accommodation.setGoodForKids(goodForKids);
        }

        if (accommodation.getNumberOfRoom() != numberOfRoom) {
            accommodationDAO.updateNumberOfRoom(idAcc, numberOfRoom);
            accommodation.setNumberOfRoom(numberOfRoom);
        }

        if (accommodation.isWelcomeAnimal() != welcomeAnimal) {
            accommodationDAO.updateWelcomeAnimal(idAcc, welcomeAnimal);
            accommodation.setWelcomeAnimal(welcomeAnimal);
        }

        if (accommodation.getMaxNumberOfPeople() != maxNumberOfPeople) {
            accommodationDAO.updateMaxNumberOfPeople(idAcc, maxNumberOfPeople);
            accommodation.setMaxNumberOfPeople(maxNumberOfPeople);
        }
    }

 // teniamo conto che se la disponibilità è uguale a zero allora darà errore inoltre quando creo un accommodation di default avrà una stella
    public void addAccommodation(String name, String address, String place, int disponibility, AccommodationType type, float ratePrice, LocalDateTime availableFrom, LocalDateTime availableEnd, String description, boolean refundable, boolean freewifi, boolean haveSmokingArea, boolean haveParking, boolean coffeMachine, boolean roomService, boolean cleaningService, boolean haveSpa, boolean goodForKids, int numberOfRoom, boolean welcomeAnimal, int maxPeople ){
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        AccommodationRating rating= AccommodationRating.OneStar;
        accommodationDAO.addAccommodation( name,  address,  place,  disponibility, type,  ratePrice,  availableFrom, availableEnd, description, rating, refundable,  freewifi,  haveSmokingArea, haveParking,  coffeMachine,  roomService, cleaningService,  haveSpa,  goodForKids, numberOfRoom,  welcomeAnimal, maxPeople);
        
    }
    
    public void removeUser(int idUser) throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO();
        userDAO.removeUser(idUser);
    }
    
    public RegisterUser searchUser(int idUser) throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO();
        return userDAO.getUserById(idUser);
    }

    public ArrayList<Accommodation> getAllAccomodation() throws SQLException, ClassNotFoundException {
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        return accommodationDAO.getAllAccommodation();
    }

    public ArrayList<RegisterUser> getAllUser() throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO();
        return userDAO.getAllUsers();
    }

    public ArrayList<Review> getReviewByUser(RegisterUser user){
        ReviewDAO reviewDAO = new ReviewDAO();
        return reviewDAO.getReviewByUser(user);
    }

    public ArrayList<Review> getReviewByAccomodation(Accommodation accommodation){
        ReviewDAO reviewDAO = new ReviewDAO();
        return reviewDAO.getReviewByAccomodation(accommodation);
    }

    public Accommodation getAccomodationById(int id){
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        return accommodationDAO.getAccommodationByID(id);
    }

    public void exit(){
        isLoggedIn = false;
    }

    //fixme non so se tenerla è praticamente inutile e difficile da implementare
//    public void updatePassword(String newPassword) throws SQLException, ClassNotFoundException {
//        String emailAdmin="admin@gmail.com";
//        UserDAO userDAO = new UserDAO();
//        userDAO.updatePassword(emailAdmin, newPassword, isLoggedIn);
//    }

    public boolean loginAdmin(String password) throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO();
        isLoggedIn = userDAO.getAdminByPassword(password);
        return isLoggedIn;
    }
}
