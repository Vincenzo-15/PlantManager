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

                // Seleziona tutte le righe della tabella PianteUtente che hanno almeno un sensore assegnato
                String queryPianteUtente = "SELECT Id FROM PianteUtente WHERE Id IN (SELECT DISTINCT PiantaUtenteId FROM SensoriPianta)";
                try (PreparedStatement stmtPU = conn.prepareStatement(queryPianteUtente);
                     ResultSet rsPU = stmtPU.executeQuery()) {

                    Random rand = new Random();

                    while (rsPU.next()) {
                        String puId = rsPU.getString("Id");

                        // Inizializza le variabili per i valori che andranno aggiornati
                        Double acquaVal = null;
                        Double luceVal = null;
                        Double umiditaVal = null;
                        Double temperaturaVal = null;
                        Double phVal = null;

                        // Query per recuperare i sensori assegnati a questa pianta e ottenere anche l'unità di misura
                        String querySensori = "SELECT SP.Id AS SPId, S.Funzione, S.Unita_di_misura " +
                                "FROM SensoriPianta SP JOIN Sensori S ON SP.SensoreId = S.Id " +
                                "WHERE SP.PiantaUtenteId = ?";
                        try (PreparedStatement stmtSensori = conn.prepareStatement(querySensori)) {
                            stmtSensori.setString(1, puId);
                            try (ResultSet rsSensori = stmtSensori.executeQuery()) {
                                while (rsSensori.next()) {
                                    String sensorePiantaId = rsSensori.getString("SPId");
                                    String funzione = rsSensori.getString("Funzione").toLowerCase();
                                    String unitaDiMisura = rsSensori.getString("Unita_di_misura");

                                    double valoreRandom = 0;
                                    switch (funzione) {
                                        case "acqua":
                                            // Genera un valore tra 0.2 e 1.0
                                            valoreRandom = 0.2 + (1.0 - 0.2) * rand.nextDouble();
                                            acquaVal = valoreRandom;
                                            break;
                                        case "luce":
                                            // Genera un valore intero tra 200 e 800
                                            valoreRandom = 200 + rand.nextInt(601);
                                            luceVal = valoreRandom;
                                            break;
                                        case "umidita":
                                            // Genera un valore intero tra 30 e 90
                                            valoreRandom = 30 + rand.nextInt(61);
                                            umiditaVal = valoreRandom;
                                            break;
                                        case "temperatura":
                                            // Genera un valore intero tra 15 e 30
                                            valoreRandom = 15 + rand.nextInt(16);
                                            temperaturaVal = valoreRandom;
                                            break;
                                        case "ph":
                                            // Genera un valore double tra 5.5 e 7.5
                                            valoreRandom = 5.5 + (7.5 - 5.5) * rand.nextDouble();
                                            phVal = valoreRandom;
                                            break;
                                        default:
                                            // Se la funzione non è riconosciuta, ignorala
                                            break;
                                    }

                                    // Inserisce la misurazione nella tabella Misurazioni includendo anche l'unità di misura
                                    String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                                    String insertMisurazione = "INSERT INTO Misurazioni (Id, SensorePiantaId, Data_e_ora, Valore, Unita_di_misura) VALUES (?, ?, ?, ?, ?)";
                                    try (PreparedStatement stmtInsert = conn.prepareStatement(insertMisurazione)) {
                                        String misId = UUID.randomUUID().toString();
                                        stmtInsert.setString(1, misId);
                                        stmtInsert.setString(2, sensorePiantaId);
                                        stmtInsert.setString(3, now);
                                        stmtInsert.setDouble(4, valoreRandom);
                                        stmtInsert.setString(5, unitaDiMisura);
                                        stmtInsert.executeUpdate();
                                    }
                                }
                            }
                        }

                        // Aggiorna la tabella PianteUtente con i valori generati, solo per i sensori assegnati
                        String updatePU = "UPDATE PianteUtente SET Acqua = COALESCE(?, Acqua), " +
                                "Luce = COALESCE(?, Luce), Umidita = COALESCE(?, Umidita), " +
                                "Temperatura = COALESCE(?, Temperatura), PH_terreno = COALESCE(?, PH_terreno) " +
                                "WHERE Id = ?";
                        try (PreparedStatement stmtUpdate = conn.prepareStatement(updatePU)) {
                            if (acquaVal != null)
                                stmtUpdate.setDouble(1, acquaVal);
                            else
                                stmtUpdate.setNull(1, Types.DOUBLE);

                            if (luceVal != null)
                                stmtUpdate.setDouble(2, luceVal);
                            else
                                stmtUpdate.setNull(2, Types.DOUBLE);

                            if (umiditaVal != null)
                                stmtUpdate.setDouble(3, umiditaVal);
                            else
                                stmtUpdate.setNull(3, Types.DOUBLE);

                            if (temperaturaVal != null)
                                stmtUpdate.setDouble(4, temperaturaVal);
                            else
                                stmtUpdate.setNull(4, Types.DOUBLE);

                            if (phVal != null)
                                stmtUpdate.setDouble(5, phVal);
                            else
                                stmtUpdate.setNull(5, Types.DOUBLE);

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
