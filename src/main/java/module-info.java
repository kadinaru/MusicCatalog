module musiccatalog {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.net.http;
    requires com.google.gson;
    requires java.base;

    opens coursework.musiccatalog to javafx.fxml;
    opens coursework.musiccatalog.controller to javafx.fxml;
    exports coursework.musiccatalog;
    exports coursework.musiccatalog.controller;
}
