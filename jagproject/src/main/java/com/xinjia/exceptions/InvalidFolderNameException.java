
package com.xinjia.exceptions;


/**
 * Exception that would be thrown if a given folder name is not found
 * @author Xin Jia Cao
 */
public class InvalidFolderNameException extends Exception {
 
    public InvalidFolderNameException(String errorMessage) {
        super(errorMessage);
    }
}
