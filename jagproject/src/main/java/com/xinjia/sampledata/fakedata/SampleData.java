/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xinjia.sampledata.fakedata;

import com.xinjia.properties.propertybean.EmailData;
import com.xinjia.properties.propertybean.FolderData;
import com.xinjia.properties.propertybean.FormData;
import com.xinjia.properties.MailConfigBean;
import com.xinjia.properties.propertybean.HtmlData;
import java.time.LocalDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Class that generates fake/sample data to populate entries in the Email App.
 * 
 * @author Xin Jia Cao
 */
public class SampleData {
    
    private MailConfigBean mailBean;
    
    /**
     * Non-default constructor that initializes a MailConfigPropertyBean
     * @param mailBean The MailConfigPropertyBean
     */
    public SampleData(MailConfigBean mailBean){
        this.mailBean = mailBean;
    }
    /**
     * Default constructor 
     */
    public SampleData(){
        this(new MailConfigBean());
    }
    
    /**
     * Creates an ObservableList of FolderData and populate it with sample values
     * @return The ObservableList<FolderData>
     */
    public ObservableList<FolderData> getSampleFolderData(){
        ObservableList<FolderData> folders = FXCollections.observableArrayList();
        folders.addAll(new FolderData(1, "Inbox"), new FolderData(2, "Sent"), new FolderData(3, "Draft"));
        return folders;
    }

    /**
     * Creates an ObservableList of EmailData and populate it with sample values for the Inbox folder
     * @return The ObservableList<EmailData>
     */
    public ObservableList<EmailData> getSampleInboxEmailData(){
        LocalDateTime date = LocalDateTime.now();
        ObservableList<EmailData> emails = FXCollections.observableArrayList();
        emails.addAll(new EmailData(1, "alice12@hotmail.com", "Meeting", date), new EmailData(2, "bob3y7@gmail.com", "Project", date), new EmailData(3, "alice12@hotmail.com", "Meeting2", date));
        return emails;
    }
    
    /**
     * Creates an ObservableList of EmailData and populate it with sample values for the Sent folder
     * @return The ObservableList<EmailData>
     */
    public ObservableList<EmailData> getSampleSentEmailData(){
        LocalDateTime date = LocalDateTime.now();
        ObservableList<EmailData> emails = FXCollections.observableArrayList();
        emails.addAll(new EmailData(4, mailBean.getEmailAddress(), "Breakfast", date), new EmailData(5, mailBean.getEmailAddress(), "Game Project", date), new EmailData(6, mailBean.getEmailAddress(), "Subject 3", date));
        return emails;
    }
    
    /**
     * Creates an ObservableList of EmailData and populate it with sample values for the Draft folder
     * @return The ObservableList<EmailData>
     */
    public ObservableList<EmailData> getSampleDraftEmailData(){
        LocalDateTime date = LocalDateTime.now();
        ObservableList<EmailData> emails = FXCollections.observableArrayList();
        emails.addAll(new EmailData(7, mailBean.getEmailAddress(), "Draft 1", date), new EmailData(8, mailBean.getEmailAddress(), "Draft 2", date), new EmailData(9, mailBean.getEmailAddress(), "Draft 3", date));
        return emails;
    }
    
    //SAMPLE FORMDATA FOR INBOX EMAILS
    public FormData getSampleFromData1(){
        FormData form = new FormData(mailBean.getEmailAddress(), "", "carole@yahoo.com", "Meeting Tomorrow");
        return form;
    }
    public FormData getSampleFromData2(){
        FormData form = new FormData(mailBean.getEmailAddress(), "hola@gmail.com", "", "Fixed a bug");
        return form;
    }
    public FormData getSampleFromData3(){
        FormData form = new FormData("koala@gmail.com", mailBean.getEmailAddress(), "ryd7@gmail.com", "Project due");
        return form;
    }
    
    //SAMPLE FORMDATA FOR SENT EMAILS
    public FormData getSampleFromData4(){
        FormData form = new FormData("sdsaasd@gmail.com", "", "", "Happy Birthday");
        return form;
    }
    public FormData getSampleFromData5(){
        FormData form = new FormData("sdsaasd@gmail.com", "", "", "Happy Birthday");
        return form;
    }
    public FormData getSampleFromData6(){
        FormData form = new FormData("", "maisha@hotmail.com", "", "Travel");
        return form;
    }
    
    //SAMPLE FORMDATA FOR DRAFT EMAILS
    public FormData getSampleFromData7(){
        FormData form = new FormData("hola@gmail.com", "jumbo45@gmail.com", "", "Getting back");
        return form;
    }
    public FormData getSampleFromData8(){
        FormData form = new FormData("teacher3@gmail.com", "", "", "Essay");
        return form;
    }
    public FormData getSampleFromData9(){
        FormData form = new FormData("pierce@gmail.com", "maisha@hotmail.com", "", "Essay2");
        return form;
    }
    
    //SAMPLE HTMLDATA FOR ALL EMAILS
    public HtmlData getSampleHtmlData(){
        HtmlData htmlData = new HtmlData("<h4>A sample message for every emails<h4>");
        return htmlData;
    }
    
    

}
