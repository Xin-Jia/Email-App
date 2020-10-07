
package com.xinjia.test.jdbc;

import com.xinjia.exceptions.FolderAlreadyExistsException;
import com.xinjia.jdbc.beans.EmailData;
import com.xinjia.jdbc.persistence.EmailDAOImpl;
import com.xinjia.properties.MailConfigBean;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
 * Unit tests for Emails operations
 * @author Xin Jia Cao
 */
public class EmailJDBCTest {

    private final static Logger LOG = LoggerFactory.getLogger(EmailJDBCTest.class);

    private static MailConfigBean configBean;
    private EmailDAOImpl mailFunction;
    
    //Initializes fields and tables before every test
    @Before
    public void initializeTest(){
        String dbUrl = "jdbc:mysql://localhost:3306/EMAILAPP?characterEncoding=UTF-8&autoReconnect=true&zeroDateTimeBehavior=CONVERT_TO_NULL&useSSL=false&allowPublicKeyRetrieval=true&useTimezone=true&serverTimezone=UTC";
        configBean = new MailConfigBean("", "", "", "", "", "", "", dbUrl, "EMAILAPP", "3306", "userxj", "dawson2");
        seedDatabase();
        mailFunction = new EmailDAOImpl(configBean);
    }
    
    //Test to retrieve all emails in the Email table
    @Test
    public void testFindAll() throws SQLException{
        LOG.info("----------TEST FIND ALL----------");
        List<EmailData> data = mailFunction.findAllEmails();
        assertEquals(10, data.size());
    }
    
    //Test to find an email by its ID
    @Test
    public void testFindByID1() throws SQLException{
        LOG.info("----------TEST FIND BY ID 1----------");
        Email email = new Email();
        email.from("xinjia.caoxin@gmail.com");
        email.subject("subject1");
        email.textMessage("Inbox Msg");
        email.htmlMessage("");
        email.attachment(EmailAttachment.with().content("img1.png"));
        email.attachment(EmailAttachment.with().content("img2.png"));
        email.to("xinjia.caoxin@gmail.com");
        email.cc("xinjia2.cao@gmail.com");
        EmailData mailData1 = new EmailData(1, 2, null, email);

        EmailData mailData2 = mailFunction.findEmailById(1);
        mailData1.equals(mailData2);
    }
    
    //Test to create an email
    @Test
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
        EmailData mailData1 = new EmailData(getLastId() + 1, 3, date, email);

