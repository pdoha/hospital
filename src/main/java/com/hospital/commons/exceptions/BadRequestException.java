package com.hospital.commons.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends CommonException {
    //응답코드가 400으로 고정되어있다
    public BadRequestException(String message){
        super(message, HttpStatus.BAD_REQUEST); //400
    }
}
