import Controller.LoginController;
import Controller.UserController;
import NetworkUtils.ProtobufProxy;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class StartClient extends Application {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8888;

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        logger.traceEntry("Starting the application...");
        Properties properties = new Properties();
        try {
            properties.load(StartClient.class.getResourceAsStream("/client.properties"));
            logger.info("Properties loaded successfully.");
            properties.list(System.out);
        } catch (IOException e) {
            logger.error("Couldn't load the properties file: {}", e.getMessage());
            System.err.println("Something wrong happened: " + e.getMessage());
            return;
        }

        String hostname = properties.getProperty("server.host", DEFAULT_HOST);
        int port = DEFAULT_PORT;
        try {
            port = Integer.parseInt(properties.getProperty("server.port"));
        } catch (NumberFormatException e) {
            logger.error("Invalid port number. Using the default port: {}", DEFAULT_PORT);
            System.err.println("Invalid port number. Using the default port: " + DEFAULT_PORT);
        }
        System.out.println("Connecting to the server on " + hostname + ":" + port);

        ProtobufProxy proxy = new ProtobufProxy(hostname, port);

        FXMLLoader loginLoader = new FXMLLoader(StartClient.class.getResource("/views/login.fxml"));
        Parent loginRoot = loginLoader.load();
        LoginController loginController = loginLoader.getController();

        FXMLLoader userLoader = new FXMLLoader(StartClient.class.getResource("/views/user.fxml"));
        Parent userRoot = userLoader.load();
        UserController userController = userLoader.getController();
        proxy.setClient(userController);

        Stage userStage = new Stage();
        userStage.setScene(new Scene(userRoot));
        userController.setMainStage(userStage);
        userController.setParentStage(stage);

        loginController.setMainStage(stage);
        loginController.initLoginController(proxy, userController);

        stage.setScene(new Scene(loginRoot));
        stage.setTitle("Login - Motorcycle Contest");
        stage.show();

        logger.traceExit("Initialized the application!");
    }
}
