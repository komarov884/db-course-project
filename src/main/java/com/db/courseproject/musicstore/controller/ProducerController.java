package com.db.courseproject.musicstore.controller;

import com.db.courseproject.musicstore.dao.ProducerDAO;
import com.db.courseproject.musicstore.exception.EntityNotFoundException;
import com.db.courseproject.musicstore.exception.ForeignKeyViolationException;
import com.db.courseproject.musicstore.exception.ServiceException;
import com.db.courseproject.musicstore.model.FullName;
import com.db.courseproject.musicstore.model.Producer;
import com.db.courseproject.musicstore.service.ProducerService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static com.db.courseproject.musicstore.util.EntitiesConstants.ID;
import static com.db.courseproject.musicstore.util.EntitiesConstants.MEMBER_BIRTH_DATE;
import static com.db.courseproject.musicstore.util.ViewConstants.CSS_FILE;
import static com.db.courseproject.musicstore.util.ViewConstants.MAIN_FORM_FXML;
import static com.db.courseproject.musicstore.util.ViewConstants.MAIN_FORM_TITLE;
import static com.db.courseproject.musicstore.util.ViewConstants.CREATE_UPDATE_PRODUCER_CONTROLLER_FXML;
import static com.db.courseproject.musicstore.util.ViewConstants.CREATE_PRODUCER_CONTROLLER_TITLE;
import static com.db.courseproject.musicstore.util.ViewConstants.UPDATE_PRODUCER_CONTROLLER_TITLE;

/**
 * JavaFX form controller for Producer.fxml.
 * <p>
 * Created on 6/11/2018.
 *
 * @author Vasilii Komarov
 */
