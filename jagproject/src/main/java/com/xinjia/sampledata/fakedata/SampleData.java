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
 *
 * @author Xin Jia Cao
 */
public class SampleData {
    
    private MailConfigPropertyBean mailBean;
    public SampleData(MailConfigPropertyBean mailBean){
        this.mailBean = mailBean;
    }
    public SampleData(){
        this(new MailConfigPropertyBean());
    }
            
    
    public ObservableList<FolderData> getSampleFolderData(){
        ObservableList<FolderData> folders = FXCollections.observableArrayList();
        folders.addAll(new FolderData(1, "Inbox"), new FolderData(2, "Sent"), new FolderData(3, "Draft"));
        return folders;
    }

    LocalDateTime date = LocalDateTime.now();
    
    public ObservableList<EmailData> getSampleInboxEmailData(){
        ObservableList<EmailData> emails = FXCollections.observableArrayList();
        emails.addAll(new EmailData(1, "alice12@hotmail.com", "Meeting", date), new EmailData(2, "bob3y7@gmail.com", "Project", date), new EmailData(3, "alice12@hotmail.com", "Meeting2", date));
        return emails;
    }
    
    public ObservableList<EmailData> getSampleSentEmailData(){
        ObservableList<EmailData> emails = FXCollections.observableArrayList();
        emails.addAll(new EmailData(1, mailBean.getEmailAddress(), "Breakfast", date), new EmailData(2, mailBean.getEmailAddress(), "Game Project", date), new EmailData(3, mailBean.getEmailAddress(), "Subject 3", date));
        return emails;
    }
    
    public ObservableList<EmailData> getSampleDraftEmailData(){
        ObservableList<EmailData> emails = FXCollections.observableArrayList();
        emails.addAll(new EmailData(1, mailBean.getEmailAddress(), "Draft 1", date), new EmailData(2, mailBean.getEmailAddress(), "Draft 2", date), new EmailData(3, mailBean.getEmailAddress(), "Draft 3", date));
        return emails;
    }
    
    public ObservableList<EmailData> getSampleOtherEmailData(){
        ObservableList<EmailData> emails = FXCollections.observableArrayList();
        emails.addAll(new EmailData(1, mailBean.getEmailAddress(), "Other 1", date), new EmailData(2, mailBean.getEmailAddress(), "Other 2", date), new EmailData(3, mailBean.getEmailAddress(), "Other 3", date));
        return emails;
    }
    
    public ObservableList<FormData> getSampleFormData(){
        ObservableList<FormData> forms = FXCollections.observableArrayList();
        forms.addAll(new FormData("matt48@gmail.com", "alice12@hotmail.com", "", "Presentation"), new FormData("", "bob3y7@gmail.com", "elfi79@hotmail.com", "Project3"), new FormData("elfi79@hotmail.com", "", "", "Trip"));
        return forms;
    }
}
