package informatica.plantmanager;

import informatica.plantmanager.model.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseConnection.initialize();
        primaryStage.setTitle("Plant Manager");

        primaryStage.setResizable(false);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
        AnchorPane root = fxmlLoader.load();

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> DatabaseConnection.closeConnection());
    }

    public static void main(String[] args) {
        launch();
    }
}
