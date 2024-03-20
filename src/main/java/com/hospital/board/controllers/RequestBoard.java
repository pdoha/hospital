package com.hospital.board.controllers;

import com.hospital.file.entities.FileInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class RequestBoard {
    private String mode = "write";
    private Long seq; //게시글 수정시 게시글번호
    private String bid; //게시판 id

    private String gid = UUID.randomUUID().toString(); //파일이미지

    @NotBlank
    private String poster; //글작성자
    private String guestPw; //비회원 비밀번호 - 미로그인시

    private boolean notice; //공지사항 여부 - 관리자일때만 체크

    @NotBlank
    private String subject; //글제목
    @NotBlank
    private String content; //글내용

    private List<FileInfo> editorFiles; // 에디터 파일 목록
    private List<FileInfo> attachFiles; // 첨부 파일 목록
}
