
package com.xinjia.test.jdbc;

import com.xinjia.exceptions.FolderAlreadyExistsException;
import com.xinjia.exceptions.InvalidFolderNameException;
import com.xinjia.exceptions.NotDraftFolderException;
import com.xinjia.jdbc.beans.EmailData;
import com.xinjia.jdbc.persistence.EmailDAO;
import com.xinjia.jdbc.persistence.EmailDAOImpl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import jodd.mail.Email;
import jodd.mail.EmailAttachment;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Xin Jia Cao
 */
public class EmailJDBCTest {

    private final static Logger LOG = LoggerFactory.getLogger(EmailJDBCTest.class);

    private final static String URL = "jdbc:mysql://localhost:3306/EMAILAPP?characterEncoding=UTF-8&autoReconnect=true&zeroDateTimeBehavior=CONVERT_TO_NULL&useSSL=false&allowPublicKeyRetrieval=true&useTimezone=true&serverTimezone=UTC";
    private final static String USER = "userxj";
    private final static String PASSWORD = "dawson1";

    /*@Test
    public void testFindAll() throws SQLException{
        LOG.info("----------TEST FIND ALL----------");
        EmailDAO mailFunction = new EmailDAOImpl();
        List<EmailData> data = mailFunction.findAllEmails();
        assertEquals(getNumberRows(), data.size());
    }
    
    @Test
    public void testFindByID2() throws SQLException{
        LOG.info("----------TEST FIND BY ID: 2----------");
        Email email = new Email();
        email.from("xinjia.caoxin@gmail.com");
        email.subject("subject1_1");
        email.textMessage("Inbox Msg");
        email.htmlMessage("");
        EmailData mailData1 = new EmailData(2, 2, null, email);
        EmailDAO mailFunction = new EmailDAOImpl();
        EmailData mailData2 = mailFunction.findEmailById(1);
        mailData1.equals(mailData2);
    }*/
    /*@Test
    public void testCreateEmail() throws SQLException {
        LOG.info("----------TEST CREATE EMAIL----------");
        
        Email email = new Email();
        email.from("xinjia123@gmail.com");
        email.to("xinjia345@gmail.com");
        email.cc("xinjia1.cao@gmail.com");
        email.subject("sub");
        email.textMessage("test create email");
        email.htmlMessage("test");
        email.embeddedAttachment(EmailAttachment.with().content("img1.png"));
        LocalDateTime date = LocalDateTime.now();
        EmailData email1 = new EmailData(getLastId() + 1, 3, date, email);
        EmailDAO mailFunction = new EmailDAOImpl();
        mailFunction.createEmail(email1);
        EmailData email2 = mailFunction.findEmailById(email1.getEmailId());
        email1.equals(email2);
    }*/

    /*@Test
    public void testFindSentEmails() throws SQLException, InvalidFolderNameException{
        LOG.info("----------TEST FIND SENT EMAILS----------");
        EmailDAO mailFunction = new EmailDAOImpl();
        List<EmailData> data = mailFunction.findEmailsByFolder("Sent");
        assertEquals(4, data.size());
    }
    
    @Test
    public void testFindReceivedEmails() throws SQLException, InvalidFolderException, InvalidFolderNameException{
        LOG.info("----------TEST FIND RECEIVED EMAILS (INBOX)----------");
        EmailDAO mailFunction = new EmailDAOImpl();
        List<EmailData> data = mailFunction.findEmailsByFolder("Inbox");
        assertEquals(2, data.size());
    }*/

    /*@Test
    public void testFindEmailsByTextSubString() throws SQLException{
        LOG.info("----------TEST FIND EMAILS BY TEXT SUBSTRING----------");
        EmailDAO mailFunction = new EmailDAOImpl();
        List<EmailData> data = mailFunction.findEmailsByTextSubString("Msg");
        assertEquals(6, data.size());
    }
    @Test
    public void testDeleteEmail() throws SQLException{
        LOG.info("----------TEST DELETE EMAIL----------");
        EmailDAO mailFunction = new EmailDAOImpl();
        int numDeleted = mailFunction.deleteEmail(5);
        assertEquals(1, numDeleted);
    }
    
    @Test
    public void testUpdateEmail() throws SQLException{
        LOG.info("----------TEST UPDATE EMAIL----------");
        Email email = new Email();
        email.from("xinjia3@gmail.com");
        email.to("updatedRecipient1@gmail.com");
        email.bcc("updatedRecipient2@gmail.com");
        email.subject("subject_updated");
        email.textMessage("updated Msg");
        email.htmlMessage("updated HTML Msg");
        email.attachment(EmailAttachment.with().content("img2.png"));
        EmailData mailData = new EmailData();
        mailData.setEmailId(6);
        mailData.setFolderId(3);
        mailData.setEmail(email);
        
        EmailDAO mailFunction = new EmailDAOImpl();
        int rows = mailFunction.updateEmailDraft(mailData);
        assertEquals(3, rows);
    }
    
    @Test
    public void testFindFolderNames() throws SQLException{
        LOG.info("----------TEST FIND FOLDER NAMES----------");
        String[] folderNames = {"Inbox", "Sent", "Draft"};
        EmailDAO mailFunction = new EmailDAOImpl();
        ArrayList<String> dbFolderNames = mailFunction.findFolderNames();
        assertEquals(Arrays.asList(folderNames), dbFolderNames);
    }
    
    @Test
    public void testFindAllAddresses() throws SQLException{
        LOG.info("----------TEST FIND ALL ADDRESSES----------");
        String[] addresses = {"xinjia.caoxin@gmail.com", "xinjia1.cao@gmail.com", 
            "xinjia2.cao@gmail.com", "xinjia3.cao@gmail.com", "xinjia4.cao@gmail.com"};
        EmailDAO mailFunction = new EmailDAOImpl();
        ArrayList<String> dbAddresses = mailFunction.findAllAddresses();
        assertEquals(Arrays.asList(addresses), dbAddresses);
    }*/
    
