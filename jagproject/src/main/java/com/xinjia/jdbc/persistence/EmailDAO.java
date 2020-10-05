
package com.xinjia.jdbc.persistence;

import com.xinjia.jdbc.beans.EmailData;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Interface for CRUD operations 
 * @author Xin Jia Cao
 */
public interface EmailDAO {

    public void createEmail(EmailData mailData) throws SQLException;
    public ArrayList<EmailData> findAllEmails() throws SQLException;
    public EmailData findEmailById(int id) throws SQLException;
    public ArrayList<EmailData> findEmailsByFolder(String address, String folder) throws SQLException;
    public ArrayList<EmailData> findEmailsBySubject(String address, String subject) throws SQLException;
    public int updateEmail(EmailData mailData) throws SQLException;
    public int deleteEmail(int id) throws SQLException;
    
}
