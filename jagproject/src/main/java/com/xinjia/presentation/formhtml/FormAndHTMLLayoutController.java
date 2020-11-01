package com.xinjia.presentation.formhtml;

import com.xinjia.properties.propertybean.EmailData;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.web.HTMLEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the Form/HTML Editor. Contain event handlers for the + buttons
 * to add more recipients EmailData infos are displayed in the HTML Editor
 * whenever a user selects a row in the TableView. Internationalization is used
 * for the labels To and Subject in the form to display in english or in french.
 *
 * @author Xin Jia Cao
 */
public class FormAndHTMLLayoutController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="subjectField"
    private TextField subjectField; // Value injected by FXMLLoader

    @FXML // fx:id="htmlEditor"
    private HTMLEditor htmlEditor; // Value injected by FXMLLoader

    @FXML // fx:id="toHBox"
    private HBox toHBox; // Value injected by FXMLLoader

    @FXML // fx:id="ccHBox"
    private HBox ccHBox; // Value injected by FXMLLoader

    @FXML // fx:id="bccHBox"
    private HBox bccHBox; // Value injected by FXMLLoader

    private final static Logger LOG = LoggerFactory.getLogger(FormAndHTMLLayoutController.class);

    
    /**
     * Called by the FXMLLoader when initialization is complete. When the FXML
     * is loaded, if a control is not present, an exception is thrown and quits
     * the FXML loading process.
     */
    @FXML
    private void initialize() {
        assert toHBox != null : "fx:id=\"toHBox\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
        assert ccHBox != null : "fx:id=\"ccHBox\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
        assert subjectField != null : "fx:id=\"subjectField\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
        assert bccHBox != null : "fx:id=\"bccHBox\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
        assert htmlEditor != null : "fx:id=\"htmlEditor\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
    }
    
    /**
     * Called when the button (+) next to To is pressed. Add a new TextField
     * next to the previous one so the user can add more To recipients to his
     * email.
     *
     * @param event The ActionEvent (button clicked)
     */
    @FXML
    private void addToTextField(ActionEvent event) {
        toHBox.getChildren().add(new TextField());
    }

    /**
     * Called when the button (+) next to CC is pressed. Add a new TextField
     * next to the previous one so the user can add more CC recipients to his
     * email.
     *
     * @param event The ActionEvent (button clicked)
     */
    @FXML
    private void addCCTextField(ActionEvent event) {
        ccHBox.getChildren().add(new TextField());
    }

    /**
     * Called when the button (+) next to BCC is pressed. Add a new TextField
     * next to the previous one so the user can add more BCC recipients to his
     * email.
     *
     * @param event The ActionEvent (button clicked)
     */
    @FXML
    private void addBCCTextField(ActionEvent event) {
        bccHBox.getChildren().add(new TextField());
    }

    /**
     * Called by the TableLayoutController when listening to selected rows in
     * the TableView. This method displays infos (Send, Subject, Date) of the
     * EmailData selected in the table in the html editor.
     *
     * @param emailData
     */
    public void writeToEditorEmailData(EmailData emailData) {
        LOG.info("Writing to HTML Editor");

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body contenteditable='false'>");

        sb.append(resources.getString("from")).append(": ").append(emailData.getFrom()).append("</br>");
        sb.append("Date: ").append(emailData.getDate()).append("</br>");
        sb.append(resources.getString("subject")).append(": ").append(emailData.getSubject()).append("</br>");

        sb.append("</body></html>");
        htmlEditor.setHtmlText(sb.toString());
    }
    
    /**
     * Put the newly created email to the draft folder
     */
    @FXML
    private void saveEmail(){
        //TODO: Put the email in the Draft folder
    }
    
    /**
     * Put the newly created email to the sent folder
     */
    @FXML
    private void sendEmail(){
        //TODO: Put the email in the Sent folder
    }

}
