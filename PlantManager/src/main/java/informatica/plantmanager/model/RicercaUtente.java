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
    private Utente user;

    public void setLoginCredentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Utente getUser() {
        return user;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String query = "SELECT * FROM Utenti WHERE Email = ?";
                Connection conn = DatabaseConnection.getConnection();
                if(conn == null){
                    System.err.println("Connessione non disponibile.");
                    return false;
                }
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, email);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            String hashedPassword = rs.getString("password");
                            if (BCrypt.checkpw(password, hashedPassword)) {

                                String id = rs.getString("Id");
                                String nickname = rs.getString("Nickname");
                                String emailDB = rs.getString("Email");

                                user = new Utente(nickname, emailDB, hashedPassword);
                                user.setId(id);
                                return true;
                            } else {
                                return false;
                            }
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


