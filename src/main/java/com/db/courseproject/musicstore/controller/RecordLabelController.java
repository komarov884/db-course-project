package com.db.courseproject.musicstore.controller;

import com.db.courseproject.musicstore.dao.RecordLabelDAO;
import com.db.courseproject.musicstore.exception.ForeignKeyViolationException;
import com.db.courseproject.musicstore.model.RecordLabel;
import com.db.courseproject.musicstore.service.RecordLabelService;
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
import java.util.List;
import java.util.ResourceBundle;

import static com.db.courseproject.musicstore.util.EntitiesConstants.ID;
import static com.db.courseproject.musicstore.util.EntitiesConstants.RECORD_LABEL_NAME;
import static com.db.courseproject.musicstore.util.EntitiesConstants.RECORD_LABEL_COUNTRY;
import static com.db.courseproject.musicstore.util.EntitiesConstants.RECORD_LABEL_FOUNDATION_YEAR;
import static com.db.courseproject.musicstore.util.ViewConstants.CSS_FILE;
import static com.db.courseproject.musicstore.util.ViewConstants.MAIN_FORM_FXML;
import static com.db.courseproject.musicstore.util.ViewConstants.MAIN_FORM_TITLE;
import static com.db.courseproject.musicstore.util.ViewConstants.CREATE_UPDATE_RECORD_LABEL_CONTROLLER_FXML;
import static com.db.courseproject.musicstore.util.ViewConstants.CREATE_RECORD_LABEL_CONTROLLER_TITLE;
import static com.db.courseproject.musicstore.util.ViewConstants.UPDATE_RECORD_LABEL_CONTROLLER_TITLE;

/**
 * JavaFX form controller for RecordLabel.fxml.
 * <p>
 * Created on 6/11/2018.
 *
 * @author Vasilii Komarov
 */
public class RecordLabelController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(RecordLabelController.class);

    @FXML
    private TableView<RecordLabel> recordLabelTable;
    @FXML
    private TableColumn<RecordLabel, Long> clId;
    @FXML
    private TableColumn<RecordLabel, String> clName;
    @FXML
    private TableColumn<RecordLabel, String> clCountry;
    @FXML
    private TableColumn<RecordLabel, Integer> clFoundationYear;

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

    private ObservableList<RecordLabel> recordLabels;

    private RecordLabelService recordLabelService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        recordLabelService = new RecordLabelService(new RecordLabelDAO());
        setCellValueFactories();
        recordLabels = FXCollections.observableArrayList();
        recordLabelTable.setItems(recordLabels);
        addListenersOnTextFields();
    }

    @FXML
    private void btFindByIdClick(ActionEvent actionEvent) {
        this.recordLabels.clear();
        try {
            Long id = Long.parseLong(tfFindById.getText());
            RecordLabel recordLabel = recordLabelService.findById(id);
            this.recordLabels.add(recordLabel);
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @FXML
    private void btFindByNameClick(ActionEvent actionEvent) {
        this.recordLabels.clear();
        String name = tfFindByName.getText();
        List<RecordLabel> recordLabels = recordLabelService.findAllByName(name);
        this.recordLabels.addAll(recordLabels);
    }

    @FXML
    private void btFindAllClick(ActionEvent actionEvent) {
        this.recordLabels.clear();
        List<RecordLabel> recordLabels = recordLabelService.findAll();
        this.recordLabels.addAll(recordLabels);
    }

    @FXML
    private void btCreateClick(ActionEvent actionEvent) {
        showCreateUpdateWindow(CREATE_UPDATE_RECORD_LABEL_CONTROLLER_FXML,
                CREATE_RECORD_LABEL_CONTROLLER_TITLE, true);
    }

    @FXML
    private void btUpdateClick(ActionEvent actionEvent) {
        showCreateUpdateWindow(CREATE_UPDATE_RECORD_LABEL_CONTROLLER_FXML,
                UPDATE_RECORD_LABEL_CONTROLLER_TITLE, false);
    }

    @FXML
    private void btDeleteByIdClick(ActionEvent actionEvent) {
        this.recordLabels.clear();
        try {
            Long id = Long.parseLong(tfDeleteById.getText());
            recordLabelService.delete(id);
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());
        } catch (ForeignKeyViolationException e) {
            LOGGER.error(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error record label deleting");
            alert.setHeaderText("Operation does not possible");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
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
            LOGGER.error(String.format("Error loading fxml-file: %s", MAIN_FORM_FXML));
        }
    }

    protected void createRecordLabel(RecordLabel recordLabel) {
        this.recordLabels.clear();
        RecordLabel createdRecordLabel = recordLabelService.create(recordLabel);
        this.recordLabels.add(createdRecordLabel);
    }

    protected void updateRecordLabel(RecordLabel recordLabel, Long id) {
        this.recordLabels.clear();
        RecordLabel updatedRecordLabel = recordLabelService.update(recordLabel, id);
        this.recordLabels.add(updatedRecordLabel);
    }

    private void showCreateUpdateWindow(String fxmlFileName, String title, boolean isCreationOperation) {
        Stage createUpdateRecordLabelStage = new Stage();
        URL fxmlFile = ClassLoader.getSystemClassLoader().getResource(fxmlFileName);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlFile);
            Pane root = fxmlLoader.load();

            CreateUpdateRecordLabelController controller = fxmlLoader.getController();
            controller.setRecordLabelController(this);
            controller.setTfIdDisable(isCreationOperation);

            Scene scene = new Scene(root);

            URL cssFile = ClassLoader.getSystemClassLoader().getResource(CSS_FILE);
            scene.getStylesheets().add((cssFile).toExternalForm());

            Stage mainStage = (Stage) btCreate.getScene().getWindow();

            createUpdateRecordLabelStage.setTitle(title);
            createUpdateRecordLabelStage.setResizable(false);
            createUpdateRecordLabelStage.setScene(scene);
            createUpdateRecordLabelStage.initModality(Modality.WINDOW_MODAL);
            createUpdateRecordLabelStage.initOwner(mainStage);
            createUpdateRecordLabelStage.show();
        } catch (IOException e) {
            LOGGER.error(String.format("Error loading fxml-file: %s", fxmlFileName));
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
        clName.setCellValueFactory(new PropertyValueFactory<>(RECORD_LABEL_NAME));
        clCountry.setCellValueFactory(new PropertyValueFactory<>(RECORD_LABEL_COUNTRY));
        clFoundationYear.setCellValueFactory(new PropertyValueFactory<>(RECORD_LABEL_FOUNDATION_YEAR));
    }
}
