package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AggiornaTema extends Service<Boolean> {

    private String utenteId;
    private String nuovoTema;
    private boolean notificheAttive;

    /**
     * Metodo per impostare le nuove preferenze utente
     */
    public void setTema(String utenteId, String nuovoTema, boolean notificheAttive) {
        this.utenteId = utenteId;
        this.nuovoTema = nuovoTema;
        this.notificheAttive = notificheAttive;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String query = "UPDATE ImpostazioniUtente SET Tema = ?, Notifiche = ? WHERE UtenteId = ?";
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = null;

                try {
                    stmt = conn.prepareStatement(query);
                    stmt.setString(1, nuovoTema);
                    stmt.setBoolean(2, notificheAttive);
                    stmt.setString(3, utenteId);

                    int rowsUpdated = stmt.executeUpdate();
                    return rowsUpdated > 0;

                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;

                } finally {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
    }
}
