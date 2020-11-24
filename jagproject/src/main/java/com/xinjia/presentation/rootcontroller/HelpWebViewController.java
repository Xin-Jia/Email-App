package com.xinjia.presentation.rootcontroller;

import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the HelpWebView.
 * Displays the WebView with an HTML page that tells the user how to use the Email App.
 * 
 * @author Xin Jia Cao
 */
public class HelpWebViewController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;
    
    @FXML // fx:id="helpWebView"
    private WebView helpWebView; // Value injected by FXMLLoader
    private final static Logger LOG = LoggerFactory.getLogger(HelpWebViewController.class);

    /**
     * Called by the FXMLLoader when initialization is complete. When the FXML
     * is loaded, if a control is not present, an exception is thrown and quits
     * the FXML loading process. Load the HTML file to be displayed based on the locale.
     */
    @FXML
    void initialize() {
        LOG.info("Displaying WebView");
        assert helpWebView != null : "fx:id=\"helpWebView\" was not injected: check your FXML file 'HelpWebView.fxml'.";
        String htmlFile = "";
        if(resources.getString("help").equals("Help")){
            htmlFile = "help.html";
        }
        else if(resources.getString("help").equals("Aide")){
                        
            htmlFile = "help_fr.html";
        }

        final java.net.URI uri = java.nio.file.Paths.get(htmlFile).toAbsolutePath().toUri();
        //Load the given HTML file
        helpWebView.getEngine().load(uri.toString());
    }
}

