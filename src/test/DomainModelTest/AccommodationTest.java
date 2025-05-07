package test.DomainModelTest;
import DomainModel.Accommodation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AccommodationTest {
    //testiamo principalmente solo il funzionamento del dirtyflag

    @Test
    void dirtyFlagTracksAndClears() {
        Accommodation acc = new Accommodation();
        // 1) partiamo puliti
        assertFalse(acc.isFieldModified("name"));
        assertFalse(acc.isFieldModified("ratePrice"));
        assertTrue(acc.getModifiedFields().isEmpty());

        // 2) cambio un campo
        acc.setName("Hotel Test");
        assertTrue(acc.isFieldModified("name"));
        assertFalse(acc.isFieldModified("ratePrice"));
        assertFalse(acc.getModifiedFields().isEmpty());
        assertTrue(acc.getModifiedFields().contains("name"));

        // 3) clear
        acc.clearModifiedFields();
        assertFalse(acc.isFieldModified("name"));
        assertTrue(acc.getModifiedFields().isEmpty());
    }

}