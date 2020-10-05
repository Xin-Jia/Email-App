package com.xinjia.jdbc.persistence;

import com.xinjia.jdbc.beans.EmailData;
import com.xinjia.jdbc.beans.FolderData;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jodd.mail.EmailAddress;
import jodd.mail.EmailAttachment;
import jodd.mail.EmailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Xin Jia Cao
 */
public class EmailDAOImpl implements EmailDAO {

    private final static Logger LOG = LoggerFactory.getLogger(EmailDAOImpl.class);

    private final static String URL = "jdbc:mysql://localhost:3306/EMAILAPP?characterEncoding=UTF-8&autoReconnect=true&zeroDateTimeBehavior=CONVERT_TO_NULL&useSSL=false&allowPublicKeyRetrieval=true&useTimezone=true&serverTimezone=UTC";
    private final static String USER = "root";
    private final static String PASSWORD = "dawson";

    @Override
    public ArrayList<EmailData> findAllEmails() throws SQLException {

        ArrayList<EmailData> data = new ArrayList<>();
        String selectQuery = "SELECT EMAILID, FOLDERID, FROMADDRESS, SENTDATE, RECEIVEDATE, SUBJECT, MESSAGE, HTMLMESSAGE FROM EMAIL";

        try ( Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);  PreparedStatement pStatement = connection.prepareStatement(selectQuery);  ResultSet resultSet = pStatement.executeQuery()) {
            while (resultSet.next()) {
                data.add(createEmailData(resultSet));
            }
        }

