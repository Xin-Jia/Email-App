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
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import jodd.mail.Email;
import jodd.mail.RFC2822AddressParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the Form/HTML Editor. Contain event handlers for the + buttons
 * to add more recipients. EmailData text, HTML messages and embedded images are displayed in the HTML Editor
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

    @FXML // fx:id="addCCBtn"
    private Button addCCBtn; // Value injected by FXMLLoader

    @FXML // fx:id="addToBtn"
    private Button addToBtn; // Value injected by FXMLLoader

    @FXML // fx:id="addBCCBtn"
    private Button addBCCBtn; // Value injected by FXMLLoader

    @FXML // fx:id="downloadIcon"
    private ImageView downloadIcon; // Value injected by FXMLLoader

    @FXML // fx:id="replyBtn"
    private Button replyBtn; // Value injected by FXMLLoader
    
    @FXML // fx:id="attachmentBtn"
    private Button attachmentBtn; // Value injected by FXMLLoader

    private EmailDAO emailDAO;
    private EmailFXData clickedEmailBean;
    private SendAndReceive emailOperations;
    private static final int DRAFT_ID = 3;
    private static final int SENT_ID = 2;

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
        assert addCCBtn != null : "fx:id=\"addCCBtn\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
        assert addToBtn != null : "fx:id=\"addToBtn\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
        assert addBCCBtn != null : "fx:id=\"addBCCBtn\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
        assert bccHBox != null : "fx:id=\"bccHBox\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
        assert saveBtn != null : "fx:id=\"saveBtn\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
        assert sendBtn != null : "fx:id=\"sendBtn\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
        assert attachmentsHBox != null : "fx:id=\"attachmentsHBox\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
        assert clearIcon != null : "fx:id=\"clearIcon\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
        assert htmlEditor != null : "fx:id=\"htmlEditor\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
        assert downloadIcon != null : "fx:id=\"downloadIcon\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
        assert replyBtn != null : "fx:id=\"replyBtn\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";

    }

    /**
     * Set the EmailDAO
     *
     * @param emailDAO
     */
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
     * the TableView. This method displays text and HTML messages as well as embedded attachments of the
     * EmailData selected in the table in the html editor.
     */
    public void writeToEditorEmailData() {
        displayEmailMessages();
        displayEmailEmbeddedImages();
    }
    
    /**
     * Display the HTML messages of the selected email in the HTML editor
     */
    private void displayEmailMessages(){
        StringBuilder builder = new StringBuilder();
        builder.append("<html><body contenteditable='false'>");

        //sb.append(clickedEmailBean.getTextMsg()).append("</br>");
        builder.append(clickedEmailBean.getHtmlMsg()).append("</br>");
        builder.append("</body></html>");
        htmlEditor.setHtmlText(builder.toString());
    }
    
    /**
     * Display the embedded images of the selected email in the HTML editor
     */
    private void displayEmailEmbeddedImages(){
        StringBuilder builder = new StringBuilder();
        //remove the img tags in the html message
        String[] parts = htmlEditor.getHtmlText().replaceAll("\\<img.*?\\>", "toreplace").split("toreplace");
        builder.append(parts[0]);
        int count = 1;
        LOG.info("LENGTH OF EMBEDDED ATTS: " + clickedEmailBean.getEmbedAttachments().size());
        for (int i = 0; i < clickedEmailBean.getEmbedAttachments().size(); i++) {
            File file = new File(clickedEmailBean.getEmbedAttachments().get(i));
            builder.append("<img src=' ").append(file.toURI()).append("'/>");
            if(count < parts.length){
                builder.append(parts[count]);
                count++;
            }
        }
       
        htmlEditor.setHtmlText(builder.toString());
    }

    /**
     * Display recipients (to, cc, bcc) and subject of the selected email.
     *
     * @param emailData The JavaFX Email bean selected
     */
    public void displayEmailRecipientsAndAttachments(EmailFXData emailData) {
        //set the clicked email bean to the clicked email so we can use it later 
        clickedEmailBean = emailData;
        //user can only clear attachments when they create a new email or when the email is in the draft folder
        clearIcon.setVisible(false);
        putValuesInNodes(emailData.getTo(), emailData.getCC(), emailData.getBCC(), emailData.getSubject());
        //clear the old attachment labels to display the attachments from the selected email
        attachmentsHBox.getChildren().clear();
        for (String filename : emailData.getRegAttachments()) {
            displayAttachment(filename);
        }

    }

    /**
     * Set the text to each TextField to its corresponding values (to, cc, bcc, subject)
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

    /**
     * Add new TextField(s) to a given HBox based on the given recipients
     * @param recipients the ObservableList of String of the recipients (email addresses)
     * @param box the HBox to be added to
     */
    private void addTextFieldsToRecipients(ObservableList<String> recipients, HBox box) {

        for (String email : recipients) {
            box.getChildren().add(new TextField(email));
        }
    }

    /**
     * Check if the TextFields are empty and then if they are valid recipients (email addresses).
     * If the email saved is not in the Draft folder, update and put that email in the draft folder.
     * If it is in the Draft folder, update the content directly.
     */
    @FXML
    private void saveEmail() throws SQLException {
        if (checkRecipientsNotEmpty()) {
            if (checkValidRecipients()) {
                if (clickedEmailBean != null && clickedEmailBean.getFolderId() == DRAFT_ID) {
                    LOG.info("SAVING A DRAFT");
                    EmailData mailData = createEmailCustomBean();
                    //update the email in the draft folder
                    emailDAO.updateEmailDraft(mailData);

                } else {
                    LOG.info("SAVING NOT A DRAFT");
                    EmailData mailData = createEmailCustomBean();
                    //put email in the draft folder
                    putEmailInDatabase(mailData, DRAFT_ID);

                }
            }
        }
    }

    /**
     * Check if all the addresses entered in the TextFields are valid addresses
     * @return true if all addresses are valid and false if at least one address is not
     */
    private boolean checkValidRecipients() {
        ArrayList<String> toRecipients = getRecipients(toHBox);
        ArrayList<String> ccRecipients = getRecipients(ccHBox);
        ArrayList<String> bccRecipients = getRecipients(bccHBox);
        if (checkAreAddressesValid(toRecipients) && checkAreAddressesValid(ccRecipients) && checkAreAddressesValid(bccRecipients)) {
            return true;
        } else {
            //display an alert saying that an address is not valid
            displayEmailError(resources.getString("invalidRecipientsHeader"), resources.getString("invalidRecipientsText"));

            return false;
        }
    }

    /**
     * Check if all the recipients HBoxs are not all empty
     * @return true if at least one TextField is not empty and false otherwise
     */
    private boolean checkRecipientsNotEmpty() {
        ArrayList<String> toRecipients = getRecipients(toHBox);
        ArrayList<String> ccRecipients = getRecipients(ccHBox);
        ArrayList<String> bccRecipients = getRecipients(bccHBox);

        if (toRecipients.isEmpty() && ccRecipients.isEmpty() && bccRecipients.isEmpty()) {
            //display an alert saying that no address has been entered
            displayEmailError(resources.getString("noRecipientsHeader"), resources.getString("noRecipientsText"));
            return false;
        }
        return true;
    }

    /**
     * Create a new custom Email bean based on the values entered in the form and editor
     * @return the custom email bean EmailData
     * @throws SQLException 
     */
    private EmailData createEmailCustomBean() throws SQLException {
        emailOperations = new SendAndReceive(((EmailDAOImpl) emailDAO).getMailConfigBean());
        EmailData mailData = new EmailData();
        if (clickedEmailBean != null) {
            mailData.setEmailId(clickedEmailBean.getId());
        }
        mailData.setFolderId(DRAFT_ID);

        String htmlMsg = htmlEditor.getHtmlText();
        ArrayList<File> regFiles = getAttachments();

        ArrayList<String> toRecipients = getRecipients(toHBox);
        ArrayList<String> ccRecipients = getRecipients(ccHBox);
        ArrayList<String> bccRecipients = getRecipients(bccHBox);
        Email joddEmail = emailOperations.createMail(toRecipients, ccRecipients, bccRecipients, subjectField.getText(), htmlMsg, htmlMsg, regFiles, new ArrayList<>());
        mailData.setEmail(joddEmail);
        return mailData;
    }

    /**
     * Send an email and put that email in the Sent folder
     * @throws SQLException
     * @throws NullToEmailException
     * @throws NullToEmailAddressException
     * @throws NullCCEmailException
     * @throws NullCCEmailAddressException
     * @throws NullBCCEmailException
     * @throws NullBCCEmailAddressException 
     */
    @FXML
    private void sendEmail() throws SQLException, NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {

        emailOperations = new SendAndReceive(((EmailDAOImpl) emailDAO).getMailConfigBean());
        String htmlMsg = htmlEditor.getHtmlText();
        ArrayList<File> regFiles = getAttachments();

        ArrayList<String> toRecipients = getRecipients(toHBox);
        ArrayList<String> ccRecipients = getRecipients(ccHBox);
        ArrayList<String> bccRecipients = getRecipients(bccHBox);
        //check if the recipients are empty and if not, check if the addresses are all valid
        if (checkRecipientsNotEmpty()) {
            if (checkValidRecipients()) {
                //send the email
                emailOperations.sendMail(toRecipients, ccRecipients, bccRecipients, subjectField.getText(), htmlMsg, htmlMsg, regFiles, new ArrayList<>());
                EmailData mailData = createEmailCustomBean();
                if (clickedEmailBean != null && clickedEmailBean.getFolderId() == DRAFT_ID) {
                    //if the sent email was in the draft folder, update it and then place it in the Sent folder
                    emailDAO.updateEmailDraftAndSend(mailData);
                } else {
                    //if the sent email was not in the draft folder, put it directly in the Sent folder
                    putEmailInDatabase(mailData, SENT_ID);
                }
            }

        }
    }
    
    /**
     * Put the custom email bean in the given folder in the database
     * @param emailData
     * @param folderId
     * @throws SQLException 
     */
    private void putEmailInDatabase(EmailData emailData, int folderId) throws SQLException {
        if (folderId == SENT_ID) {
            emailData.email.sentDate(new Date());
        }
        emailData.setFolderId(folderId);
        //put the email in the database
        emailDAO.createEmail(emailData);
    }

    /**
     * Redraw the form so that the user can send the email to the sender of that email
     * For example, if the From of the email is xxx, then xxx will be on the To recipients
     */
    @FXML
    private void replyEmail() {
        //remove all TextFields in the HBoxs
        toHBox.getChildren().clear();
        ccHBox.getChildren().clear();
        bccHBox.getChildren().clear();
        clearIcon.setVisible(false);
        downloadIcon.setVisible(false);
        attachmentBtn.setDisable(false);

        toHBox.getChildren().add(new TextField(clickedEmailBean.getFrom()));
        clickedEmailBean.getTo().stream().filter(to -> (!to.equals(((EmailDAOImpl) emailDAO).getMailConfigBean().getEmailAddress()))).forEachOrdered(to -> {
            toHBox.getChildren().add(new TextField(to));
        });
        clickedEmailBean.getCC().stream().filter(cc -> (!cc.equals(((EmailDAOImpl) emailDAO).getMailConfigBean().getEmailAddress()))).forEachOrdered(cc -> {
            ccHBox.getChildren().add(new TextField(cc));
        });
        clickedEmailBean.getBCC().stream().filter(bcc -> (!bcc.equals(((EmailDAOImpl) emailDAO).getMailConfigBean().getEmailAddress()))).forEachOrdered(bcc -> {
            bccHBox.getChildren().add(new TextField(bcc));
        });

        //if there are attachments, allow the user to clear/remove them 
        if(!attachmentsHBox.getChildren().isEmpty()){
            clearIcon.setVisible(true);
        }
        //add a separator to differentiate the original message with the reply
        htmlEditor.setHtmlText("</br>---------------------------------------</br>"+htmlEditor.getHtmlText());
        saveBtn.setDisable(false);
        sendBtn.setDisable(false);
        clickedEmailBean = null;
    }

    /**
     * Check if all the addresses are valid
     * @param addresses
     * @return true if all addresses are valid and false otherwise
     */
    private boolean checkAreAddressesValid(ArrayList<String> addresses) {
        if (!addresses.stream().noneMatch(address -> (!(RFC2822AddressParser.STRICT.parseToEmailAddress(address) != null)))) {
            return false;
        }
        return true;
    }

    /**
     * Get the recipients/addresses in the given HBox (to, cc, bcc)
     * @param box
     * @return an ArrayList<String> that contain the email addresses
     */
    private ArrayList<String> getRecipients(HBox box) {
        ArrayList<String> recipients = new ArrayList<>();
        box.getChildren().stream().filter(n -> (n instanceof TextField)).filter(n -> (!((TextField) n).getText().trim().isEmpty())).forEachOrdered(n -> {
            recipients.add(((TextField) n).getText());
        });
        return recipients;
    }

    /**
     * Get the attachments of the selected email and create a File for each of them
     * @return an ArrayList<File> 
     */
    private ArrayList<File> getAttachments() {
        ArrayList<File> files = new ArrayList<>();
        attachmentsHBox.getChildren().stream().filter(n -> (n instanceof Label)).map(n -> new File(((Label) n).getText())).forEachOrdered(file -> {
            files.add(file);
        });
        return files;
    }

    /**
     * Display a new Label for the provided attachment name
     * @param filename the name of the attachment
     */
    public void displayAttachment(String filename) {
        Label label = new Label(filename);
        label.setPadding(new Insets(5, 5, 5, 5));
        attachmentsHBox.getChildren().add(label);
    }

    /**
     * Remove the attachments added to an email in the form as well as in the root of the project
     * @param event 
     */
    @FXML
    void clearAttachments(MouseEvent event) {
        ArrayList<File> files = getAttachments();
        files.forEach(file -> {
            file.delete();
        });
        attachmentsHBox.getChildren().clear();
        clearIcon.setVisible(false);
    }

    /**
     * Save/Download the attachments of an email in the selected directory folder
     * @param event
     * @throws IOException 
     */
    @FXML
    void downloadAttachments(MouseEvent event) throws IOException {
        Stage stage = (Stage) htmlEditor.getScene().getWindow();
        DirectoryChooser dirChooser = new DirectoryChooser();
        File selectedDirectory = dirChooser.showDialog(stage);

        if (selectedDirectory != null) {
            for (int i = 0; i < clickedEmailBean.getRegAttachmentsBytes().size(); i++) {
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(clickedEmailBean.getRegAttachmentsBytes().get(i)));
                LOG.info("SAVING SELECTED EMAIL FILES TO DISK: " + clickedEmailBean.getRegAttachments().get(i));
                File file = new File(selectedDirectory.getAbsolutePath() + "/" + clickedEmailBean.getRegAttachments().get(i));
                ImageIO.write(img, "png", file);
            }
        }
    }

    /**
     * Empty all the fields, content in the form and in the HTML editor
     */
    public void emptyAllFields() {
        toHBox.getChildren().clear();
        ccHBox.getChildren().clear();
        bccHBox.getChildren().clear();
        subjectField.clear();
        attachmentsHBox.getChildren().clear();
        clearIcon.setVisible(false);
        htmlEditor.setHtmlText("");
        downloadIcon.setVisible(false);
        clickedEmailBean = null;
    }

    /**
     * Display an alert dialog with the given header text and content text
     *
     * @param header The header text in the MessagesBundle
     * @param content The content text in the MessagesBundle
     */
    private void displayEmailError(String header, String content) {
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.setTitle(resources.getString("errorTitle"));
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        dialog.show();
    }

    /**
     * Disable/enable the buttons in the form based on the provided boolean
     * @param isEnabled 
     */
    public void disableButtons(boolean isEnabled) {
        saveBtn.setDisable(isEnabled);
        sendBtn.setDisable(isEnabled);
        addToBtn.setDisable(isEnabled);
        addCCBtn.setDisable(isEnabled);
        addBCCBtn.setDisable(isEnabled);
        //if true, it is not a draft
        if (isEnabled) {
            if (!attachmentsHBox.getChildren().isEmpty()) {
                downloadIcon.setVisible(true);
            } else {
                downloadIcon.setVisible(false);
            }
            replyBtn.setDisable(false);
            attachmentBtn.setDisable(true);
        } else {
            //it is a draft
            if (!attachmentsHBox.getChildren().isEmpty()) {
                clearIcon.setVisible(true);
            } else {
                clearIcon.setVisible(false);
            }
            downloadIcon.setVisible(false);
            replyBtn.setDisable(true);
            attachmentBtn.setDisable(false);
        }
    }
    
    /**
     * Let the user add an attachment to an email using the File Explorer.
     * @throws IOException 
     */
    @FXML
    private void addAttachmentToEmail() throws IOException{
        Stage stage = (Stage) attachmentBtn.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            copyFileToRoot(file);
            LOG.info("Absolute Path: " + file.getAbsolutePath());
            clearIcon.setVisible(true);
            //display the attachment in a label
            displayAttachment(file.getName());
        }
    }
    
    /**
     * Copy the provided File to the root folder of the project
     * @param file
     * @throws IOException 
     */
    private void copyFileToRoot(File file) throws IOException {
        Files.copy(file.toPath(),
                (new File(file.getName())).toPath(),
                StandardCopyOption.REPLACE_EXISTING);
    }

}
