package informatica.plantmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
//import javafx.stage.Screen;
import javafx.stage.Stage;
//import javafx.geometry.Rectangle2D;

public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Plant Manager");

        // Ottieni le dimensioni dello schermo
        //Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        //double screenWidth = screenBounds.getWidth();
        //double screenHeight = screenBounds.getHeight();
        primaryStage.setResizable(false);  // Disabilita il ridimensionamento

        // Posiziona la finestra al centro dello schermo
        //primaryStage.setX((screenWidth - 1920) / 2);
        //primaryStage.setY((screenHeight - 1080) / 2);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
        AnchorPane root = fxmlLoader.load();

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
