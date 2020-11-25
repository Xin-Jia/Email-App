
package com.xinjia.exceptions;

/**
 * Exception that would be thrown if the To email list is null
 * @author Xin Jia Cao
 */
public class NullToEmailException extends Exception {
 
    public NullToEmailException(String errorMessage) {
        super(errorMessage);
    }
}
