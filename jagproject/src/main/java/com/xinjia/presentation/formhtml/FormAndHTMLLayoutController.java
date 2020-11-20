package com.xinjia.presentation.formhtml;

import com.xinjia.business.SendAndReceive;
import com.xinjia.exceptions.NullBCCEmailAddressException;
import com.xinjia.exceptions.NullBCCEmailException;
import com.xinjia.exceptions.NullCCEmailAddressException;
import com.xinjia.exceptions.NullCCEmailException;
import com.xinjia.exceptions.NullToEmailAddressException;
import com.xinjia.exceptions.NullToEmailException;
import com.xinjia.jdbc.persistence.EmailDAO;
import com.xinjia.jdbc.persistence.EmailDAOImpl;
import com.xinjia.properties.propertybean.EmailFXData;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.web.HTMLEditor;
import jodd.mail.Email;
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

    @FXML // fx:id="toHBox"
    private HBox toHBox; // Value injected by FXMLLoader

    @FXML // fx:id="ccHBox"
    private HBox ccHBox; // Value injected by FXMLLoader

    @FXML // fx:id="subjectField"
    private TextField subjectField; // Value injected by FXMLLoader

    @FXML // fx:id="bccHBox"
    private HBox bccHBox; // Value injected by FXMLLoader

    @FXML // fx:id="htmlEditor"
    private HTMLEditor htmlEditor; // Value injected by FXMLLoader

    @FXML // fx:id="attachmentsHBox"
    private HBox attachmentsHBox; // Value injected by FXMLLoader

    @FXML // fx:id="clearIcon"
    private ImageView clearIcon; // Value injected by FXMLLoader

    private EmailDAO emailDAO;
    private SendAndReceive emailOperations;

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
        assert attachmentsHBox != null : "fx:id=\"attachmentsHBox\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
        assert clearIcon != null : "fx:id=\"clearIcon\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
        assert htmlEditor != null : "fx:id=\"htmlEditor\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";

    }

    public void setEmailDAO(EmailDAO emailDAO) {
        this.emailDAO = emailDAO;
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
    public void writeToEditorEmailData(EmailFXData emailData) {

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body contenteditable='false'>");

        sb.append(emailData.getTextMsg()).append("</br>");
        sb.append(emailData.getHtmlMsg()).append("</br>");
        File file = new File("img2.png");

        sb.append("<img src=' ").append(file.toURI()).append("'/>");
        sb.append("</body></html>");
        htmlEditor.setHtmlText(sb.toString());
    }

    /**
     * Display recipients and subject based on the Email ID in the Form
     * container as well as the sample HTML message from the fake data class.
     *
     * @param emailData
     * @param propertyBean
     */
    public void displayFormAndMessage(EmailFXData emailData) {
        attachmentsHBox.getChildren().clear();
        clearIcon.setVisible(false);
        putValuesInNodes(emailData.getTo(), emailData.getCC(), emailData.getBCC(), emailData.getSubject());
        displayAttachment("Attachments: " + emailData.getAttachments().size());

    }

    /**
     * Set the text to each TextField to its corresponding string
     *
     * @param to
     * @param cc
     * @param bcc
     * @param subject
     */
    private void putValuesInNodes(ObservableList<String> to, ObservableList<String> cc, ObservableList<String> bcc, String subject) {
        //remove all TextFields in the HBoxs
        toHBox.getChildren().clear();
        ccHBox.getChildren().clear();
        bccHBox.getChildren().clear();

        addTextFieldsToRecipients(to, "to");
        addTextFieldsToRecipients(cc, "cc");
        addTextFieldsToRecipients(bcc, "bcc");
        subjectField.setText(subject);
    }

    private void addTextFieldsToRecipients(ObservableList<String> emails, String recipientType) {
        switch (recipientType) {
            case "to" -> {
                for (String email : emails) {
                    toHBox.getChildren().add(new TextField(email));
                }
            }
            case "cc" -> {
                for (String email : emails) {
                    ccHBox.getChildren().add(new TextField(email));
                }
            }
            case "bcc" -> {
                for (String email : emails) {
                    bccHBox.getChildren().add(new TextField(email));
                }
            }
        }
    }

    /**
     * Put the newly created email to the draft folder
     */
    @FXML
    private void saveEmail() {
        //TODO: Put the email in the Draft folder
    }

    /**
     * Put the newly created email to the sent folder
     */
    @FXML
    private void sendEmail() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        //TODO: Put the email in the Sent folder
        LOG.info("MAIL CONFIG BEAN ADDRESS: " + ((EmailDAOImpl) emailDAO).getMailConfigBean().getEmailAddress());
        LOG.info("MAIL CONFIG BEAN PWD: " + ((EmailDAOImpl) emailDAO).getMailConfigBean().getMailPassword());

        emailOperations = new SendAndReceive(((EmailDAOImpl) emailDAO).getMailConfigBean());
        String htmlMsg = htmlEditor.getHtmlText();
        ArrayList<File> regFiles = getAttachments();
        if (getRecipients(toHBox).isEmpty() && getRecipients(ccHBox).isEmpty() && getRecipients(bccHBox).isEmpty()) {
            displayEmailError(resources.getString("noRecipientsHeader"), resources.getString("noRecipientsText"));
        } else {
            emailOperations.sendMail(getRecipients(toHBox), getRecipients(ccHBox), getRecipients(bccHBox), subjectField.getText(), "", htmlMsg, regFiles, new ArrayList<>());
        }
    }

    private ArrayList<String> getRecipients(HBox box) {
        ArrayList<String> recipients = new ArrayList<>();
        for (Node n : box.getChildren()) {
            if (n instanceof TextField) {
                if (!((TextField) n).getText().trim().isEmpty()) {
                    recipients.add(((TextField) n).getText());
                }
            }
        }
        return recipients;
    }

    private ArrayList<File> getAttachments() {
        ArrayList<File> files = new ArrayList<>();
        for (Node n : attachmentsHBox.getChildren()) {
            if (n instanceof Label) {
                File file = new File(((Label) n).getText());
                files.add(file);
            }
        }
        return files;
    }

    public void displayAttachment(String filename) {
        if (!clearIcon.isVisible()) {
            clearIcon.setVisible(true);
        }
        Label label = new Label(filename);
        label.setPadding(new Insets(5, 5, 5, 5));
        attachmentsHBox.getChildren().add(label);
    }

    @FXML
    void clearAttachments(MouseEvent event) {
        attachmentsHBox.getChildren().clear();
        clearIcon.setVisible(false);
    }

    public void emptyAllFields() {
        //remove all TextFields in the HBoxs
        toHBox.getChildren().clear();
        ccHBox.getChildren().clear();
        bccHBox.getChildren().clear();
        subjectField.clear();
        //clear the attachments names
        attachmentsHBox.getChildren().clear();
        clearIcon.setVisible(false);
        htmlEditor.setHtmlText("");

    }

    /**
     * Display an alert dialog with the given header text and content text when
     * an error occured when the recipients are empty.
     *
     * @param header The header text
     * @param content The content text
     */
    private void displayEmailError(String header, String content) {
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.setTitle(resources.getString("errorTitle"));
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        dialog.show();
    }
    
    private void putEmailInDatabase(){
        
    }

}
