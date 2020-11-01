package com.xinjia.presentation.treecontroller;

import com.xinjia.presentation.tablecontroller.TableLayoutController;
import com.xinjia.properties.propertybean.FolderData;
import com.xinjia.sampledata.fakedata.SampleData;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TreeLayoutController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="folderTree"
    private TreeView<FolderData> folderTree; // Value injected by FXMLLoader
    
    private TableLayoutController tableController;
    private ObservableList<FolderData> foldersList;

    private final static Logger LOG = LoggerFactory.getLogger(TreeLayoutController.class);

    /**
     * Called by the FXMLLoader when initialization is complete. When the FXML
     * is loaded, if a control is not present, an exception is thrown and quits
     * the FXML loading process. Instantiate the SampleData class to get the sample folders and initialize the TreeView.
     */
    @FXML
    private void initialize() {
        assert folderTree != null : "fx:id=\"folderTree\" was not injected: check your FXML file 'TreeLayout.fxml'.";
        //Instantiate the fake data class with the default constructor because we don't need the MailConfig property yet
        SampleData sd = new SampleData();
        foldersList = sd.getSampleFolderData();
        initializeTree();
    }

    /**
     * Set the TableLayoutController so we can use it to display emails based on the selected folder
     * @param tableController The TableLayoutController
     */
    public void setTableController(TableLayoutController tableController) {
        this.tableController = tableController;

    }

    /**
     * Initialize the TreeView.
     * Add event handlers for drag over and drag dropped.
     */
    public void initializeTree() {
        FolderData rootFolder = new FolderData();
        rootFolder.setFolderName(resources.getString("folder"));
        folderTree.setRoot(new TreeItem<>(rootFolder));

        folderTree.setCellFactory((e) -> new TreeCell<FolderData>() {
            @Override
            protected void updateItem(FolderData item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getFolderName());
                    setGraphic(getTreeItem().getGraphic());
                    this.setOnDragOver(new EventHandler<DragEvent>() {
                        @Override
                        public void handle(DragEvent dragEvent) {
                            onDragOver(dragEvent);
                        }
                    });
                    this.setOnDragDropped(new EventHandler<DragEvent>() {
                        @Override
                        public void handle(DragEvent dragEvent) {
                            onDragDropped(dragEvent);
                        }
                    });
                } else {
                    setText("");
                    setGraphic(null);
                }
            }
        });
    }

    /**
     * Populate the TreeView based on the ObservableList of FolderData.
     * Display an icon next to the folders name.
     */
    public void displayTree() {

        // Build an item for each folder and add it to the TreeView
        if (foldersList != null) {
            foldersList.stream().map((fd) -> new TreeItem<>(fd)).map((item) -> {
                item.setGraphic(new ImageView(getClass().getResource("/images/folder.png").toExternalForm()));
                return item;
            }).forEachOrdered((item) -> {
                // Add the TreeItem to the TreeView
                folderTree.getRoot().getChildren().add(item);
            });
        }

        // Open the tree
        folderTree.getRoot().setExpanded(true);

        // Listen for selection changes and show the folder's emails in the TableView when
        // changed.
        folderTree.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> showEmailDetailsTree(newValue));
    }

    /**
     * Called whenever the selection changes.
     * Use the TableLayoutController to display emails based on the selected folder.
     * @param folderData The TreeItem<FolderData>
     */
    private void showEmailDetailsTree(TreeItem<FolderData> folderData) {
        String folderName = folderData.getValue().getFolderName();

        tableController.displayEmailsBasedOnFolder(folderName);
        LOG.info("Folder selected: " + folderData.getValue());
    }

    /**
     * Called in FolderPopUpController when the Create button is pressed.
     * Check if the given folder name is valid first.
     * Add a folder to the TreeView.
     * @param folderName 
     */
    public void addCustomFolder(String folderName) {
        //Get the list of folder names currently in the ObservableList of FolderData
        List<String> folderNames = getFolderNames();
        //Check if the folder name is empty
        if (folderName.equals("")){
            displayFolderError(resources.getString("emptyFolderNameHeader"), resources.getString("errorEmptyFolderNameText"));
        } 
        //Check if the folder name already exists
        else if(folderNames.contains(folderName.toLowerCase())) {
            displayFolderError(resources.getString("invalidFolderNameHeader"), resources.getString("errorFolderNameText"));
        } else {
            //TODO: INSERT CREATED FOLDER IN THE FOLDER TABLE
            FolderData newFolder = new FolderData(1, folderName);
            foldersList.add(newFolder);
            TreeItem<FolderData> folder = new TreeItem<>(newFolder);
            folder.setGraphic(new ImageView(getClass().getResource("/images/folder.png").toExternalForm()));
            folderTree.getRoot().getChildren().add(folder);
        }
    }

    /**
     * Get the folder names currently in the ObservableList of FolderData
     * @return the list of folder names
     */
    private List<String> getFolderNames() {
        List<String> folderNames = new ArrayList<>();
        for (FolderData folder : foldersList) {
            folderNames.add(folder.getFolderName().toLowerCase());
        }
        return folderNames;
    }

    /**
     * Delete a custom folder (not Sent, Inbox or Draft)
     */
    public void deleteCustomFolder() {
        TreeItem<FolderData> selectedFolder = folderTree.getSelectionModel().getSelectedItem();
        //Check if a folder has been selected
        if (selectedFolder == null) {
            displayFolderError(resources.getString("noFolderSelectedHeader"), resources.getString("errorSelectText"));
        } else {
            String folderName = selectedFolder.getValue().getFolderName();
            //Check if the selected folder is one of the default folders
            boolean isDefaultFolders = folderName.equalsIgnoreCase("inbox") || folderName.equalsIgnoreCase("sent") || folderName.equalsIgnoreCase("draft");
            if (isDefaultFolders) {
                displayFolderError(resources.getString("cannotDelFolderHeader"), resources.getString("errorDelText"));

            } else {
                //remove the selected folder from the TreeView
                folderTree.getRoot().getChildren().remove(selectedFolder);
            }
        }
    }

    /**
     * Display an alert dialog with the given header text and content text when an error occured when trying
     * to create or delete a folder.
     * @param header The header text
     * @param content The content text
     */
    private void displayFolderError(String header, String content){
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.setTitle(resources.getString("errorTitle"));
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        dialog.show();
    }

    /**
     * Event handler when something is being dragged over a TreeCell
     * @param event The DragEvent
     */
    private void onDragOver(DragEvent event) {

        // Accept it only if it is not dragged from the same control and if it
        // has a string data
        if (event.getGestureSource() != folderTree && event.getDragboard().hasString()) {
            // allow for both copying and moving, whatever user chooses
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }


    /**
     * Event handler when something is being dropped into a TreeCell
     * @param event The DragEvent
     */
    private void onDragDropped(DragEvent event) {
        LOG.info("onDragDropped");
        
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
            TreeCell item = (TreeCell) event.getSource();
            //add the EmailData dragged to the new folder 
            tableController.addEmailRow(item.getText());
            success = true;
        }
        //let the source know whether the string was successfully transferred
        // and used
        event.setDropCompleted(success);
        event.consume();
    }
}
