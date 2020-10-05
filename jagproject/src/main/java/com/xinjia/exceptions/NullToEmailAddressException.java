
package com.xinjia.exceptions;

/**
 * Exception that would be thrown if the To email address in a list is null
 * @author Xin Jia Cao
 */
public class NullToEmailAddressException extends Exception {
 
    public NullToEmailAddressException(String errorMessage) {
        super(errorMessage);
    }
}