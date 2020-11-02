package com.xinjia.presentation;

import com.xinjia.presentation.mailconfigcontroller.PropertiesFormController;
import com.xinjia.presentation.rootcontroller.RootLayoutSplitController;
import com.xinjia.properties.MailConfigBean;
import com.xinjia.properties.propertybean.propertiesmanager.MailConfigPropertiesManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.Paths.get;
import java.util.Locale;
import java.util.ResourceBundle;
import static javafx.application.Application.launch;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

/**
 * This class starts the Email App. It creates the Mail Config Form and the main
 * Email App scene and stage. The stage displayed at runtime depends on whether
 * the properties file exists or not, as well as if at least one of its fields
 * is empty. The current Locale is initialized here.
 *
 * @author Xin Jia Cao
 * @version 2.0
 */
public class MainEmailApp extends Application {

    private Stage primaryStage;
    private MailConfigBean propertyBean;
    private final Locale currentLocale;
    private final static Logger LOG = LoggerFactory.getLogger(MainEmailApp.class);

    /**
     * Default constructor that instantiates the current Locale used. Will need
     * to add a EmailDAO object when accessing the database.
     */
    public MainEmailApp() {
        //Left-out comments so we can test internationalization quicker

        //currentLocale = Locale.getDefault();
        //currentLocale = new Locale("fr", "CA");
        currentLocale = new Locale("en", "CA");
        // currentLocale = Locale.CANADA;
        // currentLocale = Locale.CANADA_FRENCH;
        LOG.debug("Locale = " + currentLocale);

        //TODO : instantiate a EmailDAO object
    }

    /**
     * Instantiate the primary stage with the Root (or email app) scene if the
     * properties file exists and all fields are filled.If not, it creates a new
     * stage to display the Mail Config Form. If the user fills all the fields
     * and save them, the Root scene will appear. If the user clicks on Cancel
     * or the x button, the stage closes and nothing is saved.
     *
     * @param primaryStage The primary stage
     * @throws java.io.IOException
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        retrieveMailConfig();

        //Check if the properties files exists and no fields are empty
        if (!checkProperties()) {
            //If not, display the mail config form
            createMailConfigScene();
        }
        //Retrieve the properties again in case the form was displayed
        retrieveMailConfig();
        //Check whether the user filled all the fields in case the form was displayed
        if (checkProperties()) {
            //Display the Root scene if the properties are all filled and the file exists
            createEmailAppScene();
        }
    }

    /**
     * Create the Root (or email app) scene, set it to the primary stage and
     * display it.
     *
     * @throws IOException
     */
    private void createEmailAppScene() throws IOException {
        LOG.info("Creating Root/Email App Scene");

        FXMLLoader loader = new FXMLLoader();
        loader.setResources(ResourceBundle.getBundle("MessagesBundle", currentLocale));
        loader.setLocation(this.getClass().getResource("/fxml/RootLayout.fxml"));

        Parent root = (BorderPane) loader.load();
        RootLayoutSplitController emailAppController = loader.getController();
        Scene scene = new Scene(root);

        this.primaryStage.setScene(scene);
        this.primaryStage.setTitle(ResourceBundle.getBundle("MessagesBundle", currentLocale).getString("mailTitle"));
        this.primaryStage.getIcons().add(new Image("/images/emailIcon.png"));
        this.primaryStage.show();
    }

    /**
     * Create the Mail Config Form scene and a new stage. Set the scene to the
     * stage, and then show and wait so when the user clicks on save or cancel,
     * the method that called this method will continue so it can display the
     * Root scene if all fields are entered.
     *
     * @throws IOException
     */
    private void createMailConfigScene() throws IOException {
        LOG.info("Creating Mail Config Scene");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/fxml/MailConfigPropertiesForm.fxml"));
        loader.setResources(ResourceBundle.getBundle("MessagesBundle", currentLocale));

        Parent root = (GridPane) loader.load();
        PropertiesFormController propFormController = loader.getController();

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.getIcons().add(new Image("/images/emailIcon.png"));
        stage.setTitle(ResourceBundle.getBundle("MessagesBundle", currentLocale).getString("title"));
        stage.setScene(scene);
        stage.showAndWait();

    }

    /**
     * Retrieve the Mail Config properties so we can later check if the property
     * bean has all its fields not empty.
     *
     * @throws IOException
     */
    private void retrieveMailConfig() throws IOException {
        MailConfigPropertiesManager propertiesManager = new MailConfigPropertiesManager();
        propertyBean = new MailConfigBean();
        //read the mail config properties file and set the property bean fields accordingly
        propertiesManager.loadTextProperties(propertyBean, "", "MailConfig");
    }

    /**
     * Check if the mail config properties file exists as well as if at least of
     * one its fields is empty.
     *
     * @return false if at least one property is empty or if the file does not
     * exists.
     * @throws IOException
     */
    private boolean checkProperties() throws IOException {
        Path txtFile = get("MailConfig.properties");

        boolean arePropertiesValid = true;
        boolean hasEmptyField = propertyBean.getEmailAddress().equals("")
                || propertyBean.getImapPort().equals("")
                || propertyBean.getImapURL().equals("")
                || propertyBean.getMailPassword().equals("")
                || propertyBean.getMysqlDatabase().equals("")
                || propertyBean.getMysqlPassword().equals("")
                || propertyBean.getMysqlPort().equals("")
                || propertyBean.getMysqlURL().equals("")
                || propertyBean.getMysqlUser().equals("")
                || propertyBean.getSmtpPort().equals("")
                || propertyBean.getSmtpURL().equals("")
                || propertyBean.getUserName().equals("");
        if (hasEmptyField || !Files.exists(txtFile)) {
            arePropertiesValid = false;
        }
        LOG.info("Are properties non-empty & file exists? " + arePropertiesValid);
        return arePropertiesValid;
    }

    /**
     * The stop method is called before the stage is closed. Might need it later
     * to perform any tasks that must be performed before the stage is closed.
     */
    @Override
    public void stop() {
        LOG.info("Stage is closing");
    }

    /**
     * Main method that launches the program.
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
