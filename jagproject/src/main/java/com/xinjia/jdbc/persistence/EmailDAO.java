
package com.xinjia.jdbc.persistence;

import com.xinjia.exceptions.FolderAlreadyExistsException;
import com.xinjia.jdbc.beans.EmailData;
import com.xinjia.properties.propertybean.FolderData;
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.collections.ObservableList;

/**
 * Interface for Email and Folder CRUD operations 
 * @author Xin Jia Cao
 */
public interface EmailDAO {

    public void createEmail(EmailData mailData) throws SQLException;
    public int createFolder(String folderName) throws SQLException, FolderAlreadyExistsException;
    
    public ArrayList<EmailData> findAllEmails() throws SQLException;
    public EmailData findEmailById(int id) throws SQLException;
    public ArrayList<EmailData> findEmailsByFolder(String folderName) throws SQLException;
    public ArrayList<EmailData> findEmailsBySubject(String subject) throws SQLException;
    public ArrayList<EmailData> findEmailsByRecipients(String recipient) throws SQLException;
    public ObservableList<FolderData> findFolders() throws SQLException;
    public ArrayList<String> findFolderNames() throws SQLException;
    public ArrayList<String> findAllAddresses() throws SQLException;
    
    public int updateEmailDraft(EmailData mailData) throws SQLException;
    public int updateEmailDraftAndSend(EmailData mailData) throws SQLException;
    public int changeEmailFolder(int emailId, int folderId) throws SQLException;
    public int updateFolderName(String toReplace, String newName) throws SQLException, FolderAlreadyExistsException;
    
    public int deleteEmail(int id) throws SQLException;
    public int deleteFolder(String folderName) throws SQLException;
    
}
