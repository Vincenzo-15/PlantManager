package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RecuperaValoriConsigliati extends Service<Double> {

    private String piantaUtenteId;
    private String sensorName;

    public void setParameters(String piantaUtenteId, String sensorName) {
        this.piantaUtenteId = piantaUtenteId;
        this.sensorName = sensorName.toLowerCase();
    }

    @Override
    protected Task<Double> createTask() {
        return new Task<Double>() {
            @Override
            protected Double call() throws Exception {
                Connection conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    System.err.println("Connessione non disponibile.");
                    return null;
                }
                // Aggiornata la query per includere anche la colonna Vento dalla tabella Piante.
                String query = "SELECT p.Acqua, p.Luce, p.Umidita, p.Temperatura, p.PH_terreno, p.Vento " +
                        "FROM Piante p " +
                        "JOIN PianteUtente pu ON p.Id = pu.PiantaId " +
                        "WHERE pu.Id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, piantaUtenteId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            switch (sensorName) {
                                case "acqua":
                                    return rs.getDouble("Acqua");
                                case "luce":
                                    return rs.getDouble("Luce");
                                case "umidita":
                                    return rs.getDouble("Umidita");
                                case "temperatura":
                                    return rs.getDouble("Temperatura");
                                case "ph":
                                    return rs.getDouble("PH_terreno");
                                case "vento":
                                    return rs.getDouble("Vento");
                                default:
                                    return null;
                            }
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
