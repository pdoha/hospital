package com.hospital.commons.exceptions;

import org.springframework.http.HttpStatus;

public class AlertBackException extends AlertException{

    //alert + back
    public AlertBackException(String message, HttpStatus status){
        super(message, status);
    }
}
