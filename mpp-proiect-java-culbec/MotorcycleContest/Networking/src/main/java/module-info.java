module MotorcycleContest.Networking.main {
    exports Workers;
    exports NetworkUtils;

    requires com.google.protobuf;
    requires MotorcycleContest.Common.main;
    requires MotorcycleContest.Model.main;
    requires org.apache.logging.log4j;
}