package com.db.courseproject.musicstore.controller;

import com.db.courseproject.musicstore.dao.AuthorDAO;
import com.db.courseproject.musicstore.exception.ForeignKeyViolationException;
import com.db.courseproject.musicstore.model.Author;
import com.db.courseproject.musicstore.model.AuthorType;
import com.db.courseproject.musicstore.model.FullName;
import com.db.courseproject.musicstore.service.AuthorService;
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
import static com.db.courseproject.musicstore.util.EntitiesConstants.AUTHOR_AUTHOR_TYPE;
import static com.db.courseproject.musicstore.util.ViewConstants.CSS_FILE;
import static com.db.courseproject.musicstore.util.ViewConstants.MAIN_FORM_FXML;
import static com.db.courseproject.musicstore.util.ViewConstants.MAIN_FORM_TITLE;
import static com.db.courseproject.musicstore.util.ViewConstants.CREATE_UPDATE_AUTHOR_CONTROLLER_FXML;
import static com.db.courseproject.musicstore.util.ViewConstants.CREATE_AUTHOR_CONTROLLER_TITLE;
import static com.db.courseproject.musicstore.util.ViewConstants.UPDATE_AUTHOR_CONTROLLER_TITLE;

/**
 * JavaFX form controller for Author.fxml.
 * <p>
 * Created on 6/11/2018.
 *
 * @author Vasilii Komarov
 */
public class AuthorController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(AuthorController.class);

    @FXML
    private TableView<Author> authorTable;
    @FXML
    private TableColumn<Author, Long> clId;
    @FXML
    private TableColumn<Author, String> clFirstName;
    @FXML
    private TableColumn<Author, String> clLastName;
    @FXML
    private TableColumn<Author, Date> clBirthDate;
    @FXML
    private TableColumn<Author, AuthorType> clAuthorType;

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

    private ObservableList<Author> authors;

    private AuthorService authorService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        authorService = new AuthorService(new AuthorDAO());
        setCellValueFactories();
        authors = FXCollections.observableArrayList();
        authorTable.setItems(authors);
        addListenersOnTextFields();
    }

    @FXML
    private void btFindByIdClick(ActionEvent actionEvent) {
        this.authors.clear();
        try {
            Long id = Long.parseLong(tfFindById.getText());
            Author author = authorService.findById(id);
            this.authors.add(author);
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @FXML
    private void btFindByNameClick(ActionEvent actionEvent) {
        this.authors.clear();
        String name = tfFindByName.getText();
        List<Author> authors = authorService.findAllByName(name);
        this.authors.addAll(authors);
    }

    @FXML
    private void btFindAllClick(ActionEvent actionEvent) {
        this.authors.clear();
        List<Author> authors = authorService.findAll();
        this.authors.addAll(authors);
    }

    @FXML
    private void btCreateClick(ActionEvent actionEvent) {
        showCreateUpdateWindow(CREATE_UPDATE_AUTHOR_CONTROLLER_FXML,
                CREATE_AUTHOR_CONTROLLER_TITLE, true);
    }

    @FXML
    private void btUpdateClick(ActionEvent actionEvent) {
        showCreateUpdateWindow(CREATE_UPDATE_AUTHOR_CONTROLLER_FXML,
                UPDATE_AUTHOR_CONTROLLER_TITLE, false);
    }

    @FXML
    private void btDeleteByIdClick(ActionEvent actionEvent) {
        this.authors.clear();
        try {
            Long id = Long.parseLong(tfDeleteById.getText());
            authorService.delete(id);
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());
        } catch (ForeignKeyViolationException e) {
            LOGGER.error(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error author deleting");
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

    protected void createAuthor(Author author) {
        this.authors.clear();
        Author createdAuthor = authorService.create(author);
        this.authors.add(createdAuthor);
    }

    protected void updateAuthor(Author author, Long id) {
        this.authors.clear();
        Author updatedAuthor = authorService.update(author, id);
        this.authors.add(updatedAuthor);
    }

    private void showCreateUpdateWindow(String fxmlFileName, String title, boolean isCreationOperation) {
        Stage createUpdateAuthorStage = new Stage();
        URL fxmlFile = ClassLoader.getSystemClassLoader().getResource(fxmlFileName);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlFile);
            Pane root = fxmlLoader.load();

            CreateUpdateAuthorController controller = fxmlLoader.getController();
            controller.setAuthorController(this);
            controller.setTfIdDisable(isCreationOperation);

            Scene scene = new Scene(root);

            URL cssFile = ClassLoader.getSystemClassLoader().getResource(CSS_FILE);
            scene.getStylesheets().add((cssFile).toExternalForm());

            Stage mainStage = (Stage) btCreate.getScene().getWindow();

            createUpdateAuthorStage.setTitle(title);
            createUpdateAuthorStage.setResizable(false);
            createUpdateAuthorStage.setScene(scene);
            createUpdateAuthorStage.initModality(Modality.WINDOW_MODAL);
            createUpdateAuthorStage.initOwner(mainStage);
            createUpdateAuthorStage.show();
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
            Author author = param.getValue();
            if (author != null) {
                FullName fullName = author.getName();
                if (fullName != null) {
                    firstName = fullName.getFirstName();
                }
            }
            return new SimpleStringProperty(firstName);
        });
        clLastName.setCellValueFactory(param -> {
            String lastName = null;
            Author author = param.getValue();
            if (author != null) {
                FullName fullName = author.getName();
                if (fullName != null) {
                    lastName = fullName.getLastName();
                }
            }
            return new SimpleStringProperty(lastName);
        });
        clBirthDate.setCellValueFactory(new PropertyValueFactory<>(MEMBER_BIRTH_DATE));
        clAuthorType.setCellValueFactory(new PropertyValueFactory<>(AUTHOR_AUTHOR_TYPE));
    }
}
