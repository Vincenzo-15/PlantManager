package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VerificaUtente extends Service<Boolean> {

    private String email;

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<>() {
            @Override
            protected Boolean call() {
                String query = "SELECT * FROM utenti WHERE email = ?";
                Connection conn = DatabaseConnection.getConnection();
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, email);
                    try (ResultSet rs = stmt.executeQuery()) {
                        return rs.next(); // Restituisce true se l'email è già registrata
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };
    }
}
