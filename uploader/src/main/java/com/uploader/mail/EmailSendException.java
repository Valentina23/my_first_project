package com.uploader.mail;

public class EmailSendException extends Exception {

    public EmailSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
