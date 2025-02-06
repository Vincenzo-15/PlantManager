package informatica.plantmanager.model;

import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecuperaMisurazioni {
    private String utenteId;

    public void setUtenteId(String utenteId) {
        this.utenteId = utenteId;
    }

    public Task<List<Misurazione>> createTask() {
        return new Task<>() {
            @Override
            protected List<Misurazione> call() throws Exception {
                List<Misurazione> misurazioni = new ArrayList<>();

                String query = """
    SELECT
        m.Data_e_ora,
        m.Valore,
        s.Nome AS TipoSensore,
        p.Nome AS NomePianta
    FROM Misurazioni m
    JOIN SensoriPianta sp ON m.SensoriPiantaId = sp.Id
    JOIN Sensori s ON sp.SensoreId = s.Id
    JOIN PianteUtente pu ON sp.PiantaUtenteId = pu.Id
    JOIN Piante p ON pu.PiantaId = p.Id
    WHERE pu.UtenteId = ?
      AND s.Nome IN ('Umidita', 'Temperatura', 'Acqua')
      AND pu.Id IN (
          SELECT Id FROM PianteUtente WHERE UtenteId = ? LIMIT 5
      )
    ORDER BY pu.Id, m.Data_e_ora ASC;
""";

                Connection conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    System.err.println("Connessione non disponibile.");
                    return null;
                }

                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, utenteId);
                    stmt.setString(2, utenteId);
                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        Misurazione misurazione = new Misurazione(
                                rs.getString("NomePianta"),
                                rs.getString("TipoSensore"),
                                rs.getDouble("Valore"),
                                rs.getTimestamp("Data_e_ora").toLocalDateTime()
                        );
                        misurazioni.add(misurazione);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return misurazioni;
            }
        };
    }
}