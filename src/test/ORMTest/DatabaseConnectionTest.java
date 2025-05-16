package test.ORMTest;

import ORM.DatabaseConnection;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    @Test
    void testSingletonInstanceNotNull() {
        DatabaseConnection instance = assertDoesNotThrow(DatabaseConnection::getInstance);
        assertNotNull(instance);
    }

    @Test
    void testSingletonIsSameInstance() {
        DatabaseConnection instance1 = DatabaseConnection.getInstance();
        DatabaseConnection instance2 = DatabaseConnection.getInstance();
        assertSame(instance1, instance2, "Le istanze dovrebbero essere le stesse (singleton)");
    }

    @Test
    void testConnectionIsValid() throws SQLException {
        Connection connection = assertDoesNotThrow(() -> DatabaseConnection.getInstance().getConnection());
        assertNotNull(connection);
        assertFalse(connection.isClosed(), "La connessione non dovrebbe essere chiusa");
    }
}