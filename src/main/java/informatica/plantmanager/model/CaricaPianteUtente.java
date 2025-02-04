package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CaricaPianteUtente {

    public static Service<List<Pianta>> getPlantsByUserService(String userId, String posizioneFilter) {
        return new Service<List<Pianta>>() {
            @Override
            protected Task<List<Pianta>> createTask() {
                return new Task<List<Pianta>>() {
                    @Override
                    protected List<Pianta> call() throws Exception {
                        List<Pianta> plants = new ArrayList<>();
                        String query = "SELECT pu.Id AS PianteUtenteId, p.Nome, p.Acqua, p.Luce, p.Umidita, p.Temperatura, p.PH_terreno, p.Percorso_immagine " +
                                "FROM Piante p " +
                                "JOIN PianteUtente pu ON p.Id = pu.PiantaId " +
                                "WHERE pu.UtenteId = ?";

                        List<String> positions = new ArrayList<>();
                        if (posizioneFilter != null && !posizioneFilter.equalsIgnoreCase("Tutte")) {
                            String[] posArray = posizioneFilter.split(",");
                            for (String pos : posArray) {
                                positions.add(pos.trim());
                            }
                            query += " AND pu.Posizione IN (" + String.join(",", positions.stream().map(pos -> "?").toArray(String[]::new)) + ")";
                        }

                        Connection conn = DatabaseConnection.getConnection();
                        if (conn == null) {
                            System.err.println("Connessione non disponibile.");
                            return plants;
                        }
                        try (PreparedStatement stmt = conn.prepareStatement(query)) {
                            stmt.setString(1, userId);
                            for (int i = 0; i < positions.size(); i++) {
                                stmt.setString(i + 2, positions.get(i));
                            }
                            try (ResultSet rs = stmt.executeQuery()) {
                                while (rs.next()) {
                                    String id = rs.getString("PianteUtenteId");
                                    String nome = rs.getString("Nome");
                                    double acqua = rs.getDouble("Acqua");
                                    double luce = rs.getDouble("Luce");
                                    double umidita = rs.getDouble("Umidita");
                                    double temperatura = rs.getDouble("Temperatura");
                                    double phTerreno = rs.getDouble("PH_terreno");
                                    String percorsoImmagine = rs.getString("Percorso_immagine");
                                    plants.add(new Pianta(id, nome, acqua, luce, umidita, temperatura, phTerreno, percorsoImmagine));
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return plants;
                    }
                };
            }
        };
    }

    public static Service<List<Pianta>> getPlantsByUserService(String userId) {
        return getPlantsByUserService(userId, "Tutte");
    }
}