package informatica.plantmanager.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class ProfileMenuController {

    @FXML
    private CheckBox checkBoxSelezionaTema;

    @FXML
    private ComboBox<String> comboBoxSelezionaFont;

    @FXML
    private Label labelLogout;

    @FXML
    public void initialize() {
        comboBoxSelezionaFont.getItems().addAll("Arial", "Tahoma", "Times New Roman");
        comboBoxSelezionaFont.setOnAction(event -> {
            String selectedFont = comboBoxSelezionaFont.getValue();
            applyFontStyle(selectedFont);
            saveFontStyle(selectedFont);
        });

//        checkBoxSelezionaTema.setOnAction(event -> {
//            boolean isDarkTheme = checkBoxSelezionaTema.isSelected();
//            applyThemeStyle(isDarkTheme);
//            saveThemeStyle(isDarkTheme);
//        });

        String savedFont = loadFontStyle();
        if (savedFont != null) {
            comboBoxSelezionaFont.setValue(savedFont);
            applyFontStyle(savedFont);
        }

//        boolean savedTheme = loadThemeStyle();
//        checkBoxSelezionaTema.setSelected(savedTheme);
//        applyThemeStyle(savedTheme);

    }

    private void saveFontStyle(String fontStyle) {
        Properties properties = loadProperties();
        properties.setProperty("fontStyle", fontStyle);
        saveProperties(properties);
    }

    private String loadFontStyle() {
        Properties properties = loadProperties();
        return properties.getProperty("fontStyle", "Arial");
    }

    private void applyFontStyle(String fontStyle) {
        Scene scene = comboBoxSelezionaFont.getScene();
        if (scene != null) {
            scene.getRoot().lookupAll(".label").forEach(node -> {
                if (node instanceof Label) {
                    ((Label) node).setStyle("-fx-font-family: '" + fontStyle + "';");
                }
            });
        }
    }

//    private void saveThemeStyle(boolean isDarkTheme) {
//        Properties properties = loadProperties();
//        properties.setProperty("themeStyle", Boolean.toString(isDarkTheme));
//        saveProperties(properties);
//    }
//
//    private boolean loadThemeStyle() {
//        Properties properties = loadProperties();
//        return Boolean.parseBoolean(properties.getProperty("themeStyle", "false"));
//    }
//
//    private void applyThemeStyle(boolean isDarkTheme) {
//        Scene scene = checkBoxSelezionaTema.getScene();
//        if (scene != null) {
//            if (isDarkTheme) {
//                scene.getStylesheets().add(getClass().getResource("/informatica/styles/darkTheme.css").toExternalForm());
//            } else {
//                scene.getStylesheets().remove(getClass().getResource("/informatica/styles/darkTheme.css").toExternalForm());
//            }
//        }
//    }

    @FXML
    void labelLogoutClick(MouseEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Conferma Logout");
        alert.setHeaderText("Sei sicuro di voler effettuare il logout?");
        alert.setContentText("Conferma per effettuare il logout.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/Login.fxml"));
                AnchorPane loginPane = loader.load();

                Stage stage = (Stage) labelLogout.getScene().getWindow();
                stage.getScene().setRoot(loginPane);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        try (FileReader reader = new FileReader("config.properties")) {
            properties.load(reader);
        } catch (IOException e) {
            System.out.println("Nessuna configurazione salvata, verranno usati i valori di default.");
        }
        return properties;
    }

    private void saveProperties(Properties properties) {
        try (FileWriter writer = new FileWriter("config.properties")) {
            properties.store(writer, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}