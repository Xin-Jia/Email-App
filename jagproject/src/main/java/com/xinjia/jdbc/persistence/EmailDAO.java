
package com.xinjia.jdbc.persistence;

import com.xinjia.exceptions.FolderAlreadyExistsException;
import com.xinjia.exceptions.InvalidFolderNameException;
import com.xinjia.exceptions.NotDraftFolderException;
import com.xinjia.jdbc.beans.EmailData;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Interface for CRUD operations 
 * @author Xin Jia Cao
 */
public interface EmailDAO {

    public void createEmail(EmailData mailData) throws SQLException;
    public int createFolder(String folderName) throws SQLException, FolderAlreadyExistsException;
    
    public ArrayList<EmailData> findAllEmails() throws SQLException;
    public EmailData findEmailById(int id) throws SQLException;
    public ArrayList<EmailData> findEmailsByFolder(String folderName) throws SQLException, InvalidFolderNameException;
    public ArrayList<EmailData> findEmailsBySubject(String subject) throws SQLException;
    public ArrayList<String> findFolderNames() throws SQLException;
    public ArrayList<String> findAllAddresses() throws SQLException;
    
    public int updateEmailDraft(EmailData mailData) throws SQLException, NotDraftFolderException;
    public int updateEmailDraftAndSend() throws SQLException, NotDraftFolderException;
    public int changeEmailFolder(EmailData mailData, String folderName) throws SQLException;
    public int updateFolderName(String folderName) throws SQLException;
    
    public int deleteEmail(int id) throws SQLException;
    public int deleteFolder(String folderName) throws SQLException;
    
}
