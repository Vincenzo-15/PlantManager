package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RecuperaDatiPiante extends Service<DatiPiante> {

    private String plantId;  // ID della riga in PianteUtente

    public void setPlantId(String plantId) {
        this.plantId = plantId;
    }

    @Override
    protected Task<DatiPiante> createTask() {
        return new Task<DatiPiante>() {
            @Override
            protected DatiPiante call() throws Exception {
                Connection conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    System.err.println("Connessione non disponibile.");
                    return null;
                }
                String query = "SELECT pu.Salute, pu.Acqua, pu.Luce, p.Percorso_immagine " +
                        "FROM PianteUtente pu " +
                        "JOIN Piante p ON pu.PiantaId = p.Id " +
                        "WHERE pu.Id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, plantId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            int salute = rs.getInt("Salute");
                            double acqua = rs.getDouble("Acqua");
                            double luce = rs.getDouble("Luce");
                            String percorsoImmagine = rs.getString("Percorso_immagine");
                            //System.out.println("Salute: " + salute + ", Acqua: " + acqua + ", Luce: " + luce + ", Immagine: " + percorsoImmagine);
                            return new DatiPiante(salute, acqua, luce, percorsoImmagine);
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

