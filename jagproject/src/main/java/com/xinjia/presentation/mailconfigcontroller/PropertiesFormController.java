package com.xinjia.presentation.mailconfigcontroller;

import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import com.xinjia.properties.MailConfigBean;
import com.xinjia.properties.propertybean.propertiesmanager.MailConfigPropertiesManager;
import java.io.IOException;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the Mail Config Form. Allow a user to save properties or
 * cancel (close) the form. After filling the form, the properties are saved to
 * the mail config properties file. Internationalization is used to display the
 * form in english or in french.
 *
 * @author Xin Jia Cao
 */
public class PropertiesFormController {

    private final static Logger LOG = LoggerFactory.getLogger(MailConfigPropertiesManager.class);

    private MailConfigBean propertyBean;
    private MailConfigPropertiesManager propertiesManager;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="userNameField"
    private TextField userNameField; // Value injected by FXMLLoader

    @FXML // fx:id="emailAddressField"
    private TextField emailAddressField; // Value injected by FXMLLoader

    @FXML // fx:id="mailPasswordField"
    private TextField mailPasswordField; // Value injected by FXMLLoader

    @FXML // fx:id="imapURLField"
    private TextField imapURLField; // Value injected by FXMLLoader

    @FXML // fx:id="smtpURLField"
    private TextField smtpURLField; // Value injected by FXMLLoader

    @FXML // fx:id="imapPortField"
    private TextField imapPortField; // Value injected by FXMLLoader

    @FXML // fx:id="smtpPortField"
    private TextField smtpPortField; // Value injected by FXMLLoader

    @FXML // fx:id="mysqlURLField"
    private TextField mysqlURLField; // Value injected by FXMLLoader

    @FXML // fx:id="mysqlDatabaseField"
    private TextField mysqlDatabaseField; // Value injected by FXMLLoader

    @FXML // fx:id="mysqlPortField"
    private TextField mysqlPortField; // Value injected by FXMLLoader

    @FXML // fx:id="mysqlUserField"
    private TextField mysqlUserField; // Value injected by FXMLLoader

    @FXML // fx:id="mysqlPasswordField"
    private TextField mysqlPasswordField; // Value injected by FXMLLoader

    @FXML // fx:id="mailConfigGrid"
    private GridPane mailConfigGrid; // Value injected by FXMLLoader

    
    /**
     * Called by the FXMLLoader when initialization is complete. When the FXML
     * is loaded, if a control is not present, an exception is thrown and quits
     * the FXML loading process. Set up the properties.
     *
     * @throws IOException
     */
    @FXML
    void initialize() throws IOException {
        LOG.info("Mail Config Form Loaded");

        assert mailConfigGrid != null : "fx:id=\"mailConfigGrid\" was not injected: check your FXML file 'MailConfigPropertiesForm.fxml'.";
        assert userNameField != null : "fx:id=\"userNameField\" was not injected: check your FXML file 'MailConfigPropertiesForm.fxml'.";
        assert emailAddressField != null : "fx:id=\"emailAddressField\" was not injected: check your FXML file 'MailConfigPropertiesForm.fxml'.";
        assert mailPasswordField != null : "fx:id=\"mailPasswordField\" was not injected: check your FXML file 'MailConfigPropertiesForm.fxml'.";
        assert imapURLField != null : "fx:id=\"imapURLField\" was not injected: check your FXML file 'MailConfigPropertiesForm.fxml'.";
        assert smtpURLField != null : "fx:id=\"smtpURLField\" was not injected: check your FXML file 'MailConfigPropertiesForm.fxml'.";
        assert imapPortField != null : "fx:id=\"imapPortField\" was not injected: check your FXML file 'MailConfigPropertiesForm.fxml'.";
        assert smtpPortField != null : "fx:id=\"smtpPortField\" was not injected: check your FXML file 'MailConfigPropertiesForm.fxml'.";
        assert mysqlURLField != null : "fx:id=\"mysqlURLField\" was not injected: check your FXML file 'MailConfigPropertiesForm.fxml'.";
        assert mysqlDatabaseField != null : "fx:id=\"mysqlDatabaseField\" was not injected: check your FXML file 'MailConfigPropertiesForm.fxml'.";
        assert mysqlPortField != null : "fx:id=\"mysqlPortField\" was not injected: check your FXML file 'MailConfigPropertiesForm.fxml'.";
        assert mysqlUserField != null : "fx:id=\"mysqlUserField\" was not injected: check your FXML file 'MailConfigPropertiesForm.fxml'.";
        assert mysqlPasswordField != null : "fx:id=\"mysqlPasswordField\" was not injected: check your FXML file 'MailConfigPropertiesForm.fxml'.";

        setupProperties();
    }
    
