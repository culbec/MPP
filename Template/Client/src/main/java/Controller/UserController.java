package Controller;

import CommonUtils.IGUIObserver;
import CommonUtils.IService;
import Exceptions.AppException;
import Exceptions.ServiceException;
import Model.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class UserController implements IGUIObserver {
    private static final Logger logger = LogManager.getLogger();
    private IService proxy;
    private User user;
    private Stage mainStage;
    private Stage parentStage;

    public void setUser(User user) {
        this.user = user;
    }

    public void setParentStage(Stage parentStage) {
        this.parentStage = parentStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void initUserController(IService service) throws IOException {
        logger.traceEntry("Initializing the user controller...");
        this.proxy = service;

        this.mainStage.setTitle("User: " + this.user.getFirstName() + " " + this.user.getLastName());

        logger.traceExit("User controller initialized successfully!");
        this.mainStage.show();
    }

    @FXML
    public void handleLogout() {
        logger.traceEntry("Handling the logout event...");
        try {
            this.proxy.logout(user, this);
            this.mainStage.close();
            this.parentStage.show();
        } catch (ServiceException | AppException e) {
            logger.error("Error while logging out: {}", e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error while logging out!");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        logger.traceExit("Logged out successfully!");
        System.exit(0);
    }

    @Override
    public void shutdownGUI() {
        Platform.runLater(() -> {
            try {
                logger.info("The server has been closed. Shutting down the GUI...");
                Alert alert = new Alert(Alert.AlertType.ERROR, "The server has been closed. Shutting down the GUI...", ButtonType.OK);
                alert.showAndWait();

                logger.info("Closing the main stage...");
                this.mainStage.close();

                logger.info("Showing the parent stage...");
                this.parentStage.show();
            } catch (Exception e) {
                logger.error("Error while shutting down the GUI: {}", e.getMessage());
            }
        });
    }
}
