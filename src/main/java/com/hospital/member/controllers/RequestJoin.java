package com.hospital.member.controllers;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class RequestJoin {
    //엔티티는 데이터를 담는 역할
    //커맨드객체는 검증도해준다
    //요청 데이터를 처리할 수 있는 커맨 객체 추가

    //프로필이미지
    private  String gid = UUID.randomUUID().toString();
    //필수항목 검증 NotBlank
    @NotBlank @Email
    private String email;
    @NotBlank
    private String userId;
    @NotBlank
    private String password;
    @NotBlank
    private String confirmPassword;
    @NotBlank
    private String name;
    @AssertTrue //참값 체크
    private boolean agree;
}
