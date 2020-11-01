package com.xinjia.presentation.tablecontroller;

import com.xinjia.presentation.formhtml.FormAndHTMLLayoutController;
import com.xinjia.properties.propertybean.EmailData;
import com.xinjia.properties.propertybean.MailConfigPropertyBean;
import com.xinjia.properties.propertybean.propertiesmanager.MailConfigPropertiesManager;
import com.xinjia.sampledata.fakedata.SampleData;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
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

    @FXML // fx:id="fromColumn"
    private TableColumn<EmailData, String> fromColumn; // Value injected by FXMLLoader

    @FXML // fx:id="subjectColumn"
    private TableColumn<EmailData, String> subjectColumn; // Value injected by FXMLLoader

    @FXML // fx:id="dateColumn"
    private TableColumn<EmailData, String> dateColumn; // Value injected by FXMLLoader

    @FXML
    private TableView<EmailData> emailDataTable;

    private final static Logger LOG = LoggerFactory.getLogger(TableLayoutController.class);
    private EmailData emailDataDragged;
    private ObservableList<EmailData> inboxEmails;
    private ObservableList<EmailData> sentEmails;
    private ObservableList<EmailData> draftEmails;
    private MailConfigPropertyBean propertyBean;
    private FormAndHTMLLayoutController editorController;

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

        retrieveMailConfig();
        //Instantiate the fake data class and populate each ObservableList
        SampleData fakeData = new SampleData(propertyBean);
        inboxEmails = fakeData.getSampleInboxEmailData();
        sentEmails = fakeData.getSampleSentEmailData();
        draftEmails = fakeData.getSampleDraftEmailData();

        setCellFactory();
        setRowFactory();
        adjustColumnWidths();
    }

    
    /**
     * Called when the app is being initialized. Set the items in the table to
     * be the emails in the inbox folder.
     */
    public void displayTheTable() {
        emailDataTable.setItems(inboxEmails);
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
    }

    /**
     * Set the Row Factory for the TableView so we can retrieve the EmailData
     * object from a selected row. When a row is selected in the table, it
     * displays the object infos in the HTML editor.
     */
    private void setRowFactory() {
        emailDataTable.setRowFactory(tv -> {
            TableRow<EmailData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    EmailData clickedRow = row.getItem();
                    editorController.writeToEditorEmailData(clickedRow);
                }
            });
            return row;
        });
    }

    /**
     * Retrieve the mail config properties so we can display the user's email
     * address in the From column.
     *
     * @throws IOException
     */
    private void retrieveMailConfig() throws IOException {
        MailConfigPropertiesManager propertiesManager = new MailConfigPropertiesManager();
        propertyBean = new MailConfigPropertyBean();
        propertiesManager.loadTextProperties(propertyBean, "", "MailConfig");
    }

    /**
     * Set the FormAndHTMLLayoutController passed so we can display an EmailData
     * infos in the HTML editor.
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

        /* put the selected EmailData string on the dragboard */
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
        fromColumn.setPrefWidth(width * .33);
        subjectColumn.setPrefWidth(width * .33);
        dateColumn.setPrefWidth(width * .28);
    }



    /**
     * Display rows of EmailData based on the selected folder.
     *
     * @param folder The selected folder's name
     */
    public void displayEmailsBasedOnFolder(String folder) {

        switch (folder) {
            case "Inbox" ->
                emailDataTable.setItems(inboxEmails);
            case "Sent" ->
                emailDataTable.setItems(sentEmails);
            case "Draft" ->
                emailDataTable.setItems(draftEmails);
            default -> {
                //ALWAYS EMPTY, NEED TO CHANGE IN PHASE 4
                ObservableList<EmailData> list = FXCollections.observableArrayList();
                emailDataTable.setItems(list);

            }
        }
    }

    /**
     * First remove the dragged email from the original folder and then add the
     * dragged EmailData to the dropped folder.
     *
     * @param folderName
     */
    public void addEmailRow(String folderName) {

        deleteEmailWhenDropped();
        // TODO: INSERT SELECTED EMAIL TO THE DROPPED FOLDER
        switch (folderName) {
            case "Inbox" ->
                inboxEmails.add(emailDataDragged);
            case "Sent" ->
                sentEmails.add(emailDataDragged);
            case "Draft" ->
                draftEmails.add(emailDataDragged);
            default -> {
                //TODO: INSERT TO CUSTOM FOLDERS

            }
        }
    }

    /**
     * Check in each ObservableList if they contain the email dropped.
     * Delete the email dropped from its original folder.
     */
    private void deleteEmailWhenDropped() {
        checkIfInFolder(inboxEmails);
        checkIfInFolder(sentEmails);
        checkIfInFolder(draftEmails);
    }

    /**
     * If the ObservableList contains the EmailData dropped, delete it.
     * Will be updated automatically in the TableView. (Row will be removed)
     * @param mails
     */
    private void checkIfInFolder(ObservableList<EmailData> mails) {
        //TODO: REMOVE SELECTED EMAIL DRAGGED FROM THE SOURCE FOLDER
        if (mails.contains(emailDataDragged)) {
            mails.remove(emailDataDragged);
        }
    }

}
