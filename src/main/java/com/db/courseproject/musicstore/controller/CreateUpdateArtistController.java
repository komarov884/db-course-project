package com.db.courseproject.musicstore.controller;

import com.db.courseproject.musicstore.model.Artist;
import com.db.courseproject.musicstore.model.FullName;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.net.URL;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * <p>
 * Created on 6/17/2018.
 *
 * @author Vasilii Komarov
 */
public class CreateUpdateArtistController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(CreateUpdateArtistController.class);

    @FXML
    private TextField tfId;
    @FXML
    private TextField tfFirstName;
    @FXML
    private TextField tfLastName;
    @FXML
    private DatePicker dpBirthDate;

    @FXML
    private Button btConfirm;
    @FXML
    private Button btCancel;

    private ArtistController artistController;
    private boolean isCreationOperation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addListenersOnTextFields();
    }

    @FXML
    private void btConfirmClick(ActionEvent actionEvent) {
        if (checkRequiredFields()) {
            try {
                Artist artist = new Artist();
                artist.setName(getName());
                artist.setBirthDate(getBirthDate());

                if (isCreationOperation) {
                    artistController.createArtist(artist);
                } else {
                    Long id = getId();
                    artist.setId(id);
                    artistController.updateArtist(artist, id);
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

    protected void setArtistController(ArtistController artistController) {
        this.artistController = artistController;
    }

    private boolean checkRequiredFields() {
        if (tfFirstName.getText().isEmpty()
                || tfLastName.getText().isEmpty()
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

    private FullName getName() {
        return new FullName()
                .setFirstName(tfFirstName.getText())
                .setLastName(tfLastName.getText());
    }

    private Date getBirthDate() {
        if (dpBirthDate.getValue() == null) {
            return null;
        } else {
            return Date.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
    }

    private void addListenersOnTextFields() {
        tfId.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfId.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
}
