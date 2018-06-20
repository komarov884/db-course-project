package com.db.courseproject.musicstore.controller;

import com.db.courseproject.musicstore.dao.SongDAO;
import com.db.courseproject.musicstore.exception.ForeignKeyViolationException;
import com.db.courseproject.musicstore.model.Author;
import com.db.courseproject.musicstore.model.ExtendedSong;
import com.db.courseproject.musicstore.model.Song;
import com.db.courseproject.musicstore.service.SongService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import static com.db.courseproject.musicstore.util.EntitiesConstants.SONG_ORDER_NUMBER;
import static com.db.courseproject.musicstore.util.EntitiesConstants.SONG_TITLE;
import static com.db.courseproject.musicstore.util.ViewConstants.CSS_FILE;
import static com.db.courseproject.musicstore.util.ViewConstants.MAIN_FORM_FXML;
import static com.db.courseproject.musicstore.util.ViewConstants.MAIN_FORM_TITLE;
import static com.db.courseproject.musicstore.util.ViewConstants.CREATE_UPDATE_SONG_CONTROLLER_FXML;
import static com.db.courseproject.musicstore.util.ViewConstants.CREATE_SONG_CONTROLLER_TITLE;
import static com.db.courseproject.musicstore.util.ViewConstants.UPDATE_SONG_CONTROLLER_TITLE;

/**
 * JavaFX form controller for Song.fxml.
 * <p>
 * Created on 6/11/2018.
 *
 * @author Vasilii Komarov
 */
public class SongController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(SongController.class);

    @FXML
    private TableView<Song> songTable;
    @FXML
    private TableColumn<Song, Long> clId;
    @FXML
    private TableColumn<Song, Integer> clOrderNumber;
    @FXML
    private TableColumn<Song, String> clTitle;

    @FXML
    private Button btShowAuthors;
    @FXML
    private Button btFindById;
    @FXML
    private Button btFindByTitle;
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
    private TextField tfFindByTitle;
    @FXML
    private TextField tfDeleteById;

    private ObservableList<Song> songs;

    private SongService songService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        songService = new SongService(new SongDAO());
        setCellValueFactories();
        songs = FXCollections.observableArrayList();
        songTable.setItems(songs);
        addListenersOnTextFields();
    }

    @FXML
    private void btFindByIdClick(ActionEvent actionEvent) {
        this.songs.clear();
        try {
            Long id = Long.parseLong(tfFindById.getText());
            Song song = songService.findById(id);
            this.songs.add(song);
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @FXML
    private void btFindByTitleClick(ActionEvent actionEvent) {
        this.songs.clear();
        String title = tfFindByTitle.getText();
        List<Song> songs = songService.findAllByTitle(title);
        this.songs.addAll(songs);
    }

    @FXML
    private void btFindAllClick(ActionEvent actionEvent) {
        this.songs.clear();
        List<Song> songs = songService.findAll();
        this.songs.addAll(songs);
    }

    @FXML
    private void btCreateClick(ActionEvent actionEvent) {
        showCreateUpdateWindow(CREATE_UPDATE_SONG_CONTROLLER_FXML,
                CREATE_SONG_CONTROLLER_TITLE, true);
    }

    @FXML
    private void btUpdateClick(ActionEvent actionEvent) {
        showCreateUpdateWindow(CREATE_UPDATE_SONG_CONTROLLER_FXML,
                UPDATE_SONG_CONTROLLER_TITLE, false);
    }

    @FXML
    private void btDeleteByIdClick(ActionEvent actionEvent) {
        this.songs.clear();
        try {
            Long id = Long.parseLong(tfDeleteById.getText());
            songService.delete(id);
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());
        } catch (ForeignKeyViolationException e) {
            LOGGER.error(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error song deleting");
            alert.setHeaderText("Operation does not possible");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void btShowAuthorsClick(ActionEvent actionEvent) {
        Song selectedSong = songTable.getSelectionModel().getSelectedItem();
        if (selectedSong != null) {
            List<Author> selectedAuthors = selectedSong.getAuthors();
            if (selectedAuthors != null && !selectedAuthors.isEmpty()) {
                showInfoDialog("Authors",
                        String.format("Authors of song with id: %s", selectedSong.getId()),
                        formatAuthors(selectedAuthors));
            } else {
                showInfoDialog("Authors", "No authors", "-");
            }
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

    protected void createSong(ExtendedSong song) {
        this.songs.clear();
        Song createdSong = songService.create(song);
        this.songs.add(createdSong);
    }

    protected void updateSong(ExtendedSong song, Long id) {
        this.songs.clear();
        Song updatedSong = songService.update(song, id);
        this.songs.add(updatedSong);
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
        clOrderNumber.setCellValueFactory(new PropertyValueFactory<>(SONG_ORDER_NUMBER));
        clTitle.setCellValueFactory(new PropertyValueFactory<>(SONG_TITLE));
    }

    private void showCreateUpdateWindow(String fxmlFileName, String title, boolean isCreationOperation) {
        Stage createUpdateSongStage = new Stage();
        URL fxmlFile = ClassLoader.getSystemClassLoader().getResource(fxmlFileName);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlFile);
            Pane root = fxmlLoader.load();

            CreateUpdateSongController controller = fxmlLoader.getController();
            controller.setSongController(this);
            controller.setTfIdDisable(isCreationOperation);

            Scene scene = new Scene(root);

            URL cssFile = ClassLoader.getSystemClassLoader().getResource(CSS_FILE);
            scene.getStylesheets().add((cssFile).toExternalForm());

            Stage mainStage = (Stage) btCreate.getScene().getWindow();

            createUpdateSongStage.setTitle(title);
            createUpdateSongStage.setResizable(false);
            createUpdateSongStage.setScene(scene);
            createUpdateSongStage.initModality(Modality.WINDOW_MODAL);
            createUpdateSongStage.initOwner(mainStage);
            createUpdateSongStage.show();
        } catch (IOException e) {
            LOGGER.error(String.format("Error loading fxml-file: %s", fxmlFileName));
        }
    }

    private void showInfoDialog(String title, String header, String context) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(context);

        ButtonType btBack = new ButtonType("Back");

        alert.getButtonTypes().clear();
        alert.getButtonTypes().add(btBack);

        alert.showAndWait();
    }

    private String formatAuthors(List<Author> authors) {
        StringBuilder builder = new StringBuilder();
        for (Author author : authors) {
            builder.append("[").append(author.getId()).append("]");
            builder.append(" ");
            builder.append(author.getName().getFirstName());
            builder.append(", ");
            builder.append(author.getName().getLastName());
            builder.append(", ");
            builder.append(author.getBirthDate() == null ? "-" : author.getBirthDate());
            builder.append(", ");
            builder.append(author.getAuthorType());
            builder.append("\n");
        }
        return builder.toString();
    }
}
