package com.hospital.commons;

import com.hospital.commons.exceptions.CommonException;
import com.hospital.commons.rests.JSONData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public interface ExceptionRestProcessor {

    //형식을 고정하고
    //동일한형식으로
    //예외가 발생하면 유입되게

    @ExceptionHandler(Exception.class)
    default ResponseEntity<JSONData<Object>> errorHandler(Exception e){
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; //500
        //발생한 예외가 commonException 의 객체이면
        //상태코드도 가져와서 교체
        if(e instanceof CommonException){
            CommonException commonException = (CommonException) e;
            status = commonException.getStatus();
        }
        //json 데이터를 만들고
        JSONData<Object> data = new JSONData<>();
        data.setSuccess(false); //실패시
        data.setStatus(status); //상태코드가 바뀜
        data.setMessage(e.getMessage()); //메세지

        e.printStackTrace(); //console 출력

        //ResponseEntity 내의 응답코드 status 과 바디데이터를 반환
        return ResponseEntity.status(status).body(data);

    }
}
