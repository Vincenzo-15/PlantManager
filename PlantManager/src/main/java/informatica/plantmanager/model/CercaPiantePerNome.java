package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CercaPiantePerNome extends Service<List<String>> {

    private String searchQuery;

    // Setter per il parametro di ricerca
    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    @Override
    protected Task<List<String>> createTask() {
        return new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                List<String> names = new ArrayList<>();
                // La query usa LIKE per cercare corrispondenze parziali (case-sensitive, eventualmente potresti usare COLLATE NOCASE)
                String query = "SELECT Nome FROM Piante WHERE Nome LIKE ? COLLATE NOCASE";
                Connection conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    System.err.println("Errore: connessione al database non disponibile.");
                    return names;
                }
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    // Utilizza i caratteri jolly % per cercare corrispondenze parziali
                    stmt.setString(1, "%" + searchQuery + "%");
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            names.add(rs.getString("Nome"));
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return names;
            }
        };
    }
}

