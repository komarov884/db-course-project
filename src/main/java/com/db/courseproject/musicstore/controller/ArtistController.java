package com.db.courseproject.musicstore.controller;

import com.db.courseproject.musicstore.dao.ArtistDAO;
import com.db.courseproject.musicstore.exception.ForeignKeyViolationException;
import com.db.courseproject.musicstore.model.Artist;
import com.db.courseproject.musicstore.model.FullName;
import com.db.courseproject.musicstore.service.ArtistService;
import javafx.beans.property.SimpleStringProperty;
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
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static com.db.courseproject.musicstore.util.EntitiesConstants.ID;
import static com.db.courseproject.musicstore.util.EntitiesConstants.MEMBER_BIRTH_DATE;
import static com.db.courseproject.musicstore.util.ViewConstants.CSS_FILE;
import static com.db.courseproject.musicstore.util.ViewConstants.MAIN_FORM_FXML;
import static com.db.courseproject.musicstore.util.ViewConstants.MAIN_FORM_TITLE;
import static com.db.courseproject.musicstore.util.ViewConstants.CREATE_UPDATE_ARTIST_CONTROLLER_FXML;
import static com.db.courseproject.musicstore.util.ViewConstants.CREATE_ARTIST_CONTROLLER_TITLE;
import static com.db.courseproject.musicstore.util.ViewConstants.UPDATE_ARTIST_CONTROLLER_TITLE;

/**
 * <p>
 * Created on 6/11/2018.
 *
 * @author Vasilii Komarov
 */
public class ArtistController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(ArtistController.class);

    @FXML
    private TableView<Artist> artistTable;
    @FXML
    private TableColumn<Artist, Long> clId;
    @FXML
    private TableColumn<Artist, String> clFirstName;
    @FXML
    private TableColumn<Artist, String> clLastName;
    @FXML
    private TableColumn<Artist, Date> clBirthDate;

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

    private ObservableList<Artist> artists;

    private ArtistService artistService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        artistService = new ArtistService(new ArtistDAO());
        setCellValueFactories();
        artists = FXCollections.observableArrayList();
        artistTable.setItems(artists);
        addListenersOnTextFields();
    }

    @FXML
    private void btFindByIdClick(ActionEvent actionEvent) {
        this.artists.clear();
        try {
            Long id = Long.parseLong(tfFindById.getText());
            Artist artist = artistService.findById(id);
            this.artists.add(artist);
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @FXML
    private void btFindByNameClick(ActionEvent actionEvent) {
        this.artists.clear();
        String name = tfFindByName.getText();
        List<Artist> artists = artistService.findAllByName(name);
        this.artists.addAll(artists);
    }

    @FXML
    private void btFindAllClick(ActionEvent actionEvent) {
        this.artists.clear();
        List<Artist> artists = artistService.findAll();
        this.artists.addAll(artists);
    }

    @FXML
    private void btCreateClick(ActionEvent actionEvent) {
        showCreateUpdateWindow(CREATE_UPDATE_ARTIST_CONTROLLER_FXML,
                CREATE_ARTIST_CONTROLLER_TITLE, true);
    }

    @FXML
    private void btUpdateClick(ActionEvent actionEvent) {
        showCreateUpdateWindow(CREATE_UPDATE_ARTIST_CONTROLLER_FXML,
                UPDATE_ARTIST_CONTROLLER_TITLE, false);
    }

    @FXML
    private void btDeleteByIdClick(ActionEvent actionEvent) {
        this.artists.clear();
        try {
            Long id = Long.parseLong(tfDeleteById.getText());
            artistService.delete(id);
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());
        } catch (ForeignKeyViolationException e) {
            LOGGER.error(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error artist deleting");
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

    protected void createArtist(Artist artist) {
        this.artists.clear();
        Artist createdArtist = artistService.create(artist);
        this.artists.add(createdArtist);
    }

    protected void updateArtist(Artist artist, Long id) {
        this.artists.clear();
        Artist updatedArtist = artistService.update(artist, id);
        this.artists.add(updatedArtist);
    }

    private void showCreateUpdateWindow(String fxmlFileName, String title, boolean isCreationOperation) {
        Stage createUpdateArtistStage = new Stage();
        URL fxmlFile = ClassLoader.getSystemClassLoader().getResource(fxmlFileName);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlFile);
            Pane root = fxmlLoader.load();

            CreateUpdateArtistController controller = fxmlLoader.getController();
            controller.setArtistController(this);
            controller.setTfIdDisable(isCreationOperation);

            Scene scene = new Scene(root);

            URL cssFile = ClassLoader.getSystemClassLoader().getResource(CSS_FILE);
            scene.getStylesheets().add((cssFile).toExternalForm());

            Stage mainStage = (Stage) btCreate.getScene().getWindow();

            createUpdateArtistStage.setTitle(title);
            createUpdateArtistStage.setResizable(false);
            createUpdateArtistStage.setScene(scene);
            createUpdateArtistStage.initModality(Modality.WINDOW_MODAL);
            createUpdateArtistStage.initOwner(mainStage);
            createUpdateArtistStage.show();
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
        clFirstName.setCellValueFactory(param -> {
            String firstName = null;
            Artist artist = param.getValue();
            if (artist != null) {
                FullName fullName = artist.getName();
                if (fullName != null) {
                    firstName = fullName.getFirstName();
                }
            }
            return new SimpleStringProperty(firstName);
        });
        clLastName.setCellValueFactory(param -> {
            String lastName = null;
            Artist artist = param.getValue();
            if (artist != null) {
                FullName fullName = artist.getName();
                if (fullName != null) {
                    lastName = fullName.getLastName();
                }
            }
            return new SimpleStringProperty(lastName);
        });
        clBirthDate.setCellValueFactory(new PropertyValueFactory<>(MEMBER_BIRTH_DATE));
    }
}
