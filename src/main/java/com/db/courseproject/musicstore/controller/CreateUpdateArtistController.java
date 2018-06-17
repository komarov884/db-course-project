package com.db.courseproject.musicstore.controller;

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
    private TextField tfBirthDate;

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
        /*if (checkRequiredFields()) {
            try {
                Album album = new Album();
                album.setTitle(getTitle());
                album.setIssueYear(getIssueYear());
                album.setPrice(getPrice());
                album.setGenre(getGenre());
                album.setArtist(getArtist());
                album.setRecordLabel(getRecordLabel());
                album.setProducers(getProducers());

                if (isCreationOperation) {
                    albumController.createAlbum(album);
                } else {
                    Long id = getId();
                    album.setId(id);
                    albumController.updateAlbum(album, id);
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
        }*/
    }

    @FXML
    private void btCancelClick(ActionEvent actionEvent) {
        Stage stage = (Stage) btCancel.getScene().getWindow();
        stage.close();
    }

    private void addListenersOnTextFields() {

    }

    protected void setTfIdDisable(boolean param) {
        tfId.setDisable(param);
        isCreationOperation = param;
    }

    protected void setArtistController(ArtistController artistController) {
        this.artistController = artistController;
    }
}
