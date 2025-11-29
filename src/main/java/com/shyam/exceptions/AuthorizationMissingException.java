package com.shyam.exceptions;

public class AuthorizationMissingException extends Exception {
    String path;

    public AuthorizationMissingException(String path) {
        super("JWT Authorization Header is missing");
        this.path = path;
    }

    public AuthorizationMissingException(String msg, String path) {
        super(msg);
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
    
}
