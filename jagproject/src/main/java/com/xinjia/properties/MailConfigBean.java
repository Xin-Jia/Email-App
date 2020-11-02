
package com.xinjia.properties;

import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * JavaFX Bean for JAG/Mail config Properties
 * Overrides toString, hashCode and equals
 * 
 * @author Xin Jia Cao
 */

public class MailConfigBean {

    private StringProperty userName;
    private StringProperty emailAddress;
    private StringProperty mailPassword;
    private StringProperty imapURL;
    private StringProperty smtpURL;
    private StringProperty imapPort;
    private StringProperty smtpPort;
    private StringProperty mysqlURL;
    private StringProperty mysqlDatabase;
    private StringProperty mysqlPort;
    private StringProperty mysqlUser;
    private StringProperty mysqlPassword;

    /**
     * Non-default constructor that initializes all the fields
     * @param userName
     * @param emailAddress
     * @param mailPassword
     * @param imapURL
     * @param smtpURL
     * @param imapPort
     * @param smtpPort
     * @param mysqlURL
     * @param mysqlDatabase
     * @param mysqlPort
     * @param mysqlUser
     * @param mysqlPassword 
     */
    public MailConfigBean(String userName, String emailAddress, String mailPassword,
            String imapURL, String smtpURL, String imapPort,
            String smtpPort, String mysqlURL, String mysqlDatabase,
            String mysqlPort, String mysqlUser, String mysqlPassword) {
        this.userName = new SimpleStringProperty(userName);
        this.emailAddress = new SimpleStringProperty(emailAddress);
        this.mailPassword = new SimpleStringProperty(mailPassword);
        this.imapURL = new SimpleStringProperty(imapURL);
        this.smtpURL = new SimpleStringProperty(smtpURL);
        this.imapPort = new SimpleStringProperty(imapPort);
        this.smtpPort = new SimpleStringProperty(smtpPort);
        this.mysqlURL = new SimpleStringProperty(mysqlURL);
        this.mysqlDatabase = new SimpleStringProperty(mysqlDatabase);
        this.mysqlPort = new SimpleStringProperty(mysqlPort);
        this.mysqlUser = new SimpleStringProperty(mysqlUser);
        this.mysqlPassword = new SimpleStringProperty(mysqlPassword);
    }

    /**
     * Default constructor
     */
    public MailConfigBean() {
        this("", "", "", "", "", "993", "465", "", "", "3306", "", "");
    }

    public String getUserName() {
        return userName.get();
    }

    public void setUserName(String userName) {
        this.userName.set(userName);
    }

    public StringProperty userNameProperty() {
        return userName;
    }

    public String getEmailAddress() {
        return emailAddress.get();
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress.set(emailAddress);
    }

    public StringProperty emailAddressProperty() {
        return emailAddress;
    }

    public String getMailPassword() {
        return mailPassword.get();
    }

    public void setMailPassword(String mailPassword) {
        this.mailPassword.set(mailPassword);
    }

    public StringProperty mailPasswordProperty() {
        return mailPassword;
    }

    public String getImapURL() {
        return imapURL.get();
    }

    public void setImapURL(String imapURL) {
        this.imapURL.set(imapURL);
    }

    public StringProperty imapURLProperty() {
        return imapURL;
    }

    public String getSmtpURL() {
        return smtpURL.get();
    }

    public void setSmtpURL(String smtpURL) {
        this.smtpURL.set(smtpURL);
    }

    public StringProperty smtpURLProperty() {
        return smtpURL;
    }

    public String getImapPort() {
        return imapPort.get();
    }

    public void setImapPort(String imapPort) {
        this.imapPort.set(imapPort);
    }

    public StringProperty imapPortProperty() {
        return imapPort;
    }

    public String getSmtpPort() {
        return smtpPort.get();
    }

    public void setSmtpPort(String smtpPort) {
        this.smtpPort.set(smtpPort);
    }

    public StringProperty smtpPortProperty() {
        return smtpPort;
    }

    public String getMysqlURL() {
        return mysqlURL.get();
    }

