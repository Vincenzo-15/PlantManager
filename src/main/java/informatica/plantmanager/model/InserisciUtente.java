package informatica.plantmanager.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class InserisciUtente {

    public static boolean inserisciUtente(Utente utente) {
        // Query aggiornata per includere l'ID UUID
        String query = "INSERT INTO utenti (id, email, nickname, password) VALUES (?, ?, ?, ?)";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.err.println("Errore: Connessione al database non disponibile.");
            return false;
        }

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Genera un nuovo UUID per l'utente
            String id = UUID.randomUUID().toString();
            stmt.setString(1, id); // Imposta l'ID
            stmt.setString(2, utente.getEmail());
            stmt.setString(3, utente.getNickname());
            stmt.setString(4, utente.getPassword());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Ritorna true se l'inserimento è avvenuto con successo
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

