package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RecuperaPosizionePianta extends Service<String> {

    private String plantId;

    public void setPlantId(String plantId) {
        this.plantId = plantId;
    }

    @Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {
                Connection conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    System.err.println("Connessione non disponibile.");
                    return null;
                }
                String query = "SELECT Posizione FROM PianteUtente WHERE Id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, plantId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            return rs.getString("Posizione");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
}
