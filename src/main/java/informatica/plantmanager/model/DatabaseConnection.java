//package informatica.plantmanager.model;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//public class DatabaseConnection {
//
//    private static final String URL = "jdbc:sqlite:C:\\Users\\vi200\\OneDrive\\Desktop\\Progetto-2024-2025-Merli-Calabrò\\PlantManager\\database.sqlite";
//    private static Connection connection;
//
//    public static void initialize() {
//        try {
//            if (connection == null || connection.isClosed()) {
//                connection = DriverManager.getConnection(URL);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static Connection getConnection() {
//        return connection;
//    }
//
//    public static void closeConnection() {
//        try {
//            if (connection != null && !connection.isClosed()) {
//                connection.close();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//}

package informatica.plantmanager.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:C:\\Users\\vi200\\OneDrive\\Desktop\\Progetto-2024-2025-Merli-Calabrò\\PlantManager\\database.sqlite";
    private static Connection connection;

    /**
     * Ritorna una connessione sempre valida al database.
     * Se la connessione è chiusa o nulla, la ricrea automaticamente.
     */
    public static synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL);
                System.out.println("🔗 Connessione al database (ri)creata.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Chiude la connessione solo se strettamente necessario (es. chiusura app).
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("❌ Connessione al database chiusa.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


