
package com.xinjia.properties.propertybean.propertiesmanager;
import com.xinjia.properties.propertybean.MailConfigPropertyBean;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;
import java.nio.file.Path;
import static java.nio.file.Paths.get;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that manages the properties of a MailConfigPropertyBean
 * Read the mail config properties file to set the propertyBean's properties
 * Write to the mail config properties file to save the properties
 *
 * @author Xin Jia Cao
 */
public class MailConfigPropertiesManager {

    private final static Logger LOG = LoggerFactory.getLogger(MailConfigPropertiesManager.class);
    
    /**
     * Updates a PropertBean object with the contents of the properties file
     *
     * @param propertyBean
     * @param path
     * @param propFileName
     * @return
     * @throws java.io.IOException
     */
    public final boolean loadTextProperties(final MailConfigPropertyBean propertyBean, final String path, final String propFileName) throws IOException {

        boolean found = false;
        Properties prop = new Properties();

        Path txtFile = get(path, propFileName + ".properties");

        // File must exist
        if (Files.exists(txtFile)) {
            try ( InputStream propFileStream = newInputStream(txtFile);) {
                prop.load(propFileStream);
            }

            propertyBean.setUserName(prop.getProperty("userName"));
            propertyBean.setEmailAddress(prop.getProperty("emailAddress"));
            propertyBean.setMailPassword(prop.getProperty("mailPassword"));
            propertyBean.setImapURL(prop.getProperty("imapURL"));
            propertyBean.setSmtpURL(prop.getProperty("smtpURL"));
            propertyBean.setImapPort(prop.getProperty("imapPort"));
            propertyBean.setSmtpPort(prop.getProperty("smtpPort"));
            propertyBean.setMysqlURL(prop.getProperty("mysqlURL"));
            propertyBean.setMysqlDatabase(prop.getProperty("mysqlDatabase"));
            propertyBean.setMysqlPort(prop.getProperty("mysqlPort"));
            propertyBean.setMysqlUser(prop.getProperty("mysqlUser"));
            propertyBean.setMysqlPassword(prop.getProperty("mysqlPassword"));

            found = true;
        }
        LOG.info("Finished reading MailConfig properties file");
        return found;
    }

    /**
     * Creates a plain text properties file based on the parameters
     *
     * @param path Must exist, will not be created
     * @param propFileName Name of the properties file
     * @param propertyBean The bean to store into the properties
     * @throws IOException
     */
    public final void writeTextProperties(final String path, final String propFileName, final MailConfigPropertyBean propertyBean) throws IOException {

        Properties prop = new Properties();

        prop.setProperty("userName", propertyBean.getUserName());
        prop.setProperty("emailAddress", propertyBean.getEmailAddress());
        prop.setProperty("mailPassword", propertyBean.getMailPassword());
        prop.setProperty("imapURL", propertyBean.getImapURL());
        prop.setProperty("smtpURL", propertyBean.getSmtpURL());
        prop.setProperty("imapPort", propertyBean.getImapPort());
        prop.setProperty("smtpPort", propertyBean.getSmtpPort());
        prop.setProperty("mysqlURL", propertyBean.getMysqlURL());
        prop.setProperty("mysqlDatabase", propertyBean.getMysqlDatabase());
        prop.setProperty("mysqlPort", propertyBean.getMysqlPort());
        prop.setProperty("mysqlUser", propertyBean.getMysqlUser());
        prop.setProperty("mysqlPassword", propertyBean.getMysqlPassword());

        Path txtFile = get(path, propFileName + ".properties");

        // Creates the file or if file exists it is truncated to length of zero
        // before writing
        try ( OutputStream propFileStream = newOutputStream(txtFile)) {
            prop.store(propFileStream, "SMTP Properties");
        }
      LOG.info("Finished writing to MailConfig properties file");
    }
}
