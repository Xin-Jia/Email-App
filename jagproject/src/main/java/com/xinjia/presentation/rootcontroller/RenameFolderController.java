
package com.xinjia.presentation.rootcontroller;

import com.xinjia.exceptions.FolderAlreadyExistsException;
import com.xinjia.presentation.treecontroller.TreeLayoutController;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the RenameFolderPopUp. Contains the event handler for the
 * Rename button when the user wants to rename a folder. Also set the
 * TreeLayoutController to be used to rename the folder in the TreeView.
 * @author Xin Jia Cao
 */
public class RenameFolderController {

    @FXML // fx:id="folderName"
    private TextField folderName; // Value injected by FXMLLoader

    @FXML // fx:id="renameBtn"
    private Button renameBtn; // Value injected by FXMLLoader
    
    private TreeLayoutController treeController;
    private final static Logger LOG = LoggerFactory.getLogger(RenameFolderController.class);

    @FXML
    void renameFolder(ActionEvent event) throws SQLException, FolderAlreadyExistsException {
        LOG.info("Renaming folder");
        Stage stage = (Stage) renameBtn.getScene().getWindow();
        String newFolderName = folderName.getText();
        treeController.renameCustomFolder(newFolderName);
        stage.close();
    }
    
    /**
     * Initialize the TreeLayoutController to be used to rename a new folder
     *
     * @param treeController The TreeLayoutController to be set to
     */
    public void setTreeController(TreeLayoutController treeController) {
        this.treeController = treeController;
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert folderName != null : "fx:id=\"folderName\" was not injected: check your FXML file 'RenameFolderPopUp.fxml'.";
        assert renameBtn != null : "fx:id=\"renameBtn\" was not injected: check your FXML file 'RenameFolderPopUp.fxml'.";

    }
}
