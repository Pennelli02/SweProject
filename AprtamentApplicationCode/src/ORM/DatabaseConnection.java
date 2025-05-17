package ORM;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Istanza singleton
    private static DatabaseConnection instance;

    // Connessione al database
    private Connection connection;


    // Costruttore privato per prevenire l'istanziazione diretta
    private DatabaseConnection() {
        try {
            // Parametri di connessione (da personalizzare)
            String DB_URL = "jdbc:postgresql://localhost:5432/ApartamentDB";
            String DB_USER = "postgres";
            String DB_PASSWORD = "postgres";

            // Carica il driver JDBC
            Class.forName("org.postgresql.Driver");

            // Crea la connessione
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connessione al database stabilita con successo!");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC non trovato!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Errore durante la connessione al database!");
            e.printStackTrace();
        }
    }

    // Metodo per ottenere l'istanza singleton
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // Metodo per ottenere la connessione al database
    public Connection getConnection() {
        return connection;
    }

   // Metodo per chiudere la connessione
//    public void closeConnection() {
//        try {
//            if (connection != null && !connection.isClosed()) {
//                connection.close();
//                System.out.println("Connessione al database chiusa con successo!");
//            }
//        } catch (SQLException e) {
//            System.err.println("Errore durante la chiusura della connessione!");
//            e.printStackTrace();
//        }
//    }
}
