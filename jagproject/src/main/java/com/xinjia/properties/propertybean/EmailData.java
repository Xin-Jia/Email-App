
package com.xinjia.properties.propertybean;

import java.time.LocalDateTime;
import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Xin Jia Cao
 */
public class EmailData {
    private IntegerProperty id;
    private StringProperty from;
    private StringProperty subject;
    private StringProperty date;
    
    public EmailData(final int id, final String from, final String subject, final LocalDateTime date) {
        this.id = new SimpleIntegerProperty(id);
        this.from = new SimpleStringProperty(from);
        this.subject = new SimpleStringProperty(subject);
        this.date = new SimpleStringProperty(date.toString());
    }
     
    public EmailData(){
        this(-1, "", "", null);
    }

    public int getId() {
        return id.get();
    }

    public void setId(final int id) {
        this.id.set(id);
    }
    
    public IntegerProperty idProperty() {
        return id;
    }

    public String getFrom() {
        return from.get();
    }

    public void setFrom(final String from) {
        this.from.set(from);
    }
    
    public StringProperty fromProperty() {
        return from;
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

    public String getDate() {
        return date.get();
    }

    public void setDate(final String date) {
        this.date.set(date);
    }
    
    public StringProperty dateProperty() {
        return date;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + Objects.hashCode(this.from);
        hash = 97 * hash + Objects.hashCode(this.subject);
        hash = 97 * hash + Objects.hashCode(this.date);
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
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.from, other.from)) {
            return false;
        }
        if (!Objects.equals(this.subject, other.subject)) {
            return false;
        }
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EmailData{" + "id=" + id + ", from=" + from + ", subject=" + subject + ", date=" + date + '}';
    }
    


     
     
}
