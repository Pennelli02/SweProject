package DAO;

import java.sql.*;


public class DBUtils {

    //Stampa tutti i dettagli dell'eccezione SQL, comprese le eventuali eccezioni concatenate.
    public static void printSQLException(SQLException ex) {
        while (ex != null) {
            System.err.println("SQL Error Message: " + ex.getMessage());
            System.err.println("SQL State: " + ex.getSQLState());
            System.err.println("Error Code: " + ex.getErrorCode());
            System.err.println("------------------------------");
            ex = ex.getNextException();
        }
    }


    //  Chiude in sicurezza un oggetto AutoCloseable (es. PreparedStatement, ResultSet).

    public static void closeQuietly(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                System.err.println("Errore durante la chiusura risorsa: " + e.getMessage());
            }
        }
    }


}
