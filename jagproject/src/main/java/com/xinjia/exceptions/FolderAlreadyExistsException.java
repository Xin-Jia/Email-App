package com.xinjia.exceptions;

/**
 * Exception that would be thrown if a given folder name already exists when trying to create a new one
 * @author Xin Jia Cao
 */
public class FolderAlreadyExistsException extends Exception {
 
    public FolderAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
