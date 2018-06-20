package com.db.courseproject.musicstore.controller;

import com.db.courseproject.musicstore.dao.AlbumDAO;
import com.db.courseproject.musicstore.exception.ForeignKeyViolationException;
import com.db.courseproject.musicstore.model.Album;
import com.db.courseproject.musicstore.model.Artist;
import com.db.courseproject.musicstore.model.Producer;
import com.db.courseproject.musicstore.model.RecordLabel;
import com.db.courseproject.musicstore.model.Song;
import com.db.courseproject.musicstore.service.AlbumService;
import javafx.beans.property.SimpleObjectProperty;
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
import static com.db.courseproject.musicstore.util.EntitiesConstants.ALBUM_TITLE;
import static com.db.courseproject.musicstore.util.EntitiesConstants.ALBUM_ISSUE_YEAR;
import static com.db.courseproject.musicstore.util.EntitiesConstants.ALBUM_PRICE;
import static com.db.courseproject.musicstore.util.EntitiesConstants.ALBUM_GENRE;
import static com.db.courseproject.musicstore.util.ViewConstants.CSS_FILE;
import static com.db.courseproject.musicstore.util.ViewConstants.MAIN_FORM_FXML;
import static com.db.courseproject.musicstore.util.ViewConstants.MAIN_FORM_TITLE;
import static com.db.courseproject.musicstore.util.ViewConstants.CREATE_UPDATE_ALBUM_CONTROLLER_FXML;
import static com.db.courseproject.musicstore.util.ViewConstants.CREATE_ALBUM_CONTROLLER_TITLE;
import static com.db.courseproject.musicstore.util.ViewConstants.UPDATE_ALBUM_CONTROLLER_TITLE;

/**
 * JavaFX form controller for Album.fxml.
 * <p>
 * Created on 6/11/2018.
 *
 * @author Vasilii Komarov
 */
