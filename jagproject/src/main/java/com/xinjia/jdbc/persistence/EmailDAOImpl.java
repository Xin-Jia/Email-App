package com.xinjia.jdbc.persistence;

import com.xinjia.exceptions.FolderAlreadyExistsException;
import com.xinjia.jdbc.beans.EmailData;
import com.xinjia.properties.MailConfigBean;
import com.xinjia.properties.propertybean.FolderData;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jodd.mail.EmailAddress;
import jodd.mail.EmailAttachment;
import jodd.mail.EmailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CRUD implementation of Emails operations using JDBC. Opens a database and
 * retrieve information (rows) in the Email, EmailToAddress, Address,
 * Attachments and Folder tables.
 *
 * @author Xin Jia Cao
 */
public class EmailDAOImpl implements EmailDAO {

    private final static Logger LOG = LoggerFactory.getLogger(EmailDAOImpl.class);

    private MailConfigBean configBean;
    private String url;

    /**
     * Constructor that initializes the MailConfigBean
     *
     * @param mailConfigBean the MailConfigBean
     */
    public EmailDAOImpl(MailConfigBean mailConfigBean) {
        configBean = mailConfigBean;
        url = "jdbc:mysql://"
                + configBean.getMysqlURL()
                + ":"
                + configBean.getMysqlPort()
                + "/"
                + configBean.getMysqlDatabase()
                + "?characterEncoding=UTF-8&autoReconnect=true&zeroDateTimeBehavior=CONVERT_TO_NULL&useSSL=false&allowPublicKeyRetrieval=true&useTimezone=true&serverTimezone=UTC";
    }

    /**
     * Getter to retrieve the mail config bean
     * @return the MailConfigBean
     */
    public MailConfigBean getMailConfigBean() {
        return configBean;
    }

    /**
     * Retrieve all emails in the Email table and create an EmailData(bean) for
     * each of them
     *
     * @return ArrayList<EmailData> all the Email beans found in the Email table
     * @throws SQLException
     */
    @Override
    public ArrayList<EmailData> findAllEmails() throws SQLException {

        ArrayList<EmailData> data = new ArrayList<>();
        String selectQuery = "SELECT EMAILID, FOLDERID, FROMADDRESS, SENTDATE, RECEIVEDATE, SUBJECT, MESSAGE, HTMLMESSAGE FROM EMAIL";

        try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement pStatement = connection.prepareStatement(selectQuery);  ResultSet resultSet = pStatement.executeQuery()) {
            while (resultSet.next()) {
                data.add(createEmailData(resultSet));
            }
        }

        LOG.debug("Total number of emails : " + data.size());

