package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SalvaLayoutPianta extends Service<Void> {

    private String utenteId;
    private String piantaUtenteId;
    private String posizioneGriglia;

    public void setDati(String utenteId, String piantaUtenteId, int row, int col) {
        this.utenteId = utenteId;
        this.piantaUtenteId = piantaUtenteId;
        this.posizioneGriglia = row + "," + col;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                String queryRecupero = "SELECT Id FROM ImpostazioniUtente WHERE UtenteId = ?";
                String queryInserimento = "INSERT INTO LayoutPianteDashboard (Id, ImpostazioniUtenteId, PiantaUtenteId, PosizioneGriglia) " +
                        "VALUES (?, ?, ?, ?)";

                Connection conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    throw new RuntimeException("Errore: Connessione al database non disponibile.");
                }

                conn.setAutoCommit(false);

                String impostazioniUtenteId = null;

                try {
                    try (PreparedStatement pstmtRecupero = conn.prepareStatement(queryRecupero)) {
                        pstmtRecupero.setString(1, utenteId);
                        try (ResultSet rs = pstmtRecupero.executeQuery()) {
                            if (rs.next()) {
                                impostazioniUtenteId = rs.getString("Id");
                            } else {
                                throw new RuntimeException("Errore: Nessuna impostazione utente trovata per l'utente ID: " + utenteId);
                            }
                        }
                    }
                    try (PreparedStatement pstmtInserimento = conn.prepareStatement(queryInserimento)) {
                        pstmtInserimento.setString(1, UUID.randomUUID().toString()); // Genera un ID univoco
                        pstmtInserimento.setString(2, impostazioniUtenteId);
                        pstmtInserimento.setString(3, piantaUtenteId);
                        pstmtInserimento.setString(4, posizioneGriglia);

                        pstmtInserimento.executeUpdate();
                    }

                    conn.commit();
                } catch (SQLException e) {
                    conn.rollback();
                    e.printStackTrace();
                    throw new RuntimeException("Errore nell'inserimento del layout della pianta", e);
                }
                return null;
            }
        };
    }
}

