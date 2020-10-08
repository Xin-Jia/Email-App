package com.xinjia.jdbc.beans;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import jodd.mail.Email;

/**
 * Data bean for email Overrides toString, hashCode and equals
 *
 * @author Xin Jia Cao
 */
public class EmailData {

    private int emailId;
    private int folderId;
    private LocalDateTime receivedDate;
    public Email email;

    public EmailData(int emailId, int folderId, LocalDateTime receivedDate, Email email) {
        this.emailId = emailId;
        this.folderId = folderId;
        this.receivedDate = receivedDate;
        this.email = email;
    }

    public EmailData() {
        this.emailId = -1;
        this.folderId = -1;
        this.receivedDate = LocalDateTime.now();
        this.email = new Email();
    }

    public int getEmailId() {
        return emailId;
    }

    public void setEmailId(int id) {
        this.emailId = id;
    }

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int id) {
        this.folderId = id;
    }

    public LocalDateTime getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDateTime date) {
        this.receivedDate = date;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email mail) {
        this.email = mail;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + this.emailId;
        hash = 37 * hash + this.folderId;
        hash = 37 * hash + Objects.hashCode(this.receivedDate);
        hash = 37 * hash + Objects.hashCode(this.email.attachments());
        hash = 37 * hash + Objects.hashCode(this.email.messages());
        hash = 37 * hash + Objects.hashCode(this.email.from());
        hash = 37 * hash + Objects.hashCode(this.email.to());
        hash = 37 * hash + Objects.hashCode(this.email.cc());
        hash = 37 * hash + Objects.hashCode(this.email.bcc());
        hash = 37 * hash + Objects.hashCode(this.email.sentDate());
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
        final EmailData other = (EmailData) obj;
        if (this.emailId != other.emailId) {
            return false;
        }
        if (this.folderId != other.folderId) {
            return false;
        }
        if (!Objects.equals(this.receivedDate, other.receivedDate)) {
            return false;
        }
        if (this.email.attachments() != other.email.attachments()) {
            return false;
        }
        if (this.email.to() != other.email.to()) {
            return false;
        }
        if (this.email.cc() != other.email.cc()) {
            return false;
        }
        if (this.email.bcc() != other.email.bcc()) {
            return false;
        }
        if (this.email.messages() != other.email.messages()) {
            return false;
        }
        if (this.email.sentDate() != other.email.sentDate()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String infos = "\n" + "Email Id: " + emailId + "\n"
                + "Address: " + email.from() + "\n"
                + "Date: " + receivedDate + "\n"
                + "Subject: " + email.subject() + "\n"
                + "Message: " + email.messages().get(0).getContent() + "\n"
                + "HTML Message: " + email.messages().get(1) + "\n"
                + "Attachments: " + email.attachments().size() + "\n"
                + "To Recipients: " + Arrays.toString(email.to()) + "\n"
                + "CC Recipients: " + Arrays.toString(email.cc()) + "\n"
                + "BCC Recipients: " + Arrays.toString(email.bcc()) + "\n";

        return infos;
    }

}
