package com.xinjia.presentation.tablecontroller;

import com.xinjia.jdbc.persistence.EmailDAO;
import com.xinjia.jdbc.persistence.EmailDAOImpl;
import com.xinjia.presentation.formhtml.FormAndHTMLLayoutController;
import com.xinjia.properties.propertybean.EmailFXData;
import com.xinjia.properties.MailConfigBean;
import com.xinjia.properties.propertybean.propertiesmanager.MailConfigPropertiesManager;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the TableLayout. Contain a drag event and display emails based
 * on the selected folder. An Email info is displayed in the HTML editor when a
 * row is selected. Internationalization is used to display the column names.
 *
 * @author Xin Jia Cao
 */
public class TableLayoutController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="fromColumn"
    private TableColumn<EmailFXData, String> fromColumn; // Value injected by FXMLLoader

    @FXML // fx:id="subjectColumn"
    private TableColumn<EmailFXData, String> subjectColumn; // Value injected by FXMLLoader

    @FXML // fx:id="dateColumn"
    private TableColumn<EmailFXData, String> dateColumn; // Value injected by FXMLLoader

    @FXML // fx:id="toColumn"
    private TableColumn<EmailFXData, String> toColumn; // Value injected by FXMLLoader

    @FXML
    private TableView<EmailFXData> emailDataTable;

    private final static Logger LOG = LoggerFactory.getLogger(TableLayoutController.class);
    private EmailFXData emailDataDragged;
    private MailConfigBean propertyBean;
    private FormAndHTMLLayoutController editorController;
    private EmailDAO emailDAO;
    private EmailFXData clickedRow;

    private ObservableList<EmailFXData> emailsToDisplay;

    /**
     * Called by the FXMLLoader when initialization is complete. When the FXML
     * is loaded, if a control is not present, an exception is thrown and quits
     * the FXML loading process. Retrieve the mail config properties to get the
     * email address of the user. Instantiate a SampleData to fill each
     * ObservableList (inbox, sent, draft) of EmailData. Set the Cell factory
     * and Row factory, and adjust the columns width.
     *
     * @throws IOException
     */
    @FXML
    public void initialize() throws IOException {

        assert emailDataTable != null : "fx:id=\"emailDataTable\" was not injected: check your FXML file 'TableLayout.fxml'.";
        assert fromColumn != null : "fx:id=\"fromColumn\" was not injected: check your FXML file 'TableLayout.fxml'.";
        assert subjectColumn != null : "fx:id=\"subjectColumn\" was not injected: check your FXML file 'TableLayout.fxml'.";
        assert dateColumn != null : "fx:id=\"dateColumn\" was not injected: check your FXML file 'TableLayout.fxml'.";
        assert toColumn != null : "fx:id=\"toColumn\" was not injected: check your FXML file 'TableLayout.fxml'.";

        retrieveMailConfig();

        setCellFactory();
        setRowFactory();
        adjustColumnWidths();
    }

    public void setEmailDAO(EmailDAO emailDAO) {
        this.emailDAO = emailDAO;
    }

    /**
     * Called when the app is being initialized. Set the items in the table to
     * be the emails in the inbox folder.
     */
    public void displayTheTable() throws SQLException {
        displayEmailsBasedOnFolder("Inbox");
    }

    /**
     * Set the Cell Factory for each column. Connects the property in the
     * EmailData object to the column in the table
     */
    private void setCellFactory() {
        fromColumn.setCellValueFactory(cellData -> cellData.getValue()
                .fromProperty());
        subjectColumn.setCellValueFactory(cellData -> cellData.getValue()
                .subjectProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue()
                .dateProperty());
        toColumn.setCellValueFactory(cellData -> cellData.getValue()
                .toProperty());
    }

    /**
     * Set the Row Factory for the TableView so we can retrieve the EmailData
     * object from a selected row. When a row is selected in the table, it
     * displays the object infos in the HTML editor as well as its recipients
     * and subject.
     */
    private void setRowFactory() {
        emailDataTable.setRowFactory(tv -> {
            TableRow<EmailFXData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    clickedRow = row.getItem();
                    
                    editorController.displayEmailRecipientsAndAttachments(clickedRow);
                    //enable save and send buttons
                    if(row.getItem().getFolderId() == 3){
                        editorController.enableButtons(true);
                    }
                    else{
                         editorController.enableButtons(false);
                    }
                    try {
                        saveFileToDisk();
                        editorController.writeToEditorEmailData();
                    } catch (IOException ex) {
                        LOG.error("Error saving file to disk");
                    }

                }
            });
            return row;
        });
    }

    private void saveFileToDisk() throws IOException {
        LOG.info("attachments size: "+clickedRow.getEmbedAttachmentsBytes().size());
        for (int i = 0; i < clickedRow.getEmbedAttachmentsBytes().size(); i++) {
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(clickedRow.getEmbedAttachmentsBytes().get(i)));
            LOG.info("SAVING SELECTED EMAIL FILES TO DISK: "+clickedRow.getEmbedAttachments().get(i));
            File file = new File(clickedRow.getEmbedAttachments().get(i));
            ImageIO.write(img, "png", file);
        }
    }

    /**
     * Retrieve the mail config properties so we can display the user's email
     * address in the From column.
     *
     * @throws IOException
     */
    private void retrieveMailConfig() throws IOException {
        MailConfigPropertiesManager propertiesManager = new MailConfigPropertiesManager();
        propertyBean = new MailConfigBean();
        propertiesManager.loadTextProperties(propertyBean, "", "MailConfig");
    }

    /**
     * Set the FormAndHTMLLayoutController passed so we can display an
     * EmailFXData infos in the HTML editor.
     *
     * @param editorController the FormAndHTMLLayoutController
     */
    public void setEditorController(FormAndHTMLLayoutController editorController) {
        this.editorController = editorController;
    }

    /**
     * Event handler for when a drag is being detected. Allow a row in the table
     * to be dragged and put a string in the dragboard. Update the emailData to
     * be the newly dragged EmailData so when dropped, we can add it to the new
     * folder and delete it from the folder being dragged from.
     *
     * @param event
     */
    @FXML
    public void onDragDetected(MouseEvent event) {
        LOG.info("onDragDetected");

        emailDataDragged = emailDataTable.getSelectionModel().getSelectedItem();
        /* allow any transfer mode */
        Dragboard db = emailDataTable.startDragAndDrop(TransferMode.ANY);

        /* put the selected EmailFXData string on the dragboard */
        ClipboardContent content = new ClipboardContent();
        content.putString(emailDataTable.getSelectionModel().getSelectedItem().toString());
        db.setContent(content);

        event.consume();
    }

    /**
     * Adjust the columns widths so they can fill the table properly
     */
    private void adjustColumnWidths() {
        // Get the current width of the table
        double width = emailDataTable.getPrefWidth();
        // Set width of each column
        fromColumn.prefWidthProperty().bind(emailDataTable.widthProperty().multiply(0.25));
        subjectColumn.prefWidthProperty().bind(emailDataTable.widthProperty().multiply(0.25));
        dateColumn.prefWidthProperty().bind(emailDataTable.widthProperty().multiply(0.2));
        toColumn.prefWidthProperty().bind(emailDataTable.widthProperty().multiply(0.3));
    }

    /**
     * Display rows of EmailFXData based on the selected folder.
     *
     * @param folder The selected folder's name
     */
    public void displayEmailsBasedOnFolder(String folder) throws SQLException {
        emailsToDisplay = emailDAO.findEmailsByFolder(folder);
        emailDataTable.setItems(emailsToDisplay);
    }

    /**
     * First remove the dragged email from the original folder and then add the
     * dragged EmailFXData to the dropped folder.
     *
     * @param folderId the folder Id to be changed to
     */
    public void changeEmailFolder(int folderId) throws SQLException {

        emailDAO.changeEmailFolder(emailDataDragged.getId(), folderId);
        emailsToDisplay.remove(emailDataDragged);
    }

    public void deleteEmailRow() throws SQLException {
        if (clickedRow == null) {
            displayEmailError(resources.getString("NoEmailSelectedHeader"), resources.getString("NoEmailSelectedText"));
        } else {
            int id = clickedRow.getId();
            emailDAO.deleteEmail(id);
            emailsToDisplay.remove(clickedRow);
        }
    }

    /**
     * Display an alert dialog with the given header text and content text when
     * an error occured when trying to create or delete a folder.
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
    
    public void displayEmailsBySearchValue(ObservableList<EmailFXData> emailsToDisplay){
        this.emailsToDisplay = emailsToDisplay;
        emailDataTable.setItems(this.emailsToDisplay);
    }
    
    public void unselectRow(){
        emailDataTable.getSelectionModel().clearSelection();
    }
}
