package com.db.courseproject.musicstore.controller;

import com.db.courseproject.musicstore.model.Album;
import com.db.courseproject.musicstore.model.Artist;
import com.db.courseproject.musicstore.model.Producer;
import com.db.courseproject.musicstore.model.RecordLabel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


/**
 * <p>
 * Created on 6/16/2018.
 *
 * @author Vasilii Komarov
 */
public class CreateUpdateAlbumController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(CreateUpdateAlbumController.class);

    @FXML
    private TextField tfId;
    @FXML
    private TextField tfTitle;
    @FXML
    private TextField tfIssueYear;
    @FXML
    private TextField tfPrice;
    @FXML
    private TextField tfGenre;
    @FXML
    private TextField tfArtistId;
    @FXML
    private TextField tfRecordLabelId;
    @FXML
    private TextArea taProducerIds;

    @FXML
    private Button btConfirm;
    @FXML
    private Button btCancel;

    private AlbumController albumController;
    private boolean isCreationOperation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addListenersOnTextFields();
    }

    @FXML
    private void btConfirmClick(ActionEvent actionEvent) {
        if (checkRequiredFields()) {
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
                    album.setId(getId());
                    albumController.updateAlbum(album);
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

    private boolean checkRequiredFields() {
        if (tfTitle.getText().isEmpty()
                || tfPrice.getText().isEmpty()
                || tfArtistId.getText().isEmpty()
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

    private String getTitle() {
        return tfTitle.getText();
    }

    private Integer getIssueYear() {
        return tfIssueYear.getText().isEmpty() ? null : Integer.parseInt(tfIssueYear.getText());
    }

    private Integer getPrice() {
        return Integer.parseInt(tfPrice.getText());
    }

    private String getGenre() {
        return tfGenre.getText().isEmpty() ? null : tfGenre.getText();
    }

    private Artist getArtist() {
        return (Artist) new Artist()
                .setId(Long.parseLong(tfArtistId.getText()));
    }

    private RecordLabel getRecordLabel() {
        return tfRecordLabelId.getText().isEmpty() ? null
                : new RecordLabel().setId(Long.parseLong(tfRecordLabelId.getText()));
    }

    private List<Producer> getProducers() {
        if (taProducerIds.getText().isEmpty()) {
            return null;
        } else {
            List<Producer> producers = new ArrayList<>();
            String[] producerIds = taProducerIds.getText().split(",");
            for (String producerId : producerIds) {
                producers.add((Producer) new Producer().setId(Long.parseLong(producerId)));
            }
            return producers;
        }
    }

    @FXML
    private void btCancelClick(ActionEvent actionEvent) {
        Stage stage = (Stage) btCancel.getScene().getWindow();
        stage.close();
    }

    public void setTfIdDisable(boolean param) {
        tfId.setDisable(param);
        isCreationOperation = param;
    }

    public void setAlbumController(AlbumController albumController) {
        this.albumController = albumController;
    }

    private void addListenersOnTextFields() {
        tfId.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfId.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        tfIssueYear.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfIssueYear.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        tfPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfPrice.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        tfArtistId.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfArtistId.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        tfRecordLabelId.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfRecordLabelId.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        taProducerIds.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*") && !newValue.matches(",")) {
                taProducerIds.setText(newValue.replaceAll("[^\\d,]", ""));
            }
        });
    }
}
