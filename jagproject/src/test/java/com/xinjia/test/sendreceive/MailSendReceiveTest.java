package com.xinjia.test.sendreceive;

import com.xinjia.business.SendAndReceive;
import com.xinjia.exceptions.NullBCCEmailAddressException;
import com.xinjia.exceptions.NullBCCEmailException;
import com.xinjia.exceptions.NullCCEmailAddressException;
import com.xinjia.exceptions.NullCCEmailException;
import com.xinjia.exceptions.NullToEmailAddressException;
import com.xinjia.exceptions.NullToEmailException;
import com.xinjia.properties.MailConfigBean;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jodd.mail.Email;
import jodd.mail.EmailMessage;
import jodd.mail.MailException;
import jodd.mail.ReceivedEmail;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;

/**
 * Unit test for sending and receiving emails.
 * Note : I could not test the bcc field for receive. 
 * I could not see the bcc address in the To field of ReceivedEmail as you said it would.
 * The only address I can see from the bcc address is the recipients in the 
 * To field excluding bcc and the address of the sender.
 * @author Xin Jia Cao
 */
@Ignore
public class MailSendReceiveTest {

    private final Logger log = LoggerFactory.getLogger(getClass().getName());
    private MailConfigBean sendConfigBean;
    private MailConfigBean receiveConfigBean;
    private SendAndReceive mailFunction;
    private ArrayList<String> toRecipients;
    private ArrayList<String> ccRecipients;
    private ArrayList<String> bccRecipients;
    private String subject;
    private String msg;
    private String htmlMsg;
    private ArrayList<File> regFiles;
    private ArrayList<File> embedFiles;

    //Initialize all the fields required in an email before every unit test.
    @Before
    public void initializeFields() {
        sendConfigBean = new MailConfigBean("", "xinjia.caoxin@gmail.com", "39499799", "", "smtp.gmail.com", "", "", "", "", "", "", "");
        receiveConfigBean = new MailConfigBean("", "xinjia1.cao@gmail.com", "3949979a", "imap.gmail.com", "", "", "", "", "", "", "", "");
        mailFunction = new SendAndReceive(sendConfigBean);
        toRecipients = new ArrayList<>();
        ccRecipients = new ArrayList<>();
        bccRecipients = new ArrayList<>();
        subject = "A test subject";
        msg = "This is the message of the test";
        htmlMsg = " ";
        regFiles = new ArrayList<>();
        embedFiles = new ArrayList<>();
    }

    //Checks if the email sent's sender is the same as the address in the send config bean
    @Test
    public void checkSenderFromBean() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check If Sender Is The Send Bean---------------");
        
