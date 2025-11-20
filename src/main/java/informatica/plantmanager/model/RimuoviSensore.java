package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RimuoviSensore extends Service<Double> {

    private String piantaUtenteId;
    private String sensoreId;

    public void setParameters(String piantaUtenteId, String sensoreId) {
        this.piantaUtenteId = piantaUtenteId;
        this.sensoreId = sensoreId.toLowerCase();
    }

    @Override
    protected Task<Double> createTask() {
        return new Task<Double>() {
            @Override
            protected Double call() throws Exception {
                System.out.println("Rimuovi sensore: " + sensoreId + " dalla pianta: " + piantaUtenteId);
                Connection conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    System.err.println("Connessione non disponibile.");
                    return -1.0;
                }
                String query = "DELETE FROM SensoriPianta WHERE PiantaUtenteId = ? AND SensoreId = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, piantaUtenteId);
                    stmt.setString(2, sensoreId);
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return 1.0;
            }
        };
    }
}

