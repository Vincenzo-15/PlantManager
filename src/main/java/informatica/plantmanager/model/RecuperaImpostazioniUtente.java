package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RecuperaImpostazioniUtente extends Service<ImpostazioniUtente> {

    private String userId;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    protected Task<ImpostazioniUtente> createTask() {
        return new Task<>() {
            @Override
            protected ImpostazioniUtente call() throws Exception {
                String query = "SELECT Tema, Notifiche FROM ImpostazioniUtente WHERE UtenteId = ?";
                Connection conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    System.err.println("Connessione non disponibile.");
                    return null;
                }
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, userId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            String tema = rs.getString("Tema");
                            boolean notifiche = rs.getBoolean("Notifiche");
                            return new ImpostazioniUtente(tema, notifiche);
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
