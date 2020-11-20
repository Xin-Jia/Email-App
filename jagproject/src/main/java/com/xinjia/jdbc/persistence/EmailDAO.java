
package com.xinjia.jdbc.persistence;

import com.xinjia.exceptions.FolderAlreadyExistsException;
import com.xinjia.jdbc.beans.EmailData;
import com.xinjia.properties.propertybean.EmailFXData;
import com.xinjia.properties.propertybean.FolderData;
import java.sql.SQLException;
import javafx.collections.ObservableList;

/**
 * Interface for CRUD operations 
 * @author Xin Jia Cao
 */
public interface EmailDAO {

    public void createEmail(EmailData mailData) throws SQLException;
    public int createFolder(String folderName) throws SQLException, FolderAlreadyExistsException;
    
    public ObservableList<EmailFXData> findAllEmails() throws SQLException;
    public EmailFXData findEmailById(int id) throws SQLException;
    public ObservableList<EmailFXData> findEmailsByFolder(String folderName) throws SQLException;
    public ObservableList<EmailFXData> findEmailsBySubject(String subject) throws SQLException;
    public ObservableList<EmailFXData> findEmailsByRecipients(String recipient) throws SQLException;
    public ObservableList<FolderData> findFolders() throws SQLException;
    public ObservableList<String> findFolderNames() throws SQLException;
    public ObservableList<String> findAllAddresses() throws SQLException;
    
    public int updateEmailDraft(EmailData mailData) throws SQLException;
    public int updateEmailDraftAndSend(EmailData mailData) throws SQLException;
    public int changeEmailFolder(EmailFXData mailData, int folderId) throws SQLException;
    public int updateFolderName(String toReplace, String newName) throws SQLException, FolderAlreadyExistsException;
    
    public int deleteEmail(int id) throws SQLException;
    public int deleteFolder(String folderName) throws SQLException;
    
}
