
package com.xinjia.exceptions;

/**
 * Exception that would be thrown if the email to update is not a draft
 * @author Xin Jia Cao
 */
public class NotDraftFolderException extends Exception {
 
    public NotDraftFolderException(String errorMessage) {
        super(errorMessage);
    }
}