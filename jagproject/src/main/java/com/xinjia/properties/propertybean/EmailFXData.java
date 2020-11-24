
package com.xinjia.properties.propertybean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * JavaFX custom Email bean that contains an id, a from field, a subject and a date.
 * Overrides toString, hashCode and equals.
 * 
 * @author Xin Jia Cao
 */
public class EmailFXData {
    private int id;
    private int folderId;
    private StringProperty date;
    private StringProperty from;
    private StringProperty subject;
    private ListProperty to;
    private ListProperty cc;
    private ListProperty bcc;
    private StringProperty textMsg;
    private StringProperty htmlMsg;
    private List<String> embedAttachments;
    private List<String> regAttachments;
    private List<byte[]> regAttachmentsBytes;
    private List<byte[]> embedAttachmentsBytes;
    
    
    /**
     * Non-default constructor that initializes all the fields
     * @param id
     * @param folderId
     * @param date
     * @param from
     * @param subject
     * @param to
     * @param cc
     * @param bcc
     * @param textMsg
     * @param htmlMsg 
     * @param regAttachments 
     * @param regAttachmentsBytes 
     * @param embedAttachments 
     * @param embedAttachmentsBytes 
     */
    public EmailFXData(final int id, final int folderId, final LocalDateTime date, 
            final String from, final String subject, final ObservableList<String> to, 
            final ObservableList<String> cc, final ObservableList<String> bcc, 
            final String textMsg, final String htmlMsg, final List<String> regAttachments, 
            final List<byte[]> regAttachmentsBytes, final List<String> embedAttachments, final List<byte[]> embedAttachmentsBytes) {
        this.id = id;
        this.folderId = folderId;
        this.from = new SimpleStringProperty(from);
        this.subject = new SimpleStringProperty(subject);
        this.date = new SimpleStringProperty(date.toString());
        this.to =  new SimpleListProperty(to);
        this.cc =  new SimpleListProperty(cc);
        this.bcc =  new SimpleListProperty(bcc);
        this.textMsg =  new SimpleStringProperty(textMsg);
        this.htmlMsg =  new SimpleStringProperty(htmlMsg);
        this.regAttachments = regAttachments;
        this.regAttachmentsBytes = regAttachmentsBytes;
        this.embedAttachments = embedAttachments;
        this.embedAttachmentsBytes = embedAttachmentsBytes;
    }
    
    /**
     * Default constructor 
     */
    public EmailFXData(){
        this(-1, -1, null, "", "", FXCollections.observableArrayList(), FXCollections.observableArrayList(), FXCollections.observableArrayList(), "", "", new ArrayList<>(), new ArrayList<>(),new ArrayList<>(), new ArrayList<>());
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }
    
    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(final int id) {
        this.folderId = id;
    }

    public String getFrom() {
        return from.get();
    }

    public void setFrom(final String from) {
        this.from.set(from);
    }
    
    public StringProperty fromProperty() {
        return from;
    }

    public String getSubject() {
        return subject.get();
    }

    public void setSubject(final String subject) {
        this.subject.set(subject);
    }
    
    public StringProperty subjectProperty() {
        return subject;
    }

    public String getDate() {
        return date.get();
    }

    public void setDate(final String date) {
        this.date.set(date);
    }
    
    public StringProperty dateProperty() {
        return date;
    }
    
    public ObservableList<String> getTo() {
        return (ObservableList<String>) to.get();
    }

    public void setTo(final ObservableList<String> to) {
        this.to.set(to);
    }
    
    public ListProperty toProperty() {
        return to;
    }
    
    public ObservableList<String> getCC() {
        return (ObservableList<String>) cc.get();
    }

    public void setCC(final ObservableList<String> cc) {
        this.cc.set(cc);
    }
    
    public ListProperty ccProperty() {
        return cc;
    }
    
    public ObservableList<String> getBCC() {
        return (ObservableList<String>) bcc.get();
    }

    public void setBCC(final ObservableList<String> bcc) {
        this.bcc.set(bcc);
    }
    
