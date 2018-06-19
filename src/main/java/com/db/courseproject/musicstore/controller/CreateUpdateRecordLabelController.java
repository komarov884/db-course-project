package com.db.courseproject.musicstore.controller;

import com.db.courseproject.musicstore.model.RecordLabel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * JavaFX form controller for CreateUpdateRecordLabel.fxml.
 * <p>
 * Created on 6/17/2018.
 *
 * @author Vasilii Komarov
 */
public class CreateUpdateRecordLabelController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(CreateUpdateRecordLabelController.class);

    @FXML
    private TextField tfId;
    @FXML
    private TextField tfName;
    @FXML
    private TextField tfCountry;
    @FXML
    private TextField tfFoundationYear;

    @FXML
    private Button btConfirm;
    @FXML
    private Button btCancel;

    private RecordLabelController recordLabelController;
    private boolean isCreationOperation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addListenersOnTextFields();
    }

    @FXML
    private void btConfirmClick(ActionEvent actionEvent) {
        if (checkRequiredFields()) {
            try {
                RecordLabel recordLabel = new RecordLabel();
                recordLabel.setName(getName());
                recordLabel.setCountry(getCountry());
                recordLabel.setFoundationYear(getFoundationYear());

                if (isCreationOperation) {
                    recordLabelController.createRecordLabel(recordLabel);
                } else {
                    Long id = getId();
                    recordLabel.setId(id);
                    recordLabelController.updateRecordLabel(recordLabel, id);
                }

                btCancelClick(null);
            } catch (NumberFormatException e) {
                LOGGER.error(e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Operation does not possible");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void btCancelClick(ActionEvent actionEvent) {
        Stage stage = (Stage) btCancel.getScene().getWindow();
        stage.close();
    }

    protected void setTfIdDisable(boolean param) {
        tfId.setDisable(param);
        isCreationOperation = param;
    }

    protected void setRecordLabelController(RecordLabelController recordLabelController) {
        this.recordLabelController = recordLabelController;
    }

    private boolean checkRequiredFields() {
        if (tfName.getText().isEmpty()
                || (!isCreationOperation && tfId.getText().isEmpty())) {
            LOGGER.info("Not all required fields are filled");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Operation does not possible");
            alert.setContentText("Fill all required fields");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private Long getId() {
        return Long.parseLong(tfId.getText());
    }

    private String getName() {
        return tfName.getText();
    }

    private String getCountry() {
        return tfCountry.getText().isEmpty() ? null : tfCountry.getText();
    }

    private Integer getFoundationYear() {
        return tfFoundationYear.getText().isEmpty() ? null : Integer.parseInt(tfFoundationYear.getText());
    }

    private void addListenersOnTextFields() {
        tfId.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfId.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        tfFoundationYear.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfId.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
}
