package com.db.courseproject.musicstore;

import com.db.courseproject.musicstore.util.DBAdministrator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.net.URL;
import java.sql.SQLException;

/**
 * Music store application.
 * <p>
 * The course project on the discipline of "Databases".
 * Student: Vasilii Komarov.
 * Group: IV-44-ZF.
 * <p>
 * Created on 5/14/2018.
 *
 * @author Vasilii Komarov
 */
public class MusicStoreApplication extends Application {
    private static final Logger LOGGER = Logger.getLogger(MusicStoreApplication.class);

    private static final String FXML_FILE = "forms/MainForm.fxml";
    private static final String CSS_FILE = "css/styles.css";
    private static final String FORM_TITLE = "Music store";

    private static final String CREATE_TABLES_PARAM = "createTables";
    private static final String INSERT_DATA_PARAM = "insertData";
    private static final String DROP_TABLES_PARAM = "dropTables";

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlFile = ClassLoader.getSystemClassLoader().getResource(FXML_FILE);

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlFile);
        Pane root = fxmlLoader.load();

        //Scene scene = new Scene(root, FORM_WIDTH, FORM_HEIGHT);
        Scene scene = new Scene(root);

        URL cssFile = ClassLoader.getSystemClassLoader().getResource(CSS_FILE);
        scene.getStylesheets().add((cssFile).toExternalForm());

        primaryStage.setTitle(FORM_TITLE);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Application entry point.
     * Takes one of the following program arguments: "createTables", "insertData", "dropTables".
     *
     * @param args program arguments.
     */
    public static void main(String[] args) {
        executeActionsOnDB(args);
        launch(args);
    }

    private static void executeActionsOnDB(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case CREATE_TABLES_PARAM:
                    try {
                        DBAdministrator.createTables();
                    } catch (SQLException e) {
                        LOGGER.warn(e.getMessage());
                    }
                    break;
                case INSERT_DATA_PARAM:
                    try {
                        DBAdministrator.insertData();
                    } catch (SQLException e) {
                        LOGGER.warn(e.getMessage());
                    }
                    break;
                case DROP_TABLES_PARAM:
                    try {
                        DBAdministrator.dropTables();
                    } catch (SQLException e) {
                        LOGGER.warn(e.getMessage());
                    }
                    break;
            }
        }
    }
}
