package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.mindrot.jbcrypt.BCrypt;

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
                String query = "SELECT password FROM utenti WHERE email = ?";
                Connection conn = DatabaseConnection.getConnection();
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, email);

                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            String hashedPassword = rs.getString("password");
                            return BCrypt.checkpw(password, hashedPassword);
                        } else {
                            return false;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };
    }
}

