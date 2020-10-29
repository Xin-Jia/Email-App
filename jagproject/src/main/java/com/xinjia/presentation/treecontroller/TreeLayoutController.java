package com.xinjia.presentation.treecontroller;

/**
 * Sample Skeleton for 'TreeLayout.fxml' Controller Class
 */
import com.xinjia.presentation.tablecontroller.TableLayoutController;
import com.xinjia.properties.propertybean.EmailData;
import com.xinjia.properties.propertybean.FolderData;
import com.xinjia.sampledata.fakedata.SampleData;
import java.net.URL;
import java.sql.SQLException;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TreeLayoutController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="folderTree"
    private TreeView<FolderData> folderTree; // Value injected by FXMLLoader

    private TableLayoutController tableController;

    private ObservableList<FolderData> folders;
    private final static Logger LOG = LoggerFactory.getLogger(TreeLayoutController.class);

    @FXML
    private void initialize() {

        //MUST KEEP ASSERTS
        SampleData sd = new SampleData();

        folders = sd.getSampleFolderData();
        // We need a root node for the tree and it must be the same type as all
        // nodes
        FolderData rootFolder = new FolderData();

        // The tree will display common name so we set this for the root
        // Because we are using i18n the root name comes from the resource
        // bundle
        rootFolder.setFolderName("Folders");

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

    public void setTableController(TableLayoutController tableController) {
        this.tableController = tableController;

    }

    /**
     * Build the tree from the database
     *
     */
    public void displayTree() {

        // Build an item for each fish and add it to the root
        if (folders != null) {
            folders.stream().map((fd) -> new TreeItem<>(fd)).map((item) -> {
                item.setGraphic(new ImageView(getClass().getResource("/images/folder.png").toExternalForm()));
                return item;
            }).forEachOrdered((item) -> {
                folderTree.getRoot().getChildren().add(item);
            });
        }

        // Open the tree
        folderTree.getRoot().setExpanded(true);

        // Listen for selection changes and show the fishData details when
        // changed.
        folderTree.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> showEmailDetailsTree(newValue));
    }

    private void showEmailDetailsTree(TreeItem<FolderData> folderData) {
        String folderName = folderData.getValue().getFolderName();

        tableController.displayEmailsBasedOnFolder(folderName);
        //LOG.info("showEmailDetailsTree\n" + folderData.getValue());
    }

    public void addCustomFolder(String folderName) {
        FolderData newFolder = new FolderData(1, folderName);
        folders.add(newFolder);
        TreeItem<FolderData> folder = new TreeItem<>(newFolder);
        folder.setGraphic(new ImageView(getClass().getResource("/images/folder.png").toExternalForm()));
        folderTree.getRoot().getChildren().add(folder);
        //tableController.addFolderRecords(folderName);
    }

    public void deleteCustomFolder() {
        TreeItem<FolderData> selectedFolder = folderTree.getSelectionModel().getSelectedItem();
        String folderName = selectedFolder.getValue().getFolderName();
        LOG.info(folderName);
        boolean isDefaultFolders = folderName.equals("Inbox") || folderName.equals("Sent") || folderName.equals("Draft");
        if (isDefaultFolders) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error");
            dialog.setHeaderText("Cannot Delete Folder");
            dialog.setContentText("Defaut Folders (Inbox, Sent, Draft) cannot be deleted");
            dialog.show();
        } else {
            folderTree.getRoot().getChildren().remove(selectedFolder);
        }
    }

    //@FXML
    private void onDragOver(DragEvent event) {
        /* data is dragged over the target */
        // Accept it only if it is not dragged from the same control and if it
        // has a string data
        if (event.getGestureSource() != folderTree && event.getDragboard().hasString()) {
            // allow for both copying and moving, whatever user chooses
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    //@FXML
    private void onDragDropped(DragEvent event) {
        LOG.debug("onDragDropped");
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
            TreeCell item = (TreeCell) event.getSource();
            tableController.addEmail(item.getText());
            success = true;
        }
        //let the source know whether the string was successfully transferred
        // and used
        event.setDropCompleted(success);

        event.consume();
    }
}
