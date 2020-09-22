package com.xinjia.business;

import com.xinjia.data.MailConfigBean;
import java.io.File;
import java.util.ArrayList;
import javax.mail.Flags;
import jodd.mail.Email;
import jodd.mail.EmailAttachment;
import jodd.mail.EmailFilter;
import jodd.mail.ImapServer;
import jodd.mail.MailException;
import jodd.mail.MailServer;
import jodd.mail.RFC2822AddressParser;
import jodd.mail.ReceiveMailSession;
import jodd.mail.ReceivedEmail;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SendAndReceive is class used to send and receive emails using a
 * MailConfigBean that is used to represent the authentication of an email
 * account. The send bean can send emails using To, CC and BCC, and can as well
 * attach files in its emails.
 *
 * @author Xin Jia Cao
 */
public class SendAndReceive {

    private MailConfigBean mailConfBean;
    private final static Logger LOG = LoggerFactory.getLogger(SendAndReceive.class);

    /**
     * Sets the config bean property to the given input bean
     *
     * @param themail the send or receive MailConfigBean
     * @throws NullPointerException when the bean is null
     */
    public SendAndReceive(MailConfigBean themail) {
        if (themail == null) {
            LOG.warn("Null: please enter a valid send config bean");
            throw new NullPointerException();
        } else {
            mailConfBean = themail;
        }
    }

    /**
     * Creates an email based on given parameters. At least one recipient needs
     * to be given for the email to be sent.
     *
     * @param toRecipients Recipients in the To field
     * @param ccRecipients Recipients in the CC field
     * @param bccRecipients Recipients in the BCC field
     * @param subject Subject of the email (can be empty)
     * @param msg Message of the email (can be empty)
     * @param htmlMsg HTML message of the email (can be empty)
     * @param regFiles Regular files attached to the email (can be empty)
     * @param embedFiles Embedded files attached to the email (can be empty)
     * @return Email an email containing all the input fields
     * @throws MailException when the bean's email address is invalid or when
     * all recipients are empty
     */
    private Email createMail(ArrayList<String> toRecipients, ArrayList<String> ccRecipients, ArrayList<String> bccRecipients, String subject, String msg, String htmlMsg, ArrayList<File> regFiles, ArrayList<File> embedFiles) {

        Email email = null;

        if (checkEmail(mailConfBean.getUserEmailAddress())) {
            email = Email.create()
                    .from(mailConfBean.getUserEmailAddress());
        } else {
            LOG.warn("Invalid send bean: " + mailConfBean.getUserEmailAddress());
            throw new MailException("Invalid email");
        }

        if (toRecipients.isEmpty() && ccRecipients.isEmpty() && bccRecipients.isEmpty()) {
            LOG.warn("Please enter at least one recipient");
            throw new MailException("No valid recipients.");
        }

        if (!toRecipients.isEmpty()) {
            for (String mailTo : toRecipients) {
                if (mailTo != null) {
                    email.to(mailTo);
                }
            }
        }
        if (!ccRecipients.isEmpty()) {
            email.cc(ccRecipients.toArray(new String[0]));
        }
        if (!bccRecipients.isEmpty()) {
            email.bcc(bccRecipients.toArray(new String[0]));
        }

        if (!regFiles.isEmpty()) {
            for (File rfile : regFiles) {
                if (rfile != null) {
                    email.attachment(EmailAttachment.with().content(rfile));
                }
            }
        }
        if (!embedFiles.isEmpty()) {
            for (File efile : embedFiles) {
                if (efile != null) {
                    email.embeddedAttachment(EmailAttachment.with().content(efile));
                }
            }
        }
        email.subject(subject)
                .textMessage(msg)
                .htmlMessage(htmlMsg);

        return email;

    }

