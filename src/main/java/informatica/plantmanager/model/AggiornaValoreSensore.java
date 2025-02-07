package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.*;

public class AggiornaValoreSensore extends Service<DatiMisurazioni> {

    private String piantaUtenteId;
    private String sensoreId;

    public void setParameters(String piantaUtenteId, String sensoreId) {
        this.piantaUtenteId = piantaUtenteId;
        this.sensoreId = sensoreId;
    }

    @Override
    public Task<DatiMisurazioni> createTask() {
        return new Task<DatiMisurazioni>() {
            @Override
            protected DatiMisurazioni call() throws Exception {
                Connection conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    System.err.println("Connessione non disponibile.");
                    return null;
                }
                String query = "SELECT M.Valore, S.Nome, S.Unita_di_misura " +
                        "FROM Misurazioni M " +
                        "JOIN SensoriPianta SP ON M.SensoriPiantaId = SP.Id " +
                        "JOIN Sensori S ON SP.SensoreId = S.Id " +
                        "WHERE SP.PiantaUtenteId = ? AND SP.SensoreId = ? " +
                        "ORDER BY M.Data_e_ora DESC LIMIT 1";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, piantaUtenteId);
                    stmt.setString(2, sensoreId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            double valore = rs.getDouble("Valore");
                            String nomeSensore = rs.getString("Nome");
                            String unitaMisura = rs.getString("Unita_di_misura");
                            System.out.println("Misurazione: " + valore + " " + unitaMisura + " per " + nomeSensore);
                            return new DatiMisurazioni(valore, nomeSensore, unitaMisura);
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
