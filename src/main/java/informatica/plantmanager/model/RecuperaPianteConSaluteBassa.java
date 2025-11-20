package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecuperaPianteConSaluteBassa extends Service<List<PlantAlert>> {

    private String utenteId;
    private int soglia = 25;

    public void setUtenteId(String utenteId) {
        this.utenteId = utenteId;
    }

    public void setSoglia(int soglia) {
        this.soglia = soglia;
    }

    @Override
    protected Task<List<PlantAlert>> createTask() {
        return new Task<>() {
            @Override
            protected List<PlantAlert> call() throws Exception {
                List<PlantAlert> alerts = new ArrayList<>();
                String query = """
                        SELECT pu.Id AS PiantaUtenteId, p.Nome, pu.Posizione, pu.Salute
                        FROM PianteUtente pu
                        JOIN Piante p ON pu.PiantaId = p.Id
                        WHERE pu.UtenteId = ? AND pu.Salute <= ?
                        """;
                Connection conn = DatabaseConnection.getConnection();
                if (conn == null) return alerts;

                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, utenteId);
                    stmt.setInt(2, soglia);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            String puId = rs.getString("PiantaUtenteId");
                            String nome = rs.getString("Nome");
                            String posizione = rs.getString("Posizione");
                            int salute = rs.getInt("Salute");
                            String suggerimento = suggerimentoPerSalute(salute);
                            alerts.add(new PlantAlert(puId, nome, posizione, salute, suggerimento));
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return alerts;
            }
        };
    }

    private String suggerimentoPerSalute(int salute) {
        if (salute <= 10) return "Innaffia immediatamente e controlla il terreno.";
        if (salute <= 25) return "Aumenta l'irrigazione o sposta la pianta alla luce.";
        return "Controlla parametri ambientali.";
    }
}

