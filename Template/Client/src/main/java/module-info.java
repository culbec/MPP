module Template.Client.main {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.apache.logging.log4j;
    requires Template.Common.main;
    requires Template.Model.main;
    requires Template.Network.main;

    opens Controller to javafx.fxml;
}