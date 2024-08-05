module MotorcycleContest.Client.main {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires MotorcycleContest.Model.main;
    requires MotorcycleContest.Common.main;
    requires MotorcycleContest.Networking.main;

    opens Controller to javafx.fxml;
}