package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class AggiungiSensori extends Service<Boolean> {

    private String pianteUtenteId;
    private String sensoreId;
    private String posizioneGriglia;

    public void setParameters(String pianteUtenteId, String sensoreId, String posizioneGriglia) {
        this.pianteUtenteId = pianteUtenteId;
        this.sensoreId = sensoreId;
        this.posizioneGriglia = posizioneGriglia;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                Connection conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    System.err.println("Connessione non disponibile.");
                    return false;
                }
                String query = "INSERT INTO SensoriPianta (Id, PiantaUtenteId, SensoreId, posizioneGriglia) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    String newId = UUID.randomUUID().toString();
                    stmt.setString(1, newId);
                    stmt.setString(2, pianteUtenteId);
                    stmt.setString(3, sensoreId);
                    stmt.setString(4, posizioneGriglia);
                    int rowsAffected = stmt.executeUpdate();
                    return rowsAffected > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };
    }
}
