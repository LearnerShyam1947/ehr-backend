package com.shyam.exceptions;

public class InvalidOTPException extends Exception {
    public InvalidOTPException() {
        super("The Provided OTP is expired");
    }
    
    public InvalidOTPException(String msg) {
        super(msg);
    }
}
