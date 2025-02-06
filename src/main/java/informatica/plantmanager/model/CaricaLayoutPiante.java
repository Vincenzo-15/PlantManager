package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CaricaLayoutPiante extends Service<List<PlantComboItem>> {

    private String utenteId;

    public void setUtenteId(String utenteId) {
        this.utenteId = utenteId;
    }

    @Override
    protected Task<List<PlantComboItem>> createTask() {
        return new Task<List<PlantComboItem>>() {
            @Override
            protected List<PlantComboItem> call() throws Exception {
                List<PlantComboItem> lista = new ArrayList<>();
                Connection conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    System.err.println("Connessione non disponibile.");
                    return lista;
                }
                // La query recupera:
                // - L'id della riga della tabella PianteUtente (plantUtenteId)
                // - Il nome della pianta (da Piante)
                // - La posizione salvata (PosizioneGriglia)
                String query = "SELECT lp.PiantaUtenteId, p.Nome, lp.PosizioneGriglia " +
                        "FROM LayoutPianteDashboard lp " +
                        "JOIN ImpostazioniUtente i ON lp.ImpostazioniUtenteId = i.Id " +
                        "JOIN PianteUtente pu ON lp.PiantaUtenteId = pu.Id " +
                        "JOIN Piante p ON pu.PiantaId = p.Id " +
                        "WHERE i.UtenteId = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, utenteId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            String plantUtenteId = rs.getString("PiantaUtenteId");
                            String nome = rs.getString("Nome");
                            String posizione = rs.getString("PosizioneGriglia");
                            lista.add(new PlantComboItem(plantUtenteId, nome, posizione));
                        }
                    }
                }
                return lista;
            }
        };
    }
}
