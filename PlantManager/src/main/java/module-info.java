module informatica.plantmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires jbcrypt;

    opens informatica.plantmanager to javafx.fxml;
    opens informatica.plantmanager.controller to javafx.fxml;
    exports informatica.plantmanager;
}