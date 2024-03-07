package com.hospital.admin.config.controllers;

import lombok.Data;

@Data
public class BasicConfig {
    //관리가 기본 설정 커맨드 객체
    private String siteTitle = "";
    private String siteDescription = "";
    private String siteKeywords = "";
    private int cssJsVersion = 1;
    private String joinTerms = "";
    private String thumbSize = "";
}
