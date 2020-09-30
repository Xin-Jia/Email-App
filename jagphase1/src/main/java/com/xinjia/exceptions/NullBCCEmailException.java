
package com.xinjia.exceptions;

/**
 * Exception that would be thrown if the BCC email list is null
 * @author Xin Jia Cao
 */
public class NullBCCEmailException extends Exception{
    
    public NullBCCEmailException(String errorMessage) {
        super(errorMessage);
    }
}
