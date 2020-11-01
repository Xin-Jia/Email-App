package com.xinjia.properties.propertybean;

import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * JavaFX HTMLData bean that contains a message 
 * Overrides toString, hashCode and equals
 *
 * @author Xin Jia Cao
 */
public class HtmlData {

    private StringProperty message;

    /**
     * Non-default constructor that initializes the message
     *
     * @param message
     */
    public HtmlData(final String message) {
        this.message = new SimpleStringProperty(message);
    }

    /**
     * Default constructor
     */
    public HtmlData() {
        this("");
    }

    public String getMessage() {
        return message.get();
    }

    public void setMessage(final String message) {
        this.message.set(message);
    }

    public StringProperty messageProperty() {
        return message;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.message);
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
        final HtmlData other = (HtmlData) obj;
        if (!Objects.equals(this.message, other.message)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "HtmlData{" + "message=" + message + '}';
    }

}
