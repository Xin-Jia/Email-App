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
import com.xinjia.presentation.rootcontroller.RootLayoutSplitController;
import com.xinjia.properties.propertybean.EmailFXData;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import jodd.mail.Email;
import jodd.mail.EmailAttachment;
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

    @FXML // fx:id="addCCBtn"
    private Button addCCBtn; // Value injected by FXMLLoader

    @FXML // fx:id="addToBtn"
    private Button addToBtn; // Value injected by FXMLLoader

    @FXML // fx:id="addBCCBtn"
    private Button addBCCBtn; // Value injected by FXMLLoader

    @FXML // fx:id="downloadIcon"
    private ImageView downloadIcon; // Value injected by FXMLLoader

    private EmailDAO emailDAO;
    private EmailFXData clickedEmailBean;
    private SendAndReceive emailOperations;
    private RootLayoutSplitController rootController;

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

    }

    public void setEmailDAO(EmailDAO emailDAO) {
        this.emailDAO = emailDAO;
    }

    public void setRootController(RootLayoutSplitController controller) {
        this.rootController = controller;
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
    public void writeToEditorEmailData() {

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body contenteditable='false'>");

        //sb.append(clickedEmailBean.getTextMsg()).append("</br>");
        sb.append(clickedEmailBean.getHtmlMsg()).append("</br>");

        LOG.info("LENGTH OF EMBEDDED ATTS: " + clickedEmailBean.getEmbedAttachments().size());
        for (int i = 0; i < clickedEmailBean.getEmbedAttachments().size(); i++) {
            File file = new File(clickedEmailBean.getEmbedAttachments().get(i));
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
     */
    public void displayEmailRecipientsAndAttachments(EmailFXData emailData) {
        clickedEmailBean = emailData;
        attachmentsHBox.getChildren().clear();
        clearIcon.setVisible(false);
        putValuesInNodes(emailData.getTo(), emailData.getCC(), emailData.getBCC(), emailData.getSubject());
        LOG.info("LENGTH OF REGULAR ATTS: " + emailData.getRegAttachments().size());
        for (String filename : emailData.getRegAttachments()) {
            displayAttachment(filename);
        }

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
        if (checkRecipientsNotEmpty()) {
            if (checkValidRecipients()) {
                if (clickedEmailBean != null && clickedEmailBean.getFolderId() == 3) {
                    LOG.info("SAVING A DRAFT");
                    EmailData mailData = createEmailCustomBean();
                    emailDAO.updateEmailDraft(mailData);

                } else {
                    LOG.info("SAVING NOT A DRAFT");
                    EmailData mailData = createEmailCustomBean();
                    //put email in the draft folder
                    putEmailInDatabase(mailData, 3);

                }
            }
        }
    }

    private boolean checkValidRecipients() {
        ArrayList<String> toRecipients = getRecipients(toHBox);
        ArrayList<String> ccRecipients = getRecipients(ccHBox);
        ArrayList<String> bccRecipients = getRecipients(bccHBox);
        if (checkAreAddressesValid(toRecipients) && checkAreAddressesValid(ccRecipients) && checkAreAddressesValid(bccRecipients)) {
            return true;
        } else {
            displayEmailError(resources.getString("invalidRecipientsHeader"), resources.getString("invalidRecipientsText"));

            return false;
        }
    }

    private boolean checkRecipientsNotEmpty() {
        ArrayList<String> toRecipients = getRecipients(toHBox);
        ArrayList<String> ccRecipients = getRecipients(ccHBox);
        ArrayList<String> bccRecipients = getRecipients(bccHBox);

        if (toRecipients.isEmpty() && ccRecipients.isEmpty() && bccRecipients.isEmpty()) {
            displayEmailError(resources.getString("noRecipientsHeader"), resources.getString("noRecipientsText"));
            return false;
        }
        return true;
    }

    private EmailData createEmailCustomBean() throws SQLException {
        emailOperations = new SendAndReceive(((EmailDAOImpl) emailDAO).getMailConfigBean());
        EmailData mailData = new EmailData();
        if (clickedEmailBean != null) {
            mailData.setEmailId(clickedEmailBean.getId());
        }
        mailData.setFolderId(3);

        String htmlMsg = htmlEditor.getHtmlText();
        ArrayList<File> regFiles = getAttachments();

        ArrayList<String> toRecipients = getRecipients(toHBox);
        ArrayList<String> ccRecipients = getRecipients(ccHBox);
        ArrayList<String> bccRecipients = getRecipients(bccHBox);
        Email joddEmail = emailOperations.createMail(toRecipients, ccRecipients, bccRecipients, subjectField.getText(), htmlMsg, htmlMsg, regFiles, new ArrayList<>());
        mailData.setEmail(joddEmail);
        return mailData;
    }

    @FXML
    private void sendEmail() throws SQLException, NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        LOG.info("MAIL CONFIG BEAN ADDRESS: " + ((EmailDAOImpl) emailDAO).getMailConfigBean().getEmailAddress());
        LOG.info("MAIL CONFIG BEAN PWD: " + ((EmailDAOImpl) emailDAO).getMailConfigBean().getMailPassword());

        emailOperations = new SendAndReceive(((EmailDAOImpl) emailDAO).getMailConfigBean());
        String htmlMsg = htmlEditor.getHtmlText();
        ArrayList<File> regFiles = getAttachments();

        ArrayList<String> toRecipients = getRecipients(toHBox);
        ArrayList<String> ccRecipients = getRecipients(ccHBox);
        ArrayList<String> bccRecipients = getRecipients(bccHBox);
        if (checkRecipientsNotEmpty()) {
            if (checkValidRecipients()) {
                Email emailSent = emailOperations.sendMail(toRecipients, ccRecipients, bccRecipients, subjectField.getText(), htmlMsg, htmlMsg, regFiles, new ArrayList<>());
                EmailData mailData = createEmailCustomBean();
                if (clickedEmailBean != null && clickedEmailBean.getFolderId() == 3) {

                    emailDAO.updateEmailDraftAndSend(mailData);
                } else {
                    putEmailInDatabase(mailData, 2);
                    //rootController.reloadInbox();
                }
            }

        }
    }

    private boolean checkAreAddressesValid(ArrayList<String> addresses) {
        for (String address : addresses) {
            if (!(RFC2822AddressParser.STRICT.parseToEmailAddress(address) != null)) {
                return false;
            }
        }
        return true;
    }

    private void putEmailInDatabase(EmailData emailData, int folderId) throws SQLException {
        if (folderId == 2) {
            emailData.email.sentDate(new Date());
        }
        emailData.setFolderId(folderId);
        emailDAO.createEmail(emailData);
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
        Label label = new Label(filename);
        label.setPadding(new Insets(5, 5, 5, 5));
        attachmentsHBox.getChildren().add(label);
    }

    public void addAttachment(String filename) {
        clearIcon.setVisible(true);
        displayAttachment(filename);
    }

    @FXML
    void clearAttachments(MouseEvent event) {
        ArrayList<File> files = getAttachments();
        files.forEach(file -> {
            file.delete();
        });
        attachmentsHBox.getChildren().clear();
        clearIcon.setVisible(false);
    }

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
        downloadIcon.setVisible(false);
        clickedEmailBean = null;
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

    public void enableButtons(boolean isVisible) {
        saveBtn.setVisible(isVisible);
        sendBtn.setVisible(isVisible);
        addToBtn.setVisible(isVisible);
        addCCBtn.setVisible(isVisible);
        addBCCBtn.setVisible(isVisible);
        if (!isVisible) {
            if (!attachmentsHBox.getChildren().isEmpty()) {
                downloadIcon.setVisible(true);
            } else {
                downloadIcon.setVisible(false);
            }
        } else {
            if (!attachmentsHBox.getChildren().isEmpty()) {
                clearIcon.setVisible(true);
            } else {
                clearIcon.setVisible(false);
            }
            downloadIcon.setVisible(false);
        }
    }

}
