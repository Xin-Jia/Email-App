/**
 * Xin Jia Cao
 */
package com.xinjia.presentation.rootcontroller;
import com.xinjia.presentation.formhtml.FormAndHTMLLayoutController;
import com.xinjia.presentation.tablecontroller.TableLayoutController;
import com.xinjia.presentation.treecontroller.TreeLayoutController;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RootLayoutSplitController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="folderTreeView"
    private BorderPane folderTreeView; // Value injected by FXMLLoader

    @FXML // fx:id="emailTableView"
    private BorderPane emailTableView; // Value injected by FXMLLoader

    @FXML // fx:id="formAndHtml"
    private BorderPane formAndHtml; // Value injected by FXMLLoader

    //private final EmailDAO emailDAO;
    private TreeLayoutController treeController;
    private TableLayoutController tableController;
    private FormAndHTMLLayoutController formHtmlController;

    /*public RootLayoutSplitController() {
        emailDAO = new EmailDAOImpl();
    }*/
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() throws SQLException {

        initTreeViewLayout();
        initTableViewLayout();
        initFormAndEditorLayout();

        // Tell the tree about the table
        setTableControllerToTree();
        //treeController.displayTree();
        //tableController.displayTheTable();
        try {
            treeController.displayTree();
            tableController.displayTheTable();
            //fishFXHTMLController.displayFishAsHTML();
        } catch (SQLException ex) {
            errorAlert("initialize()");
            Platform.exit();
        }
    }
    
    private void setTableControllerToTree() {
        treeController.setTableController(tableController);
    }
    
    /**
     * The TreeView Layout
     */
    private void initTreeViewLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);

            loader.setLocation(RootLayoutSplitController.class
                    .getResource("/fxml/TreeLayout.fxml"));
            AnchorPane treeView = (AnchorPane) loader.load();

            // Give the controller the data object.
            treeController = loader.getController();
           // treeController.setFishDAO(emailDAO);

            folderTreeView.getChildren().add(treeView);
        } catch (IOException ex) {
            errorAlert("initTreeViewLayout()");
            //Platform.exit();
        }
    }
    
    /**
     * The TableView Layout
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
            //tableController.setFishDAO(emailDAO);

            emailTableView.getChildren().add(tableView);
        } catch (IOException ex) {
            errorAlert("initTableViewLayout()");
            //Platform.exit();
        }
    }
    
    /**
     * The HTMLEditor Layout
     */
    private void initFormAndEditorLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);

            loader.setLocation(RootLayoutSplitController.class
                    .getResource("/fxml/FormHTMLLayout.fxml"));
            BorderPane htmlView = (BorderPane) loader.load();

            // Give the controller the data object.
            formHtmlController = loader.getController();
            //formHtmlController.setFishDAO(emailDAO);

            formAndHtml.getChildren().add(htmlView);
        } catch (IOException ex) {
            //errorAlert("initLowerRightLayout()");
            Platform.exit();
        }
    }
    
    /**
     * Error message popup dialog
     *
     * @param msg
     */
    private void errorAlert(String msg) {
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.setTitle("SQL ERROR");
        dialog.setHeaderText("SQL ERROR");
        dialog.setContentText(msg);
        dialog.show();
    }
    
    @FXML
    private void addFolder(ActionEvent event) throws IOException{
        Stage stage = new Stage();    
        stage.getIcons().add(new Image("/images/folder.png"));
        
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        TextField folderName = new TextField();
        folderName.setMaxWidth(140);
        Button createBtn = new Button("Create");
  
        root.getChildren().add(new Label("Folder name"));
        root.getChildren().add(folderName);
        root.getChildren().add(createBtn);
        
        Scene scene = new Scene(root,270,170);
        stage.setTitle("Add a Folder");
        stage.setScene(scene);
        stage.show();
        
        createBtn.setOnAction(new EventHandler<ActionEvent>() {
        @Override public void handle(ActionEvent e) {
            treeController.addCustomFolder(folderName.getText());
            stage.close();
    }});
    }
    
    @FXML
    private void deleteFolder(){
        treeController.deleteCustomFolder();
    }
    
    @FXML
    private void displayAbout(){
        
    }
            
    
}
