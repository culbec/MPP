module Template.Network.main {
    exports Workers;
    exports NetworkUtils;

    requires com.google.protobuf;
    requires org.apache.logging.log4j;
    requires Template.Common.main;
    requires Template.Model.main;
}