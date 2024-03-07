package com.hospital.commons;

import com.hospital.commons.exceptions.AlertBackException;
import com.hospital.commons.exceptions.AlertException;
import com.hospital.commons.exceptions.CommonException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;

public interface ExceptionProcessor {




    //모든예외가 발생하면 여기로옴
    @ExceptionHandler(Exception.class)
    default String errorHandler(Exception e, HttpServletResponse response,
                               HttpServletRequest request, Model model){
        //예외 객체 e , 응답 코드 , 추가데이터
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; //500으로 고정

        //e가 내가만든 CommonException의 객체인지 체크
        if(e instanceof CommonException){
            //넘어온 객체 e는 상위클래스 하위클래스로 이용하려면 원래형태 객체 CommonException이어야함
            //-> 형변환
            CommonException commonException = (CommonException) e;
            status = commonException.getStatus(); //각 예외에 따라 바꿔줌
        }

        response.setStatus(status.value());

        e.printStackTrace();

        //자바스크립트 Alert 형태로 응답
        if(e instanceof AlertException){
            String script = String.format("alert('%s');", e.getMessage());

            if(e instanceof AlertBackException){
                script += "history.back()";
            }

            model.addAttribute("script", script);
            return "common/_execute_script"; //스크립트 출력할 템플릿
        }

        //에러정보 추가
        model.addAttribute("status", status.value());
        model.addAttribute("path", request.getRequestURL());
        model.addAttribute("method", request.getMethod()); //요청방식
        model.addAttribute("message", e.getMessage());
        return "error/common"; //공통 예외 템플릿 연결
    }
}
