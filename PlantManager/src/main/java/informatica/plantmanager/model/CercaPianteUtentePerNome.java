package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CercaPianteUtentePerNome extends Service<List<Pianta>> {

    private String userId;
    private String nomeFilter;
    private String posizioneFilter;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setNomeFilter(String nomeFilter) {
        this.nomeFilter = nomeFilter;
    }

    public void setPosizioneFilter(String posizioneFilter) {
        this.posizioneFilter = posizioneFilter;
    }

    @Override
    protected Task<List<Pianta>> createTask() {
        return new Task<List<Pianta>>() {
            @Override
            protected List<Pianta> call() throws Exception {
                List<Pianta> plants = new ArrayList<>();
                Connection conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    System.err.println("Connessione non disponibile.");
                    return plants;
                }

                String query = "SELECT p.Id, p.Nome, p.Acqua, p.Luce, p.Umidita, p.Temperatura, p.PH_terreno, p.Percorso_immagine " +
                        "FROM Piante p " +
                        "JOIN PianteUtente pu ON p.Id = pu.PiantaId " +
                        "WHERE pu.UtenteId = ? AND p.Nome LIKE ? COLLATE NOCASE";
                if (posizioneFilter != null && !posizioneFilter.equalsIgnoreCase("Tutte") && !posizioneFilter.trim().isEmpty()) {
                    query += " AND pu.Posizione = ?";
                }
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, userId);
                    stmt.setString(2, "%" + nomeFilter + "%");
                    if (posizioneFilter != null && !posizioneFilter.equalsIgnoreCase("Tutte") && !posizioneFilter.trim().isEmpty()) {
                        stmt.setString(3, posizioneFilter);
                    }
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
}
