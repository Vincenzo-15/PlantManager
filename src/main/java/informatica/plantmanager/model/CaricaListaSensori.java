package informatica.plantmanager.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CaricaListaSensori extends Service<List<Sensore>> {

    private String plantId;

    public void setPlantId(String plantId) {
        this.plantId = plantId;
    }

    @Override
    protected Task<List<Sensore>> createTask() {
        return new Task<List<Sensore>>() {
            @Override
            protected List<Sensore> call() throws Exception {
                List<Sensore> allSensori = new ArrayList<>();
                Set<String> assegnati = new HashSet<>();

                Connection conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    System.err.println("Connessione non disponibile.");
                    return allSensori;
                }

                String queryAll = "SELECT Id, Nome, Funzione, Unita_di_misura FROM Sensori";
                try (PreparedStatement stmtAll = conn.prepareStatement(queryAll);
                     ResultSet rsAll = stmtAll.executeQuery()) {
                    while (rsAll.next()) {
                        String id = rsAll.getString("Id");
                        String nome = rsAll.getString("Nome");
                        String funzione = rsAll.getString("Funzione");
                        String unita = rsAll.getString("Unita_di_misura");
                        allSensori.add(new Sensore(id, nome, funzione, unita));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                String queryAssegnati = "SELECT SensoreId FROM SensoriPianta WHERE PiantaUtenteId = ?";
                try (PreparedStatement stmtAssigned = conn.prepareStatement(queryAssegnati)) {
                    stmtAssigned.setString(1, plantId);
                    try (ResultSet rsAssigned = stmtAssigned.executeQuery()) {
                        while (rsAssigned.next()) {
                            String sensoreId = rsAssigned.getString("SensoreId");
                            assegnati.add(sensoreId);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                List<Sensore> sensoriDisponibili = new ArrayList<>();
                for (Sensore s : allSensori) {
                    if (!assegnati.contains(s.getId())) {
                        sensoriDisponibili.add(s);
                    }
                }

                return sensoriDisponibili;
            }
        };
    }
}

