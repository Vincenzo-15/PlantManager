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
                        Double ventoVal = null;  // Nuovo campo per il vento

                        // Query per recuperare i sensori associati alla pianta
                        String querySensori = "SELECT SP.Id AS SPId, S.Nome " +
                                "FROM SensoriPianta SP JOIN Sensori S ON SP.SensoreId = S.Id " +
                                "WHERE SP.PiantaUtenteId = ?";
                        try (PreparedStatement stmtSensori = conn.prepareStatement(querySensori)) {
                            stmtSensori.setString(1, puId);
                            try (ResultSet rsSensori = stmtSensori.executeQuery()) {
                                while (rsSensori.next()) {
                                    String sensorePiantaId = rsSensori.getString("SPId");
                                    // Recupera il nome del sensore dalla tabella Sensori
                                    String funzione = rsSensori.getString("Nome").toLowerCase();

                                    double valoreRandom = 0;
                                    switch (funzione) {
                                        case "acqua":
                                            valoreRandom = rand.nextInt(500);
                                            acquaVal = valoreRandom;
                                            break;
                                        case "luce":
                                            valoreRandom = rand.nextInt(600);
                                            luceVal = valoreRandom;
                                            break;
                                        case "umidita":
                                            valoreRandom = rand.nextInt(100);
                                            umiditaVal = valoreRandom;
                                            break;
                                        case "temperatura":
                                            valoreRandom = rand.nextInt(40);
                                            temperaturaVal = valoreRandom;
                                            break;
                                        case "ph":
                                            valoreRandom = Math.floor(5.5 + (7.5 - 5.5) * rand.nextDouble() * 10) / 10.0;
                                            phVal = valoreRandom;
                                            break;
                                        case "vento":
                                            valoreRandom = rand.nextInt(100);
                                            ventoVal = valoreRandom;
                                            break;
                                        default:
                                            break;
                                    }

                                    // Inserisce la misurazione nella tabella Misurazioni
                                    String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                                    String insertMisurazione = "INSERT INTO Misurazioni (Id, Data_e_ora, Valore, UtenteId, SensoriPiantaId) VALUES (?, ?, ?, ?, ?)";
                                    try (PreparedStatement stmtInsert = conn.prepareStatement(insertMisurazione)) {
                                        String misId = UUID.randomUUID().toString();
                                        stmtInsert.setString(1, misId);
                                        stmtInsert.setString(2, now);
                                        stmtInsert.setDouble(3, valoreRandom);
                                        stmtInsert.setString(4, utenteId);
                                        stmtInsert.setString(5, sensorePiantaId);
                                        stmtInsert.executeUpdate();
                                    }
                                }
                            }
                        }

                        String updatePU = "UPDATE PianteUtente SET Acqua = COALESCE(?, Acqua), " +
                                "Luce = COALESCE(?, Luce), Umidita = COALESCE(?, Umidita), " +
                                "Temperatura = COALESCE(?, Temperatura), PH_terreno = COALESCE(?, PH_terreno), " +
                                "Vento = COALESCE(?, Vento) " +
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

                            if (ventoVal != null) stmtUpdate.setDouble(6, ventoVal);
                            else stmtUpdate.setNull(6, Types.DOUBLE);

                            stmtUpdate.setString(7, puId);
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