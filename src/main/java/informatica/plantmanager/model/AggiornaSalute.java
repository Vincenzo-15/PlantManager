package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AggiornaSalute extends Service<Boolean> {

    private String plantId;
    private int saluteValue;

    public void setParameters(String plantId, int saluteValue) {
        this.plantId = plantId;
        this.saluteValue = saluteValue;
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
                String query = "UPDATE PianteUtente SET Salute = ? WHERE Id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, saluteValue);
                    stmt.setString(2, plantId);
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

