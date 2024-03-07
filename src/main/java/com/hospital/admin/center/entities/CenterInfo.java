package com.hospital.admin.center.entities;

import jakarta.persistence.*;

public class CenterInfo  {

    @Id
    @GeneratedValue
    private Long cCode;   // 센터 코드

    @Column(length = 50, nullable = false)
    private String cName;   // 센터명

    @Column(length = 10, nullable = false)
    private String zonecode; // 우편번호

    @Column(length = 100, nullable = false)
    private String address; // 주소

    private String addressSub; // 나머지 주소

    @Column(length = 15, nullable = false)
    private String telNum; // 전화번호

    @Column(length = 80, nullable = false)
    private String operHour; // 운영시간

    // ex) 월, 화, 수, 목, 금, 토, 일 => 쪼개서 사용
    @Column(length = 40)
    private String bookYoil; //  예약 요일 설정

    @Column(length = 80, nullable = false)
    private String bookAvl; // 예약 가능 시간 09:00-18:00

    /**
     * 13:00-16:00
     * 15:30-18:00
     */
    @Lob
    private String bookNotAvl;  // 예약 불가 시간

    private boolean bookHday;   // 공휴일 예약 가능 여부

    private int bookBlock;      // 예약 블록 10

    private int bookCapacity;   // 예약가능인원: -1이면 무제한, 0이면 예약 불가

    /**
     * 안 쓰는 csv 원본 파일 데이터
     */
    private String location;
    private String centerType;

    @Transient
    private String bookAvlShour; // 예약 가능 시작 시간
    @Transient
    private String bookAvlSmin; // 예약 가능 시작 분
    @Transient
    private String bookAvlEhour; // 예약 가능 종료 시간
    @Transient
    private String bookAvlEmin; // 예약 가능 종료 분
    @Transient
    private boolean bookAfter18; // 18시 이후 예약 가능 여부
}
