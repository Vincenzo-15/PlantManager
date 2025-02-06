package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CaricaPianteUtentePopup extends Service<List<PlantComboItem>> {

    private String userId;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    protected Task<List<PlantComboItem>> createTask() {
        return new Task<List<PlantComboItem>>() {
            @Override
            protected List<PlantComboItem> call() throws Exception {
                List<PlantComboItem> items = new ArrayList<>();
                Connection conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    System.err.println("Connessione non disponibile.");
                    return items;
                }

                String query = "SELECT pu.Id AS plantUtenteId, p.Nome, pu.Posizione " +
                        "FROM Piante p " +
                        "JOIN PianteUtente pu ON p.Id = pu.PiantaId " +
                        "WHERE pu.UtenteId = ? " +
                        "AND pu.Id NOT IN (SELECT PiantaUtenteId FROM LayoutPianteDashboard)";

                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, userId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            String plantUtenteId = rs.getString("plantUtenteId");
                            String nome = rs.getString("Nome");
                            String posizione = rs.getString("Posizione");
                            items.add(new PlantComboItem(plantUtenteId, nome, posizione));
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return items;
            }
        };
    }
}
