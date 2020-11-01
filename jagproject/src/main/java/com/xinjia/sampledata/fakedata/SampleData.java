/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xinjia.sampledata.fakedata;

import com.xinjia.properties.propertybean.EmailData;
import com.xinjia.properties.propertybean.FolderData;
import com.xinjia.properties.propertybean.FormData;
import com.xinjia.properties.propertybean.MailConfigPropertyBean;
import java.time.LocalDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Class that generates fake/sample data to populate entries in the Email App.
 * 
 * @author Xin Jia Cao
 */
public class SampleData {
    
    private MailConfigPropertyBean mailBean;
    
    /**
     * Non-default constructor that initializes a MailConfigPropertyBean
     * @param mailBean The MailConfigPropertyBean
     */
    public SampleData(MailConfigPropertyBean mailBean){
        this.mailBean = mailBean;
    }
    /**
     * Default constructor 
     */
    public SampleData(){
        this(new MailConfigPropertyBean());
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
        emails.addAll(new EmailData(1, mailBean.getEmailAddress(), "Breakfast", date), new EmailData(2, mailBean.getEmailAddress(), "Game Project", date), new EmailData(3, mailBean.getEmailAddress(), "Subject 3", date));
        return emails;
    }
    
    /**
     * Creates an ObservableList of EmailData and populate it with sample values for the Draft folder
     * @return The ObservableList<EmailData>
     */
    public ObservableList<EmailData> getSampleDraftEmailData(){
        LocalDateTime date = LocalDateTime.now();
        ObservableList<EmailData> emails = FXCollections.observableArrayList();
        emails.addAll(new EmailData(1, mailBean.getEmailAddress(), "Draft 1", date), new EmailData(2, mailBean.getEmailAddress(), "Draft 2", date), new EmailData(3, mailBean.getEmailAddress(), "Draft 3", date));
        return emails;
    }

}
