
package com.xinjia.data;

/**
 * Class to contain the information for an email account. This is sufficient for
 * this project but will need more fields if you wish the program to work with
 * mail systems other than GMail. This should be stored in properties file. If
 * you are feeling adventurous you can look into how you might encrypt the
 * password as it will be in a simple text file.
 *
 * @author Xin Jia Cao
 *
 */
public class MailConfigBean {

    private String host;
    private String userEmailAddress;
    private String password;
    private String imapUrl;
    private String smtpUrl;
    private int imapPort;
    private int smtpPort;
    private String dbUrl;
    private String dbName;
    private int dbPort;
    private String dbUsername;
    private String dbPassword;
        

    /**
     * Default Constructor
     */
    public MailConfigBean() {
        this.host = "";
        this.userEmailAddress = "";
        this.password = "";
        this.imapUrl = "";
        this.smtpUrl = "";
        this.imapPort = 0;
        this.smtpPort = 0;
        this.dbUrl = "";
        this.dbName = "";
        this.dbPort = 0;
        this.dbUsername = "";
        this.dbPassword = "";
        
    }

    /**
     * Non-default constructor
     * 
     * @param host
     * @param userEmailAddress
     * @param password
     */
    public MailConfigBean(final String host, final String userEmailAddress, final String password) {
        this.host = host;
        this.userEmailAddress = userEmailAddress;
        this.password = password;
    }

    /**
     * @return the host
     */
    public final String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public final void setHost(final String host) {
        this.host = host;
    }

    /**
     * @return the userEmailAddress
     */
    public final String getUserEmailAddress() {
        return userEmailAddress;
    }

    /**
     * @param userEmailAddress
     */
    public final void setUserEmailAddress(final String userEmailAddress) {
        this.userEmailAddress = userEmailAddress;
    }

    /**
     * @return the password
     */
    public final String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public final void setPassword(final String password) {
        this.password = password;
    }
    
    /**
     * @return the imap url
     */
    public final String getImapUrl() {
        return imapUrl;
    }
    
    /**
     * @return the smtp url
     */
    public final String getSmtpUrl() {
        return smtpUrl;
    }
    
    /**
     * @return the imap port
     */
    public final int getImapPort() {
        return imapPort;
    }
    /**
     * @return the smtp port
     */
    public final int getSmtpPort() {
        return smtpPort;
    }
    /**
     * @return the database url
     */
    public final String getDbUrl() {
        return dbUrl;
    }
    /**
     * @return the database name
     */
    public final String getDbName() {
        return dbName;
    }
    /**
     * @return the database port
     */
    public final int getDbPort() {
        return dbPort;
    }
    /**
     * @return the database username
     */
    public final String getDbUsername() {
        return dbUsername;
    }
    /**
     * @return the database password
     */
    public final String getDbPassword() {
        return dbPassword;
    }
}

