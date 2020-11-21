package com.xinjia.presentation.formhtml;

import com.xinjia.business.SendAndReceive;
import com.xinjia.exceptions.NullBCCEmailAddressException;
import com.xinjia.exceptions.NullBCCEmailException;
import com.xinjia.exceptions.NullCCEmailAddressException;
import com.xinjia.exceptions.NullCCEmailException;
import com.xinjia.exceptions.NullToEmailAddressException;
import com.xinjia.exceptions.NullToEmailException;
import com.xinjia.jdbc.beans.EmailData;
import com.xinjia.jdbc.persistence.EmailDAO;
import com.xinjia.jdbc.persistence.EmailDAOImpl;
import com.xinjia.properties.propertybean.EmailFXData;
import java.io.File;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.web.HTMLEditor;
import jodd.mail.Email;
import jodd.mail.EmailAddress;
import jodd.mail.EmailAttachment;
import jodd.mail.EmailMessage;
import jodd.mail.RFC2822AddressParser;
import jodd.mail.ReceivedEmail;
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

    @FXML // fx:id="saveBtn"
    private Button saveBtn; // Value injected by FXMLLoader

    @FXML // fx:id="sendBtn"
    private Button sendBtn; // Value injected by FXMLLoader

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
        assert saveBtn != null : "fx:id=\"saveBtn\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
        assert sendBtn != null : "fx:id=\"sendBtn\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
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

        for (int i = 0; i < emailData.getAttachments().size(); i++) {
            File file = new File(emailData.getAttachments().get(i));
            sb.append("<img src=' ").append(file.toURI()).append("'/>");
        }
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
    public void displayEmailRecipientsAndAttachments(EmailFXData emailData) {
        attachmentsHBox.getChildren().clear();
        clearIcon.setVisible(false);
        putValuesInNodes(emailData.getTo(), emailData.getCC(), emailData.getBCC(), emailData.getSubject());
        displayAttachments("Attachments: " + emailData.getAttachments().size());

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

        addTextFieldsToRecipients(to, toHBox);
        addTextFieldsToRecipients(cc, ccHBox);
        addTextFieldsToRecipients(bcc, bccHBox);
        subjectField.setText(subject);
    }

    private void addTextFieldsToRecipients(ObservableList<String> emails, HBox box) {

        for (String email : emails) {
            box.getChildren().add(new TextField(email));
        }
    }

    /**
     * Put the newly created email to the draft folder
     */
    @FXML
    private void saveEmail() throws SQLException {

        emailOperations = new SendAndReceive(((EmailDAOImpl) emailDAO).getMailConfigBean());

        String htmlMsg = htmlEditor.getHtmlText();
        ArrayList<File> regFiles = getAttachments();

        ArrayList<String> toRecipients = getRecipients(toHBox);
        ArrayList<String> ccRecipients = getRecipients(ccHBox);
        ArrayList<String> bccRecipients = getRecipients(bccHBox);
        Email emailSaved = emailOperations.createMail(toRecipients, ccRecipients, bccRecipients, subjectField.getText(), "", htmlMsg, regFiles, new ArrayList<>());
        //put email in the draft folder
        putEmailInDatabase(emailSaved, 3);
    }

    /**
     * Put the newly created email to the sent folder
     */
    @FXML
    private void sendEmail() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException, SQLException {
        //TODO: Put the email in the Sent folder
        LOG.info("MAIL CONFIG BEAN ADDRESS: " + ((EmailDAOImpl) emailDAO).getMailConfigBean().getEmailAddress());
        LOG.info("MAIL CONFIG BEAN PWD: " + ((EmailDAOImpl) emailDAO).getMailConfigBean().getMailPassword());

        emailOperations = new SendAndReceive(((EmailDAOImpl) emailDAO).getMailConfigBean());
        String htmlMsg = htmlEditor.getHtmlText();
        ArrayList<File> regFiles = getAttachments();

        ArrayList<String> toRecipients = getRecipients(toHBox);
        ArrayList<String> ccRecipients = getRecipients(ccHBox);
        ArrayList<String> bccRecipients = getRecipients(bccHBox);
        if (toRecipients.isEmpty() && ccRecipients.isEmpty() && bccRecipients.isEmpty()) {
            displayEmailError(resources.getString("noRecipientsHeader"), resources.getString("noRecipientsText"));
        } else {
            if (checkAreAddressesValid(toRecipients) && checkAreAddressesValid(ccRecipients) && checkAreAddressesValid(bccRecipients)) {
                Email emailSent = emailOperations.sendMail(toRecipients, ccRecipients, bccRecipients, subjectField.getText(), "", htmlMsg, regFiles, new ArrayList<>());
                putEmailInDatabase(emailSent, 2);
                ReceivedEmail[] receivedEmails = emailOperations.receiveMail(((EmailDAOImpl) emailDAO).getMailConfigBean());
                insertReceivedEmails(receivedEmails);
            } else {
                displayEmailError(resources.getString("invalidRecipientsHeader"), resources.getString("invalidRecipientsText"));
            }

        }
        LOG.info(htmlEditor.getHtmlText());
    }

    private boolean checkAreAddressesValid(ArrayList<String> addresses) {
        for (String address : addresses) {
            if (!(RFC2822AddressParser.STRICT.parseToEmailAddress(address) != null)) {
                return false;
            }
        }
        return true;
    }

    private void putEmailInDatabase(Email emailSent, int folderId) throws SQLException {
        EmailData emailBean = new EmailData();
        //int emailId, int folderId, LocalDateTime receivedDate, Email email
        emailBean.setFolderId(folderId);
        emailSent.sentDate(new Date());
        emailBean.setEmail(emailSent);
        emailDAO.createEmail(emailBean);
    }

    private void insertReceivedEmails(ReceivedEmail[] receivedEmails) throws SQLException {
        ReceivedEmail receivedEmail = receivedEmails[receivedEmails.length-1];

        EmailData emailBean = new EmailData();
        //int emailId, int folderId, LocalDateTime receivedDate, Email email
        emailBean.setFolderId(1);
        Date date = receivedEmail.receivedDate();
        Timestamp timeStamp = new Timestamp(date.getTime()); 
        LocalDateTime localDateTime = timeStamp.toLocalDateTime();
        emailBean.setReceivedDate(localDateTime);

        Email email = new Email();
        for(EmailAddress ea : receivedEmail.to()){
            email.to(ea);
        }
        for(EmailAddress ea : receivedEmail.cc()){
            email.cc(ea);
        }
        email.subject(receivedEmail.subject());
        
        
        emailBean.setEmail(email);
        emailDAO.createEmail(emailBean);
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

    public void displayAttachments(String filename) {
        clearIcon.setVisible(false);
        Label label = new Label(filename);
        label.setPadding(new Insets(5, 5, 5, 5));
        attachmentsHBox.getChildren().add(label);
    }

    public void addAttachment(String filename) {
        clearIcon.setVisible(true);

        displayAttachments(filename);
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

    public void enableButtons(int folderId) {
        if (folderId == 3) {
            saveBtn.setDisable(false);
            sendBtn.setDisable(false);
        } else {
            saveBtn.setDisable(true);
            sendBtn.setDisable(true);
        }
    }

}
