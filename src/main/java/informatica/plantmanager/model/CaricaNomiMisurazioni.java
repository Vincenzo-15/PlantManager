package informatica.plantmanager.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CaricaNomiMisurazioni extends Service<ObservableList<String>> {

    @Override
    protected Task<ObservableList<String>> createTask() {
        return new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() throws Exception {
                ObservableList<String> sensorList = FXCollections.observableArrayList();
                String sql = "SELECT Nome FROM Sensori";
                Connection connection = DatabaseConnection.getConnection();
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            String sensorName = rs.getString("Nome");
                            sensorList.add(sensorName);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw e;
                }
                return sensorList;
            }
        };
    }
}
