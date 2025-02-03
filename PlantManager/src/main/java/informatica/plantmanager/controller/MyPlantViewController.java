package informatica.plantmanager.controller;

import informatica.plantmanager.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;

public class MyPlantViewController {

    @FXML
    private AnchorPane anchorPaneScroll;

    @FXML
    private HBox boxButtons;

    @FXML
    private ScrollPane boxScrollPane;

    @FXML
    private AnchorPane buttonAddPlant;

    @FXML
    private AnchorPane buttonSearch;

    @FXML
    private Label labelTitolo;

    @FXML
    private ScrollPane plantScrollPane;

    @FXML
    private TextField searchBar;

    @FXML
    private ToggleButton toggleButtonPosizione;

    private Utente utente;

    private GridPane gridPane = new GridPane();

    @FXML
    public void initialize() {
        // Configura il GridPane
        gridPane.setPadding(new Insets(5, 0, 0, 5));
        gridPane.setHgap(20);
        gridPane.setVgap(25);

        // Aggiungi listener alla searchBar per aggiornare la ricerca al variare del testo
        searchBar.textProperty().addListener((obs, oldText, newText) -> searchPlantsByName());
    }

    private void loadUserPlants() {
        if (gridPane == null) {
            gridPane = new GridPane();
        } else {
            gridPane.getChildren().clear();
        }
        gridPane.setPadding(new Insets(5, 0, 0, 5));
        gridPane.setHgap(20);
        gridPane.setVgap(25);
        anchorPaneScroll.getChildren().clear();
        anchorPaneScroll.getChildren().add(gridPane);

        if (utente == null) {
            System.err.println("Utente non impostato.");
            return;
        }
        String filtroPosizione = getSelectedPositionFilter();
        String nomeFilter = searchBar.getText();

        Service<List<Pianta>> service;
        if (nomeFilter != null && !nomeFilter.trim().isEmpty()) {
            CercaPianteUtentePerNome searchService = new CercaPianteUtentePerNome();
            searchService.setUserId(utente.getId());
            searchService.setNomeFilter(nomeFilter);
            searchService.setPosizioneFilter(filtroPosizione);
            service = searchService;
        } else {
            service = CaricaPianteUtente.getPlantsByUserService(utente.getId(), filtroPosizione);
        }
        service.setOnSucceeded(event -> {
            List<Pianta> plants = service.getValue();
            if (plants == null || plants.isEmpty()) {
                System.out.println("Nessuna pianta trovata con i filtri: nome = " + nomeFilter + ", posizione = " + filtroPosizione);
                gridPane.getChildren().clear();
                return;
            }
            ObservableList<Pianta> observablePlants = FXCollections.observableArrayList(plants);
            int row = 0, col = 0;
            for (Pianta plant : observablePlants) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/MyPlantView.fxml"));
                    AnchorPane plantComponent = loader.load();
                    PlantComponentController componentController = loader.getController();
                    componentController.setPlantData(plant);
                    gridPane.add(plantComponent, col, row);
                    col++;
                    if (col >= 3) {
                        col = 0;
                        row++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        service.setOnFailed(event -> {
            Throwable error = service.getException();
            System.err.println("Errore nel caricamento delle piante: " + error.getMessage());
        });
        service.start();
    }

    private void searchPlantsByName() {
        loadUserPlants();
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
        loadUserPlants();
        loadPositions();
    }

    @FXML
    void addPlant(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/AddPlantPopup.fxml"));
            AnchorPane popupRoot = fxmlLoader.load();

            AddPlantPopupController popupController = fxmlLoader.getController();
            popupController.setUtente(utente);

            Scene scene = new Scene(popupRoot);
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadPositions();
            loadUserPlants();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void showSearchbar(MouseEvent event) {
        searchBar.setVisible(true);
        searchBar.setDisable(false);
        searchBar.requestFocus();
        buttonSearch.setVisible(false);
        buttonSearch.setDisable(true);

        searchBar.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                searchBar.setVisible(false);
                searchBar.setDisable(true);
                buttonSearch.setVisible(true);
                buttonSearch.setDisable(false);
            }
        });
    }

    private void loadPositions() {
        if (utente == null) {
            System.err.println("Utente non impostato per caricare le posizioni.");
            return;
        }
        CercaPosizioniUtente posService = new CercaPosizioniUtente();
        posService.setUserId(utente.getId());
        posService.setOnSucceeded(event -> {
            List<String> posizioni = posService.getValue();
            boxButtons.getChildren().clear();
            ToggleButton btnTutte = new ToggleButton("Tutte");
            btnTutte.setSelected(true);
            btnTutte.setOnAction(e -> {
                if (btnTutte.isSelected()) {
                    clearAllToggleSelections();
                    btnTutte.setSelected(true);
                    loadUserPlants();
                }
            });
            boxButtons.getChildren().add(btnTutte);

            for (String posizione : posizioni) {
                if ("Tutte".equalsIgnoreCase(posizione)) continue;
                ToggleButton tb = new ToggleButton(posizione);
                tb.setOnAction(e -> {
                    if (tb.isSelected()) {
                        btnTutte.setSelected(false);
                    }
                    loadUserPlants();
                });
                boxButtons.getChildren().add(tb);
            }
        });
        posService.setOnFailed(event -> {
            Throwable error = posService.getException();
            System.err.println("Errore nel caricamento delle posizioni: " + error.getMessage());
        });
        posService.start();
    }

    private void clearAllToggleSelections() {
        for (Node node : boxButtons.getChildren()) {
            if (node instanceof ToggleButton) {
                ((ToggleButton) node).setSelected(false);
            }
        }
    }

    private String getSelectedPositionFilter() {
        StringBuilder selectedPositions = new StringBuilder();
        for (Node node : boxButtons.getChildren()) {
            if (node instanceof ToggleButton) {
                ToggleButton tb = (ToggleButton) node;
                if (tb.isSelected() && !tb.getText().equalsIgnoreCase("Tutte")) {
                    if (selectedPositions.length() > 0) {
                        selectedPositions.append(",");
                    }
                    selectedPositions.append(tb.getText());
                }
            }
        }
        return selectedPositions.length() > 0 ? selectedPositions.toString() : "Tutte";
    }
}