        mailFunction.createEmail(mailData1);
        EmailData mailData2 = mailFunction.findEmailById(mailData1.getEmailId());
        mailData1.equals(mailData2);
    }

    //Test to find all emails in the Sent folder
    @Test
    public void testFindSentEmails() throws SQLException{
        LOG.info("----------TEST FIND SENT EMAILS----------");

        List<EmailData> data = mailFunction.findEmailsByFolder("Sent");
        assertEquals(5, data.size());
    }
    
    //Test to find all emails in the Inbox folder
    @Test
    public void testFindReceivedEmails() throws SQLException{
        LOG.info("----------TEST FIND RECEIVED EMAILS (INBOX)----------");

        List<EmailData> data = mailFunction.findEmailsByFolder("Inbox");
        assertEquals(3, data.size());
    }
    
    //Test to find all emails in the Draft folder
    @Test
    public void testFindDraftEmails() throws SQLException{
        LOG.info("----------TEST FIND DRAFT EMAILS----------");

        List<EmailData> data = mailFunction.findEmailsByFolder("Draft");
        assertEquals(2, data.size());
    }

    //Test to find all emails that contains a given sub-string for the subject field
    @Test
    public void testFindEmailsBySubject() throws SQLException{
        LOG.info("----------TEST FIND EMAILS BY SUBJECT----------");

        List<EmailData> data = mailFunction.findEmailsBySubject("sub");
        assertEquals(9, data.size());
    }
    
    //Test to find all emails that contains a given sub-string for the recipients field
    @Test
    public void testFindEmailsByRecipients() throws SQLException{
        LOG.info("----------TEST FIND EMAILS BY RECIPIENTS----------");

        List<EmailData> data = mailFunction.findEmailsByRecipients("123");
        assertEquals(2, data.size());
    }
    
    //Test to check the deletion of an email
    @Test
    public void testDeleteEmail() throws SQLException{
        LOG.info("----------TEST DELETE EMAIL----------");

        int numDeleted = mailFunction.deleteEmail(5);
        assertEquals(1, numDeleted);
    }
    
    //Test to update an email in the Draft folder
    @Test
    public void testUpdateDraftEmail() throws SQLException{
        LOG.info("----------TEST UPDATE DRAFT----------");
        Email email = new Email();
        //first row added
        email.to("updatedRecipient1@gmail.com");
        //second row added
        email.bcc("updatedRecipient2@gmail.com");
        //third row updated
        email.subject("subject_updated");
        email.textMessage("updated Msg");
        email.htmlMessage("updated HTML Msg");
        //fourth row added
        email.attachment(EmailAttachment.with().content("img2.png"));
        EmailData mailData = new EmailData();
        mailData.setEmailId(6);
        mailData.setEmail(email);
        //not counting deletion, total of rows updated/added: 4
        int rows = mailFunction.updateEmailDraft(mailData);
        assertEquals(4, rows);
    }
    
    //Test to update an Email in the draft folder and send it
    @Test
    public void testUpdateDraftEmailAndSend() throws SQLException{
        LOG.info("----------TEST UPDATE DRAFT AND SEND----------");
        EmailData mailData = new EmailData();
        mailData.setEmailId(1);
        //first row updated
        mailData.email.subject("changed subject");
        mailData.email.textMessage("changed message");
        mailData.email.htmlMessage("changed html message");
        //second row added
        mailData.email.bcc("alice123@gmail.com");
        //third row changed (folder)
        int rows = mailFunction.updateEmailDraftAndSend(mailData);
        //not counting deletion, total of rows updated/added: 3
        assertEquals(3, rows);
    }
    
    //Test to find all the folder names in the Folder table
    @Test
    public void testFindFolderNames() throws SQLException{
        LOG.info("----------TEST FIND FOLDER NAMES----------");
        String[] folderNames = {"Inbox", "Sent", "Draft"};

        ArrayList<String> dbFolderNames = mailFunction.findFolderNames();
        assertEquals(Arrays.asList(folderNames), dbFolderNames);
    }
    
    //Test to find all the email addresses in the Address table
    @Test
    public void testFindAllAddresses() throws SQLException{
        LOG.info("----------TEST FIND ALL ADDRESSES----------");
        String[] addresses = {"xinjia.caoxin@gmail.com", "xinjia1.cao@gmail.com", 
            "xinjia2.cao@gmail.com", "xinjia3.cao@gmail.com", "xinjia4.cao@gmail.com", 
            "alice123@gmail.com", "bob123@gmail.com"};

        ArrayList<String> dbAddresses = mailFunction.findAllAddresses();
        assertEquals(Arrays.asList(addresses), dbAddresses);
    }
    
    //Test to create a new folder with a given name
    @Test
    public void testCreateFolder() throws SQLException, FolderAlreadyExistsException{
        LOG.info("----------TEST CREATE FOLDER----------");

        int rows = mailFunction.createFolder("Special");
        assertEquals(1, rows);
    }
    
    //Test to expect an exception because the given folder name already exists when trying to create it
    @Test(expected=FolderAlreadyExistsException.class) 
    public void testInvalidCreateFolder() throws SQLException, FolderAlreadyExistsException{
        LOG.info("----------TEST INVALID CREATE FOLDER----------");

        int rows = mailFunction.createFolder("Inbox");
    }
    
    //Test to change an email's folder
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
        
        mailFunction.createEmail(mailData);
        int rows = mailFunction.changeEmailFolder(mailData, "Inbox");
        assertEquals(1, rows);
    }
    
    //Test to update a folder name
    @Test
    public void testUpdateFolderName() throws SQLException, FolderAlreadyExistsException{
        LOG.info("----------TEST UPDATE FOLDER NAME----------");
        mailFunction.createFolder("NewFolder");
        int rows = mailFunction.updateFolderName("NewFolder", "Special");
        assertEquals(1, rows);
    }
    
    //Test to update an invalid folder
    @Test(expected=FolderAlreadyExistsException.class) 
    public void testUpdateInvalidFolderName() throws SQLException, FolderAlreadyExistsException{
        LOG.info("----------TEST UPDATE INVALID FOLDER NAME----------");
        mailFunction.createFolder("NewFolder");
        //cannot change the folder NewFolder to a folder that cannot be changed (sent, inbox, draft) 
        //so it will throw an exception
        int rows = mailFunction.updateFolderName("NewFolder", "Inbox");
    }
    
    //Test to delete a folder
    @Test
    public void testDeleteFolder() throws SQLException{
        LOG.info("----------TEST DELETE FOLDER----------");
        int rows = mailFunction.deleteFolder("Draft");
        assertEquals(1, rows);
    }
    
    //Helper to get the last ID from the Email table
    private int getLastId() throws SQLException {
        String selectLastIdQuery = "SELECT EMAILID FROM EMAIL";
        int latestId = -1;
        try ( Connection connection = DriverManager.getConnection(configBean.getDbUrl(), configBean.getDbUsername(), configBean.getDbPassword());  PreparedStatement pStatement = connection.prepareStatement(selectLastIdQuery);) {
            try ( ResultSet resultSet = pStatement.executeQuery()) {
                while (resultSet.next()) {
                    latestId = resultSet.getInt("EmailId");
                }
            }
        }
        LOG.debug("latest id: " + (latestId+1));
        return latestId + 1;
    }


    /**
     * The database is recreated before each test. If the last test is
     * destructive then the database is in an unstable state. @AfterClass is
     * called just once when the test class is finished with by the JUnit
     * framework. It is instantiating the test class anonymously so that it can
     * execute its non-static seedDatabase routine.
     */
    @AfterClass
    public static void seedAfterTestCompleted() {
        LOG.info("@AfterClass seeding");
        String dbUrl = "jdbc:mysql://localhost:3306/EMAILAPP?characterEncoding=UTF-8&autoReconnect=true&zeroDateTimeBehavior=CONVERT_TO_NULL&useSSL=false&allowPublicKeyRetrieval=true&useTimezone=true&serverTimezone=UTC";
        configBean = new MailConfigBean("", "", "", "", "", "", "", dbUrl, "EMAILAPP", "3306", "userxj", "dawson2");
        new EmailJDBCTest().seedDatabase();
    }

    /**
     * This routine recreates the database before every test. This makes sure
     * that a destructive test will not interfere with any other test. Does not
     * support stored procedures.
     *
     * This routine is courtesy of Bartosz Majsak, an Arquillian developer at
     * JBoss
     */
    public void seedDatabase() {
        LOG.info("@Before seeding");

        final String seedDataScript = loadAsString("createEmailTables.sql");

        try (Connection connection = DriverManager.getConnection(configBean.getDbUrl(), configBean.getDbUsername(), configBean.getDbPassword());) {
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
    }
}
