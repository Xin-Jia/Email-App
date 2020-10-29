package com.xinjia.presentation.tablecontroller;

/**
 * Sample Skeleton for 'TableLayout.fxml' Controller Class
 */
import com.xinjia.properties.propertybean.EmailData;
import com.xinjia.sampledata.fakedata.SampleData;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableLayoutController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="fromColumn"
    private TableColumn<EmailData, String> fromColumn; // Value injected by FXMLLoader

    @FXML // fx:id="subjectColumn"
    private TableColumn<EmailData, String> subjectColumn; // Value injected by FXMLLoader

    @FXML // fx:id="dateColumn"
    private TableColumn<EmailData, String> dateColumn; // Value injected by FXMLLoader

    @FXML
    private TableView<EmailData> emailDataTable;


    private final static Logger LOG = LoggerFactory.getLogger(TableLayoutController.class);
    private EmailData emailData;
    private ObservableList<EmailData> inboxEmails;
    private ObservableList<EmailData> sentEmails;
    private ObservableList<EmailData> draftEmails;
    private List<ObservableList<EmailData>> otherEmails;

    @FXML
    public void initialize() {

        SampleData sd = new SampleData();
        inboxEmails = sd.getSampleInboxEmailData();
        sentEmails = sd.getSampleSentEmailData();
        draftEmails = sd.getSampleDraftEmailData();
        otherEmails = new ArrayList<>();
        // Connects the property in the FishData object to the column in the
        // table
        fromColumn.setCellValueFactory(cellData -> cellData.getValue()
                .fromProperty());
        subjectColumn.setCellValueFactory(cellData -> cellData.getValue()
                .subjectProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue()
                .dateProperty());

        adjustColumnWidths();

    }

    @FXML
    public void onDragDetected(MouseEvent event) {
        LOG.debug("onDragDetected");
        emailData = emailDataTable.getSelectionModel().getSelectedItem();
        /* allow any transfer mode */
        Dragboard db = emailDataTable.startDragAndDrop(TransferMode.ANY);

        /* put a string on dragboard */
        ClipboardContent content = new ClipboardContent();
        content.putString(emailDataTable.getSelectionModel().getSelectedItem().toString());
        db.setContent(content);

        event.consume();
    }

    private void adjustColumnWidths() {
        // Get the current width of the table
        double width = emailDataTable.getPrefWidth();
        // Set width of each column
        fromColumn.setPrefWidth(width * .30);
        subjectColumn.setPrefWidth(width * .25);
        dateColumn.setPrefWidth(width * .25);
    }

    public TableView<EmailData> getEmailDataTable() {
        return emailDataTable;
    }

    /**
     * The table displays the fish data
     *
     * @throws SQLException
     */
    public void displayTheTable() throws SQLException {
        // Add observable list data to the table

        emailDataTable.setItems(inboxEmails);
    }

    public void removeRow(EmailData mailData) {
        emailDataTable.getItems().remove(mailData);
    }

    /*public void addFolderRecords(String folderName) {
        ObservableList<EmailData> list = FXCollections.observableArrayList();
        list.add(new EmailData(1, "", "", folderName));
        otherEmails.add(list);
    }*/

    public void displayEmailsBasedOnFolder(String folder) {

        switch (folder) {
            case "Inbox" -> emailDataTable.setItems(inboxEmails);
            case "Sent" -> emailDataTable.setItems(sentEmails);
            case "Draft" -> emailDataTable.setItems(draftEmails);
            default -> {
                ObservableList<EmailData> list = FXCollections.observableArrayList();
                emailDataTable.setItems(list);

        }
    }
    }

    public void addEmail(String folderName) {
        //emailData.setFolderName(folderName);
        checkIfInFolder(inboxEmails);
        checkIfInFolder(sentEmails);
        checkIfInFolder(draftEmails);
        checkIfInCustomFolders();
        switch (folderName) {
            case "Inbox" -> inboxEmails.add(emailData);
            case "Sent" -> sentEmails.add(emailData);
            case "Draft" -> draftEmails.add(emailData);
            default -> {
                //TODO
                
            } 
        }
    }

    private void checkIfInFolder(ObservableList<EmailData> mails) {
        if (mails.contains(emailData)) {
            mails.remove(emailData);
        }
    }

    private void checkIfInCustomFolders() {
        for (ObservableList<EmailData> item : otherEmails) {
            if (item.contains(emailData)) {
                item.remove(emailData);
            }
        }
    }
}
