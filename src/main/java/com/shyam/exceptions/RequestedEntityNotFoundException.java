package com.shyam.exceptions;

public class RequestedEntityNotFoundException extends Exception {
    
    public RequestedEntityNotFoundException() {
        super("Required Entity is not found");
    }
    
    public RequestedEntityNotFoundException(String msg) {
        super(msg);
    }

}