    /**
     * Sends an email to its recipient(s). Opens an smtp server and creates a
     * session to send the email created. If at least one of the given inputs is
     * null or one of the recipients has an invalid email address, an exception
     * is thrown.
     *
     * @param toRecipients Recipients in the To field
     * @param ccRecipients Recipients in the CC field
     * @param bccRecipients Recipients in the BCC field
     * @param subject Subject of the email (can be empty)
     * @param msg Message of the email (can be empty)
     * @param htmlMsg HTML message of the email (can be empty)
     * @param regFiles Regular files attached to the email (can be empty)
     * @param embedFiles Embedded files attached to the email (can be empty)
     * @return Email an email containing all the input fields
     * @throws NullPointerException when at least one parameter is null. Every
     * field needs to be valid in order to send the email.
     * @throws MailException when one of the recipients' email address is
     * invalid
     */
    public Email sendMail(ArrayList<String> toRecipients, ArrayList<String> ccRecipients, ArrayList<String> bccRecipients, String subject, String msg, String htmlMsg, ArrayList<File> regFiles, ArrayList<File> embedFiles) {

        Email email = null;
        if (toRecipients == null || ccRecipients == null || bccRecipients == null || subject == null || msg == null || htmlMsg == null || regFiles == null || embedFiles == null) {
            LOG.warn("Null: one or more parameters are null");
            throw new NullPointerException();
        }
        if (checkEmails(toRecipients) && checkEmails(ccRecipients) && checkEmails(bccRecipients)) {
            SmtpServer smtpServer = MailServer.create()
                    .ssl(true)
                    .host(mailConfBean.getHost())
                    .auth(mailConfBean.getUserEmailAddress(), mailConfBean.getPassword())
                    //.debugMode(true)
                    .buildSmtpMailServer();

            email = createMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);

            try ( // A session is the object responsible for communicating with the server
                     SendMailSession session = smtpServer.createSession()) {
                // Like a file we open the session, send the message and close the
                // session
                session.open();
                session.sendMail(email);
                LOG.info("Email sent");
            }
        } else {
            LOG.warn("One or more send addresses are invalid.");
            throw new MailException("Invalid email");
        }

        return email;

    }

    /**
     * Opens an imap server to retrieve the list of unseen emails. Marks the
     * emails as seen when they are returned.
     *
     * @param mail the receive config bean
     * @return ReceivedEmail[] the list of emails that are unseen
     * @throws NullPointerException when the receive config bean is null.
     * @throws MailException when the bean's email address is invalid
     */
    public ReceivedEmail[] receiveMail(MailConfigBean mail) {

        ReceivedEmail[] emails = null;
        if (mail == null) {
            LOG.warn("Null: please enter a valid receive config bean");
            throw new NullPointerException();
        } else {
            if (checkEmail(mail.getUserEmailAddress())) {

                // Create an IMAP server that does not display debug info
                ImapServer imapServer = MailServer.create()
                        .host(mail.getHost())
                        .ssl(true)
                        .auth(mail.getUserEmailAddress(), mail.getPassword())
                        .buildImapMailServer();

                try ( ReceiveMailSession session = imapServer.createSession()) {
                    session.open();
                    LOG.info("Message count: " + session.getMessageCount());

                    emails = session.receiveEmailAndMarkSeen(EmailFilter.filter().flag(Flags.Flag.SEEN, false));
                } catch (Exception ex) {
                    LOG.debug("Failure in receive session", ex);

                }
            } else {
                LOG.warn("Invalid receive email address");
                throw new MailException("Invalid email");
            }
        }
        return emails;
    }

    /**
     * Checks if a given email address is valid regarding its format.
     *
     * @param address the email address to be checked
     * @return boolean : true if the address is valid and false otherwise
     * @throws NullPointerException when the address is null. 
     *         (This helper is used only to check the send and receive config bean, so they must not be null)
     */
    private boolean checkEmail(String address) {
        if (address == null) {
            LOG.warn("Null: please enter a valid email address");
            throw new NullPointerException();
        }
        return RFC2822AddressParser.STRICT.parseToEmailAddress(address) != null;

    }

    /**
     * Checks if a given email address is valid in a list of addresses regarding
     * its format. If an address is null, an exception is thrown.
     *
     * @param addresses the list of email addresses to be checked
     * @throws NullPointerException if an email is null
     * @return boolean : true if the address is valid and false otherwise
     */
    private boolean checkEmails(ArrayList<String> addresses) {
            
        for (String address : addresses) {
            if (address == null) {
                LOG.warn("Null: Please enter a valid email address");
                throw new NullPointerException();
            }
            if (!(RFC2822AddressParser.STRICT.parseToEmailAddress(address) != null)) {
                return false;
            }
        }
        return true;
    }
}
