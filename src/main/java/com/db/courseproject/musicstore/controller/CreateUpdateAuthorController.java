package com.db.courseproject.musicstore.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * <p>
 * Created on 6/17/2018.
 *
 * @author Vasilii Komarov
 */
public class CreateUpdateAuthorController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(CreateUpdateAuthorController.class);

    @FXML
    private TextField tfId;

    //private ObservableList<AuthorType> authorTypes;

    private AuthorController authorController;
    private boolean isCreationOperation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //authorTypes.addAll(AuthorType.values());
        //cbAuthorType.setItems(authorTypes);
        //cbAuthorType.getSelectionModel().select(0);
    }

    protected void setTfIdDisable(boolean param) {
        tfId.setDisable(param);
        isCreationOperation = param;
    }

    protected void setAuthorController(AuthorController authorController) {
        this.authorController = authorController;
    }
}
