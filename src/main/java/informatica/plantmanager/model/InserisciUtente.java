package informatica.plantmanager.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class InserisciUtente {

    public static boolean inserisciUtente(Utente utente) {
        // Query per inserire un nuovo utente
        String queryUtente = "INSERT INTO utenti (id, email, nickname, password) VALUES (?, ?, ?, ?)";
        // Query per creare un'istanza di ImpostazioniUtente legata all'utente
        String queryImpostazioni = "INSERT INTO ImpostazioniUtente (Id, UtenteId, Tema, Font) VALUES (?, ?, ?, ?)";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.err.println("Errore: Connessione al database non disponibile.");
            return false;
        }

        try {
            conn.setAutoCommit(false); // Inizia la transazione

            // Genera gli ID univoci
            String utenteId = UUID.randomUUID().toString();
            String impostazioniId = UUID.randomUUID().toString();

            // Inserimento utente
            try (PreparedStatement stmtUtente = conn.prepareStatement(queryUtente)) {
                stmtUtente.setString(1, utenteId);
                stmtUtente.setString(2, utente.getEmail());
                stmtUtente.setString(3, utente.getNickname());
                stmtUtente.setString(4, utente.getPassword());
                stmtUtente.executeUpdate();
            }

            // Creazione delle impostazioni utente con valori di default
            try (PreparedStatement stmtImpostazioni = conn.prepareStatement(queryImpostazioni)) {
                stmtImpostazioni.setString(1, impostazioniId);
                stmtImpostazioni.setString(2, utenteId);
                stmtImpostazioni.setString(3, "Chiaro"); // Tema di default
                stmtImpostazioni.setString(4, "Poppins");  // Font di default
                stmtImpostazioni.executeUpdate();
            }

            conn.commit(); // Conferma le modifiche se tutto è andato bene
            return true;
        } catch (SQLException e) {
            try {
                conn.rollback(); // Annulla tutto se c'è un errore
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.setAutoCommit(true); // Ripristina l'auto-commit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}