    /*@Test
    public void testCreateFolder() throws SQLException, FolderAlreadyExistsException{
        LOG.info("----------TEST CREATE FOLDER----------");
        EmailDAO mailFunction = new EmailDAOImpl();
        int rows = mailFunction.createFolder("Special");
        assertEquals(1, rows);
    }
    
    @Test 
    public void testEmailFolderChanged() throws SQLException{
        LOG.info("----------TEST EMAIL FOLDER CHANGED----------");
        
        Email email = new Email();
        email.from("xinjia123@gmail.com");
        email.to("xinjia345@gmail.com");
        email.cc("xinjia1.cao@gmail.com");
        email.subject("sub");
        email.textMessage("test create email");
        email.htmlMessage("test");
        email.embeddedAttachment(EmailAttachment.with().content("img1.png"));
        LocalDateTime date = LocalDateTime.now();
        EmailData mailData = new EmailData(getLastId() + 1, 3, date, email);
        EmailDAO mailFunction = new EmailDAOImpl();
        
        mailFunction.createEmail(mailData);
        int rows = mailFunction.changeEmailFolder(mailData, "Inbox");
        assertEquals(1, rows);
    }
    
    @Test
    public void testUpdateFolderName() throws SQLException{
    LOG.info("----------TEST UPDATE FOLDER NAME----------");
        EmailDAO mailFunction = new EmailDAOImpl();
        int rows = mailFunction.updateFolderName("Inbox", "Recipients");
        assertEquals(1, rows);
    }*/
    @Test
    public void testDeleteFolder() throws SQLException{
        LOG.info("----------TEST DELETES FOLDER----------");
        EmailDAO mailFunction = new EmailDAOImpl();
        int rows = mailFunction.deleteFolder("Draft");
        assertEquals(1, rows);
    }
    
    private int getLastId() throws SQLException {
        String selectLastIdQuery = "SELECT EMAILID FROM EMAIL";
        int latestId = -1;
        try ( Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);  PreparedStatement pStatement = connection.prepareStatement(selectLastIdQuery);) {
            try ( ResultSet resultSet = pStatement.executeQuery()) {
                while (resultSet.next()) {
                    latestId = resultSet.getInt("EmailId");
                }
            }
        }
        LOG.debug("latest id: " + (latestId+1));
        return latestId + 1;
    }
    
    private int getNumberRows() throws SQLException {
        String selectLastIdQuery = "SELECT EMAILID FROM EMAIL";
        int rows = 0;
        try ( Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);  PreparedStatement pStatement = connection.prepareStatement(selectLastIdQuery);) {
            try ( ResultSet resultSet = pStatement.executeQuery()) {
                while (resultSet.next()) {
                    rows++;
                }
            }
        }
        LOG.debug("Number of rows: " + rows);
        return rows;
    }

    /**
     * The database is recreated before each test. If the last test is
     * destructive then the database is in an unstable state. @AfterClass is
     * called just once when the test class is finished with by the JUnit
     * framework. It is instantiating the test class anonymously so that it can
     * execute its non-static seedDatabase routine.
     */
    /*@AfterClass
    public static void seedAfterTestCompleted() {
        LOG.info("@AfterClass seeding");
        new EmailJDBCTest().seedDatabase();
    }*/

    /**
     * This routine recreates the database before every test. This makes sure
     * that a destructive test will not interfere with any other test. Does not
     * support stored procedures.
     *
     * This routine is courtesy of Bartosz Majsak, an Arquillian developer at
     * JBoss
     */
    /*@Before
    public void seedDatabase() {
        LOG.info("@Before seeding");

        final String seedDataScript = loadAsString("createEmailTables.sql");
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);) {
            for (String statement : splitStatements(new StringReader(seedDataScript), ";")) {
                connection.prepareStatement(statement).execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed seeding database", e);
        }
    }

    private String loadAsString(final String path) {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
                Scanner scanner = new Scanner(inputStream)) {
            return scanner.useDelimiter("\\A").next();
        } catch (IOException e) {
            throw new RuntimeException("Unable to close input stream.", e);
        }
    }

    private List<String> splitStatements(Reader reader, String statementDelimiter) {
        final BufferedReader bufferedReader = new BufferedReader(reader);
        final StringBuilder sqlStatement = new StringBuilder();
        final List<String> statements = new LinkedList<>();
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || isComment(line)) {
                    continue;
                }
                sqlStatement.append(line);
                if (line.endsWith(statementDelimiter)) {
                    statements.add(sqlStatement.toString());
                    sqlStatement.setLength(0);
                }
            }
            return statements;
        } catch (IOException e) {
            throw new RuntimeException("Failed parsing sql", e);
        }
    }

    private boolean isComment(final String line) {
        return line.startsWith("--") || line.startsWith("//") || line.startsWith("/*");
    }*/
}
