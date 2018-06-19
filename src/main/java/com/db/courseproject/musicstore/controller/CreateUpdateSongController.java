package com.db.courseproject.musicstore.controller;

import com.db.courseproject.musicstore.model.Author;
import com.db.courseproject.musicstore.model.ExtendedSong;
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
 * Created on 6/17/2018.
 *
 * @author Vasilii Komarov
 */
public class CreateUpdateSongController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(CreateUpdateSongController.class);

    @FXML
    private TextField tfId;
    @FXML
    private TextField tfAlbumId;
    @FXML
    private TextField tfOrderNumber;
    @FXML
    private TextField tfTitle;
    @FXML
    private TextArea taAuthorIds;

    @FXML
    private Button btConfirm;
    @FXML
    private Button btCancel;

    private SongController songController;
    private boolean isCreationOperation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addListenersOnTextFields();
    }

    @FXML
    private void btConfirmClick(ActionEvent actionEvent) {
        if (checkRequiredFields()) {
            try {
                ExtendedSong song = new ExtendedSong();
                song.setAlbumId(getAlbumId());
                song.setOrderNumber(getOrderNumber());
                song.setTitle(getTitle());
                song.setAuthors(getAuthors());

                if (isCreationOperation) {
                    songController.createSong(song);
                } else {
                    Long id = getId();
                    song.setId(id);
                    songController.updateSong(song, id);
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

    protected void setSongController(SongController songController) {
        this.songController = songController;
    }

    private boolean checkRequiredFields() {
        if (tfTitle.getText().isEmpty()
                || tfOrderNumber.getText().isEmpty()
                || tfAlbumId.getText().isEmpty()
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

    private Long getAlbumId() {
        return Long.parseLong(tfAlbumId.getText());
    }

    private Integer getOrderNumber() {
        return Integer.parseInt(tfOrderNumber.getText());
    }

    private String getTitle() {
        return tfTitle.getText();
    }

    private List<Author> getAuthors() {
        if (taAuthorIds.getText().isEmpty()) {
            return null;
        } else {
            List<Author> authors = new ArrayList<>();
            String[] authorIds = taAuthorIds.getText().split(",");
            for (String authorId : authorIds) {
                authors.add((Author) new Author().setId(Long.parseLong(authorId)));
            }
            return authors;
        }
    }

    private void addListenersOnTextFields() {
        tfId.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfId.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        tfOrderNumber.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfOrderNumber.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        taAuthorIds.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*") && !newValue.matches(",")) {
                taAuthorIds.setText(newValue.replaceAll("[^\\d,]", ""));
            }
        });
    }
}