    public ListProperty bccProperty() {
        return bcc;
    }
    
    public String getTextMsg() {
        return textMsg.get();
    }

    public void setTextMsg(final String msg) {
        this.textMsg.set(msg);
    }
    
    public StringProperty textMsgProperty() {
        return textMsg;
    }
    
    public String getHtmlMsg() {
        return htmlMsg.get();
    }

    public void setHtmlMsg(final String msg) {
        this.htmlMsg.set(msg);
    }
    
    public StringProperty htmlMsgProperty() {
        return htmlMsg;
    }
    
    public List<String> getEmbedAttachments(){
        return embedAttachments;
    }
    
    public void setEmbedAttachments(List<String> embedAttachments){
        this.embedAttachments = embedAttachments;
    }
    
    public List<byte[]> getEmbedAttachmentsBytes(){
        return embedAttachmentsBytes;
    }
    
    public void setEmbedAttachmentsBytes(List<byte[]> embedAttachmentsBytes){
        this.embedAttachmentsBytes = embedAttachmentsBytes;
    }
    
    public List<String> getRegAttachments(){
        return regAttachments;
    }
    
    public void setRegAttachments(List<String> regAttachments){
        this.regAttachments = regAttachments;
    }
    
    public List<byte[]> getRegAttachmentsBytes(){
        return regAttachmentsBytes;
    }
    
    public void setRegAttachmentsBytes(List<byte[]> regAttachmentsBytes){
        this.regAttachmentsBytes = regAttachmentsBytes;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + this.id;
        hash = 47 * hash + this.folderId;
        hash = 47 * hash + Objects.hashCode(this.date);
        hash = 47 * hash + Objects.hashCode(this.from);
        hash = 47 * hash + Objects.hashCode(this.subject);
        hash = 47 * hash + Objects.hashCode(this.to);
        hash = 47 * hash + Objects.hashCode(this.cc);
        hash = 47 * hash + Objects.hashCode(this.bcc);
        hash = 47 * hash + Objects.hashCode(this.textMsg);
        hash = 47 * hash + Objects.hashCode(this.htmlMsg);
        hash = 47 * hash + Objects.hashCode(this.embedAttachments);
        hash = 47 * hash + Objects.hashCode(this.regAttachments);
        hash = 47 * hash + Objects.hashCode(this.regAttachmentsBytes);
        hash = 47 * hash + Objects.hashCode(this.embedAttachmentsBytes);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EmailFXData other = (EmailFXData) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.folderId != other.folderId) {
            return false;
        }
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        if (!Objects.equals(this.from, other.from)) {
            return false;
        }
        if (!Objects.equals(this.subject, other.subject)) {
            return false;
        }
        if (!Objects.equals(this.to, other.to)) {
            return false;
        }
        if (!Objects.equals(this.cc, other.cc)) {
            return false;
        }
        if (!Objects.equals(this.bcc, other.bcc)) {
            return false;
        }
        if (!Objects.equals(this.textMsg, other.textMsg)) {
            return false;
        }
        if (!Objects.equals(this.htmlMsg, other.htmlMsg)) {
            return false;
        }
        if (!Objects.equals(this.embedAttachments, other.embedAttachments)) {
            return false;
        }
        if (!Objects.equals(this.regAttachments, other.regAttachments)) {
            return false;
        }
        if (!Objects.equals(this.regAttachmentsBytes, other.regAttachmentsBytes)) {
            return false;
        }
        if (!Objects.equals(this.embedAttachmentsBytes, other.embedAttachmentsBytes)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EmailFXData{" + "id=" + id + ", folderId=" + folderId + ", date=" + date + ", from=" + from + ", subject=" + subject + ", to=" + to + ", cc=" + cc + ", bcc=" + bcc + ", textMsg=" + textMsg + ", htmlMsg=" + htmlMsg + ", embedAttachments=" + embedAttachments + ", regAttachments=" + regAttachments + ", regAttachmentsBytes=" + regAttachmentsBytes + ", embedAttachmentsBytes=" + embedAttachmentsBytes + '}';
    }

}
