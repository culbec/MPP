package Controller;

import CommonUtils.IService;
import Exceptions.AppException;
import Exceptions.ServiceException;
import Model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class LoginController {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private IService proxy;
    private UserController userController;
    private Stage mainStage;

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @FXML
    private TextField textFieldUsername;
    @FXML
    private TextField passwordFieldPassword;
    @FXML
    private Button btnLogin;

    public void initLoginController(IService proxy, UserController userController) {
        logger.traceEntry("Initializing the LoginController...");

        this.proxy = proxy;
        this.userController = userController;
        btnLogin.setOnAction(this::loginAction);
        this.mainStage.setOnCloseRequest(event -> System.exit(0));

        logger.traceExit("Constructed the LoginController!");
    }

    private void loginAction(ActionEvent actionEvent) {
        logger.traceEntry("Identifying the user by the passed username and password...");

        logger.info("Checking if the fields are empty...");
        if (this.textFieldUsername.getText().isEmpty() || this.passwordFieldPassword.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Username and password fields cannot be empty!", ButtonType.OK);
            alert.show();
            return;
        }

        logger.info("The fields are not empty. Continuing!");
        String username = this.textFieldUsername.getText();
        String password = this.passwordFieldPassword.getText();

        logger.info("Trying to retrieve the user by the passed username '{}' and password...", username);

        try {
            User user = this.proxy.login(username, password, userController);

            logger.info("User retrieved successfully!");
            this.userController.setUser(user);
            this.userController.initUserController(proxy);

            this.mainStage.hide();
        } catch (AppException | ServiceException e) {
            logger.error("Error retrieving the user: {}", e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR, "The user couldn't be found: " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        } catch (IOException e) {
            logger.error("Couldn't initialize the user controller: {}", e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't initialize the user controller!", ButtonType.OK);
            alert.showAndWait();
        }

    }
}
