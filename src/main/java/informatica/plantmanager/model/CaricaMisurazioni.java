package informatica.plantmanager.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.chart.XYChart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CaricaMisurazioni extends Service<ObservableList<XYChart.Series<String, Number>>> {

    private final String userId;
    private final LocalDate selectedDate;
    private final String selectedMeasurement;
    private final String selectedPlant;

    public CaricaMisurazioni(String userId, LocalDate selectedDate, String selectedMeasurement, String selectedPlant) {
        this.userId = userId;
        this.selectedDate = selectedDate;
        this.selectedMeasurement = selectedMeasurement;
        this.selectedPlant = selectedPlant;
    }

    @Override
    protected Task<ObservableList<XYChart.Series<String, Number>>> createTask() {
        return new Task<ObservableList<XYChart.Series<String, Number>>>() {
            @Override
            protected ObservableList<XYChart.Series<String, Number>> call() throws Exception {
                ObservableList<XYChart.Series<String, Number>> seriesList = FXCollections.observableArrayList();

                StringBuilder sql = new StringBuilder();
                sql.append("SELECT M.Data_e_ora, M.Valore, PU.Id AS PiantaUtenteId, P.nome AS PiantaNome ")
                        .append("FROM Misurazioni M ")
                        .append("JOIN SensoriPianta SP ON M.SensoriPiantaId = SP.Id ")
                        .append("JOIN PianteUtente PU ON SP.PiantaUtenteId = PU.Id ")
                        .append("JOIN Piante P ON PU.PiantaId = P.Id ")
                        .append("JOIN Sensori S ON SP.SensoreId = S.Id ")
                        .append("WHERE M.UtenteId = ? ")
                        .append("AND date(M.Data_e_ora) = ? ");

                if (!"Tutte".equalsIgnoreCase(selectedMeasurement)) {
                    sql.append("AND S.nome = ? ");
                }

                if (selectedPlant != null && !selectedPlant.equals("Tutte")) {
                    sql.append("AND P.nome = ? ");
                }

                Connection connection = DatabaseConnection.getConnection();
                try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
                    int index = 1;
                    ps.setString(index++, userId);
                    ps.setString(index++, selectedDate.toString());
                    if (!"Tutte".equalsIgnoreCase(selectedMeasurement)) {
                        ps.setString(index++, selectedMeasurement);
                    }
                    if (selectedPlant != null && !selectedPlant.equals("Tutte")) {
                        ps.setString(index++, selectedPlant);
                    }

                    try (ResultSet rs = ps.executeQuery()) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM - HH/mm");
                        while (rs.next()) {
                            //String dataOra = rs.getString("Data_e_ora");
                            //String time = dataOra.length() >= 16 ? dataOra.substring(11, 16) : dataOra;
                            String time = rs.getString("Data_e_ora");
                            time = time.formatted(formatter);
                            System.out.println(time);
                            double valore = rs.getDouble("Valore");
                            String piantaNome = rs.getString("PiantaNome");

                            XYChart.Series<String, Number> series = findOrCreateSeries(seriesList, piantaNome);
                            series.getData().add(new XYChart.Data<>(time, valore));
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw e;
                }
                return seriesList;
            }

            private XYChart.Series<String, Number> findOrCreateSeries(ObservableList<XYChart.Series<String, Number>> seriesList, String piantaNome) {
                for (XYChart.Series<String, Number> series : seriesList) {
                    if (series.getName().equals(piantaNome)) {
                        return series;
                    }
                }

                XYChart.Series<String, Number> newSeries = new XYChart.Series<>();
                newSeries.setName(piantaNome);
                seriesList.add(newSeries);
                return newSeries;
            }
        };
    }
}
