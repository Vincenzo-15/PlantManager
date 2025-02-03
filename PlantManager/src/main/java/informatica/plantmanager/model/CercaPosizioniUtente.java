package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CercaPosizioniUtente extends Service<List<String>> {

    private String userId;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    protected Task<List<String>> createTask() {
        return new Task<List<String>>() {
            @Override
            protected List<String> call() throws Exception {
                List<String> posizioni = new ArrayList<>();
                Connection conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    System.err.println("Connessione non disponibile.");
                    return posizioni;
                }
                // Query per selezionare le posizioni distinte per l'utente
                String query = "SELECT DISTINCT Posizione FROM PianteUtente WHERE UtenteId = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, userId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            String pos = rs.getString("Posizione");
                            if (pos != null && !pos.trim().isEmpty()) {
                                posizioni.add(pos);
                            }
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return posizioni;
            }
        };
    }
}