        LOG.info("Total number of all emails : " + data.size());
        return data;
    }

    private EmailData createEmailData(ResultSet resultSet) throws SQLException {
        EmailData mailData = new EmailData();
        int emailId = resultSet.getInt("EmailId");
        mailData.setEmailId(emailId);
        mailData.setFolderId(resultSet.getInt("FolderId"));

        setDateByFolder(resultSet.getInt("FolderId"), mailData, resultSet);
        mailData.email.from(resultSet.getString("FromAddress"));

        mailData.email.subject(resultSet.getString("Subject"));
        mailData.email.textMessage(resultSet.getString("Message"));
        mailData.email.htmlMessage(resultSet.getString("HtmlMessage"));

        insertEmailDataRegularAttachments(mailData, emailId, false);
        insertEmailDataRegularAttachments(mailData, emailId, true);
        findEmailDataRecipients(mailData, emailId);

        return mailData;
    }

    private void setDateByFolder(int folderId, EmailData mailData, ResultSet rs) throws SQLException {
        switch (folderId) {
            case 1:
                if (rs.getTimestamp("ReceiveDate") != null) {
                    mailData.setReceivedDate(rs.getTimestamp("ReceiveDate").toLocalDateTime());
                    mailData.email.sentDate(new Date(rs.getTimestamp("SentDate").getTime()));
                }
                break;
            case 2:
                if (rs.getTimestamp("SentDate") != null) {
                    mailData.email.sentDate(new Date(rs.getTimestamp("SentDate").getTime()));
                }
                break;
            default:
                break;
        }
    }

    private void insertEmailDataRegularAttachments(EmailData mailData, int emailId, boolean isEmbedded) throws SQLException {

        String constraint = "";
        if (isEmbedded) {
            constraint = "NOT NULL OR ATTACHMENTS.CONTENTID != ''";
        } else {
            constraint = "NULL OR ATTACHMENTS.CONTENTID = ''";
        }

        String selectAttachmentQuery = "SELECT ATTACHMENTS.FILECONTENT, ATTACHMENTS.FILENAME, ATTACHMENTS.CONTENTID FROM ATTACHMENTS "
                + "INNER JOIN EMAIL ON ATTACHMENTS.EMAILID = EMAIL.EMAILID "
                + "WHERE EMAIL.EMAILID = ? AND ATTACHMENTS.CONTENTID IS " + constraint;

        try ( Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);  PreparedStatement pStatement = connection.prepareStatement(selectAttachmentQuery);) {
            pStatement.setInt(1, emailId);
            try ( ResultSet resultSet = pStatement.executeQuery()) {
                while (resultSet.next()) {
                    mailData.email.embeddedAttachment(EmailAttachment.with()
                            .content(resultSet.getBytes("FileContent"))
                            .contentId(resultSet.getString("ContentId"))
                            .name(resultSet.getString("FileName")));
                }
            }
        }
    }

    private void findEmailDataRecipients(EmailData mailData, int emailId) throws SQLException {
        ArrayList<String> toRecipients = new ArrayList<>();
        ArrayList<String> ccRecipients = new ArrayList<>();
        ArrayList<String> bccRecipients = new ArrayList<>();

        String selectRecipientQuery = "SELECT ADDRESS.EMAILADDRESS, EMAILTOADDRESS.RECIPIENTTYPE FROM EMAIL"
                + " INNER JOIN EMAILTOADDRESS ON EMAIL.EMAILID = EMAILTOADDRESS.EMAILID "
                + "INNER JOIN ADDRESS ON EMAILTOADDRESS.ADDRESSID = ADDRESS.ADDRESSID "
                + "WHERE EMAIL.EMAILID = ?";

        try ( Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);  PreparedStatement pStatement = connection.prepareStatement(selectRecipientQuery);) {
            pStatement.setInt(1, emailId);
            try ( ResultSet resultSet = pStatement.executeQuery()) {
                while (resultSet.next()) {
                    switch (resultSet.getString("RecipientType")) {
                        case "To" ->
                            toRecipients.add(resultSet.getString("EmailAddress"));
                        case "CC" ->
                            ccRecipients.add(resultSet.getString("EmailAddress"));
                        case "BCC" ->
                            bccRecipients.add(resultSet.getString("EmailAddress"));
                        default -> {
                        }
                    }
                }
            }
        }

        addRecipientsToEmailData(mailData, toRecipients, "To");
        addRecipientsToEmailData(mailData, ccRecipients, "CC");
        addRecipientsToEmailData(mailData, bccRecipients, "BCC");
    }

    private void addRecipientsToEmailData(EmailData mailData, ArrayList<String> recipients, String type) {
        if (!recipients.isEmpty()) {
            switch (type) {
                case "To" ->
                    mailData.email.to(recipients.toArray(new String[0]));
                case "CC" ->
                    mailData.email.cc(recipients.toArray(new String[0]));
                case "BCC" ->
                    mailData.email.bcc(recipients.toArray(new String[0]));
                default -> {
                }
            }
        }
    }

    @Override
    public void createEmail(EmailData mailData) throws SQLException {
        String createQuery = "INSERT INTO EMAIL(FROMADDRESS, SENTDATE, RECEIVEDATE, SUBJECT, MESSAGE, HTMLMESSAGE, FOLDERID) VALUES (?,?,?,?,?,?,?)";

        try ( Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);  PreparedStatement ps = connection.prepareStatement(createQuery, Statement.RETURN_GENERATED_KEYS);) {
            ps.setString(1, mailData.email.from().toString());
            createDateOnFolderId(mailData, ps, mailData.getFolderId());
            ps.setString(4, mailData.email.subject());
            List<EmailMessage> sentMessages = mailData.email.messages();
            ArrayList<String> messages = retrieveMessageContent(sentMessages, "text/plain");
            ps.setString(5, messages.get(0));

            messages = retrieveMessageContent(sentMessages, "text/html");
            ps.setString(6, messages.get(0));
            ps.setInt(7, mailData.getFolderId());
            ps.executeUpdate();

            try ( ResultSet rs = ps.getGeneratedKeys();) {
                int recordNum = -1;
                if (rs.next()) {
                    recordNum = rs.getInt(1);
                }
                mailData.setEmailId(recordNum);
                LOG.debug("New email ID is: " + recordNum);
            }
        }
        checkIfInAddressTable(mailData);
        createRecipientsField(mailData);
        createAttachmentsField(mailData);
    }

    private void createDateOnFolderId(EmailData mailData, PreparedStatement ps, int folderId) throws SQLException {

        switch (folderId) {
            case 1:
                ps.setTimestamp(2, new Timestamp(mailData.email.sentDate().getTime()));
                ps.setTimestamp(3, Timestamp.valueOf(mailData.getReceivedDate()));
                break;
            case 2:
                ps.setTimestamp(2, new Timestamp(mailData.email.sentDate().getTime()));
                ps.setTimestamp(3, null);
                break;
            default:
                ps.setTimestamp(2, null);
                ps.setTimestamp(3, null);
                break;
        }
    }

    private void checkIfInAddressTable(EmailData mailData) throws SQLException {

        EmailAddress[] toRecipients = mailData.email.to();
        EmailAddress[] ccRecipients = mailData.email.cc();
        EmailAddress[] bccRecipients = mailData.email.bcc();
        EmailAddress[] fromAddress = new EmailAddress[1];
        fromAddress[0] = mailData.email.from();
        findDBAddresses(fromAddress);
        findDBAddresses(toRecipients);
        findDBAddresses(ccRecipients);
        findDBAddresses(bccRecipients);
    }

    private void findDBAddresses(EmailAddress[] addresses) throws SQLException {

        String selectAllAddressQuery = "SELECT EMAILADDRESS FROM ADDRESS WHERE EMAILADDRESS = ?";
        for (EmailAddress mail : addresses) {
            try ( Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);  PreparedStatement pStatement = connection.prepareStatement(selectAllAddressQuery);) {
                pStatement.setString(1, mail.getEmail());
                try ( ResultSet resultSet = pStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        LOG.info("Address not in table -> create");
                        createDBAddress(mail.getEmail());
                    }
                }
            }
        }
    }

    private void createDBAddress(String address) throws SQLException {
        String createAddressQuery = "INSERT INTO ADDRESS(EMAILADDRESS) VALUES (?)";
        int rows = 0;
        try ( Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);  PreparedStatement ps = connection.prepareStatement(createAddressQuery, Statement.RETURN_GENERATED_KEYS);) {
            ps.setString(1, address);
            rows = ps.executeUpdate();
        }
        LOG.info("Creating an address - Number of rows created: " + rows);
    }

    private int createRecipientsField(EmailData mailData) throws SQLException {
        String createRecipientsQuery = "INSERT INTO EMAILTOADDRESS(EMAILID, ADDRESSID, RECIPIENTTYPE) VALUES (?,?,?)";
        int rows = 0;
        try ( Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);  PreparedStatement ps = connection.prepareStatement(createRecipientsQuery, Statement.RETURN_GENERATED_KEYS);) {
            rows += addInDBRecipients(mailData, ps, mailData.email.to(), "To");
            rows += addInDBRecipients(mailData, ps, mailData.email.cc(), "CC");
            rows+= addInDBRecipients(mailData, ps, mailData.email.bcc(), "BCC");
        }
        LOG.info("Number of recipients added: "+rows);
        return rows;
    }

    private int addInDBRecipients(EmailData mailData, PreparedStatement ps, EmailAddress[] addresses, String type) throws SQLException {
        int rows = 0;
        for (EmailAddress address : addresses) {
            ps.setInt(1, mailData.getEmailId());
            LOG.info("email "+type+ ": " + address.getEmail());
            ps.setInt(2, getAddressId(address.getEmail()));
            ps.setString(3, type);
            rows += ps.executeUpdate();
        }
        return rows;
    }

    private int getAddressId(String address) throws SQLException {
        int addressId = -1;
        String selectQuery = "SELECT ADDRESSID FROM ADDRESS WHERE EMAILADDRESS = ?";
        try ( Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);  PreparedStatement pStatement = connection.prepareStatement(selectQuery);) {
            pStatement.setString(1, address);
            try ( ResultSet resultSet = pStatement.executeQuery()) {
                if (resultSet.next()) {
                    addressId = resultSet.getInt("AddressId");
                }
            }
        }
        return addressId;
    }

    private int createAttachmentsField(EmailData mailData) throws SQLException {
        String createAttachmentQuery = "INSERT INTO ATTACHMENTS(EMAILID, FILENAME, CONTENTID, FILECONTENT) VALUES(?,?,?,?)";
        int rows = 0;
        for (EmailAttachment atts : mailData.email.attachments()) {
            try ( Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);  PreparedStatement ps = connection.prepareStatement(createAttachmentQuery, Statement.RETURN_GENERATED_KEYS);) {
                ps.setInt(1, mailData.getEmailId());
                ps.setString(2, atts.getName());

                if (atts.isEmbedded()) {
                    ps.setString(3, atts.getContentId());
                } else {
                    ps.setString(3, "");
                }
                ps.setBytes(4, atts.toByteArray());
                rows += ps.executeUpdate();
            }
        }
        LOG.info("Number of attachments added: "+rows);
        return rows;
    }

    @Override
    public EmailData findEmailById(int id) throws SQLException {
        EmailData mailData = new EmailData();
        String selectQuery = "SELECT EMAILID, FOLDERID, FROMADDRESS, SENTDATE, RECEIVEDATE, SUBJECT, MESSAGE, HTMLMESSAGE FROM EMAIL WHERE EMAILID = ?";

        try ( Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);  PreparedStatement pStatement = connection.prepareStatement(selectQuery);) {
            pStatement.setInt(1, id);

            try ( ResultSet resultSet = pStatement.executeQuery()) {
                if (resultSet.next()) {
                    mailData = createEmailData(resultSet);
                }
            }
        }
        LOG.info("Found " + id + "?: " + (mailData != null));
        return mailData;
    }

    @Override
    public int updateEmail(EmailData mailData) throws SQLException {
        int rows = 0;
        rows += updateEmailAttachments(mailData);
        
        String updateQuery = "UPDATE EMAIL SET FOLDERID = ?, SUBJECT = ?, MESSAGE = ?, HTMLMESSAGE = ? WHERE EMAILID = ?";
        
        try ( Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);  PreparedStatement ps = connection.prepareStatement(updateQuery);) {
            ps.setInt(1, mailData.getFolderId());
            ps.setString(2, mailData.email.subject());
            List<EmailMessage> sentMessages = mailData.email.messages();
            ArrayList<String> messages = retrieveMessageContent(sentMessages, "text/plain");
            ps.setString(3, messages.get(0));
            messages = retrieveMessageContent(sentMessages, "text/html");
            ps.setString(4, messages.get(0));
            ps.setInt(5, mailData.getEmailId());

            rows = ps.executeUpdate();
        }
        rows += updateEmailRecipients(mailData);
        LOG.info("Total number of rows updated and added: "+rows);
        return rows;
    }

    private int updateEmailAttachments(EmailData mailData) throws SQLException {
        String deleteAttachmentsQuery = "DELETE FROM ATTACHMENTS "
                + "WHERE ATTACHMENTS.EMAILID = ?";
        int rows = 0;
        try ( Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);  PreparedStatement ps = connection.prepareStatement(deleteAttachmentsQuery);) {
            ps.setInt(1, mailData.getEmailId());
            ps.executeUpdate();
        }
        
        rows += createAttachmentsField(mailData);
        LOG.info("Number of attachments added for update: "+rows);
       return rows;
    }
    
    private int updateEmailRecipients(EmailData mailData) throws SQLException{
        String deleteToAddressQuery = "DELETE FROM EMAILTOADDRESS "
                + "WHERE EMAILTOADDRESS.EMAILID = ?";
        int rows = 0;
        try ( Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);  PreparedStatement ps = connection.prepareStatement(deleteToAddressQuery);) {
            ps.setInt(1, mailData.getEmailId());
            ps.executeUpdate();
        }
        EmailAddress[] toRecipients = mailData.email.to();
        EmailAddress[] ccRecipients = mailData.email.cc();
        EmailAddress[] bccRecipients = mailData.email.bcc();
        findDBAddresses(toRecipients);
        findDBAddresses(ccRecipients);
        findDBAddresses(bccRecipients);
        
        rows += createRecipientsField(mailData);
        LOG.info("Number of recipients added for update: "+rows);
        return rows;
    }

    @Override
    public int deleteEmail(int id) throws SQLException {
        int result;

        String deleteAttachmentsQuery = "DELETE FROM ATTACHMENTS "
                + "WHERE ATTACHMENTS.EMAILID = ?";

        try ( Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);  PreparedStatement ps = connection.prepareStatement(deleteAttachmentsQuery);) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }

        String deleteToAddressQuery = "DELETE FROM EMAILTOADDRESS "
                + "WHERE EMAILTOADDRESS.EMAILID = ?";

        try ( Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);  PreparedStatement ps = connection.prepareStatement(deleteToAddressQuery);) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        String deleteEmailQuery = "DELETE FROM EMAIL "
                + "WHERE EMAIL.EMAILID = ?";

        try ( Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);  PreparedStatement ps = connection.prepareStatement(deleteEmailQuery);) {
            ps.setInt(1, id);
            result = ps.executeUpdate();
        }

        LOG.info("Number of rows deleted (should be 1): " + result);
        return result;
    }

    private ArrayList<String> retrieveMessageContent(List<EmailMessage> messages, String format) {
        //it uses an ArrayList since members in a lambda cannot be changed
        ArrayList<String> msgToReturn = new ArrayList<>();
        messages.stream().map((mesg) -> {
            return mesg;
        }).map((mesg) -> {
            return mesg;
        }).map((mesg) -> {
            return mesg;
        }).forEachOrdered((mesg) -> {
            if (mesg.getMimeType().equalsIgnoreCase(format)) {
                LOG.debug("The message sent: " + mesg.getContent());
                LOG.debug("The message sent type: " + mesg.getMimeType());
                msgToReturn.add(mesg.getContent());
            }

        });
        return msgToReturn;
    }

    @Override
    public ArrayList<EmailData> findEmailsByFolder(String address, String folder) throws SQLException {
        ArrayList<EmailData> emails = new ArrayList<>();
        FolderData folderData = new FolderData();
        String selectEmailsQuery = "SELECT EMAIL.EMAILID, EMAIL.FOLDERID, FROMADDRESS, SENTDATE, RECEIVEDATE, SUBJECT, MESSAGE, HTMLMESSAGE FROM EMAIL\n"
                + "INNER JOIN FOLDER ON EMAIL.FOLDERID = FOLDER.FOLDERID\n"
                + "INNER JOIN EMAILTOADDRESS ON EMAILTOADDRESS.EMAILID = EMAIL.EMAILID\n"
                + "INNER JOIN ADDRESS ON EMAILTOADDRESS.ADDRESSID = ADDRESS.ADDRESSID\n"
                + "WHERE EMAIL.FROMADDRESS = ? AND FOLDER.FOLDERNAME = ?";

        try ( Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);  PreparedStatement pStatement = connection.prepareStatement(selectEmailsQuery);) {
            pStatement.setString(1, address);
            pStatement.setString(2, folder);
            try ( ResultSet resultSet = pStatement.executeQuery()) {
                while (resultSet.next()) {
                    emails.add(createEmailData(resultSet));
                }
            }
            LOG.debug("Number of emails in " + folder + " folder from: " + address + " is: " + emails.size());
            return emails;
        }

    }

    @Override
    public ArrayList<EmailData> findEmailsBySubject(String address, String subject) throws SQLException {
        ArrayList<EmailData> emails = new ArrayList<>();
        String selectFromSubjectQuery = "SELECT EMAILID, FOLDERID, FROMADDRESS, SENTDATE, RECEIVEDATE, SUBJECT, MESSAGE, HTMLMESSAGE FROM EMAIL "
                + "WHERE SUBJECT LIKE ? AND FROMADDRESS = ?";
        try ( Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);  PreparedStatement pStatement = connection.prepareStatement(selectFromSubjectQuery);) {
            pStatement.setString(1, subject + "%");
            pStatement.setString(2, address);
            try ( ResultSet resultSet = pStatement.executeQuery()) {
                while (resultSet.next()) {
                    emails.add(createEmailData(resultSet));
                }
            }
            LOG.debug("Number of emails found with the substring: " + subject + " from: " + address + " is: " + emails.size());
            return emails;
        }
    }

}
