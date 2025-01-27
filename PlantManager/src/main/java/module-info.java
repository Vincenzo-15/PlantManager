module it.unical.demacs.informatica.plantmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires eu.hansolo.tilesfx;

    opens it.unical.demacs.informatica.plantmanager to javafx.fxml;
    exports it.unical.demacs.informatica.plantmanager;
}