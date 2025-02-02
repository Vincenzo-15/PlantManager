package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class InserisciPianteUtente extends Service<Boolean> {

    private String plantName;
    private String position;
    private Utente utente;

    public void setParameters(String plantName, String position, Utente utente) {
        this.plantName = plantName;

        this.position = position.trim().toLowerCase();
        this.utente = utente;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                Connection conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    System.err.println("Errore: connessione al database non disponibile.");
                    return false;
                }


                String plantId = null;
                String querySelect = "SELECT Id FROM Piante WHERE Nome = ? COLLATE NOCASE";
                try (PreparedStatement stmtSelect = conn.prepareStatement(querySelect)) {
                    stmtSelect.setString(1, plantName);
                    try (ResultSet rs = stmtSelect.executeQuery()) {
                        if (rs.next()) {
                            plantId = rs.getString("Id");
                        } else {
                            System.err.println("Nessuna pianta trovata con nome: " + plantName);
                            return false;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }


                String queryInsert = "INSERT INTO PianteUtente (Id, UtenteId, PiantaId, Posizione) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmtInsert = conn.prepareStatement(queryInsert)) {

                    String newId = UUID.randomUUID().toString();
                    stmtInsert.setString(1, newId);

                    stmtInsert.setString(2, utente.getId());
                    stmtInsert.setString(3, plantId);
                    stmtInsert.setString(4, position);

                    int rowsAffected = stmtInsert.executeUpdate();
                    return rowsAffected > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };
    }
}

