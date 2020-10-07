
package com.xinjia.properties;

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
    private String imapPort;
    private String smtpPort;
    private String dbUrl;
    private String dbName;
    private String dbPort;
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
        this.imapPort = "993";
        this.smtpPort = "465";
        this.dbUrl = "";
        this.dbName = "";
        this.dbPort = "3306";
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
    public MailConfigBean(final String host, final String userEmailAddress, 
            final String password, final String imapUrl, final String smtpUrl, final String imapPort, final String smtpPort,
            final String dbUrl, final String dbName, final String dbPort, final String dbUsername, final String dbPassword) {
        this.host = host;
        this.userEmailAddress = userEmailAddress;
        this.password = password;
        this.imapUrl = imapUrl;
        this.smtpUrl = smtpUrl;
        this.imapPort = imapPort;
        this.smtpPort = smtpPort;
        this.dbUrl = dbUrl;
        this.dbName = dbName;
        this.dbPort = dbPort;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUserEmailAddress() {
        return userEmailAddress;
    }

    public void setUserEmailAddress(String userEmailAddress) {
        this.userEmailAddress = userEmailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImapUrl() {
        return imapUrl;
    }

    public void setImapUrl(String imapUrl) {
        this.imapUrl = imapUrl;
    }

    public String getSmtpUrl() {
        return smtpUrl;
    }

    public void setSmtpUrl(String smtpUrl) {
        this.smtpUrl = smtpUrl;
    }

    public String getImapPort() {
        return imapPort;
    }

    public void setImapPort(String imapPort) {
        this.imapPort = imapPort;
    }

    public String getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(String smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbPort() {
        return dbPort;
    }

    public void setDbPort(String dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }
}

    