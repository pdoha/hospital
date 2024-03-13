package com.hospital.admin.board.controllers;

import lombok.Data;

@Data
public class BoardSearch {
    //페이징
    private  int page = 1;
    private int limit = 20;

    private String bid;
    private String bName;
    private boolean active;

    //검색
    private String sopt; //검색옵션
    private String skey; //검색 키워드
}
