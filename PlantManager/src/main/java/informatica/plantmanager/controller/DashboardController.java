package informatica.plantmanager.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class DashboardController {

    @FXML
    private ImageView dashboardIcon;

    @FXML
    private ImageView exportIcon;

    @FXML
    private ImageView profilePic;

    @FXML
    private ImageView plantIcon;

    @FXML
    private AnchorPane changeComponent;

    private List<ImageView> icons;

    @FXML
    public void initialize() {
        icons = new ArrayList<>();
        icons.add(dashboardIcon);
        icons.add(plantIcon);
        icons.add(exportIcon);

        resetIcons();
        loadDashboardComponent();
        dashboardIcon.setOpacity(1.0);
    }

    private void resetIcons() {
        for (ImageView icon : icons) {
            icon.setOpacity(0.5);
        }
    }

    private void loadDashboardComponent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/DashboardPanel.fxml"));
            AnchorPane dashboardPanel = loader.load();
            changeComponent.getChildren().setAll(dashboardPanel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void dashboardIconClick(MouseEvent event) {
        resetIcons();
        dashboardIcon.setOpacity(1.0);
        loadDashboardComponent();
    }

    @FXML
    void exportIconClick(MouseEvent event) {
        resetIcons();
        exportIcon.setOpacity(1.0);
    }

    @FXML
    void plantIconClick(MouseEvent event) {
        resetIcons();
        plantIcon.setOpacity(1.0);
    }
}