        return data;
    }


    /**
     * Creates an EmailData (bean) based on the ResultSet retrieved from a query
     *
     * @param resultSet A ResultSet that contains information about an Email row
     * in the database
     * @return EmailData the created custom Email bean
     * @throws SQLException
     */
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

        insertEmailDataAttachments(mailData, emailId, false);
        insertEmailDataAttachments(mailData, emailId, true);
        findEmailDataRecipients(mailData, emailId);
        return mailData;
    }

    /**
     * Set the sent and received dates of an Email bean based on the folderId
     * All emails in Inbox contain the sent and received date All emails in Sent
     * contain the sent date only All emails in Draft do not contain a date
     *
     * @param folderId the folderId (1 for Inbox, 2 for Sent and 3 for Draft)
     * @param mailData the custom Email bean we set the dates on
     * @param rs the ResultSet that contains information on an Email row
     * @throws SQLException
     */
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

    /**
     * Insert in the email of the custom Email bean all the attachments found in
     * the Email's table
     *
     * @param mailData the custom Email bean
     * @param emailId the emailId of the custom Email bean
     * @param isEmbedded boolean that suggests whether the files are embedded or
     * not
     * @throws SQLException
     */
    private void insertEmailDataAttachments(EmailData mailData, int emailId, boolean isEmbedded) throws SQLException {

        String constraint = "";
        if (isEmbedded) {
            constraint = "!= '')";
        } else {
            constraint = "= '')";
        }

        String selectAttachmentQuery = "SELECT ATTACHMENTS.FILECONTENT, ATTACHMENTS.FILENAME, ATTACHMENTS.CONTENTID FROM ATTACHMENTS "
                + "INNER JOIN EMAIL ON ATTACHMENTS.EMAILID = EMAIL.EMAILID "
                + "WHERE EMAIL.EMAILID = ? AND (ATTACHMENTS.CONTENTID " + constraint;

        try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement pStatement = connection.prepareStatement(selectAttachmentQuery);) {
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

    /**
     * Find all the recipients in the Email table from a specific email
     *
     * @param mailData the custom Email bean
     * @param emailId the emailId of the Email
     * @throws SQLException
     */
    private void findEmailDataRecipients(EmailData mailData, int emailId) throws SQLException {
        ArrayList<String> toRecipients = new ArrayList<>();
        ArrayList<String> ccRecipients = new ArrayList<>();
        ArrayList<String> bccRecipients = new ArrayList<>();

        String selectRecipientQuery = "SELECT ADDRESS.EMAILADDRESS, EMAILTOADDRESS.RECIPIENTTYPE FROM EMAIL"
                + " INNER JOIN EMAILTOADDRESS ON EMAIL.EMAILID = EMAILTOADDRESS.EMAILID "
                + "INNER JOIN ADDRESS ON EMAILTOADDRESS.ADDRESSID = ADDRESS.ADDRESSID "
                + "WHERE EMAIL.EMAILID = ?";

        try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement pStatement = connection.prepareStatement(selectRecipientQuery);) {
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

    /**
     * Add the recipients found in the EmailToAddress table in the custom Email
     * bean
     *
     * @param mailData the custom Email bean
     * @param recipients the list of recipients in an email
     * @param type the type of recipient (to, cc and bcc)
     */
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

    /**
     * Creates an Email in the database with its attachments and recipients
     * based on the custom Email bean
     *
     * @param mailData the custom Email bean
     * @throws SQLException
     */
    @Override
    public void createEmail(EmailData mailData) throws SQLException {
        String createQuery = "INSERT INTO EMAIL(FROMADDRESS, SENTDATE, RECEIVEDATE, SUBJECT, MESSAGE, HTMLMESSAGE, FOLDERID) VALUES (?,?,?,?,?,?,?)";

        //all length issues will be handled in the GUI so no need for exceptions here
        try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement ps = connection.prepareStatement(createQuery, Statement.RETURN_GENERATED_KEYS);) {
            ps.setString(1, mailData.email.from().toString());
            createDateOnFolderId(mailData, ps, mailData.getFolderId());
            if (mailData.email.subject() != null) {
                ps.setString(4, mailData.email.subject());
            } else {
                ps.setString(4, "");
            }
            //retrieve plain and html messages
            List<EmailMessage> sentMessages = mailData.email.messages();
            if (!mailData.email.messages().isEmpty()) {
                ArrayList<String> messages = retrieveMessageContent(sentMessages, "text/plain");
                if (messages.isEmpty()) {
                    ps.setString(5, "");
                } else {
                    ps.setString(5, messages.get(0));
                }
                messages = retrieveMessageContent(sentMessages, "text/html");
                if (messages.isEmpty()) {
                    ps.setString(6, "");
                } else {
                    ps.setString(6, messages.get(0));
                }

            } else {
                ps.setString(5, "");
                ps.setString(6, "");
            }
            ps.setInt(7, mailData.getFolderId());
            ps.executeUpdate();
            //set the custom bean's emailId to its new value
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
        LOG.info("Email inserted in the database");
    }

    /**
     * Creates the sent and received dates for the database based on the
     * folderId of the email
     *
     * @param mailData the custom Email bean
     * @param ps the PreparedStatement
     * @param folderId the folderId of the email
     * @throws SQLException
     */
    private void createDateOnFolderId(EmailData mailData, PreparedStatement ps, int folderId) throws SQLException {

        switch (folderId) {
            case 1:
                //set dates for send and received (Inbox)
                ps.setTimestamp(2, new Timestamp(mailData.email.sentDate().getTime()));
                ps.setTimestamp(3, Timestamp.valueOf(mailData.getReceivedDate()));
                break;
            case 2:
                //set date for send only (Sent)
                ps.setTimestamp(2, new Timestamp(mailData.email.sentDate().getTime()));
                ps.setTimestamp(3, null);
                break;
            default:
                //set dates to null (Draft or custom folders)
                ps.setTimestamp(2, null);
                ps.setTimestamp(3, null);
                break;
        }
    }

    /**
     * Checks if the address and the recipients' addresses of the custom Email
     * bean is already in the Address table or not
     *
     * @param mailData the custom Email bean
     * @throws SQLException
     */
    private void checkIfInAddressTable(EmailData mailData) throws SQLException {

        //populates EmailAddresses
        EmailAddress[] toRecipients = mailData.email.to();
        EmailAddress[] ccRecipients = mailData.email.cc();
        EmailAddress[] bccRecipients = mailData.email.bcc();
        EmailAddress[] fromAddress = new EmailAddress[1];
        fromAddress[0] = mailData.email.from();
        //checks in the database if the emails exist already
        findDBAddresses(fromAddress);
        findDBAddresses(toRecipients);
        findDBAddresses(ccRecipients);
        findDBAddresses(bccRecipients);
    }

    /**
     * Loop through an array of EmailAddress to check if an address is already
     * in the database or not If it is not in the database, add it to the
     * Address table
     *
     * @param addresses an array of EmailAddress from Jodd
     * @throws SQLException
     */
    private void findDBAddresses(EmailAddress[] addresses) throws SQLException {

        String selectAllAddressQuery = "SELECT EMAILADDRESS FROM ADDRESS WHERE EMAILADDRESS = ?";
        for (EmailAddress mail : addresses) {
            try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement pStatement = connection.prepareStatement(selectAllAddressQuery);) {
                pStatement.setString(1, mail.getEmail());
                try ( ResultSet resultSet = pStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        //address not found if row is empty, needs to create the address
                        LOG.info("Address not in table -> create");
                        createDBAddress(mail.getEmail());
                    }
                }
            }
        }
    }

    /**
     * Creates a new entry of address in the Address table
     *
     * @param address the address email to be inserted in the database
     * @throws SQLException
     */
    private void createDBAddress(String address) throws SQLException {
        String createAddressQuery = "INSERT INTO ADDRESS(EMAILADDRESS) VALUES (?)";
        int rows = 0;
        try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement ps = connection.prepareStatement(createAddressQuery, Statement.RETURN_GENERATED_KEYS);) {
            ps.setString(1, address);
            rows = ps.executeUpdate();
        }
        LOG.debug("Creating an address - Number of rows created (should be 1): " + rows);
    }

    /**
     * Creates the recipients in the database based on the recipients in the
     * custom Email bean
     *
     * @param mailData the custom Email bean
     * @return an int representing the number of rows added
     * @throws SQLException
     */
    private int createRecipientsField(EmailData mailData) throws SQLException {
        String createRecipientsQuery = "INSERT INTO EMAILTOADDRESS(EMAILID, ADDRESSID, RECIPIENTTYPE) VALUES (?,?,?)";
        int rows = 0;
        try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement ps = connection.prepareStatement(createRecipientsQuery, Statement.RETURN_GENERATED_KEYS);) {
            rows += addInDBRecipients(mailData, ps, mailData.email.to(), "To");
            rows += addInDBRecipients(mailData, ps, mailData.email.cc(), "CC");
            rows += addInDBRecipients(mailData, ps, mailData.email.bcc(), "BCC");
        }
        LOG.debug("Number of recipients added: " + rows);
        return rows;
    }

    /**
     * Add all addresses in the EmailAddress[] in the EmailToAddress table based
     * on the type of the recipients
     *
     * @param mailData the custom Email bean
     * @param ps the PreparedStatement
     * @param addresses the EmailAddress[] from Jodd
     * @param type the type of the recipients
     * @return an int representing the number of rows added
     * @throws SQLException
     */
    private int addInDBRecipients(EmailData mailData, PreparedStatement ps, EmailAddress[] addresses, String type) throws SQLException {
        int rows = 0;
        for (EmailAddress address : addresses) {
            ps.setInt(1, mailData.getEmailId());
            LOG.debug("email " + type + ": " + address.getEmail());
            ps.setInt(2, getAddressId(address.getEmail()));
            ps.setString(3, type);
            rows += ps.executeUpdate();
        }
        return rows;
    }

    /**
     * Retrieve the addressId of an email address in the database
     *
     * @param address the email address we retrieve the id from
     * @return the email address id
     * @throws SQLException
     */
    private int getAddressId(String address) throws SQLException {
        int addressId = -1;
        String selectQuery = "SELECT ADDRESSID FROM ADDRESS WHERE EMAILADDRESS = ?";
        try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement pStatement = connection.prepareStatement(selectQuery);) {
            pStatement.setString(1, address);
            try ( ResultSet resultSet = pStatement.executeQuery()) {
                if (resultSet.next()) {
                    addressId = resultSet.getInt("AddressId");
                }
            }
        }
        return addressId;
    }

    /**
     * Creates the attachments in the Attachments table based on the given
     * custom Email bean
     *
     * @param mailData the custom Email bean
     * @return an int representing the number of attachments added
     * @throws SQLException
     */
    private int createAttachmentsField(EmailData mailData) throws SQLException {
        String createAttachmentQuery = "INSERT INTO ATTACHMENTS(EMAILID, FILENAME, CONTENTID, FILECONTENT) VALUES(?,?,?,?)";
        int rows = 0;
        for (EmailAttachment atts : mailData.email.attachments()) {
            try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement ps = connection.prepareStatement(createAttachmentQuery, Statement.RETURN_GENERATED_KEYS);) {
                ps.setInt(1, mailData.getEmailId());
                ps.setString(2, atts.getName());
                //if attachment is embedded, set the contentId and if not, set it empty
                if (atts.isEmbedded()) {

                    ps.setString(3, atts.getContentId());
                } else {
                    ps.setString(3, "");
                }
                //insert the file content as a byte[]
                ps.setBytes(4, atts.toByteArray());
                rows += ps.executeUpdate();
            }
        }
        LOG.debug("Number of attachments added: " + rows);
        return rows;
    }

    /**
     * Creates a folder in the Folder table based on the given name Cannot
     * create a new folder with an existing folder name
     *
     * @param folderName the name of the folder to create
     * @return an int that represents the number of folder created (should be 1)
     * @throws SQLException
     * @throws FolderAlreadyExistsException thrown when the folder name given
     * already exists in the Folder table
     */
    @Override
    public int createFolder(String folderName) throws SQLException, FolderAlreadyExistsException {
        //checks if the folder name already exists in the database
        if (containsIgnoreCase(findFolderNames(), folderName)) {
            throw new FolderAlreadyExistsException("The given folder already exists");
        }
        int rows = 0;
        String createFolderQuery = "INSERT INTO FOLDER(FOLDERNAME) VALUES(?)";
        try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement ps = connection.prepareStatement(createFolderQuery, Statement.RETURN_GENERATED_KEYS);) {
            ps.setString(1, folderName);
            rows = ps.executeUpdate();
        }
        LOG.info("Folder created: " + folderName);
        return rows;
    }

    /**
     * Finds an email based on its id and creates a custom Email bean that
     * represents it
     *
     * @param id the emailId
     * @return the custom Email bean created
     * @throws SQLException
     */
    @Override
    public EmailData findEmailById(int id) throws SQLException {
        EmailData mailData = new EmailData();
        String selectQuery = "SELECT EMAILID, FOLDERID, FROMADDRESS, SENTDATE, RECEIVEDATE, SUBJECT, MESSAGE, HTMLMESSAGE FROM EMAIL WHERE EMAILID = ?";

        try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement pStatement = connection.prepareStatement(selectQuery);) {
            pStatement.setInt(1, id);

            try ( ResultSet resultSet = pStatement.executeQuery()) {
                if (resultSet.next()) {
                    mailData = createEmailData(resultSet);
                }
            }
        }
        LOG.debug("Email with id: " + id + "?: " + (mailData != null));

        return mailData;
    }

    /**
     * Finds all emails by the folder name and creates a custom Email bean for
     * each of them. No need to check if the folder exists or not because it
     * will be handled in the GUI.
     *
     * @param folderName the folder name we retrieve the emails from
     * @return ArrayList<EmailData> the custom Email beans from the given folder
     * @throws SQLException
     */
    @Override
    public ArrayList<EmailData> findEmailsByFolder(String folderName) throws SQLException {

        ArrayList<EmailData> emails = new ArrayList<>();

        String selectEmailsQuery = "SELECT EMAIL.EMAILID, EMAIL.FOLDERID, FROMADDRESS, SENTDATE, RECEIVEDATE, SUBJECT, MESSAGE, HTMLMESSAGE FROM EMAIL "
                + "INNER JOIN FOLDER ON EMAIL.FOLDERID = FOLDER.FOLDERID "
                + "WHERE FOLDER.FOLDERNAME = ?";

        try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement pStatement = connection.prepareStatement(selectEmailsQuery);) {
            pStatement.setString(1, folderName);
            try ( ResultSet resultSet = pStatement.executeQuery()) {
                while (resultSet.next()) {
                    emails.add(createEmailData(resultSet));
                }
            }
            LOG.debug("Number of emails in: " + folderName + " is: " + emails.size());

        }

        return emails;

    }

    /**
     * Checks if a given folder name is in a list of given folder names
     *
     * @param folders a list of folder names
     * @param name the folder name we are comparing to
     * @return true if the given folder name is in the list and false otherwise
     */
    private boolean containsIgnoreCase(List<String> folders, String name) {
        //equal operation that ignores case sensivity
        
        return folders.stream().anyMatch(folderName -> (folderName.equalsIgnoreCase(name)));
    }

    /**
     * Finds all emails that contains a sub-string of a text in the subject
     * field and creates a custom Email bean for each of them
     *
     * @param subString the sub-string
     * @return ArrayList<EmailData> the list of custom Email beans that contain
     * the sub-string
     * @throws SQLException
     */
    @Override
    public ArrayList<EmailData> findEmailsBySubject(String subString) throws SQLException {
        ArrayList<EmailData> emails = new ArrayList<>();
        String selectSubStringQuery = "SELECT EMAILID, FOLDERID, FROMADDRESS, SENTDATE, RECEIVEDATE, SUBJECT, MESSAGE, HTMLMESSAGE FROM EMAIL "
                + "WHERE SUBJECT LIKE ?";
        try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement pStatement = connection.prepareStatement(selectSubStringQuery);) {
            //checks emails that contain a sub-string of a given subject name
            pStatement.setString(1, "%" + subString + "%");
            try ( ResultSet resultSet = pStatement.executeQuery()) {
                while (resultSet.next()) {
                    emails.add(createEmailData(resultSet));
                }
            }
            LOG.debug(emails.isEmpty() ? "No emails found with subject substring: " + subString
                    : "Number of emails found with the substring: " + subString + " in Subject is: " + emails.size());
        }

        return emails;
    }

    /**
     * Finds all emails that contains a sub-string in the recipients field and
     * creates a custom Email bean for each of them
     *
     * @param subString the sub-string
     * @return ArrayList<EmailData> the list of custom Email beans that contain
     * the sub-string
     * @throws SQLException
     */
    @Override
    public ArrayList<EmailData> findEmailsByRecipients(String subString) throws SQLException {
        ArrayList<EmailData> emails = new ArrayList<>();
        String selectSubStringQuery = "SELECT EMAIL.EMAILID, FOLDERID, FROMADDRESS, SENTDATE, RECEIVEDATE, SUBJECT, MESSAGE, HTMLMESSAGE FROM EMAIL "
                + "INNER JOIN EMAILTOADDRESS ON EMAIL.EMAILID = EMAILTOADDRESS.EMAILID "
                + "INNER JOIN ADDRESS ON EMAILTOADDRESS.ADDRESSID = ADDRESS.ADDRESSID "
                + "WHERE ADDRESS.EMAILADDRESS LIKE ?";
        try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement pStatement = connection.prepareStatement(selectSubStringQuery);) {
            //checks emails that contain a sub-string of a given recipient name
            pStatement.setString(1, "%" + subString + "%");
            try ( ResultSet resultSet = pStatement.executeQuery()) {
                while (resultSet.next()) {
                    emails.add(createEmailData(resultSet));
                }
            }
            LOG.debug(emails.isEmpty() ? "No emails found with recipient substring: " + subString
                    : "Number of emails found with the substring: " + subString + " in Subject is: " + emails.size());
        }

        return emails;
    }

    /**
     * Retrieves all folder names in the Folder table
     *
     * @return ArrayList<String> the list of folder names
     * @throws SQLException
     */
    @Override
    public ArrayList<String> findFolderNames() throws SQLException {

        ArrayList<String> folders = new ArrayList<>();
        String selectFoldersQuery = "SELECT FOLDERNAME FROM FOLDER";
        try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement pStatement = connection.prepareStatement(selectFoldersQuery);) {

            try ( ResultSet resultSet = pStatement.executeQuery()) {
                while (resultSet.next()) {
                    folders.add(resultSet.getString("FolderName"));
                }
            }
        }
        return folders;
    }

    /**
     * Retrieve all folders to make an ObservableList of the javaFX bean FolderData (to be displayed in the TreeView)
     * @return ObservableList<FolderData> 
     * @throws SQLException 
     */
    @Override
    public ObservableList<FolderData> findFolders() throws SQLException {
        ObservableList<FolderData> folders = FXCollections
                .observableArrayList();

        String selectFoldersQuery = "SELECT FOLDERID, FOLDERNAME FROM FOLDER";
        try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement pStatement = connection.prepareStatement(selectFoldersQuery);) {

            try ( ResultSet resultSet = pStatement.executeQuery()) {
                while (resultSet.next()) {
                    FolderData folder = new FolderData(resultSet.getInt("FolderId"), resultSet.getString("FolderName"));
                    folders.add(folder);
                }
            }
        }
        return folders;
    }

    /**
     * Retrieves all email addresses in the Address table
     *
     * @return ArrayList<String> the list of email addresses
     * @throws SQLException
     */
    @Override
    public ArrayList<String> findAllAddresses() throws SQLException {
        ArrayList<String> addresses = new ArrayList<>();

        String selectAddressesQuery = "SELECT EMAILADDRESS FROM ADDRESS";
        try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement pStatement = connection.prepareStatement(selectAddressesQuery);) {
            try ( ResultSet resultSet = pStatement.executeQuery()) {
                while (resultSet.next()) {
                    addresses.add(resultSet.getString("EmailAddress"));
                }
            }
        }
        return addresses;
    }


    /**
     * Updates an email draft (subject, messages, recipients, attachments)
     *
     * @param mailData the custom Email bean we wish to update
     * @return an int representing the number of emails updated (should be 1)
     * @throws SQLException
     */
    @Override
    public int updateEmailDraft(EmailData mailData) throws SQLException {

        int rows = 0;
        rows += updateEmailAttachments(mailData);

        String updateQuery = "UPDATE EMAIL SET SUBJECT = ?, MESSAGE = ?, HTMLMESSAGE = ? WHERE EMAILID = ?";

        try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement ps = connection.prepareStatement(updateQuery);) {
            ps.setString(1, mailData.email.subject());
            List<EmailMessage> sentMessages = mailData.email.messages();
            ArrayList<String> messages = retrieveMessageContent(sentMessages, "text/plain");
            //the helper method returns an array list of length 1 so we want to get the first string of the list
            ps.setString(2, messages.get(0));
            messages = retrieveMessageContent(sentMessages, "text/html");
            ps.setString(3, messages.get(0));
            ps.setInt(4, mailData.getEmailId());

            rows += ps.executeUpdate();
        }
        rows += updateEmailRecipients(mailData);
        LOG.debug("Total number of rows updated and added: " + rows);
        return rows;
    }

    /**
     * Updates an email draft and sends it
     *
     * @param mailData the custom Email bean
     * @return an int representing the number of operations done (update and
     * change folder - should be 2)
     * @throws SQLException
     */
    @Override
    public int updateEmailDraftAndSend(EmailData mailData) throws SQLException {
        int rows = updateEmailDraft(mailData);
        rows += changeEmailDraftToSent(mailData);
        LOG.debug("Total number of rows updated and added before sending: " + rows);
        return rows;
    }

    /**
     * Changes the draft email folder to Sent and changes the Sent date to the
     * current date and time
     *
     * @param mailData the custom Email bean
     * @return an int representing the number of rows updated and added
     * @throws SQLException
     */
    private int changeEmailDraftToSent(EmailData mailData) throws SQLException {
        //change the Draft folder to Sent
        int folderId = findFolderIdByName("Sent");
        String updateFolderQuery = "UPDATE EMAIL SET FOLDERID = ? WHERE EMAILID = ?";
        int rows = 0;
        try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement ps = connection.prepareStatement(updateFolderQuery);) {
            ps.setInt(1, folderId);
            ps.setInt(2, mailData.getEmailId());
            rows += ps.executeUpdate();
        }
        //Set the current Sent date 
        String updateDateQuery = "UPDATE EMAIL SET SENTDATE = ? WHERE EMAILID = ?";
        try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement ps = connection.prepareStatement(updateDateQuery);) {
            Date date = new Date();
            Timestamp tsp = new Timestamp(date.getTime());

            ps.setTimestamp(1, tsp);
            ps.setInt(2, mailData.getEmailId());
            rows += ps.executeUpdate();
        }
        return rows;
    }

    /**
     * Updates an email's attachments. Deletes all the email's attachments and
     * add the ones from the given custom Email bean
     *
     * @param mailData the custom Email bean
     * @return an int representing the number of new attachments added
     * @throws SQLException
     */
    private int updateEmailAttachments(EmailData mailData) throws SQLException {
        //deletes the email's attachments
        String deleteAttachmentsQuery = "DELETE FROM ATTACHMENTS "
                + "WHERE ATTACHMENTS.EMAILID = ?";
        int rows = 0;
        try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement ps = connection.prepareStatement(deleteAttachmentsQuery);) {
            ps.setInt(1, mailData.getEmailId());
            ps.executeUpdate();
        }
        //adds the new attachments
        rows += createAttachmentsField(mailData);
        LOG.debug("Number of attachments added for update: " + rows);
        return rows;
    }

    /**
     * Updates an email's recipients. Deletes all the email's recipients and add
     * the ones from the given custom Email bean
     *
     * @param mailData the custom Email bean
     * @return an int representing the number of new recipients added
     * @throws SQLException
     */
    private int updateEmailRecipients(EmailData mailData) throws SQLException {
        //delete the email's recipients
        String deleteToAddressQuery = "DELETE FROM EMAILTOADDRESS "
                + "WHERE EMAILTOADDRESS.EMAILID = ?";
        int rows = 0;
        try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement ps = connection.prepareStatement(deleteToAddressQuery);) {
            ps.setInt(1, mailData.getEmailId());
            ps.executeUpdate();
        }
        EmailAddress[] toRecipients = mailData.email.to();
        EmailAddress[] ccRecipients = mailData.email.cc();
        EmailAddress[] bccRecipients = mailData.email.bcc();

        findDBAddresses(toRecipients);
        findDBAddresses(ccRecipients);
        findDBAddresses(bccRecipients);
        //adds the new recipients
        rows += createRecipientsField(mailData);
        LOG.debug("Number of recipients added for update: " + rows);
        return rows;
    }

    /**
     * Retrieves a message based on its format (plain text or html)
     *
     * @param messages the list of messages from a Jodd email
     * @param format the format/type of the message
     * @return ArrayList<String> the appropriate message
     */
    public ArrayList<String> retrieveMessageContent(List<EmailMessage> messages, String format) {
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
                msgToReturn.add(mesg.getContent());
            }

        });
        return msgToReturn;
    }

    /**
     * Changes an email's current folder to another folder
     *
     * @param emailId the email ID
     * @param folderId the given folder Id be changed to
     * @return an int representing the number of emails that has its folder
     * changed (should be 1)
     * @throws SQLException
     */
    @Override
    public int changeEmailFolder(int emailId, int folderId) throws SQLException {
        //No need to check if the folder name exists since only existing folders 
        //will be on the GUI when trying to drag an email to another folder
        //Also no need to check if it is a Draft since the drag option for it will be disabled in the GUI

        String updateFolderQuery = "UPDATE EMAIL SET FOLDERID = ? WHERE EMAILID = ?";
        int rows = 0;
        try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement ps = connection.prepareStatement(updateFolderQuery);) {
            ps.setInt(1, folderId);
            ps.setInt(2, emailId);
            rows = ps.executeUpdate();
        }
        LOG.debug("Number of emails' folder changed (should be 1): " + rows);
        return rows;
    }

    /**
     * Finds a folder id by the given folder name
     *
     * @param name the name of the folder
     * @return the folder id
     * @throws SQLException
     */
    public int findFolderIdByName(String name) throws SQLException {
        int id = -1;
        String selectFolderQuery = "SELECT FOLDERID FROM FOLDER WHERE FOLDERNAME = ?";
        try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement pStatement = connection.prepareStatement(selectFolderQuery);) {
            pStatement.setString(1, name);
            try ( ResultSet resultSet = pStatement.executeQuery()) {
                if (resultSet.next()) {
                    id = resultSet.getInt("FolderId");
                }
            }
        }
        LOG.debug("Folder id of folder " + name + " is: " + id);
        return id;
    }

    /**
     * Updates a folder name with a given new name.
     *
     * @param toReplace the old folder name
     * @param newName the new folder name
     * @return an int that represents the number of folder names changed (should
     * be 1)
     * @throws SQLException
     * @throws com.xinjia.exceptions.FolderAlreadyExistsException
     */
    @Override
    public int updateFolderName(String toReplace, String newName) throws SQLException, FolderAlreadyExistsException {
        //No need to check if the folder to replace is one of the initial folders (sent, inbox, draft)
        //because it will be handled in the GUI (no change options for them).
        //Exception is thrown if the user tries to update a custom folder name to one of the 
        //original folder names (where the newName is sent, inbox or draft).
        if (containsIgnoreCase(findFolderNames(), newName)) {
            throw new FolderAlreadyExistsException("The folder already exists");
        }

        String updateFolderQuery = "UPDATE FOLDER SET FOLDERNAME = ? WHERE FOLDERNAME = ?";
        int rows = 0;
        try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement ps = connection.prepareStatement(updateFolderQuery);) {
            ps.setString(1, newName);
            ps.setString(2, toReplace);
            rows = ps.executeUpdate();
        }
        LOG.debug("Number of folder name changed (should be 1): " + rows);
        LOG.info("Changed folder: " + toReplace + " to: " + newName);
        return rows;
    }

    /**
     * Deletes a given folder and all emails in it (with the recipients,
     * attachments from the emails too)
     *
     * @param folderName the folder name
     * @return an int that represents the number of folders deleted (should be
     * 1)
     * @throws SQLException
     */
    @Override
    public int deleteFolder(String folderName) throws SQLException {
        int rows;
        //table declarations have CASCADE CONSTRAINT so it will delete a folder's emails 
        //and its attachments and recipients directly
        String deleteFolderQuery = "DELETE FROM FOLDER "
                + "WHERE FOLDER.FOLDERNAME = ?";

        try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement ps = connection.prepareStatement(deleteFolderQuery);) {
            ps.setString(1, folderName);
            rows = ps.executeUpdate();
        }

        LOG.debug("Number of rows deleted in folder (should be 1): " + rows);
        return rows;
    }

    /**
     * Deletes an email and its recipients and attachments
     *
     * @param id the emailId
     * @return an int that represents the number of emails deleted (should be 1)
     * @throws SQLException
     */
    @Override
    public int deleteEmail(int id) throws SQLException {
        int rows;

        //table declarations have CASCADE CONSTRAINT so it will delete an email's recipients and attachments directly
        String deleteEmailQuery = "DELETE FROM EMAIL "
                + "WHERE EMAIL.EMAILID = ?";

        try ( Connection connection = DriverManager.getConnection(url, configBean.getMysqlUser(), configBean.getMysqlPassword());  PreparedStatement ps = connection.prepareStatement(deleteEmailQuery);) {
            ps.setInt(1, id);
            rows = ps.executeUpdate();
        }

        LOG.debug("Number of rows deleted in email (should be 1): " + rows);
        return rows;
    }

}
