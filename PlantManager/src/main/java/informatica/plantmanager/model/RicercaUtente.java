package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RicercaUtente extends Service<Boolean> {

    private String email;
    private String password;

    public void setLoginCredentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String query = "SELECT * FROM utenti WHERE email = ? AND password = ?";
                Connection conn = DatabaseConnection.getConnection();
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, email);
                    stmt.setString(2, password);

                    try (ResultSet rs = stmt.executeQuery()) {
                        return rs.next(); // Restituisce true se l'utente esiste
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };
    }
}

