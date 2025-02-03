package informatica.plantmanager.model;

import informatica.plantmanager.model.Pianta;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CaricaPianteUtente {

    public static Service<List<Pianta>> getPlantsByUserService(String userId) {
        return new Service<List<Pianta>>() {
            @Override
            protected Task<List<Pianta>> createTask() {
                return new Task<List<Pianta>>() {
                    @Override
                    protected List<Pianta> call() throws Exception {
                        List<Pianta> plants = new ArrayList<>();
                        // La query esegue una join tra la tabella Piante e PianteUtente
                        String query = "SELECT p.Id, p.Nome, p.Acqua, p.Luce, p.Umidita, p.Temperatura, p.PH_terreno, p.Percorso_immagine " +
                                "FROM Piante p " +
                                "JOIN PianteUtente pu ON p.Id = pu.PiantaId " +
                                "WHERE pu.UtenteId = ?";
                        Connection conn = DatabaseConnection.getConnection();
                        if (conn == null) {
                            System.err.println("Connessione non disponibile.");
                            return plants;
                        }
                        try (PreparedStatement stmt = conn.prepareStatement(query)) {
                            stmt.setString(1, userId);
                            try (ResultSet rs = stmt.executeQuery()) {
                                while (rs.next()) {
                                    String id = rs.getString("Id");
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
}

