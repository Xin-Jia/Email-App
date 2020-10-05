
package com.xinjia.exceptions;

/**
 * Exception that would be thrown if the CC email list is null
 * @author Xin Jia Cao
 */
public class NullCCEmailException extends Exception{
    
    public NullCCEmailException(String errorMessage) {
        super(errorMessage);
    }
}
