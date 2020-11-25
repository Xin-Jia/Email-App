package com.xinjia.presentation.treecontroller;

import com.xinjia.exceptions.FolderAlreadyExistsException;
import com.xinjia.jdbc.persistence.EmailDAO;
import com.xinjia.jdbc.persistence.EmailDAOImpl;
import com.xinjia.presentation.tablecontroller.TableLayoutController;
import com.xinjia.properties.propertybean.FolderData;
import java.sql.SQLException;
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
    private EmailDAO emailDAO;
    private static final int INBOX_ID = 1;
    private static final int SENT_ID = 2;
    private static final int DRAFT_ID = 3;
    

    private final static Logger LOG = LoggerFactory.getLogger(TreeLayoutController.class);

    /**
     * Called by the FXMLLoader when initialization is complete. When the FXML
     * is loaded, if a control is not present, an exception is thrown and quits
     * the FXML loading process. Instantiate the SampleData class to get the
     * sample folders and initialize the TreeView.
     */
    @FXML
    private void initialize() throws SQLException {
        assert folderTree != null : "fx:id=\"folderTree\" was not injected: check your FXML file 'TreeLayout.fxml'.";
        initializeTree();
    }

    /**
     * Set the TableLayoutController so we can use it to display emails based on
     * the selected folder
     *
     * @param tableController The TableLayoutController
     */
    public void setTableController(TableLayoutController tableController) {
        this.tableController = tableController;

    }
    /**
     * Setter for the EmailDAO so we can use it to manage database operations
     * @param emailDAO 
     */
    public void setEmailDAO(EmailDAO emailDAO) {
        this.emailDAO = emailDAO;
    }

    /**
     * Initialize the TreeView. Add event handlers for drag over and drag
     * dropped.
     *
     * @throws java.sql.SQLException
     */
    public void initializeTree() throws SQLException {
        FolderData rootFolder = new FolderData();
        rootFolder.setFolderName(resources.getString("folder"));
        folderTree.setRoot(new TreeItem<>(rootFolder));

        folderTree.setCellFactory((e) -> new TreeCell<FolderData>() {
            @Override
            protected void updateItem(FolderData item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    //convert the folder names if needed (locale is french)
                    setText(internationalize(item.getFolderName()));
                    setGraphic(getTreeItem().getGraphic());
                    if (item.getId() != DRAFT_ID) {
                        this.setOnDragOver(new EventHandler<DragEvent>() {
                            @Override
                            public void handle(DragEvent dragEvent) {
                                onDragOver(dragEvent);
                            }
                        });
                        this.setOnDragDropped(new EventHandler<DragEvent>() {
                            @Override
                            public void handle(DragEvent dragEvent) {
                                try {
                                    onDragDropped(dragEvent);
                                } catch (SQLException ex) {
                                    LOG.error("Exception occured when dropping");
                                }
                            }
                        });
                    }
                } else {
                    setText(null);
                    setGraphic(null);
                }
            }
        });
    }

    /**
     * Internationalize the folder names based on the resources
     * @param name the folder name
     * @return the internationalized name of the provided name
     */
    private String internationalize(String name) {
        if (name.equals("Inbox") || name.equals("Draft") || name.equals("Sent")) {
            return resources.getString(name.toLowerCase());
        }
        return name;
    }

    /**
     * Populate the TreeView based on the ObservableList of FolderData.
     * Display a folder icon next to the folders name.
     *
     * @throws java.sql.SQLException
     */
    public void displayTree() throws SQLException {

        foldersList = emailDAO.findFolders();
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
                .addListener((observable, oldValue, newValue) -> {
                    try {
                        showEmailDetails(newValue);
                        
                        tableController.unselectRow();
                    } catch (SQLException ex) {
                        LOG.error("Exception occured when selecting a tree cell", ex);
                    }
                });
    }


    /**
     * Called whenever the selection changes. Use the TableLayoutController to
     * display emails based on the selected folder.
     *
     * @param folderData The TreeItem<FolderData>
     */
    private void showEmailDetails(TreeItem<FolderData> folderData) throws SQLException {
        String folderName = folderData.getValue().getFolderName();
        int folderId = folderData.getValue().getId();
        tableController.displayEmailsBasedOnFolder(folderName, folderId);
    }

    /**
     * Called in FolderPopUpController when the Create button is pressed. Check
     * if the given folder name is valid first. Add a folder to the TreeView.
     *
     * @param folderName
     * @throws java.sql.SQLException
     * @throws com.xinjia.exceptions.FolderAlreadyExistsException
     */
    public void addCustomFolder(String folderName) throws SQLException, FolderAlreadyExistsException {
        //Get the list of folder names currently in the ObservableList of FolderData
        List<String> folderNames = getFolderNames();
        //Check if the folder name is empty
        if (folderName.equals("")) {
            displayFolderError(resources.getString("emptyFolderNameHeader"), resources.getString("errorEmptyFolderNameText"));
        } //Check if the folder name already exists
        else if (folderNames.contains(folderName.toLowerCase().replaceAll("\\s+",""))) {
            displayFolderError(resources.getString("invalidFolderNameHeader"), resources.getString("errorFolderNameText"));
        } else {
            emailDAO.createFolder(folderName);
            int folderId = ((EmailDAOImpl) emailDAO).findFolderIdByName(folderName);
            FolderData newFolder = new FolderData(folderId, folderName);
            foldersList.add(newFolder);
            TreeItem<FolderData> folder = new TreeItem<>(newFolder);
            folder.setGraphic(new ImageView(getClass().getResource("/images/folder.png").toExternalForm()));
            folderTree.getRoot().getChildren().add(folder);
        }
    }

    /**
     * Check if the selected folder can be renamed. Only custom folders can be renamed.
     * Will display an alert dialog if it cannot be renamed.
     * @return true if the folder can be renamed (not Sent, Draft, Inbox) and false otherwise
     */
    public boolean checkCanSelectedFolderBeRenamed() {
        TreeItem<FolderData> selectedFolder = folderTree.getSelectionModel().getSelectedItem();
        if (checkIfFolderSelected(selectedFolder)) {
            int folderId = selectedFolder.getValue().getId();
            //Check if the selected folder is one of the default folders
            boolean isDefaultFolders = (folderId == INBOX_ID || folderId == SENT_ID || folderId == DRAFT_ID);
            if (isDefaultFolders) {
                displayFolderError(resources.getString("cannotRenameFolderHeader"), resources.getString("errorRenameText"));
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Rename the selected folder and update it in the database.
     * If the new name for the folder is Sent, Draft or Inbox (not case-sensitive), it will display an alert dialog.
     * It will do the same if the provided new name is empty.
     * @param newFolderName the new folder name
     * @throws SQLException
     * @throws FolderAlreadyExistsException 
     */
    public void renameCustomFolder(String newFolderName) throws SQLException, FolderAlreadyExistsException {
        List<String> folderNames = getFolderNames();
        TreeItem<FolderData> selectedFolder = folderTree.getSelectionModel().getSelectedItem();
        String folderName = selectedFolder.getValue().getFolderName();
        //Check if the folder name is empty
        if (newFolderName.equals("")) {
            displayFolderError(resources.getString("emptyFolderNameHeader"), resources.getString("errorEmptyFolderNameText"));
        } //Check if the folder name already exists
        else if (folderNames.contains(newFolderName.toLowerCase().replaceAll("\\s+",""))) {
            displayFolderError(resources.getString("invalidFolderNameHeader"), resources.getString("errorFolderNameText"));
        } else {

            //update the new folder name in the tree
            folderTree.getSelectionModel().getSelectedItem().getValue().setFolderName(newFolderName);
            //update the new folder name in the database
            emailDAO.updateFolderName(folderName, newFolderName);
        }

    }

    /**
     * Check if a folder has been selected or not. If not, display an alert dialog.
     * @param selectedFolder the TreeItem of FolderData 
     * @return true if a folder is selected and false otherwise
     */
    private boolean checkIfFolderSelected(TreeItem<FolderData> selectedFolder) {
        if (selectedFolder == null) {
            displayFolderError(resources.getString("noFolderSelectedHeader"), resources.getString("errorSelectText"));
            return false;
        }
        return true;

    }

    /**
     * Get the folder names currently in the ObservableList of FolderData
     *
     * @return the list of folder names
     */
    private List<String> getFolderNames() {
        List<String> folderNames = new ArrayList<>();
        foldersList.forEach(folder -> {
            folderNames.add(folder.getFolderName().toLowerCase());
        });
        return folderNames;
    }

    /**
     * Delete a custom folder (not Sent, Inbox or Draft)
     *
     * @throws java.sql.SQLException
     */
    public void deleteCustomFolder() throws SQLException {
        TreeItem<FolderData> selectedFolder = folderTree.getSelectionModel().getSelectedItem();
        //Check if a folder has been selected
        if (selectedFolder == null) {
            displayFolderError(resources.getString("noFolderSelectedHeader"), resources.getString("errorSelectText"));
        } else {
            int folderId = selectedFolder.getValue().getId();
            //Check if the selected folder is one of the default folders
            boolean isDefaultFolders = (folderId == INBOX_ID  || folderId == SENT_ID || folderId == DRAFT_ID);
            if (isDefaultFolders) {
                displayFolderError(resources.getString("cannotDelFolderHeader"), resources.getString("errorDelText"));

            } else {
                //remove the selected folder from the TreeView
                folderTree.getRoot().getChildren().remove(selectedFolder);
                //delete the folder in the database
                String folderName = selectedFolder.getValue().getFolderName();
                emailDAO.deleteFolder(folderName);
            }
        }
    }

    /**
     * Display an alert dialog with the given header text and content text when
     * an error occured when trying to create, rename or delete a folder.
     *
     * @param header The header text
     * @param content The content text
     */
    private void displayFolderError(String header, String content) {
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.setTitle(resources.getString("errorTitle"));
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        dialog.show();
    }

    /**
     * Event handler when something is being dragged over a TreeCell
     *
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
     *
     * @param event The DragEvent
     */
    private void onDragDropped(DragEvent event) throws SQLException {
        LOG.info("onDragDropped");

        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
            TreeCell<FolderData> item = (TreeCell) event.getSource();
            int folderId = item.getTreeItem().getValue().getId();
            //add the EmailData dragged to the new folder 
            tableController.changeEmailFolder(folderId);
            success = true;
        }
        //let the source know whether the string was successfully transferred
        // and used
        event.setDropCompleted(success);
        event.consume();

    }
}
