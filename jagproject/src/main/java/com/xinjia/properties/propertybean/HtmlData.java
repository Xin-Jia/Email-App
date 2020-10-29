
package com.xinjia.properties.propertybean;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Xin Jia Cao
 */
public class HtmlData {
    private StringProperty message;
    public HtmlData(final String message) {
        this.message = new SimpleStringProperty(message);
    }
     
    public HtmlData(){
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
}
