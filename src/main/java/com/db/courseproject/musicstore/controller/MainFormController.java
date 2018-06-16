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
    private void btAlbumsClick(ActionEvent actionEvent) {
        Stage albumStage = new Stage();
        URL fxmlFile = ClassLoader.getSystemClassLoader().getResource(ALBUM_CONTROLLER_FXML);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlFile);
            Pane root = fxmlLoader.load();

            Scene scene = new Scene(root);

            URL cssFile = ClassLoader.getSystemClassLoader().getResource(CSS_FILE);
            scene.getStylesheets().add((cssFile).toExternalForm());

            albumStage.setTitle(ALBUM_CONTROLLER_TITLE);
            albumStage.setResizable(false);
            albumStage.setScene(scene);
            albumStage.initModality(Modality.WINDOW_MODAL);
            albumStage.show();

            ((Stage) ((Node) (actionEvent.getSource())).getScene().getWindow()).close();
        } catch (IOException e) {
            LOGGER.error(String.format("Error loading fxml-file: %s", ALBUM_CONTROLLER_FXML));
        }
    }
}
