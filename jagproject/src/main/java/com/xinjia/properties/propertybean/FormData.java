
package com.xinjia.properties.propertybean;

import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Xin Jia Cao
 */
public class FormData {
    private StringProperty to;
    private StringProperty cc;
    private StringProperty bcc;
    private StringProperty subject;
    
    public FormData(final String to, final String cc, final String bcc, final String subject) {
        this.to = new SimpleStringProperty(to);
        this.cc = new SimpleStringProperty(cc);
        this.bcc = new SimpleStringProperty(bcc);
        this.subject = new SimpleStringProperty(subject);
    }
     
    public FormData(){
        this("", "", "", "");
    }

    public String getTo() {
        return to.get();
    }

    public void setTo(final String to) {
        this.to.set(to);
    }

    public StringProperty toProperty() {
        return to;
    }

    public String getCc() {
        return cc.get();
    }
    
    public void setCc(final String cc) {
        this.cc.set(cc);
    }

    public StringProperty ccProperty() {
        return cc;
    }

    public String getBcc() {
        return bcc.get();
    }

    public void setBcc(final String bcc) {
        this.bcc.set(bcc);
    }
    
    public StringProperty bccProperty() {
        return bcc;
    }

    public String getSubject() {
        return subject.get();
    }

    public void setSubject(final String subject) {
        this.subject.set(subject);
    }
    
    public StringProperty subjectProperty() {
        return subject;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.to);
        hash = 79 * hash + Objects.hashCode(this.cc);
        hash = 79 * hash + Objects.hashCode(this.bcc);
        hash = 79 * hash + Objects.hashCode(this.subject);
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
        final FormData other = (FormData) obj;
        if (!Objects.equals(this.to, other.to)) {
            return false;
        }
        if (!Objects.equals(this.cc, other.cc)) {
            return false;
        }
        if (!Objects.equals(this.bcc, other.bcc)) {
            return false;
        }
        if (!Objects.equals(this.subject, other.subject)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "FormData{" + "to=" + to + ", cc=" + cc + ", bcc=" + bcc + ", subject=" + subject + '}';
    }
    
    
     
}
