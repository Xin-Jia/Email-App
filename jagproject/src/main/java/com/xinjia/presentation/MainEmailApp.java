
package com.xinjia.presentation;

import com.xinjia.presentation.mailconfigcontroller.PropertiesFormController;
import com.xinjia.properties.propertybean.MailConfigPropertyBean;
import com.xinjia.properties.propertybean.propertiesmanager.MailConfigPropertiesManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import static javafx.application.Application.launch;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;

/**
 * This class represents the common whaty in which a JavaFX application begins
 * that uses FXML/Controller architecture.
 *
 * @author Xin Jia Cao
 * @version 2.0
 */
public class MainEmailApp extends Application {

    // slf4j log4j logger
    private final Logger log = LoggerFactory.getLogger(this.getClass()
            .getName());

    private Stage primaryStage;
    private Parent rootPane;
    private MailConfigPropertyBean propertyBean;
    private MailConfigPropertiesManager pm;

    private final Locale currentLocale;
    
    

    /**
     * Default constructor that instantiates the DAO object. This is done here
     * rather than in the fxml controller so that it can be shared in other
     * controllers if they existed. Optionally, being used to support changing
     * the locale to see if i18n is functioning. Methods that access the
     * resource bundles directly are overloaded to use a Locale object
     */
    public MainEmailApp() {

        // Changing Locale is optional
        // Set locale to the default as determined by the JVM
        //currentLocale = Locale.getDefault();
        // Explicit change to either English or French Canada. Only one can be used.
        // Using Locale constructor
        currentLocale = new Locale("en", "CA");
        //currentLocale = new Locale("fr", "CA");
        // Using supplied static Locale objects
        // currentLocale = Locale.CANADA;
        // currentLocale = Locale.CANADA_FRENCH;
        log.debug("Locale = " + currentLocale);

    }

    /**
     * All JavaFX programs must override start and receive the Stage object from
     * the framework.After decorating the Stage it calls upon another method to
     * create the Scene.
     *
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(ResourceBundle.getBundle("MessagesBundle", currentLocale).getString("title"));
        this.primaryStage.getIcons().add(new Image("/images/emailIcon.png"));

        // Set the application icon using getResourceAsStream because the image
        // file is in the jar.
//        this.primaryStage.getIcons().add(
//                new Image(MainApp.class
//                        .getResourceAsStream("/images/bluefish_icon.png")));

        retrieveMailConfig();
        initRootLayout();
        primaryStage.show();
    }

    private void retrieveMailConfig() throws IOException {
        pm = new MailConfigPropertiesManager();
        propertyBean = new MailConfigPropertyBean();
        pm.loadTextProperties(propertyBean, "", "MailConfig");
        log.debug(propertyBean.toString());
    }
    /**
     * The stop method is called before the stage is closed. You can use this
     * method to perform any actions that must be carried out before the program
     * ends. The JavaFX GUI is still running. The only action you cannot perform
     * is to cancel the Platform.exit() that led to this method.
     */
    @Override
    public void stop() {
        log.info("Stage is closing");
    }

    /**
     * Load the layout and controller for an FXML application. 
     */
    public void initRootLayout() {

        try {
            // Instantiate a FXMLLoader object
            FXMLLoader loader = new FXMLLoader();

            // Configure the FXMLLoader with the i18n locale resource bundles
            loader.setResources(ResourceBundle.getBundle("MessagesBundle", currentLocale));

            // Connect the FXMLLoader to the fxml file that is stored in the jar
            loader.setLocation(MainEmailApp.class
                    .getResource("/fxml/MailConfigPropertiesForm.fxml"));

            // The load command returns a reference to the root pane of the fxml file
            rootPane = (GridPane) loader.load();

            // Retreive a refernce to the controller from the FXMLLoader
            PropertiesFormController controller = loader.getController();

            // You can now call on methods in the controller, usually to provide
            // supplemental information
            controller.setupProperties(pm, propertyBean); 
            
            // Instantiate the scene with the root layout.
            Scene scene = new Scene(rootPane);

            // Put the Scene on the Stage
            primaryStage.setScene(scene);

        } catch (IOException ex) {
            log.error("Error displaying form", ex);
            errorAlert(ex.getMessage());
        }
    }

    /**
     * Error message popup dialog
     *
     * @param msg
     */
    private void errorAlert(String msg) {
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.setTitle(ResourceBundle.getBundle("MessagesBundle", currentLocale).getString("errorTitle"));
        dialog.setHeaderText(ResourceBundle.getBundle("MessagesBundle", currentLocale).getString("errorTitle"));
        dialog.setContentText(ResourceBundle.getBundle("MessagesBundle", currentLocale).getString("errorText"));
        dialog.show();
    }

    /**
     * Where it all begins
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}