    /**
     * Event handler for the Cancel button. Get the current stage and close it
     * without saving any fields.
     */
    @FXML
    void pressCancel() throws IOException {
        Stage formStage = (Stage) userNameField.getScene().getWindow();
        formStage.close();
    }

    /**
     * Event handler for the Save button. Check if all the fields are filled in
     * the form. If they are, write/update the properties and close the stage.
     *
     * @throws IOException
     */
    @FXML
    void pressSave() throws IOException {
        boolean isAllFilled = areFieldsNotEmpty();
        if (isAllFilled) {
            propertiesManager.writeTextProperties("", "MailConfig", propertyBean);
            Stage stage = (Stage) userNameField.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Check whether or not all fields are filled in the form. If at least one
     * field is empty, an alert pops up asking the user to fill everything.
     *
     * @return true if everything is filled and false otherwise.
     */
    private boolean areFieldsNotEmpty() {
        boolean isAllFilled = true;
        for (Node n : mailConfigGrid.getChildren()) {
            if (n instanceof TextField) {
                if (((TextField) n).getText() == null || ((TextField) n).getText().trim().isEmpty()) {
                    displayEmptyFieldError();
                    isAllFilled = false;
                    break;
                }
            }
        }
        LOG.debug("Are all fields filled? " + isAllFilled);
        return isAllFilled;
    }

    /**
     * Display an alert dialog if at least of the fields is empty when the user
     * clicks on Save.
     */
    private void displayEmptyFieldError() {
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.setTitle(resources.getString("errorTitle"));
        dialog.setHeaderText(resources.getString("emptyFieldHeader"));
        dialog.setContentText(resources.getString("errorEmptyField"));
        dialog.show();
    }

    /**
     * Set up the properties Manager and the property bean when the FXML is
     * loaded. The mail config properties saved are displayed in the form. Bind
     * the JavaFX TextFields inputs to the property bean.
     *
     * @throws IOException
     */
    public void setupProperties() throws IOException {
        propertiesManager = new MailConfigPropertiesManager();
        propertyBean = new MailConfigBean();
        propertiesManager.loadTextProperties(propertyBean, "", "MailConfig");

        //bind the TextFields input to the property bean
        doBindings();
    }

    /**
     * Bind the TextFields input from the form to the property bean so that
     * whenever one of them changes, the other changes also.
     */
    private void doBindings() {
        Bindings.bindBidirectional(userNameField.textProperty(), propertyBean.userNameProperty());
        Bindings.bindBidirectional(emailAddressField.textProperty(), propertyBean.emailAddressProperty());
        Bindings.bindBidirectional(mailPasswordField.textProperty(), propertyBean.mailPasswordProperty());
        Bindings.bindBidirectional(imapURLField.textProperty(), propertyBean.imapURLProperty());
        Bindings.bindBidirectional(smtpURLField.textProperty(), propertyBean.smtpURLProperty());
        Bindings.bindBidirectional(imapPortField.textProperty(), propertyBean.imapPortProperty());
        Bindings.bindBidirectional(smtpPortField.textProperty(), propertyBean.smtpPortProperty());
        Bindings.bindBidirectional(mysqlURLField.textProperty(), propertyBean.mysqlURLProperty());
        Bindings.bindBidirectional(mysqlDatabaseField.textProperty(), propertyBean.mysqlDatabaseProperty());
        Bindings.bindBidirectional(mysqlPortField.textProperty(), propertyBean.mysqlPortProperty());
        Bindings.bindBidirectional(mysqlUserField.textProperty(), propertyBean.mysqlUserProperty());
        Bindings.bindBidirectional(mysqlPasswordField.textProperty(), propertyBean.mysqlPasswordProperty());
    }
}
