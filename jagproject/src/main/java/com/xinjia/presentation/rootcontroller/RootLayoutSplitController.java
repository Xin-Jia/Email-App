package com.xinjia.presentation.rootcontroller;

import com.xinjia.business.SendAndReceive;
import com.xinjia.jdbc.beans.EmailData;
import com.xinjia.jdbc.persistence.EmailDAO;
import com.xinjia.jdbc.persistence.EmailDAOImpl;
import com.xinjia.presentation.MainEmailApp;
import com.xinjia.presentation.formhtml.FormAndHTMLLayoutController;
import com.xinjia.presentation.mailconfigcontroller.PropertiesFormController;
import com.xinjia.presentation.tablecontroller.TableLayoutController;
import com.xinjia.presentation.treecontroller.TreeLayoutController;
import com.xinjia.properties.MailConfigBean;
import com.xinjia.properties.propertybean.propertiesmanager.MailConfigPropertiesManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.activation.DataSource;
import jodd.mail.Email;
import jodd.mail.EmailAddress;
import jodd.mail.EmailAttachment;
import jodd.mail.EmailMessage;
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

    public void setMailConfigBean(MailConfigBean configBean) throws SQLException {

        emailDAO = new EmailDAOImpl(configBean);

        //Initialize all three controllers 
        initFormAndEditorLayout();
        initTreeViewLayout();
        initTableViewLayout();

        reloadInbox();

        // Tell the tree about the table
        setTableControllerToTree();
        treeController.displayTree();
        tableController.displayTheTable();

    }

    //TODO: Default constructor that initializes an EmailDAO
    /**
     * Called by the FXMLLoader when initialization is complete. When the FXML
     * is loaded, if a control is not present, an exception is thrown and quits
     * the FXML loading process. Set up the properties. Initialize the
     * controllers and display each of them.
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
            //set the root controller so the form can access the reloading methods for the Inbox
            formHtmlController.setRootController(this);
            //Add the BorderPane of the FormHTMLLayout to the BorderPane of the RootLayout
            formAndHtml.getChildren().add(htmlView);
        } catch (IOException ex) {
            LOG.error("Error loading file in initFormAndEditorLayout()", ex);
        }
    }

    private void insertReceivedEmail(ReceivedEmail receivedEmail) throws SQLException {
        LOG.info("INSERTING RECEIVED EMAILS");
        EmailData emailBean = new EmailData();
        //int emailId, int folderId, LocalDateTime receivedDate, Email email
        //set the folder to the Inbox folder
        emailBean.setFolderId(1);
        Date date = receivedEmail.receivedDate();
        Timestamp timeStamp = new Timestamp(date.getTime());
        LocalDateTime localDateTime = timeStamp.toLocalDateTime();
        emailBean.setReceivedDate(localDateTime);
        
        Email email = new Email();
        
        List<EmailMessage> messages = receivedEmail.messages();
  
        ArrayList<String> messagesString = ((EmailDAOImpl) emailDAO).retrieveMessageContent(messages, "text/plain");
        if (!messages.isEmpty()) {
            
            if (!messages.isEmpty()) {
                LOG.info("TEXT MSG NOT EMPTY");
                email.textMessage(messagesString.get(0));
            }

            messagesString = ((EmailDAOImpl) emailDAO).retrieveMessageContent(messages, "text/html");
            if (!messages.isEmpty()) {
                LOG.info("HTML MSG NOT EMPTY");
                LOG.info(messagesString.get(0));
                email.htmlMessage(messagesString.get(0));
            }
        }
        LOG.info("ATTACHMENT SIZE IN RECEIVED EMAIL: "+receivedEmail.attachments().size());
        for(int i = 0; i< receivedEmail.attachments().size(); i++){
            if(receivedEmail.attachments().get(i).isEmbedded() && receivedEmail.attachments().get(i).getContentId() != null){
                LOG.info("QUERY: "+"img src=\"cid:"+receivedEmail.attachments().get(i).getContentId().replaceAll("[<>]", ""));
                if(!receivedEmail.attachments().get(i).getContentId().equals("") && messagesString.get(0).contains("img src=\"cid:"+receivedEmail.attachments().get(i).getContentId().replaceAll("[<>]", ""))){
                    LOG.info("it is embedded");
                    email.embeddedAttachment(receivedEmail.attachments().get(i));
                }
                else{
                LOG.info("it is NOT embedded");
                email.attachment(receivedEmail.attachments().get(i));
            }
                
            }
            else{
                LOG.info("it is NOT embedded, contentId is null");
                email.attachment(receivedEmail.attachments().get(i));
            }
            LOG.info("IS EMBEDDED??? "+receivedEmail.attachments().get(i).isEmbedded());
            LOG.info("CONTENT ID: "+ receivedEmail.attachments().get(i).getContentId());
            
        }
        LOG.info("ATTACHMENT SIZE IN NEW RECEIVED EMAIL: "+email.attachments().size());
        email.from(receivedEmail.from().getEmail());
        email.subject(receivedEmail.subject());
        email.sentDate(receivedEmail.sentDate());
       // email.attachments(receivedEmail.attachments());


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
                //folderPopUpController.setEmailDAO(emailDAO);

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
            PropertiesFormController controller = loader.getController();

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
     * Called when the user press the Save Attachment MenuItem.
     */
    @FXML
    private void saveAttachment() {
        //TODO: save an attachment to an email
    }

    /**
     * Called when the user press the Add Attachment MenuItem. For now, it only
     * displays the absolute path of the selected file.
     */
    @FXML
    private void addAttachment() throws IOException {
        Stage stage = (Stage) formAndHtml.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            moveFileToRoot(file);
            LOG.info("Absolute Path: " + file.getAbsolutePath());
            formHtmlController.addAttachment(file.getName());
        }
    }

    private void moveFileToRoot(File file) throws IOException {
        Files.copy(file.toPath(),
                (new File(file.getName())).toPath(),
                StandardCopyOption.REPLACE_EXISTING);

    }

    /**
     * Called when the user press the Delete Selected Email MenuItem Delete an
     * email and its row in the TableView
     */
    @FXML
    private void deleteSelectedEmail() throws SQLException {
        tableController.deleteEmailRow();
    }

    @FXML
    private void createNewEmail() {
        formHtmlController.emptyAllFields();
        //enable the buttons 
        formHtmlController.enableButtons(true);
    }

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

    @FXML
    void searchEmails(ActionEvent event) throws SQLException {
        if (searchEmailsTextField.getText().trim().isEmpty()) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle(resources.getString("errorTitle"));
            dialog.setHeaderText(resources.getString("emptyTextFieldHeader"));
            dialog.setContentText(resources.getString("emptyTextFieldText"));
            dialog.show();
        }
        else{
            String value = comboBox.getSelectionModel().getSelectedItem();
            switch(value){
                case "Subject":
                    tableController.displayEmailsBySearchValue(emailDAO.findEmailsBySubject(searchEmailsTextField.getText()));
                    break;
                case "Recipients":
                     tableController.displayEmailsBySearchValue(emailDAO.findEmailsByRecipients(searchEmailsTextField.getText()));
                    break;
            }

        }
    }

}
