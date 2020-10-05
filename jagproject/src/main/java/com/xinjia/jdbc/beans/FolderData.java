
package com.xinjia.jdbc.beans;
import java.util.ArrayList;

/**
 *
 * @author Xin Jia Cao
 */
public class FolderData {
    
    private int folderId;
    private String folderName;
    private ArrayList<EmailData> emails;
    
    public FolderData(int folderId, String folderName, ArrayList<EmailData> emails){
        this.folderId = folderId;
        this.folderName = folderName;
        this.emails = emails;
    }
    
    public FolderData(){
        this.folderId = -1;
        this.folderName = "";
        this.emails = new ArrayList<>();
    }
    
    
    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public ArrayList<EmailData> getEmails() {
        return emails;
    }

    public void setEmails(ArrayList<EmailData> emails) {
        this.emails = emails;
    }

    @Override
    public String toString() {
        return "FolderData{" + "folderId=" + folderId + ", folderName=" + folderName + ", emails=" + emails + '}';
    }
    
}