        toRecipients.add("xinjia1.cao@gmail.com");
        subject = "checkSenderFromBean";
        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);
        
        assertEquals(sendConfigBean.getEmailAddress(), emailSend.from().toString());
    }

    //Checks if the number of 'To' recipients of the email sent is the same as the number of recipients added
    @Test
    public void checkSendToRecipients() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check Send 'To' Recipients---------------");
        
        toRecipients.add("xinjia1.cao@gmail.com");
        toRecipients.add("xinjia2.cao@gmail.com");
        toRecipients.add("xinjia3.cao@gmail.com");
        subject = "checkSendToRecipients";
        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);
        log.debug("Expected 'To' mails: "+toRecipients.toString());
        log.debug("Sent 'To' mails: " +Arrays.toString(emailSend.to()));
    
        assertEquals(toRecipients.toString(), Arrays.toString(emailSend.to()));
    }

    //Checks if the number of 'CC' recipients of the email sent is the same as the number of recipients added
    @Test
    public void checkSendCCRecipient() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check Send CC Recipients---------------");
        
        ccRecipients.add("xinjia1.cao@gmail.com");
        ccRecipients.add("xinjia2.cao@gmail.com");
        subject = "checkSendCCRecipient";
        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);
        log.debug("Expected 'CC' mails: "+ccRecipients.toString());
        log.debug("Sent 'CC' mails: " +Arrays.toString(emailSend.cc()));
        
        assertEquals(ccRecipients.toString(), Arrays.toString(emailSend.cc()));
    }

    //Checks if the number of 'BCC' recipients of the email sent is the same as the number of recipients added
    @Test
    public void checkSendBCCRecipients() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check Send BCC Recipients---------------");

        bccRecipients.add("xinjia1.cao@gmail.com");
        bccRecipients.add("xinjia2.cao@gmail.com");
        subject = "checkSendBCCRecipients";
        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);
        log.debug("Expected 'BCC' mails: "+bccRecipients.toString());
        log.debug("Sent 'BCC' mails: " +Arrays.toString(emailSend.bcc()));
        
        assertEquals(bccRecipients.toString(), Arrays.toString(emailSend.bcc()));
    }

    //Checks if the sent email's subject is the one received as input
    @Test
    public void checkSendSubject() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check Send Subject---------------");
        
        toRecipients.add("xinjia1.cao@gmail.com");
        subject = "checkSendSubject";
        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);
        log.debug("Sent subject: " + emailSend.subject());
        log.debug("Expected subject: " + subject);
        
        assertEquals(subject, emailSend.subject());
    }

    //Checks if the sent email's message is the one received as input
    @Test
    public void checkSendMsg() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check Send Message---------------");

        toRecipients.add("xinjia1.cao@gmail.com");
        subject = "checkSendMsg";
        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);
        List<EmailMessage> sentMessages = emailSend.messages();
        ArrayList<String> msgSent = retrieveMessageContent(sentMessages, "text/plain");
        log.debug("Sent Message: " + msgSent.get(0));
        log.debug("Expected message: " + msg);
        
        assertEquals(msg, msgSent.get(0));
    }

    //Checks if the sent email's HTML message is the one received as input
    @Test
    public void checkSendHtmlMsg() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check Send HTML Message---------------");

        toRecipients.add("xinjia1.cao@gmail.com");
        subject = "checkSendHtmlMsg";
        htmlMsg = "<html><META http-equiv=Content-Type "
                + "content=\"text/html; charset=utf-8\">"
                + "<body><h1>HTML Message</h1>"
                + "<h2>Here is some text in the HTML message</h2></body></html>";
        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);
        List<EmailMessage> sentMessages = emailSend.messages();
        ArrayList<String> msgSent = retrieveMessageContent(sentMessages, "text/html");
        log.debug("Sent HTML message: " + msgSent.get(0));
        log.debug("Expected HTML message: " + htmlMsg);
        
        assertEquals(htmlMsg, msgSent.get(0));
    }

    //Checks if the sent email's number of regular an embedded files is the same as the one received as input
    @Test
    public void checkSendRegAndEmbeddedFilesCount() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check Number Of Send Regular and Embedded Files---------------");

        toRecipients.add("xinjia1.cao@gmail.com");
        subject = "checkSendRegAndEmbeddedFiles";
        File img1 = new File("img1.png");
        File img2 = new File("img2.png");
        File img3 = new File("img3.png");
        regFiles.add(img1);
        regFiles.add(img2);
        embedFiles.add(img3);
        htmlMsg = "<html><body><img src='cid:img3.png'></body></html>";

        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);
        log.debug("Sent files: " + emailSend.attachments().size());
        log.info("Expected files: 3");
       
        assertEquals(3, emailSend.attachments().size());
    }
    
    //Sends 5 emails to one recipient and checks if all 5 emails have been received
    @Test
    public void checkIfAllMailsReceived() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check If All Mails Received From One Account---------------");
        
        toRecipients.add("xinjia1.cao@gmail.com");
        subject = "checkIfAllMailsReceived";
        for (int i = 0; i < 5; i++) {
            Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);
        }
        
        sleep();
        
        ReceivedEmail[] receivedMails = mailFunction.receiveMail(receiveConfigBean);
        log.debug(receivedMails.length + " mails received");
        
        assertEquals(5, receivedMails.length);
    }

    //Checks if the sender is the one who sent the email by looking at the sender from the received email
    @Test
    public void checkMailReceivedFromRightSender() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check If Mails Are Received From the Right Sender---------------");
        
        toRecipients.add("xinjia1.cao@gmail.com");
        subject = "checkMailReceivedFromRightSender";
        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);

        sleep();
        
        ReceivedEmail[] receivedMails = mailFunction.receiveMail(receiveConfigBean);
        log.debug("From email: " + emailSend.from());
        log.debug("Email Sent From Receiver: " + receivedMails[receivedMails.length-1].from()); 
        
        assertEquals(emailSend.from().toString(), receivedMails[receivedMails.length-1].from().toString());
    }
    
    //Checks if all 'To' recipients received the email sent
    @Test
    public void checkReceivedToRecipients() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check Received 'To' Recipients---------------");
        
        toRecipients.add("xinjia1.cao@gmail.com");
        toRecipients.add("xinjia2.cao@gmail.com");
        subject = "checkReceivedToRecipients";
        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);

        sleep();
       
        ReceivedEmail[] receivedMails = mailFunction.receiveMail(receiveConfigBean);
        log.debug("Expected 'To' mails: "+Arrays.toString(emailSend.to()));
        log.debug("Sent 'To' mails: " +Arrays.toString(receivedMails[receivedMails.length-1].to()));
        assertEquals(Arrays.toString(emailSend.to()), Arrays.toString(receivedMails[receivedMails.length-1].to()));
    }

    //Checks if all 'CC' recipients received the email sent
    @Test
    public void checkReceivedCCRecipients() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check Received CC Recipients---------------");

        ccRecipients.add("xinjia1.cao@gmail.com");
        ccRecipients.add("xinjia2.cao@gmail.com");
        subject = "checkReceivedCCRecipients";
        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);
        
        sleep();
        
        ReceivedEmail[] receivedMails = mailFunction.receiveMail(receiveConfigBean);
        log.debug("Expected 'CC' mails: "+Arrays.toString(emailSend.cc()));
        log.debug("Sent 'CC' mails: " +Arrays.toString(receivedMails[receivedMails.length-1].cc()));
        assertEquals(Arrays.toString(emailSend.cc()), Arrays.toString(receivedMails[receivedMails.length-1].cc()));
    }

    //Checks if the sent email's subject is the same as the one received
    @Test
    public void checkReceivedSubject() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check Received Subject---------------");
        
        toRecipients.add("xinjia1.cao@gmail.com");
        subject = "checkReceivedSubject";
        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);

        sleep();
        
        ReceivedEmail[] receivedMails = mailFunction.receiveMail(receiveConfigBean);
        String subjectReceived = receivedMails[receivedMails.length-1].subject();
        log.debug("subject sent: " + emailSend.subject());
        log.debug("subject received: " + subjectReceived);
        
        assertEquals(emailSend.subject(), subjectReceived);
    }

    //Checks if the sent email's plain message is the same as the one received
    @Test
    public void checkReceivedPlainMsg() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check Received Plain Messages---------------");
        
        toRecipients.add("xinjia1.cao@gmail.com");
        subject = "checkReceivedPlainMsg";
        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);
        List<EmailMessage> sentMessages = emailSend.messages();
        ArrayList<String> msgSent = retrieveMessageContent(sentMessages, "text/plain"); //calls a helper method that returns the message sent

        sleep();
        
        ReceivedEmail[] receivedMails = mailFunction.receiveMail(receiveConfigBean);
        List<EmailMessage> receivedMessages = receivedMails[receivedMails.length-1].messages();
        ArrayList<String> msgReceived = retrieveMessageContent(receivedMessages, "text/plain");
        
        assertEquals(msgSent.get(0), msgReceived.get(0));
    }

    //Checks if the sent email's HTML message is the same as the one received
    @Test
    public void checkReceivedHtmlMsg() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check Received HTML messages---------------");
        
        toRecipients.add("xinjia1.cao@gmail.com");
        subject = "checkReceivedHtmlMsg";
        htmlMsg = "<html><META http-equiv=Content-Type "
                + "content=\"text/html; charset=utf-8\">"
                + "<body><h1>HTML Message</h1>"
                + "<h2>Here is some text in the HTML message</h2></body></html>";

        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);
        List<EmailMessage> sentMessages = emailSend.messages();
        ArrayList<String> msgSent = retrieveMessageContent(sentMessages, "text/html");

        sleep();
        
        ReceivedEmail[] receivedMails = mailFunction.receiveMail(receiveConfigBean);
        List<EmailMessage> receivedMessages = receivedMails[receivedMails.length-1].messages();
        ArrayList<String> msgReceived = retrieveMessageContent(receivedMessages, "text/html");
        
        assertEquals(msgSent.get(0), msgReceived.get(0));
    }

    //Checks if the sent email's attached regular file's name is the same as the one received
    @Test
    public void checkReceivedRegularFileName() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check Received Regular File Name---------------");
        
        toRecipients.add("xinjia1.cao@gmail.com");
        subject = "checkReceivedRegularFileName";
        File img1 = new File("img1.png");
        regFiles.add(img1);
        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);
        
        sleep();
        
        ReceivedEmail[] receivedMails = mailFunction.receiveMail(receiveConfigBean);
        log.debug("expected file name: " + emailSend.attachments().get(0).getEncodedName());
        log.debug("received file name: " + receivedMails[receivedMails.length-1].attachments().get(0).getEncodedName());

        assertEquals(emailSend.attachments().get(0).getEncodedName(), receivedMails[receivedMails.length-1].attachments().get(0).getEncodedName());

    }
    
    //Checks if the sent email's number of attached regular files is the same as the one received
    @Test
    public void checkReceivedRegularFilesCount() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check Number Of Received Regular Files---------------");
        
        toRecipients.add("xinjia1.cao@gmail.com");
        subject = "checkReceivedRegularFilesCount";
        File img1 = new File("img1.png");
        File img2 = new File("img2.png");
        regFiles.add(img1);
        regFiles.add(img2);
        
        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);

        sleep();
        
        int sentSize = emailSend.attachments().size();
        ReceivedEmail[] receivedMails = mailFunction.receiveMail(receiveConfigBean);
        int receivedSize = receivedMails[receivedMails.length-1].attachments().size();
        log.debug("sent files: " + sentSize);
        log.debug("received files: " + receivedSize);
        
        assertEquals(sentSize, receivedSize);

    }
    
    //Checks if the sent email's attached embedded file's name is the same as the one received
    @Test
    public void checkReceivedEmbeddedFileName() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check Received Embedded File Name---------------");
        
        toRecipients.add("xinjia1.cao@gmail.com");
        subject = "checkReceivedEmbeddedFileName";
        File img1 = new File("img1.png");
        embedFiles.add(img1);
        htmlMsg = "<html><body><img src='cid:img1.png'></body></html>";
        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);
        
        sleep();
        
        ReceivedEmail[] receivedMails = mailFunction.receiveMail(receiveConfigBean);
 
        log.debug("expected file name: " + emailSend.attachments().get(0).getEncodedName());
        log.debug("received file name: " + receivedMails[receivedMails.length-1].attachments().get(0).getEncodedName());

        assertEquals(emailSend.attachments().get(0).getEncodedName(), receivedMails[receivedMails.length-1].attachments().get(0).getEncodedName());
    }

    //Checks if the sent email's number of attached regular and embedded files is the same as the one received
    @Test
    public void checkReceivedRegularAndEmbeddedFilesCount() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check Number Of Received Embedded And Regular Files---------------");
        
        toRecipients.add("xinjia1.cao@gmail.com");
        subject = "checkReceivedRegularAndEmbeddedFilesCount";
        File img1 = new File("img1.png");
        File img2 = new File("img2.png");
        File img3 = new File("img3.png");
        regFiles.add(img1);
        embedFiles.add(img2);
        embedFiles.add(img3);
        htmlMsg = "<html><body><img src='cid:img2.png'><img src='cid:img3.png'></body></html>";
        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);
        
        sleep();
        
        int sentSize = emailSend.attachments().size();
        ReceivedEmail[] receivedMails = mailFunction.receiveMail(receiveConfigBean);
        int receivedSize = receivedMails[receivedMails.length-1].attachments().size();
        log.debug("sent files: " + sentSize);
        log.debug("received files: " + receivedSize);
        
        assertEquals(sentSize, receivedSize);

    }

    //Throws a NullPointerException when the send config bean is null
    //The user gets a warning and the email is not sent.
    @Test(expected = NullPointerException.class)
    public void checkNullSendBean() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check Null Send Bean---------------");
        sendConfigBean = null;
        mailFunction = new SendAndReceive(sendConfigBean);
        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);
    }
    
    //Throws a NullPointerException when the send config bean's address is null
    //The user gets a warning and the email is not sent.
    @Test(expected = NullPointerException.class)
    public void checkNullSendBeanAddress() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check Null Send Bean Address---------------");
        sendConfigBean.setEmailAddress(null);
        mailFunction = new SendAndReceive(sendConfigBean);
        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);

    }

    //Throws a NullPointerException when the receive config bean is null
    @Test(expected = NullPointerException.class)
    public void checkNullReceiveBean() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check Null Receive Bean---------------");
        receiveConfigBean = null;
        toRecipients.add("xinjia1.cao@gmail.com");
        subject = "checkNullReceiveBean";
        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);
        sleep();
        ReceivedEmail[] receivedMails = mailFunction.receiveMail(receiveConfigBean);
    }
    
    //Throws a NullPointerException when the receive config bean's address is null
    @Test(expected = NullPointerException.class)
    public void checkNullReceiveBeanAddress() {
        log.info("---------------Check Null Receive Bean Address---------------");
        receiveConfigBean.setEmailAddress(null);
        ReceivedEmail[] receivedMails = mailFunction.receiveMail(receiveConfigBean);
    }

    //Throws a NullPointerException when a list of recipient is null.
    //The user gets a warning and the email is not sent.
    @Test(expected = NullCCEmailException.class)
    public void checkNullCCList() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check Null CC List---------------");
        ccRecipients = null;
        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);
    }

    //Throws a NullPointerException if one of the recipient addresses is null.
    //The user gets a warning and the email is not sent.
    @Test(expected = NullToEmailAddressException.class)
    public void checkNullToRecipients() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check Null 'To' Recipient---------------");
        
        toRecipients.add("xinjia1.cao@gmail.com");
        toRecipients.add(null);
        toRecipients.add("xinjia2.cao@gmail.com");
        subject = "checkNullToRecipients";
        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);
    }


    //Throws a MailException when the requested file cannot be found.
    //The user gets a warning, output the path and the email is not sent.
    @Test(expected = MailException.class)
    public void checkNonExistentFiles() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {

        log.info("---------------Check Non-Existent Files---------------");
        toRecipients.add("xinjia1.cao@gmail.com");
        subject = "checkNonExistentFiles";
        File img1 = new File("dsfvsddsc.png");
        log.warn("Can't find file: " + img1.getPath());
        regFiles.add(img1);

        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);

    }

    //Throws a MailException when there is no recipient.
    //The user gets a warning and the email is not sent.
    @Test(expected = MailException.class)
    public void checkNoRecipients() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check No Recipients---------------");
        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);

    }

    //Throws a MailException when a recipient's email address is invalid.
    //The user gets a warning and the email is not sent.
    @Test(expected = MailException.class)
    public void checkInvalidEmail() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check Invalid Email---------------");
        toRecipients.add("invalidemail");
        subject = "checkInvalidEmail";
        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);

    }

    //Throws a MailException when the send config bean's address is invalid
    //The user gets a warning and the email is not sent.
    @Test(expected = MailException.class)
    public void checkInvalidSendBeanAddress() throws NullToEmailException, NullToEmailAddressException, NullCCEmailException, NullCCEmailAddressException, NullBCCEmailException, NullBCCEmailAddressException {
        log.info("---------------Check Invalid Send Bean Address---------------");
        sendConfigBean.setEmailAddress("invalidemail");
        mailFunction = new SendAndReceive(sendConfigBean);
        toRecipients.add("xinjia1.cao@gmail.com");

        Email emailSend = mailFunction.sendMail(toRecipients, ccRecipients, bccRecipients, subject, msg, htmlMsg, regFiles, embedFiles);
    }

    //Throws a MailException when the receive config bean's address is invalid
    @Test(expected = MailException.class)
    public void checkInvalidReceiveBeanAddress() {
        log.info("---------------Check Invalid Receive Bean Address---------------");
        receiveConfigBean.setEmailAddress("invalidemail");

        ReceivedEmail[] receivedMails = mailFunction.receiveMail(receiveConfigBean);
    }
    
    /**
     * Gets the message in an email based on its format. Ex: get the message sent that is in html format.
     * @param messages the messages in the email to be processed
     * @param format the format we need to check (plain text or html)
     * @return the message in the email (we would only get the first position of the list)
     */
    private ArrayList<String> retrieveMessageContent(List<EmailMessage> messages, String format) {
        //it uses an ArrayList since members in a lambda cannot be changed
        ArrayList<String> msgToReturn = new ArrayList<>();
        messages.stream().map((mesg) -> {
            log.info("------");
            return mesg;
        }).map((mesg) -> {
            return mesg;
        }).map((mesg) -> {
            log.info(mesg.getMimeType());
            return mesg;
        }).forEachOrdered((mesg) -> {
            if (mesg.getMimeType().equalsIgnoreCase(format)) {
                log.debug("The message sent: " + mesg.getContent());
                log.debug("The message sent type: " + mesg.getMimeType());
                msgToReturn.add(mesg.getContent());
            }

        });
        return msgToReturn;
    }

    //Helper method that pauses the program for 3 seconds between sending and receiving.
    private void sleep() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            log.error("Threaded sleep failed", e);
        }
    }
}
