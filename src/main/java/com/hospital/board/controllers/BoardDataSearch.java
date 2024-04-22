package com.hospital.board.controllers;

import lombok.Data;

import java.util.List;

@Data
public class BoardDataSearch {
    private int page = 1;
    private int limit; //기본값은 설정에서 가져옴, 1이상이면 지정된 값

    //검색옵션
    //subject : 제목
    //content : 내용
    //subject_content : 제목 + 내용
    //poster : 작성자명 + 아이디 + 회원이름
    //ALL : 통합검색
    private String sopt; //검색 옵션
    private String skey; //검색 키워드

    private List<String> bid; //게시판 ID (키워드 조회시 bid 없는 경우도 있으니까 추가)
    private String userId;
}
