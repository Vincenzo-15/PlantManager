package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CaricaSensoriPianta extends Service<List<SensorePianta>> {

    private String plantId;

    public void setPlantId(String plantId) {
        this.plantId = plantId;
    }

    @Override
    protected Task<List<SensorePianta>> createTask() {
        return new Task<List<SensorePianta>>() {
            @Override
            protected List<SensorePianta> call() throws Exception {
                List<SensorePianta> sensori = new ArrayList<>();
                Connection conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    System.err.println("Connessione non disponibile.");
                    return sensori;
                }
                String query = "SELECT Id, SensoreId, posizioneGriglia FROM SensoriPianta WHERE PiantaUtenteId = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, plantId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            String id = rs.getString("Id");
                            String sensoreId = rs.getString("SensoreId");
                            String posizioneGriglia = rs.getString("posizioneGriglia");
                            // Crea un oggetto SensorePianta (adatta il costruttore in base alla tua implementazione)
                            SensorePianta sp = new SensorePianta(id, sensoreId, plantId, posizioneGriglia);
                            sensori.add(sp);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return sensori;
            }
        };
    }
}

