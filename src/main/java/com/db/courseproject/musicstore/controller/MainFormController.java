package com.db.courseproject.musicstore.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URL;

import static com.db.courseproject.musicstore.util.ViewConstants.CSS_FILE;
import static com.db.courseproject.musicstore.util.ViewConstants.ALBUM_CONTROLLER_FXML;
import static com.db.courseproject.musicstore.util.ViewConstants.ALBUM_CONTROLLER_TITLE;
import static com.db.courseproject.musicstore.util.ViewConstants.ARTIST_CONTROLLER_FXML;
import static com.db.courseproject.musicstore.util.ViewConstants.ARTIST_CONTROLLER_TITLE;
import static com.db.courseproject.musicstore.util.ViewConstants.AUTHOR_CONTROLLER_FXML;
import static com.db.courseproject.musicstore.util.ViewConstants.AUTHOR_CONTROLLER_TITLE;
import static com.db.courseproject.musicstore.util.ViewConstants.PRODUCER_CONTROLLER_FXML;
import static com.db.courseproject.musicstore.util.ViewConstants.PRODUCER_CONTROLLER_TITLE;
import static com.db.courseproject.musicstore.util.ViewConstants.RECORD_LABEL_CONTROLLER_FXML;
import static com.db.courseproject.musicstore.util.ViewConstants.RECORD_LABEL_CONTROLLER_TITLE;
import static com.db.courseproject.musicstore.util.ViewConstants.SONG_CONTROLLER_FXML;
import static com.db.courseproject.musicstore.util.ViewConstants.SONG_CONTROLLER_TITLE;

/**
 * JavaFX controller for main form.
 * <p>
 * Created on 6/4/2018.
 *
 * @author Vasilii Komarov
 */
public class MainFormController {
    private static final Logger LOGGER = Logger.getLogger(MainFormController.class);

    @FXML
    private Button btAlbums;
    @FXML
    private Button btArtists;
    @FXML
    private Button btAuthors;
    @FXML
    private Button btProducers;
    @FXML
    private Button btRecordLabels;
    @FXML
    private Button btSongs;

    @FXML
    private void btAlbumsClick(ActionEvent actionEvent) {
        openWindow(ALBUM_CONTROLLER_FXML, ALBUM_CONTROLLER_TITLE, actionEvent);
    }

    @FXML
    private void btArtistsClick(ActionEvent actionEvent) {
        openWindow(ARTIST_CONTROLLER_FXML, ARTIST_CONTROLLER_TITLE, actionEvent);
    }

    @FXML
    private void btAuthorsClick(ActionEvent actionEvent) {
        openWindow(AUTHOR_CONTROLLER_FXML, AUTHOR_CONTROLLER_TITLE, actionEvent);
    }

    @FXML
    private void btProducersClick(ActionEvent actionEvent) {
        openWindow(PRODUCER_CONTROLLER_FXML, PRODUCER_CONTROLLER_TITLE, actionEvent);
    }

    @FXML
    private void btRecordLabelsClick(ActionEvent actionEvent) {
        openWindow(RECORD_LABEL_CONTROLLER_FXML, RECORD_LABEL_CONTROLLER_TITLE, actionEvent);
    }

    @FXML
    private void btSongsClick(ActionEvent actionEvent) {
        openWindow(SONG_CONTROLLER_FXML, SONG_CONTROLLER_TITLE, actionEvent);
    }

    private void openWindow(String fxmlFileName, String title, ActionEvent actionEvent) {
        Stage albumStage = new Stage();
        URL fxmlFile = ClassLoader.getSystemClassLoader().getResource(fxmlFileName);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlFile);
            Pane root = fxmlLoader.load();

            Scene scene = new Scene(root);

            URL cssFile = ClassLoader.getSystemClassLoader().getResource(CSS_FILE);
            scene.getStylesheets().add((cssFile).toExternalForm());

            albumStage.setTitle(title);
            albumStage.setResizable(false);
            albumStage.setScene(scene);
            albumStage.initModality(Modality.WINDOW_MODAL);
            albumStage.show();

            ((Stage) ((Node) (actionEvent.getSource())).getScene().getWindow()).close();
        } catch (IOException e) {
            LOGGER.error(String.format("Error loading fxml-file: %s", fxmlFileName));
        }
    }
}
