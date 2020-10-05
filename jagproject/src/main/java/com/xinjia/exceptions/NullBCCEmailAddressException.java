
package com.xinjia.exceptions;

/**
 * Exception that would be thrown if the BCC email address in a list is null
 * @author Xin Jia Cao
 */
public class NullBCCEmailAddressException extends Exception {
 
    public NullBCCEmailAddressException(String errorMessage) {
        super(errorMessage);
    }
}