package com.xinjia.presentation.mailconfigcontroller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import com.xinjia.properties.propertybean.MailConfigPropertyBean;
import com.xinjia.properties.propertybean.propertiesmanager.MailConfigPropertiesManager;
import java.io.IOException;
import java.util.Locale;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesFormController {

    // Real programmers use logging, not System.out.println
    private final static Logger LOG = LoggerFactory.getLogger(MailConfigPropertiesManager.class);

    private MailConfigPropertyBean propertyBean;
    private MailConfigPropertiesManager propertiesManger;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

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

    @FXML
    void pressCancel(ActionEvent event) {

    }

    @FXML
    void pressSave(ActionEvent event) throws IOException {
        propertiesManger.writeTextProperties("", "MailConfig", propertyBean);

        Button saveBtn = (Button)event.getSource();
        Stage stage = (Stage) saveBtn.getScene().getWindow();
        Locale currentLocale = new Locale("en", "CA");
        stage.setTitle(ResourceBundle.getBundle("MessagesBundle", currentLocale).getString("mailTitle"));
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/RootLayout.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void setupProperties(MailConfigPropertiesManager pm, MailConfigPropertyBean pb) {
        this.propertiesManger = pm;
        this.propertyBean = pb;
        doBindings();
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert userNameField != null : "fx:id=\"userNameField\" was not injected: check your FXML file 'PropertiesForm.fxml'.";
        assert emailAddressField != null : "fx:id=\"emailAddressField\" was not injected: check your FXML file 'PropertiesForm.fxml'.";
        assert mailPasswordField != null : "fx:id=\"mailPasswordField\" was not injected: check your FXML file 'PropertiesForm.fxml'.";
        assert imapURLField != null : "fx:id=\"imapURLField\" was not injected: check your FXML file 'PropertiesForm.fxml'.";
        assert smtpURLField != null : "fx:id=\"smtpURLField\" was not injected: check your FXML file 'PropertiesForm.fxml'.";
        assert imapPortField != null : "fx:id=\"imapPortField\" was not injected: check your FXML file 'PropertiesForm.fxml'.";
        assert smtpPortField != null : "fx:id=\"smtpPortField\" was not injected: check your FXML file 'PropertiesForm.fxml'.";
        assert mysqlURLField != null : "fx:id=\"mysqlURLField\" was not injected: check your FXML file 'PropertiesForm.fxml'.";
        assert mysqlDatabaseField != null : "fx:id=\"mysqlDatabaseField\" was not injected: check your FXML file 'PropertiesForm.fxml'.";
        assert mysqlPortField != null : "fx:id=\"mysqlPortField\" was not injected: check your FXML file 'PropertiesForm.fxml'.";
        assert mysqlUserField != null : "fx:id=\"mysqlUserField\" was not injected: check your FXML file 'PropertiesForm.fxml'.";
        assert mysqlPasswordField != null : "fx:id=\"mysqlPasswordField\" was not injected: check your FXML file 'PropertiesForm.fxml'.";

        //propertyBean = new MailConfigPropertyBean();
    }

    private void doBindings() {
        LOG.debug("PropertyBean :" + (propertyBean == null));
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