    public void setMysqlURL(String mysqlURL) {
        this.mysqlURL.set(mysqlURL);
    }

    public StringProperty mysqlURLProperty() {
        return mysqlURL;
    }

    public String getMysqlDatabase() {
        return mysqlDatabase.get();
    }

    public void setMysqlDatabase(String mysqlDatabase) {
        this.mysqlDatabase.set(mysqlDatabase);
    }

    public StringProperty mysqlDatabaseProperty() {
        return mysqlDatabase;
    }

    public String getMysqlPort() {
        return mysqlPort.get();
    }

    public void setMysqlPort(String mysqlPort) {
        this.mysqlPort.set(mysqlPort);
    }

    public StringProperty mysqlPortProperty() {
        return mysqlPort;
    }

    public String getMysqlUser() {
        return mysqlUser.get();
    }

    public void setMysqlUser(String mysqlUser) {
        this.mysqlUser.set(mysqlUser);
    }

    public StringProperty mysqlUserProperty() {
        return mysqlUser;
    }

    public String getMysqlPassword() {
        return mysqlPassword.get();
    }

    public void setMysqlPassword(String mysqlPassword) {
        this.mysqlPassword.set(mysqlPassword);
    }

    public StringProperty mysqlPasswordProperty() {
        return mysqlPassword;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.userName);
        hash = 29 * hash + Objects.hashCode(this.emailAddress);
        hash = 29 * hash + Objects.hashCode(this.mailPassword);
        hash = 29 * hash + Objects.hashCode(this.imapURL);
        hash = 29 * hash + Objects.hashCode(this.smtpURL);
        hash = 29 * hash + Objects.hashCode(this.imapPort);
        hash = 29 * hash + Objects.hashCode(this.smtpPort);
        hash = 29 * hash + Objects.hashCode(this.mysqlURL);
        hash = 29 * hash + Objects.hashCode(this.mysqlDatabase);
        hash = 29 * hash + Objects.hashCode(this.mysqlPort);
        hash = 29 * hash + Objects.hashCode(this.mysqlUser);
        hash = 29 * hash + Objects.hashCode(this.mysqlPassword);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MailConfigBean other = (MailConfigBean) obj;
        if (!Objects.equals(this.userName, other.userName)) {
            return false;
        }
        if (!Objects.equals(this.emailAddress, other.emailAddress)) {
            return false;
        }
        if (!Objects.equals(this.mailPassword, other.mailPassword)) {
            return false;
        }
        if (!Objects.equals(this.imapURL, other.imapURL)) {
            return false;
        }
        if (!Objects.equals(this.smtpURL, other.smtpURL)) {
            return false;
        }
        if (!Objects.equals(this.imapPort, other.imapPort)) {
            return false;
        }
        if (!Objects.equals(this.smtpPort, other.smtpPort)) {
            return false;
        }
        if (!Objects.equals(this.mysqlURL, other.mysqlURL)) {
            return false;
        }
        if (!Objects.equals(this.mysqlDatabase, other.mysqlDatabase)) {
            return false;
        }
        if (!Objects.equals(this.mysqlPort, other.mysqlPort)) {
            return false;
        }
        if (!Objects.equals(this.mysqlUser, other.mysqlUser)) {
            return false;
        }
        if (!Objects.equals(this.mysqlPassword, other.mysqlPassword)) {
            return false;
        }
        return true;
    }

    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PropertyBean{userName=").append(userName);
        sb.append(", emailAddress=").append(emailAddress);
        sb.append(", mailPassword=").append(mailPassword);
        sb.append(", imapURL=").append(imapURL);
        sb.append(", smtpURL=").append(smtpURL);
        sb.append(", imapPort=").append(imapPort);
        sb.append(", smtpPort=").append(smtpPort);
        sb.append(", mysqlURL=").append(mysqlURL);
        sb.append(", mysqlDatabase=").append(mysqlDatabase);
        sb.append(", mysqlPort=").append(mysqlPort);
        sb.append(", mysqlUser=").append(mysqlUser);
        sb.append(", mysqlPassword=").append(mysqlPassword);
        sb.append('}');
        return sb.toString();
    }
}

