package com.hospital.commons.exceptions;

import org.springframework.http.HttpStatus;

//공통예외를 만들고 모든예외를 공통예외를 통해 정의할 수 있게 넣어준다
public class CommonException extends RuntimeException {
    private HttpStatus status; //상태코드


    //모든예외는 한가지 기준으로 조회한다
    //→ 응답코드 가지고 조회하겠다
    public CommonException(String message, HttpStatus status){
        super(message);
        this.status = status;
    }

    //상태코드 조회
    public HttpStatus getStatus(){
        return status;
    }
}
