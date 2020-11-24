package com.xinjia.presentation.rootcontroller;

import com.xinjia.business.SendAndReceive;
import com.xinjia.jdbc.beans.EmailData;
import com.xinjia.jdbc.persistence.EmailDAO;
import com.xinjia.jdbc.persistence.EmailDAOImpl;
import com.xinjia.presentation.MainEmailApp;
import com.xinjia.presentation.formhtml.FormAndHTMLLayoutController;
import com.xinjia.presentation.tablecontroller.TableLayoutController;
import com.xinjia.presentation.treecontroller.TreeLayoutController;
import com.xinjia.properties.MailConfigBean;
import com.xinjia.properties.propertybean.EmailFXData;
import com.xinjia.properties.propertybean.propertiesmanager.MailConfigPropertiesManager;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.activation.DataSource;
import jodd.mail.Email;
import jodd.mail.EmailAddress;
import jodd.mail.EmailAttachment;
import jodd.mail.EmailMessage;
import jodd.mail.MailException;
import jodd.mail.ReceivedEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the RootLayout. Initialize all panes to be displayed in the
 * Root layout and the controllers. Contains event handlers for all MenuItems in
 * the Menu. Internationalization is used for the Menus and MenuItems.
 *
 * @author Xin Jia Cao
 */
public class RootLayoutSplitController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="folderTreeView"
    private BorderPane folderTreeView; // Value injected by FXMLLoader

    @FXML // fx:id="emailTableView"
    private BorderPane emailTableView; // Value injected by FXMLLoader

    @FXML // fx:id="formAndHtml"
    private BorderPane formAndHtml; // Value injected by FXMLLoader

    @FXML // fx:id="comboBox"
    private ComboBox<String> comboBox; // Value injected by FXMLLoader

    @FXML // fx:id="searchEmailsTextField"
    private TextField searchEmailsTextField; // Value injected by FXMLLoader

    private TreeLayoutController treeController;
    private TableLayoutController tableController;
    private FormAndHTMLLayoutController formHtmlController;
    private EmailDAO emailDAO;

    private final static Logger LOG = LoggerFactory.getLogger(RootLayoutSplitController.class);

    /**
     * Initialize the emailDAO with the given mail config bean.
     * Initialize all three controllers and display them.
     * Retrieve the new received emails and store them in the database.
     * @param configBean
     * @throws SQLException 
     */
    public void initializeRoot(MailConfigBean configBean) throws SQLException {

        emailDAO = new EmailDAOImpl(configBean);

        //Initialize all three controllers 
        initFormAndEditorLayout();
        initTreeViewLayout();
        initTableViewLayout();
        //get the received emails if any and put them in the database
        reloadInbox();

        // Tell the tree about the table
        setTableControllerToTree();
        treeController.displayTree();
        tableController.displayTheTable();

    }

    /**
     * Called by the FXMLLoader when initialization is complete. When the FXML
     * is loaded, if a control is not present, an exception is thrown and quits
     * the FXML loading process. Add items to the comboBox for searching emails.
     */
    @FXML
    private void initialize() {
        assert comboBox != null : "fx:id=\"comboBox\" was not injected: check your FXML file 'RootLayout.fxml'.";
        assert searchEmailsTextField != null : "fx:id=\"searchEmailsTextField\" was not injected: check your FXML file 'RootLayout.fxml'.";
        assert folderTreeView != null : "fx:id=\"folderTreeView\" was not injected: check your FXML file 'RootLayout.fxml'.";
        assert emailTableView != null : "fx:id=\"emailTableView\" was not injected: check your FXML file 'RootLayout.fxml'.";
        assert formAndHtml != null : "fx:id=\"formAndHtml\" was not injected: check your FXML file 'RootLayout.fxml'.";
        comboBox.getItems().addAll("Recipients", "Subject");
        comboBox.getSelectionModel().select("Subject");
    }

    /**
     * Set the table controller in the TreeLayoutController so they can
     * 'bind'/work with each other
     */
    private void setTableControllerToTree() {
        treeController.setTableController(tableController);
    }

    /**
     * Initialize the TreeViewLayoutController and the TreeView to be displayed.
     * Add the AnchorPane of the TreeLayout to the BorderPane of the RootLayout.
     */
    private void initTreeViewLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);
            loader.setLocation(RootLayoutSplitController.class
                    .getResource("/fxml/TreeLayout.fxml"));
            AnchorPane treeView = (AnchorPane) loader.load();

            treeController = loader.getController();
            treeController.setEmailDAO(emailDAO);
            //Add the AnchorPane of the TreeLayout to the BorderPane of the RootLayout
            folderTreeView.getChildren().add(treeView);
        } catch (IOException ex) {
            LOG.error("Error loading file in initTreeViewLayout()", ex);
        }
    }

    /**
     * Initialize the TableViewLayoutController and the TableView to be
     * displayed. Add the AnchorPane of the TableLayout to the BorderPane of the
     * RootLayout
     */
    private void initTableViewLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);

            loader.setLocation(RootLayoutSplitController.class
                    .getResource("/fxml/TableLayout.fxml"));
            AnchorPane tableView = (AnchorPane) loader.load();

            // Give the controller the data object.
            tableController = loader.getController();
            tableController.setEmailDAO(emailDAO);
            //Set the FormAndHTMLLayoutController to the TableLayoutController 
            //so we can display infos in an EmailData in the Editor when a row is selected
            tableController.setEditorController(formHtmlController);
            //Add the AnchorPane of the TableLayout to the BorderPane of the RootLayout
            emailTableView.getChildren().add(tableView);
        } catch (IOException ex) {
            LOG.error("Error loading file in initTableViewLayout()", ex);
        }
    }

    /**
     * Initialize the FormAndHTMLLayoutController and the Form and HTML editor
     * to be displayed. Add the BorderPane of the FormHTMLLayout to the
     * BorderPane of the RootLayout
     */
    private void initFormAndEditorLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);
            loader.setLocation(RootLayoutSplitController.class
                    .getResource("/fxml/FormHTMLLayout.fxml"));
            BorderPane htmlView = (BorderPane) loader.load();

            formHtmlController = loader.getController();
            formHtmlController.setEmailDAO(emailDAO);
            //Add the BorderPane of the FormHTMLLayout to the BorderPane of the RootLayout
            formAndHtml.getChildren().add(htmlView);
        } catch (IOException ex) {
            LOG.error("Error loading file in initFormAndEditorLayout()", ex);
        }
    }

    /**
     * Create a new custom email bean and insert the given ReceivedEmail to the database.
     * @param receivedEmail the ReceivedEmail
     * @throws SQLException 
     */
    private void insertReceivedEmail(ReceivedEmail receivedEmail) throws SQLException {
        LOG.info("INSERTING RECEIVED EMAILS");
        EmailData emailBean = new EmailData();

        //set the folder to the Inbox folder
        emailBean.setFolderId(1);
        //create the received date
        Date date = receivedEmail.receivedDate();
        Timestamp timeStamp = new Timestamp(date.getTime());
        LocalDateTime localDateTime = timeStamp.toLocalDateTime();
        emailBean.setReceivedDate(localDateTime);

        Email email = new Email();
        
        List<EmailMessage> messages = receivedEmail.messages();
        //get the text messages of the receivedEmail
        ArrayList<String> messagesString = ((EmailDAOImpl) emailDAO).retrieveMessageContent(messages, "text/plain");
        if (!messages.isEmpty()) {

            if (!messages.isEmpty()) {
                email.textMessage(messagesString.get(0));
            }
            //get the HTML messages of the receivedEmail
            messagesString = ((EmailDAOImpl) emailDAO).retrieveMessageContent(messages, "text/html");
            if (!messages.isEmpty()) {
                email.htmlMessage(messagesString.get(0));
            }
        }
        for (int i = 0; i < receivedEmail.attachments().size(); i++) {
            EmailAttachment attachment = receivedEmail.attachments().get(i);
            if (attachment.isEmbedded() && attachment.getContentId() != null) {
                //check if the attachment is embedded
                if (!attachment.getContentId().equals("") && messagesString.get(0).contains("img src=\"cid:" + attachment.getContentId().replaceAll("[<>]", ""))) {
                    email.embeddedAttachment(attachment);
                } else {
                    email.attachment(attachment);
                }
            } else {
                email.attachment(attachment);
            }

        }
        email.from(receivedEmail.from().getEmail());
        email.subject(receivedEmail.subject());
        email.sentDate(receivedEmail.sentDate());

        for (EmailAddress ea : receivedEmail.to()) {
            email.to(ea);
        }
        for (EmailAddress ea : receivedEmail.cc()) {
            email.cc(ea);
        }

        emailBean.setEmail(email);
        emailDAO.createEmail(emailBean);
    }

    /**
     * Called when the MenuItem Create Folder is pressed. Load the
     * CreateFolderPopUp FXML file, initialize the controller and display the
     * Pop up.
     */
    @FXML
    private void addFolder() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);
            loader.setLocation(RootLayoutSplitController.class
                    .getResource("/fxml/CreateFolderPopUp.fxml"));
            VBox createFolderBox = (VBox) loader.load();
            CreateFolderController folderPopUpController = loader.getController();
            //Set the TreeLayoutController to the folderPopUpController so a folder can be added in the tree
            folderPopUpController.setTreeController(treeController);
            Scene scene = new Scene(createFolderBox);
            Stage stage = new Stage();
            stage.getIcons().add(new Image("/images/folder.png"));
            stage.setTitle(resources.getString("createFolder"));
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            LOG.error("Error displaying folder Pop up", ex);
        }
    }

    /**
     * Called when the user press on the Delete Folder MenuItem. Call the
     * treeController so it can delete the selected folder (if it can be
     * deleted).
     */
    @FXML
    private void deleteFolder() throws SQLException {
        treeController.deleteCustomFolder();
    }

    /**
     * Called when the user press on the Rename Folder MenuItem. Call the
     * treeController so it can rename the selected folder (if it can be
     * renamed).
     */
    @FXML
    private void renameFolder() throws SQLException {
        if (treeController.checkCanSelectedFolderBeRenamed()) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setResources(resources);
                loader.setLocation(RootLayoutSplitController.class
                        .getResource("/fxml/RenameFolderPopUp.fxml"));
                VBox renameFolderBox = (VBox) loader.load();
                RenameFolderController folderPopUpController = loader.getController();
                //Set the TreeLayoutController to the folderPopUpController so a folder can be renamed
                folderPopUpController.setTreeController(treeController);

                Scene scene = new Scene(renameFolderBox);
                Stage stage = new Stage();
                stage.getIcons().add(new Image("/images/folder.png"));
                stage.setTitle(resources.getString("renameFolder"));
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                LOG.error("Error displaying folder Pop up", ex);
            }
        }
    }

    /**
     * Called when the user press on the About MenuItem. Load the HelpWebView
     * FXML file, initialize the controller and display the WebView.
     */
    @FXML
    private void displayHelp() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);
            loader.setLocation(this.getClass()
                    .getResource("/fxml/HelpWebView.fxml"));
            Parent rootPane = (AnchorPane) loader.load();
            loader.getController();

            Scene scene = new Scene(rootPane);
            Stage stage = new Stage();
            stage.getIcons().add(new Image("/images/emailIcon.png"));
            stage.setTitle(resources.getString("help"));
            stage.setScene(scene);
            stage.show();

        } catch (IOException ex) {
            LOG.error("Error displaying webview", ex);
        }
    }

    /**
     * Called when the user press the Edit Mail Configuration MenuItem. Load the
     * MailConfigPropertiesForm FXML file, initialize the controller and display
     * the form.
     */
    @FXML
    private void editMailConfig() {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);
            loader.setLocation(MainEmailApp.class
                    .getResource("/fxml/MailConfigPropertiesForm.fxml"));
            Parent rootPane = (GridPane) loader.load();
            loader.getController();

            //retrieve the saved properties so they can be displayed in the TextFields
            retrieveMailConfig();

            Scene scene = new Scene(rootPane);
            Stage stage = new Stage();
            stage.getIcons().add(new Image("/images/emailIcon.png"));
            stage.setTitle(resources.getString("title"));
            stage.setScene(scene);
            stage.show();

        } catch (IOException ex) {
            LOG.error("Error displaying mail config form", ex);
        }
    }

    /**
     * Retrieve the saved properties in the mail config properties file.
     *
     * @throws IOException
     */
    private void retrieveMailConfig() throws IOException {
        MailConfigPropertiesManager pm = new MailConfigPropertiesManager();
        MailConfigBean configPropertyBean = new MailConfigBean();
        pm.loadTextProperties(configPropertyBean, "", "MailConfig");
    }


    /**
     * Called when the user press the Delete Selected Email MenuItem. 
     * Delete an email and its row in the TableView
     */
    @FXML
    private void deleteSelectedEmail() throws SQLException {
        tableController.deleteEmailRow();
    }

    /**
     * Called when the user press the New Email MenuItem.
     * Empty the form and enable the buttons to allow the user to save/send the email.
     * @throws SQLException 
     */
    @FXML
    private void createNewEmail() throws SQLException {
        formHtmlController.emptyAllFields();
        formHtmlController.disableButtons(false);
        tableController.unselectRow();
    }

    /**
     * Retrieve all received emails from the mail config email address
     * @throws SQLException 
     */
    @FXML
    public void reloadInbox() throws SQLException {
        SendAndReceive emailOperations = new SendAndReceive(((EmailDAOImpl) emailDAO).getMailConfigBean());
        ReceivedEmail[] receivedEmails = emailOperations.receiveMail(((EmailDAOImpl) emailDAO).getMailConfigBean());
        if (receivedEmails.length != 0) {
            for (ReceivedEmail email : receivedEmails) {
                insertReceivedEmail(email);
            }

        }
    }

    /**
     * Called when the Search button is pressed.
     * Find all emails based on the chosen item in the comboBox and on the given value in the TextField.
     * @param event
     * @throws SQLException 
     */
    @FXML
    void searchEmails(ActionEvent event) throws SQLException {
        //if the TextField is empty, show an alert dialog
        if (searchEmailsTextField.getText().trim().isEmpty()) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle(resources.getString("errorTitle"));
            dialog.setHeaderText(resources.getString("emptyTextFieldHeader"));
            dialog.setContentText(resources.getString("emptyTextFieldText"));
            dialog.show();
        } else {
            String value = comboBox.getSelectionModel().getSelectedItem();
            switch (value) {
                case "Subject" -> tableController.displayEmailsBySearchValue(convertToJavaFXBean(emailDAO.findEmailsBySubject(searchEmailsTextField.getText())));
                case "Recipients" -> tableController.displayEmailsBySearchValue(convertToJavaFXBean(emailDAO.findEmailsByRecipients(searchEmailsTextField.getText())));
            }

        }
    }
    
    /**
     * Make a JavaFX email bean for each custom email bean.
     * @param emails
     * @return an ObservableList<EmailFXData> that represent the JavaFX beans
     */
    private ObservableList<EmailFXData> convertToJavaFXBean(ArrayList<EmailData> emails) {
        ObservableList<EmailFXData> observableData = FXCollections.observableArrayList();

        emails.forEach(email -> {
            observableData.add(convertToSingleJavaFXBean(email));
        });
        return observableData;
    }

    /**
     * Convert a custom email bean to a JavaFX email bean.
     * @param email the custom bean to be converted
     * @return the JavaFX bean EmailFXData
     */
    private EmailFXData convertToSingleJavaFXBean(EmailData email) {

        ObservableList<String> to = FXCollections.observableArrayList();
        ObservableList<String> cc = FXCollections.observableArrayList();
        ObservableList<String> bcc = FXCollections.observableArrayList();
        Email joddEmail = email.getEmail();
        String txtMsg = "";
        String htmlMsg = "";
        List<String> regAttachmentsList = new ArrayList<>();
        List<byte[]> regAttachmentsBytes = new ArrayList<>();
        List<String> embedAttachmentsList = new ArrayList<>();
        List<byte[]> embedAttachmentsBytes = new ArrayList<>();

        List<EmailMessage> messages = joddEmail.messages();
        ArrayList<String> messagesString = ((EmailDAOImpl)emailDAO).retrieveMessageContent(messages, "text/plain");
        if (!messages.isEmpty()) {

            if (!messages.isEmpty()) {
                txtMsg = messagesString.get(0);
            }

            messagesString = ((EmailDAOImpl)emailDAO).retrieveMessageContent(messages, "text/html");
            if (!messages.isEmpty()) {
                htmlMsg = messagesString.get(0);
            }
        }

        List<EmailAttachment<? extends DataSource>> attachments = joddEmail.attachments();
        if (!attachments.isEmpty()) {
            for (EmailAttachment ea : attachments) {
                try {
                    if (ea.isEmbedded() && (ea.toByteArray() != null || ea.toByteArray().length != 0)) {
                        if (!ea.getContentId().equals("") && messagesString.get(0).contains("img src=\"cid:" + ea.getContentId().replaceAll("[<>]", ""))) {
                            embedAttachmentsList.add(ea.getName());
                            embedAttachmentsBytes.add(ea.toByteArray());
                        }
                        else{
                            regAttachmentsList.add(ea.getName());
                            regAttachmentsBytes.add(ea.toByteArray());
                        }

                    } else if (!ea.isEmbedded() && (ea.toByteArray() != null || ea.toByteArray().length != 0)) {
                        regAttachmentsList.add(ea.getName());
                        regAttachmentsBytes.add(ea.toByteArray());
                    }
                } catch (MailException e) {
                    LOG.error("BYTE ARRAY NULL");
                }
            }
        }

        for (EmailAddress address : email.getEmail().to()) {
            to.add(address.getEmail());
        }
        for (EmailAddress address : email.getEmail().cc()) {
            cc.add(address.getEmail());
        }
        for (EmailAddress address : email.getEmail().bcc()) {
            bcc.add(address.getEmail());
        }

        EmailFXData observableData = new EmailFXData(email.getEmailId(), email.getFolderId(), email.getReceivedDate(),
                joddEmail.from().getEmail(), joddEmail.subject(), to, cc, bcc, txtMsg, htmlMsg, regAttachmentsList, regAttachmentsBytes, embedAttachmentsList, embedAttachmentsBytes);

        return observableData;
    }


}
