package com.xinjia.presentation.rootcontroller;

import com.xinjia.presentation.MainEmailApp;
import com.xinjia.presentation.formhtml.FormAndHTMLLayoutController;
import com.xinjia.presentation.mailconfigcontroller.PropertiesFormController;
import com.xinjia.presentation.tablecontroller.TableLayoutController;
import com.xinjia.presentation.treecontroller.TreeLayoutController;
import com.xinjia.properties.MailConfigBean;
import com.xinjia.properties.propertybean.propertiesmanager.MailConfigPropertiesManager;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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

    private TreeLayoutController treeController;
    private TableLayoutController tableController;
    private FormAndHTMLLayoutController formHtmlController;
    private FolderPopUpController folderPopUpController;

    private final static Logger LOG = LoggerFactory.getLogger(RootLayoutSplitController.class);

    //TODO: Default constructor that initializes an EmailDAO
    /**
     * Called by the FXMLLoader when initialization is complete. When the FXML
     * is loaded, if a control is not present, an exception is thrown and quits
     * the FXML loading process. Set up the properties. Initialize the
     * controllers and display each of them.
     */
    @FXML
    private void initialize() {

        assert folderTreeView != null : "fx:id=\"folderTreeView\" was not injected: check your FXML file 'RootLayout.fxml'.";
        assert emailTableView != null : "fx:id=\"emailTableView\" was not injected: check your FXML file 'RootLayout.fxml'.";
        assert formAndHtml != null : "fx:id=\"formAndHtml\" was not injected: check your FXML file 'RootLayout.fxml'.";

        //Initialize all three controllers 
        initTreeViewLayout();
        initFormAndEditorLayout();
        initTableViewLayout();

        // Tell the tree about the table
        setTableControllerToTree();

        treeController.displayTree();
        tableController.displayTheTable();
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
            //Add the BorderPane of the FormHTMLLayout to the BorderPane of the RootLayout
            formAndHtml.getChildren().add(htmlView);
        } catch (IOException ex) {
            LOG.error("Error loading file in initFormAndEditorLayout()", ex);
        }
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
            folderPopUpController = loader.getController();
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
    private void deleteFolder() {
        treeController.deleteCustomFolder();
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
    private void addAttachment() {
        Stage stage = (Stage) formAndHtml.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            LOG.info("Absolute Path: " + file.getAbsolutePath());
        }
        //TODO: add an attachment to an email
    }

    /**
     * Called when the user press the Delete Selected Email MenuItem
     */
    @FXML
    private void deleteSelectedEmail() {
        //TODO: delete selected email from the corresponding folder
    }

}
