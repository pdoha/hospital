package com.hospital.admin.center.controllers;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class RequestCenter {

    private String mode = "add_center";

    private Long cCode; // 지점 코드

    @NotBlank
    private String cName; // 지점명

    @NotBlank
    private String zonecode; // 우편번호

    @NotBlank
    private String address; // 주소
    private String addressSub; // 나머지 주소

    @NotBlank
    private String telNum; // 전화번호

    @NotBlank
    private String operHour; // 운영시간

    private List<String> bookYoil; //  예약 요일 설정
    private String bookAvlShour; // 예약 가능 시작 시간
    private String bookAvlSmin; // 예약 가능 시작 분
    private String bookAvlEhour; // 예약 가능 종료 시간
    private String bookAvlEmin; // 예약 가능 종료 분
    private String bookNotAvl; // 예약 불가 시간
    private boolean bookHday; // 공휴일 예약 가능 여부
    private int bookBlock; // 예약 블록
    private int bookCapacity; // -1이면 무제한, 0이면 예약 불가
}
