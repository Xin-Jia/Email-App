
package com.xinjia.exceptions;

/**
 * Exception that would be thrown if the CC email address in a list is null
 * @author Xin Jia Cao
 */
public class NullCCEmailAddressException extends Exception {
 
    public NullCCEmailAddressException(String errorMessage) {
        super(errorMessage);
    }
}