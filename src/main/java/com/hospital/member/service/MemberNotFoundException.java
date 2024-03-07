package com.hospital.member.service;

import com.hospital.commons.exceptions.CommonException;
import org.springframework.http.HttpStatus;

public class MemberNotFoundException extends CommonException {

    //아이디나 이메일로 조회했을때 회원이 아닐 경우 메세지
    //→ 기본생성자만 정의해서 메서드는 고정해서 넣어주자
    //메세지는 고정되고 응답코드도 고정된다
    public MemberNotFoundException(){
        super("등록된 회원이 아닙니다.", HttpStatus.NOT_FOUND);
    }
}
