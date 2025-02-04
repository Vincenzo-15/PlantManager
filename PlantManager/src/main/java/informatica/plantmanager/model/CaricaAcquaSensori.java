package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CaricaAcquaSensori extends Service<List<Double>> {

    private String userId;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    protected Task<List<Double>> createTask() {
        return new Task<>() {
            @Override
            protected List<Double> call() throws Exception {
                List<Double> waterValues = new ArrayList<>();
                String query = "SELECT m.Valore " +
                        "FROM Misurazioni m " +
                        "JOIN SensoriPianta sp ON m.SensorePiantaId = sp.Id " +
                        "JOIN Sensori s ON sp.SensoreId = s.Id " +
                        "JOIN PianteUtente pu ON sp.PiantaUtenteId = pu.Id " +
                        "WHERE pu.UtenteId = ? AND s.Nome = 'acqua'";
                Connection conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    System.err.println("Errore: connessione al database non disponibile.");
                    return waterValues;
                }
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, userId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            waterValues.add(rs.getDouble("Valore"));
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return waterValues;
            }
        };
    }
}
