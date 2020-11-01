package com.xinjia.presentation.rootcontroller;

import com.xinjia.presentation.treecontroller.TreeLayoutController;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the CreateFolderPopUp. Contains the event handler for the
 * Create button when the user wants to create a folder. Also set the
 * TreeLayoutController to be used to add the folder to the TreeView.
 *
 * @author Xin Jia Cao
 */
public class FolderPopUpController {

    @FXML // fx:id="folderName"
    private TextField folderName; // Value injected by FXMLLoader

    @FXML // fx:id="createBtn"
    private Button createBtn; // Value injected by FXMLLoader

    private TreeLayoutController treeController;
    private final static Logger LOG = LoggerFactory.getLogger(FolderPopUpController.class);

    /**
     * Called by the FXMLLoader when initialization is complete. When the FXML
     * is loaded, if a control is not present, an exception is thrown and quits
     * the FXML loading process. Set up the properties.
     */
    @FXML
    void initialize() {
        assert folderName != null : "fx:id=\"folderName\" was not injected: check your FXML file 'CreateFolderPopUp.fxml'.";
        assert createBtn != null : "fx:id=\"createBtn\" was not injected: check your FXML file 'CreateFolderPopUp.fxml'.";
    }

    /**
     * Called when the user press the Create button in the popup. Close the
     * current stage and call the TreeLayoutController to add the folder to the
     * tree if the given name is valid.
     *
     * @throws IOException
     */
    @FXML
    void createFolder() throws IOException {
        LOG.info("Creating folder");
        Stage stage = (Stage) createBtn.getScene().getWindow();
        stage.close();
        treeController.addCustomFolder(folderName.getText());
    }

    /**
     * Initialize the TreeLayoutController to be used to add a new folder to the
     * tree.
     *
     * @param treeController The TreeLayoutController to be set to
     */
    public void setTreeController(TreeLayoutController treeController) {
        this.treeController = treeController;
    }
}
