package informatica.plantmanager.model;

import informatica.plantmanager.model.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CaricaPianteUtenteExport extends Service<ObservableList<String>> {

    private final String userId;

    public CaricaPianteUtenteExport(String userId) {
        this.userId = userId;
    }

    @Override
    protected Task<ObservableList<String>> createTask() {
        return new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() throws Exception {
                ObservableList<String> plantList = FXCollections.observableArrayList();
                String sql = "SELECT P.nome " +
                        "FROM Piante AS P " +
                        "JOIN PianteUtente AS PU ON P.id = PU.PiantaId " +
                        "WHERE PU.UtenteId = ?";
                Connection connection = DatabaseConnection.getConnection();
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, userId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            String plantName = rs.getString("nome");
                            plantList.add(plantName);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw e;
                }
                return plantList;
            }
        };
    }
}
