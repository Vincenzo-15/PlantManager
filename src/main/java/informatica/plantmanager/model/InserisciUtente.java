package informatica.plantmanager.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class InserisciUtente {

    public static boolean inserisciUtente(Utente utente) {
        String queryUtente = "INSERT INTO utenti (id, email, nickname, password) VALUES (?, ?, ?, ?)";
        String queryImpostazioni = "INSERT INTO ImpostazioniUtente (Id, UtenteId, Tema, Font) VALUES (?, ?, ?, ?)";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.err.println("Errore: Connessione al database non disponibile.");
            return false;
        }

        try {
            conn.setAutoCommit(false);

            String utenteId = UUID.randomUUID().toString();
            String impostazioniId = UUID.randomUUID().toString();

            try (PreparedStatement stmtUtente = conn.prepareStatement(queryUtente)) {
                stmtUtente.setString(1, utenteId);
                stmtUtente.setString(2, utente.getEmail());
                stmtUtente.setString(3, utente.getNickname());
                stmtUtente.setString(4, utente.getPassword());
                stmtUtente.executeUpdate();
            }

            try (PreparedStatement stmtImpostazioni = conn.prepareStatement(queryImpostazioni)) {
                stmtImpostazioni.setString(1, impostazioniId);
                stmtImpostazioni.setString(2, utenteId);
                stmtImpostazioni.setString(3, "Chiaro"); // Tema di default
                stmtImpostazioni.setString(4, "Poppins");  // Font di default
                stmtImpostazioni.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}