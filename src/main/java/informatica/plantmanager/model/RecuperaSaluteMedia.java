package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecuperaSaluteMedia extends Service<Integer> {

    private String utenteId;

    public void setUtenteId(String utenteId) {
        this.utenteId = utenteId;
    }

    @Override
    public Task<Integer> createTask() {
        return new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                List<Integer> saluteList = new ArrayList<>();
                Connection conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    System.err.println("Connessione non disponibile.");
                    return 0;
                }
                String query = "SELECT Salute FROM PianteUtente WHERE UtenteId = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, utenteId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            saluteList.add(rs.getInt("Salute"));
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (saluteList.isEmpty()) {
                    return 0;
                }
                int sum = 0;
                for (Integer valore : saluteList) {
                    sum += valore;
                }
                return sum / saluteList.size();
            }
        };
    }
}