public class AlbumController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(AlbumController.class);

    @FXML
    private TableView<Album> albumTable;
    @FXML
    private TableColumn<Album, Long> clId;
    @FXML
    private TableColumn<Album, String> clTitle;
    @FXML
    private TableColumn<Album, Integer> clIssueYear;
    @FXML
    private TableColumn<Album, Integer> clPrice;
    @FXML
    private TableColumn<Album, String> clGenre;
    @FXML
    private TableColumn<Album, Long> clArtistId;
    @FXML
    private TableColumn<Album, Long> clRecordLabelId;

    @FXML
    private Button btShowArtistInfo;
    @FXML
    private Button btShowRecordLabelInfo;
    @FXML
    private Button btShowProducers;
    @FXML
    private Button btShowSongs;
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

    private ObservableList<Album> albums;

    private AlbumService albumService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        albumService = new AlbumService(new AlbumDAO());
        setCellValueFactories();
        albums = FXCollections.observableArrayList();
        albumTable.setItems(albums);
        addListenersOnTextFields();
    }

    @FXML
    private void btFindByIdClick(ActionEvent actionEvent) {
        this.albums.clear();
        try {
            Long id = Long.parseLong(tfFindById.getText());
            Album album = albumService.findById(id);
            this.albums.add(album);
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @FXML
    private void btFindByTitleClick(ActionEvent actionEvent) {
        this.albums.clear();
        String title = tfFindByTitle.getText();
        List<Album> albums = albumService.findAllByTitle(title);
        this.albums.addAll(albums);
    }

    @FXML
    private void btFindAllClick(ActionEvent actionEvent) {
        this.albums.clear();
        List<Album> albums = albumService.findAll();
        this.albums.addAll(albums);
    }

    @FXML
    private void btCreateClick(ActionEvent actionEvent) {
        showCreateUpdateWindow(CREATE_UPDATE_ALBUM_CONTROLLER_FXML,
                CREATE_ALBUM_CONTROLLER_TITLE, true);
    }

    @FXML
    private void btUpdateClick(ActionEvent actionEvent) {
        showCreateUpdateWindow(CREATE_UPDATE_ALBUM_CONTROLLER_FXML,
                UPDATE_ALBUM_CONTROLLER_TITLE, false);
    }

    @FXML
    private void btDeleteByIdClick(ActionEvent actionEvent) {
        this.albums.clear();
        try {
            Long id = Long.parseLong(tfDeleteById.getText());
            albumService.delete(id);
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());
        } catch (ForeignKeyViolationException e) {
            LOGGER.error(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error album deleting");
            alert.setHeaderText("Operation does not possible");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void btShowArtistInfoClick(ActionEvent actionEvent) {
        Album selectedAlbum = albumTable.getSelectionModel().getSelectedItem();
        if (selectedAlbum != null) {
            Artist selectedArtist = selectedAlbum.getArtist();
            if (selectedArtist != null) {
                showInfoDialog("Artist info",
                        String.format("Artist with id: %s", selectedArtist.getId()),
                        formatArtist(selectedArtist));
            } else {
                showInfoDialog("Artist info", "No artists", "-");
            }
        }
    }

    @FXML
    private void btShowRecordLabelInfoClick(ActionEvent actionEvent) {
        Album selectedAlbum = albumTable.getSelectionModel().getSelectedItem();
        if (selectedAlbum != null) {
            RecordLabel selectedRecordLabel = selectedAlbum.getRecordLabel();
            if (selectedRecordLabel != null) {
                showInfoDialog("Record label info",
                        String.format("Record label with id: %s", selectedRecordLabel.getId()),
                        formatRecordLabel(selectedRecordLabel));
            } else {
                showInfoDialog("Record label info", "No record labels", "-");
            }
        }
    }

    @FXML
    private void btShowProducersClick(ActionEvent actionEvent) {
        Album selectedAlbum = albumTable.getSelectionModel().getSelectedItem();
        if (selectedAlbum != null) {
            List<Producer> selectedProducers = selectedAlbum.getProducers();
            if (selectedProducers != null && !selectedProducers.isEmpty()) {
                showInfoDialog("Producers",
                        String.format("Producers of album with id: %s", selectedAlbum.getId()),
                        formatProducers(selectedProducers));
            } else {
                showInfoDialog("Producers", "No producers", "-");
            }
        }
    }

    @FXML
    private void btShowSongsClick(ActionEvent actionEvent) {
        Album selectedAlbum = albumTable.getSelectionModel().getSelectedItem();
        if (selectedAlbum != null) {
            List<Song> selectedSongs = selectedAlbum.getSongs();
            if (selectedSongs != null && !selectedSongs.isEmpty()) {
                showInfoDialog("Songs",
                        String.format("Songs of album with id: %s", selectedAlbum.getId()),
                        formatSongs(selectedSongs));
            } else {
                showInfoDialog("Songs", "No songs", "-");
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

    protected void createAlbum(Album album) {
        this.albums.clear();
        Album createdAlbum = albumService.create(album);
        this.albums.add(createdAlbum);
    }

    protected void updateAlbum(Album album, Long id) {
        this.albums.clear();
        Album updatedAlbum = albumService.update(album, id);
        this.albums.add(updatedAlbum);
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
        clTitle.setCellValueFactory(new PropertyValueFactory<>(ALBUM_TITLE));
        clIssueYear.setCellValueFactory(new PropertyValueFactory<>(ALBUM_ISSUE_YEAR));
        clPrice.setCellValueFactory(new PropertyValueFactory<>(ALBUM_PRICE));
        clGenre.setCellValueFactory(new PropertyValueFactory<>(ALBUM_GENRE));
        clArtistId.setCellValueFactory(param -> {
            Long artistId = null;
            Album album = param.getValue();
            if (album != null) {
                Artist artist = album.getArtist();
                if (artist != null) {
                    artistId = artist.getId();
                }
            }
            return new SimpleObjectProperty<>(artistId);
        });
        clRecordLabelId.setCellValueFactory(param -> {
            Long recordLabelId = null;
            Album album = param.getValue();
            if (album != null) {
                RecordLabel recordLabel = album.getRecordLabel();
                if (recordLabel != null) {
                    recordLabelId = recordLabel.getId();
                }
            }
            return new SimpleObjectProperty<>(recordLabelId);
        });
    }

    private void showCreateUpdateWindow(String fxmlFileName, String title, boolean isCreationOperation) {
        Stage createUpdateAlbumStage = new Stage();
        URL fxmlFile = ClassLoader.getSystemClassLoader().getResource(fxmlFileName);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlFile);
            Pane root = fxmlLoader.load();

            CreateUpdateAlbumController controller = fxmlLoader.getController();
            controller.setAlbumController(this);
            controller.setTfIdDisable(isCreationOperation);

            Scene scene = new Scene(root);

            URL cssFile = ClassLoader.getSystemClassLoader().getResource(CSS_FILE);
            scene.getStylesheets().add((cssFile).toExternalForm());

            Stage mainStage = (Stage) btCreate.getScene().getWindow();

            createUpdateAlbumStage.setTitle(title);
            createUpdateAlbumStage.setResizable(false);
            createUpdateAlbumStage.setScene(scene);
            createUpdateAlbumStage.initModality(Modality.WINDOW_MODAL);
            createUpdateAlbumStage.initOwner(mainStage);
            createUpdateAlbumStage.show();
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

    private String formatArtist(Artist artist) {
        return String.format("First name: %s\n"
                        + "Last name: %s\n"
                        + "Birth date: %s",
                artist.getName().getFirstName(), artist.getName().getLastName(), artist.getBirthDate());
    }

    private String formatRecordLabel(RecordLabel recordLabel) {
        return String.format("Name: %s\n"
                        + "Country: %s\n"
                        + "Foundation year: %s",
                recordLabel.getName(), recordLabel.getCountry(), recordLabel.getFoundationYear());
    }

    private String formatProducers(List<Producer> producers) {
        StringBuilder builder = new StringBuilder();
        for (Producer producer : producers) {
            builder.append("[").append(producer.getId()).append("]");
            builder.append(" ");
            builder.append(producer.getName().getFirstName());
            builder.append(", ");
            builder.append(producer.getName().getLastName());
            builder.append(", ");
            builder.append(producer.getBirthDate());
            builder.append("\n");
        }
        return builder.toString();
    }

    private String formatSongs(List<Song> songs) {
        StringBuilder builder = new StringBuilder();
        for (Song song : songs) {
            builder.append("[").append(song.getId()).append("]");
            builder.append(" ");
            builder.append(song.getOrderNumber()).append(". ");
            builder.append(song.getTitle());
            builder.append("\n");
        }
        return builder.toString();
    }
}