public class ProducerController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(ProducerController.class);

    @FXML
    private TableView<Producer> producerTable;
    @FXML
    private TableColumn<Producer, Long> clId;
    @FXML
    private TableColumn<Producer, String> clFirstName;
    @FXML
    private TableColumn<Producer, String> clLastName;
    @FXML
    private TableColumn<Producer, Date> clBirthDate;

    @FXML
    private Button btFindById;
    @FXML
    private Button btFindByName;
    @FXML
    private Button btFindAll;
    @FXML
    private Button btCreate;
    @FXML
    private Button btUpdate;
    @FXML
    private Button btDeleteById;
    @FXML
    private Button btMainMenu;

    @FXML
    private TextField tfFindById;
    @FXML
    private TextField tfFindByName;
    @FXML
    private TextField tfDeleteById;

    private ObservableList<Producer> producers;

    private ProducerService producerService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        producerService = new ProducerService(new ProducerDAO());
        setCellValueFactories();
        producers = FXCollections.observableArrayList();
        producerTable.setItems(producers);
        addListenersOnTextFields();
    }

    @FXML
    private void btFindByIdClick(ActionEvent actionEvent) {
        this.producers.clear();
        try {
            Long id = Long.parseLong(tfFindById.getText());
            Producer producer = producerService.findById(id);
            this.producers.add(producer);
        } catch (NumberFormatException e) {
            logExceptionAndShowAlert(Alert.AlertType.ERROR, e,
                    "Error id parsing", "Parsing operation does not possible");
        } catch (ServiceException e) {
            logExceptionAndShowAlert(Alert.AlertType.ERROR, e,
                    "Error producer finding", "Operation does not possible");
        } catch (EntityNotFoundException e) {
            logExceptionAndShowAlert(Alert.AlertType.WARNING, e,
                    "Producer not found", "Producer not found");
        }
    }

    @FXML
    private void btFindByNameClick(ActionEvent actionEvent) {
        this.producers.clear();
        String name = tfFindByName.getText();
        try {
            List<Producer> producers = producerService.findAllByName(name);
            this.producers.addAll(producers);
        } catch (ServiceException e) {
            logExceptionAndShowAlert(Alert.AlertType.ERROR, e,
                    "Error producer finding", "Operation does not possible");
        }
    }

    @FXML
    private void btFindAllClick(ActionEvent actionEvent) {
        this.producers.clear();
        try {
            List<Producer> producers = producerService.findAll();
            this.producers.addAll(producers);
        } catch (ServiceException e) {
            logExceptionAndShowAlert(Alert.AlertType.ERROR, e,
                    "Error producer finding", "Operation does not possible");
        }
    }

    @FXML
    private void btCreateClick(ActionEvent actionEvent) {
        showCreateUpdateWindow(CREATE_UPDATE_PRODUCER_CONTROLLER_FXML,
                CREATE_PRODUCER_CONTROLLER_TITLE, true);
    }

    @FXML
    private void btUpdateClick(ActionEvent actionEvent) {
        showCreateUpdateWindow(CREATE_UPDATE_PRODUCER_CONTROLLER_FXML,
                UPDATE_PRODUCER_CONTROLLER_TITLE, false);
    }

    @FXML
    private void btDeleteByIdClick(ActionEvent actionEvent) {
        this.producers.clear();
        try {
            Long id = Long.parseLong(tfDeleteById.getText());
            producerService.delete(id);
        } catch (NumberFormatException e) {
            logExceptionAndShowAlert(Alert.AlertType.ERROR, e,
                    "Error id parsing", "Parsing operation does not possible");
        } catch (ServiceException | ForeignKeyViolationException e) {
            logExceptionAndShowAlert(Alert.AlertType.ERROR, e,
                    "Error producer deleting", "Operation does not possible");
        }
    }

    @FXML
    private void btMainMenuClick(ActionEvent actionEvent) {
        Stage primaryStage = new Stage();
        URL fxmlFile = ClassLoader.getSystemClassLoader().getResource(MAIN_FORM_FXML);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlFile);
            Pane root = fxmlLoader.load();

            Scene scene = new Scene(root);

            URL cssFile = ClassLoader.getSystemClassLoader().getResource(CSS_FILE);
            scene.getStylesheets().add((cssFile).toExternalForm());

            primaryStage.setTitle(MAIN_FORM_TITLE);
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.initModality(Modality.WINDOW_MODAL);
            primaryStage.show();

            ((Stage) ((Node) (actionEvent.getSource())).getScene().getWindow()).close();
        } catch (IOException e) {
            logExceptionAndShowAlert(Alert.AlertType.ERROR, e,
                    "Error loading fxml-file", String.format("Error loading fxml-file: %s", MAIN_FORM_FXML));
        }
    }

    private void showCreateUpdateWindow(String fxmlFileName, String title, boolean isCreationOperation) {
        Stage createUpdateProducerStage = new Stage();
        URL fxmlFile = ClassLoader.getSystemClassLoader().getResource(fxmlFileName);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlFile);
            Pane root = fxmlLoader.load();

            CreateUpdateProducerController controller = fxmlLoader.getController();
            controller.setProducerController(this);
            controller.setTfIdDisable(isCreationOperation);

            Scene scene = new Scene(root);

            URL cssFile = ClassLoader.getSystemClassLoader().getResource(CSS_FILE);
            scene.getStylesheets().add((cssFile).toExternalForm());

            Stage mainStage = (Stage) btCreate.getScene().getWindow();

            createUpdateProducerStage.setTitle(title);
            createUpdateProducerStage.setResizable(false);
            createUpdateProducerStage.setScene(scene);
            createUpdateProducerStage.initModality(Modality.WINDOW_MODAL);
            createUpdateProducerStage.initOwner(mainStage);
            createUpdateProducerStage.show();
        } catch (IOException e) {
            logExceptionAndShowAlert(Alert.AlertType.ERROR, e,
                    "Error loading fxml-file", String.format("Error loading fxml-file: %s", fxmlFileName));
        }
    }

    protected void createProducer(Producer producer) {
        this.producers.clear();
        try {
            Producer createdProducer = producerService.create(producer);
            this.producers.add(createdProducer);
        } catch (ServiceException e) {
            logExceptionAndShowAlert(Alert.AlertType.ERROR, e,
                    "Error producer creating", "Operation does not possible");
        }
    }

    protected void updateProducer(Producer producer, Long id) {
        this.producers.clear();
        try {
            Producer updatedProducer = producerService.update(producer, id);
            this.producers.add(updatedProducer);
        } catch (ServiceException e) {
            logExceptionAndShowAlert(Alert.AlertType.ERROR, e,
                    "Error producer updating", "Operation does not possible");
        }
    }

    private void addListenersOnTextFields() {
        tfFindById.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfFindById.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        tfDeleteById.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfDeleteById.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void setCellValueFactories() {
        clId.setCellValueFactory(new PropertyValueFactory<>(ID));
        clFirstName.setCellValueFactory(param -> {
            String firstName = null;
            Producer producer = param.getValue();
            if (producer != null) {
                FullName fullName = producer.getName();
                if (fullName != null) {
                    firstName = fullName.getFirstName();
                }
            }
            return new SimpleStringProperty(firstName);
        });
        clLastName.setCellValueFactory(param -> {
            String lastName = null;
            Producer producer = param.getValue();
            if (producer != null) {
                FullName fullName = producer.getName();
                if (fullName != null) {
                    lastName = fullName.getLastName();
                }
            }
            return new SimpleStringProperty(lastName);
        });
        clBirthDate.setCellValueFactory(new PropertyValueFactory<>(MEMBER_BIRTH_DATE));
    }

    private void logExceptionAndShowAlert(Alert.AlertType alertType, Exception e, String title, String headerText) {
        switch (alertType) {
            case ERROR:
                LOGGER.error(e.getMessage());
                break;
            case WARNING:
                LOGGER.warn(e.getMessage());
                break;
            default:
                LOGGER.info(e.getMessage());
                break;
        }
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}
