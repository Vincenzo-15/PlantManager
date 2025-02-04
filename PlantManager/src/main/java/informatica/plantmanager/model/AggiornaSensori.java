package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

public class AggiornaSensori extends Service<Boolean> {

    @Override
    public Task<Boolean> createTask() {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                Connection conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    System.err.println("Errore: connessione al database non disponibile.");
                    return false;
                }

                // Seleziona tutte le pianteUtente con almeno un sensore assegnato, recuperando anche UtenteId
                String queryPianteUtente = "SELECT Id, UtenteId FROM PianteUtente WHERE Id IN (SELECT DISTINCT PiantaUtenteId FROM SensoriPianta)";
                try (PreparedStatement stmtPU = conn.prepareStatement(queryPianteUtente);
                     ResultSet rsPU = stmtPU.executeQuery()) {

                    Random rand = new Random();

                    while (rsPU.next()) {
                        String puId = rsPU.getString("Id");
                        String utenteId = rsPU.getString("UtenteId"); // Recupera l'Utente associato

                        // Inizializza i valori dei sensori
                        Double acquaVal = null;
                        Double luceVal = null;
                        Double umiditaVal = null;
                        Double temperaturaVal = null;
                        Double phVal = null;

                        // Query per recuperare i sensori associati alla pianta
                        String querySensori = "SELECT SP.Id AS SPId, S.Nome " +
                                "FROM SensoriPianta SP JOIN Sensori S ON SP.SensoreId = S.Id " +
                                "WHERE SP.PiantaUtenteId = ?";
                        try (PreparedStatement stmtSensori = conn.prepareStatement(querySensori)) {
                            stmtSensori.setString(1, puId);
                            try (ResultSet rsSensori = stmtSensori.executeQuery()) {
                                while (rsSensori.next()) {
                                    String sensorePiantaId = rsSensori.getString("SPId");
                                    String funzione = rsSensori.getString("Nome").toLowerCase();

                                    double valoreRandom = 0;
                                    switch (funzione) {
                                        case "acqua":
                                            valoreRandom = 0.2 + (1.0 - 0.2) * rand.nextDouble();
                                            acquaVal = valoreRandom;
                                            break;
                                        case "luce":
                                            valoreRandom = 200 + rand.nextInt(601);
                                            luceVal = valoreRandom;
                                            break;
                                        case "umidita":
                                            valoreRandom = 30 + rand.nextInt(61);
                                            umiditaVal = valoreRandom;
                                            break;
                                        case "temperatura":
                                            valoreRandom = 15 + rand.nextInt(16);
                                            temperaturaVal = valoreRandom;
                                            break;
                                        case "ph":
                                            valoreRandom = 5.5 + (7.5 - 5.5) * rand.nextDouble();
                                            phVal = valoreRandom;
                                            break;
                                        default:
                                            break;
                                    }

                                    // Inserisce la misurazione nella tabella Misurazioni_temp
                                    String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                                    String insertMisurazione = "INSERT INTO Misurazioni (Id, Data_e_ora, Valore, UtenteId, SensoriPiantaId) VALUES (?, ?, ?, ?, ?)";
                                    try (PreparedStatement stmtInsert = conn.prepareStatement(insertMisurazione)) {
                                        String misId = UUID.randomUUID().toString();
                                        stmtInsert.setString(1, misId);
                                        stmtInsert.setString(2, now);
                                        stmtInsert.setDouble(3, valoreRandom); // Assicurati che valoreRandom sia impostato correttamente
                                        stmtInsert.setString(4, utenteId); // Associa la misurazione all'utente
                                        stmtInsert.setString(5, sensorePiantaId);
                                        stmtInsert.executeUpdate();
                                    }
                                }
                            }
                        }

                        // Aggiorna i valori della pianta con gli ultimi dati misurati
                        String updatePU = "UPDATE PianteUtente SET Acqua = COALESCE(?, Acqua), " +
                                "Luce = COALESCE(?, Luce), Umidita = COALESCE(?, Umidita), " +
                                "Temperatura = COALESCE(?, Temperatura), PH_terreno = COALESCE(?, PH_terreno) " +
                                "WHERE Id = ?";
                        try (PreparedStatement stmtUpdate = conn.prepareStatement(updatePU)) {
                            if (acquaVal != null) stmtUpdate.setDouble(1, acquaVal);
                            else stmtUpdate.setNull(1, Types.DOUBLE);

                            if (luceVal != null) stmtUpdate.setDouble(2, luceVal);
                            else stmtUpdate.setNull(2, Types.DOUBLE);

                            if (umiditaVal != null) stmtUpdate.setDouble(3, umiditaVal);
                            else stmtUpdate.setNull(3, Types.DOUBLE);

                            if (temperaturaVal != null) stmtUpdate.setDouble(4, temperaturaVal);
                            else stmtUpdate.setNull(4, Types.DOUBLE);

                            if (phVal != null) stmtUpdate.setDouble(5, phVal);
                            else stmtUpdate.setNull(5, Types.DOUBLE);

                            stmtUpdate.setString(6, puId);
                            stmtUpdate.executeUpdate();
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        };
    }
}

