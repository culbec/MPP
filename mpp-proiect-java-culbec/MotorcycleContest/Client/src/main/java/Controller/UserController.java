package Controller;

import CommonUtils.GUIObserver;
import CommonUtils.IService;
import Exceptions.AppException;
import Exceptions.ServiceException;
import Model.Participant;
import Model.Race;
import Model.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class UserController implements GUIObserver {
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

    @FXML
    private TableView<Race> tableViewRace;
    @FXML
    private TableColumn<Race, Integer> tableColumnEngineRace;
    @FXML
    private TableColumn<Race, String> tableColumnNoParticipantsRace;
    @FXML
    private TableView<Participant> tableViewParticipant;
    @FXML
    private TableColumn<Participant, String> tableColumnFirstName;
    @FXML
    private TableColumn<Participant, String> tableColumnLastName;
    @FXML
    private TableColumn<Participant, String> tableColumnEngineParticipant;
    @FXML
    private TableColumn<Participant, String> tableColumnTeam;
    @FXML
    private TextField textFieldTeam;
    @FXML
    private Button btnSearch;
    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;
    @FXML
    private ComboBox<Integer> comboBoxEngineCapacity;
    @FXML
    private Button btnRegister;
    @FXML
    private TextField textFieldRegisterTeam;
    @FXML
    private Button btnLogout;

    private void initComboBoxEngineCapacity() throws ServiceException {
        logger.info("Initializing the engine capacity combo box...");

        List<Integer> engineCapacities = (List<Integer>) this.proxy.findAllRaceEngineCapacities();
        this.comboBoxEngineCapacity.getItems().addAll(engineCapacities);
    }

    private void initTableViewRace() throws ServiceException {
        logger.info("Initializing the table view for the races...");
        this.tableColumnEngineRace.setCellValueFactory(new PropertyValueFactory<>("engineCapacity"));
        this.tableColumnNoParticipantsRace.setCellValueFactory(new PropertyValueFactory<>("noParticipants"));

        List<Race> races = (List<Race>) this.proxy.findAllRaces();

        this.tableViewRace.getItems().addAll(races);
    }

    private void initTableViewParticipant() {
        logger.info("Initializing the table view for the participants...");
        this.tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        this.tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        this.tableColumnEngineParticipant.setCellValueFactory(new PropertyValueFactory<>("engineCapacity"));
        this.tableColumnTeam.setCellValueFactory(new PropertyValueFactory<>("team"));
    }

    public void initUserController(IService service) throws IOException {
        logger.traceEntry("Initializing the user controller...");
        this.proxy = service;

        try {
            this.initTableViewRace();
        } catch (ServiceException e) {
            logger.error("Couldn't initialize the table view for the races: {}", e.getMessage());
            throw new IOException(e.getMessage());
        }
        this.initTableViewParticipant();

        try {
            this.initComboBoxEngineCapacity();
        } catch (ServiceException e) {
            logger.error("Couldn't initialize the engine capacity combo box: {}", e.getMessage());
            throw new IOException(e.getMessage());
        }

        this.mainStage.setTitle(user.getFirstName() + " " + user.getLastName() +  " - Motorcycle Contest");

        this.btnRegister.setOnAction(this::registerParticipantAction);
        this.btnSearch.setOnAction(this::searchParticipantsByTeam);
        this.btnLogout.setOnAction(event -> this.handleLogout());
        this.mainStage.setOnCloseRequest(event -> this.handleLogout());

        logger.traceExit("Initialized the user controller!");
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

    private void clearFields() {
        this.textFieldFirstName.clear();
        this.textFieldLastName.clear();
        this.textFieldRegisterTeam.clear();
        this.comboBoxEngineCapacity.getSelectionModel().clearSelection();
    }

    private void registerParticipantAction(ActionEvent event) {
        String firstName = this.textFieldFirstName.getText();
        String lastName = this.textFieldLastName.getText();
        String team = this.textFieldRegisterTeam.getText();
        int engineCapacity = this.comboBoxEngineCapacity.getSelectionModel().getSelectedItem();

        if (firstName.isEmpty() || lastName.isEmpty() || team.isEmpty() || engineCapacity == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error while registering the participant!");
            alert.setContentText("All fields must be filled!");
            alert.showAndWait();
            return;
        }

        try {
            this.proxy.addParticipant(firstName, lastName, team, engineCapacity);
            this.clearFields();
        } catch (ServiceException | AppException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error while registering the participant!");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void searchParticipantsByTeam(ActionEvent event) {
        String team = this.textFieldTeam.getText();

        if (team.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error while searching the participants!");
            alert.setContentText("The team field must be filled!");
            alert.showAndWait();
            return;
        }

        Platform.runLater(() -> {
            try {
                Iterable<Participant> participants = this.proxy.findParticipantsByTeam(team);
                this.tableViewParticipant.getItems().clear();
                this.tableViewParticipant.getItems().addAll((List<Participant>) participants);
            } catch (ServiceException | AppException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error while searching the participants!");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        });
    }

    @Override
    public void participantAdded(Participant participant) {
        Platform.runLater(() -> {
            String inputTeam = this.textFieldTeam.getText();

            // Adding the participant to the table if the team matches the input team.
            if (participant.getTeam().equals(inputTeam)) {
                this.tableViewParticipant.getItems().add(participant);
            }

            // Incrementing the value of the race in the table.
            Race oldRace = this.tableViewRace.getItems().filtered(race -> Objects.equals(race.getEngineCapacity(), participant.getEngineCapacity())).getFirst();
            int index = this.tableViewRace.getItems().indexOf(oldRace);

            Race newRace = new Race.Builder()
                    .setId(oldRace.getId())
                    .setEngineCapacity(oldRace.getEngineCapacity())
                    .setNoParticipants(oldRace.getNoParticipants() + 1)
                    .build();

            this.tableViewRace.getItems().set(index, newRace);
        });
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
