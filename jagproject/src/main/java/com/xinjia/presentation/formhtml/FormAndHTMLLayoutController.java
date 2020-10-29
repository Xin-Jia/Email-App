/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xinjia.presentation.formhtml;
/**
 * Sample Skeleton for 'FormHTMLLayout.fxml' Controller Class
 */


import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.web.HTMLEditor;

public class FormAndHTMLLayoutController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="toHbox"
    private HBox toHbox; // Value injected by FXMLLoader

    @FXML // fx:id="toTextField"
    private TextField toTextField; // Value injected by FXMLLoader

    @FXML // fx:id="ccHbox"
    private HBox ccHbox; // Value injected by FXMLLoader

    @FXML // fx:id="ccTextField"
    private TextField ccTextField; // Value injected by FXMLLoader

    @FXML // fx:id="bccHbox"
    private HBox bccHbox; // Value injected by FXMLLoader

    @FXML // fx:id="bccTextField"
    private TextField bccTextField; // Value injected by FXMLLoader

    @FXML // fx:id="subjectField"
    private TextField subjectField; // Value injected by FXMLLoader

    @FXML // fx:id="htmlEditor"
    private HTMLEditor htmlEditor; // Value injected by FXMLLoader

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert toHbox != null : "fx:id=\"toHbox\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
        assert toTextField != null : "fx:id=\"toTextField\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
        assert ccHbox != null : "fx:id=\"ccHbox\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
        assert ccTextField != null : "fx:id=\"ccTextField\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
        assert bccHbox != null : "fx:id=\"bccHbox\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
        assert bccTextField != null : "fx:id=\"bccTextField\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
        assert subjectField != null : "fx:id=\"subjectField\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";
        assert htmlEditor != null : "fx:id=\"htmlEditor\" was not injected: check your FXML file 'FormHTMLLayout.fxml'.";

    }
}

