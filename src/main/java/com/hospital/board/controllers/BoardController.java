package com.hospital.board.controllers;

import com.hospital.board.entities.Board;
import com.hospital.board.entities.BoardData;
import com.hospital.board.service.BoardAuthService;
import com.hospital.board.service.GuestPasswordCheckException;
import com.hospital.board.service.config.BoardConfigInfoService;
import com.hospital.commons.ExceptionProcessor;
import com.hospital.commons.Utils;
import com.hospital.file.entities.FileInfo;
import com.hospital.file.service.FileInfoService;
import com.hospital.member.MemberUtil;
import com.hospital.member.entities.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController implements ExceptionProcessor {
    private final BoardConfigInfoService configInfoService;
    private final FileInfoService fileInfoService;

    private final BoardFormValidator boardFormValidator;
//    private final BoardSaveService boardSaveService;
//    private final BoardInfoService boardInfoService;
//    private final BoardDeleteService boardDeleteService;
    private final BoardAuthService boardAuthService;

    private final MemberUtil memberUtil;
    private final Utils utils;

    private Board board; // 게시판 설정
    private BoardData boardData; // 게시글
    //게시판 목록
    @GetMapping("/list/{bid}")
    public String list(@PathVariable("bid") String bid, Model model){
        commonProcess(bid, "list", model);

        return utils.tpl("board/list"); //모바일, pc 구분
    }

    //게시글 보기
    //게시글 번호 seq
    @GetMapping("/view/{seq}")
    public String view(@PathVariable("seq") Long seq, Model model){
        commonProcess(seq, "view", model);

        return utils.tpl("board/view");

    }

    //게시글 쓰기
    @GetMapping("/write/{bid}")
    public String write(@PathVariable("bid") String bid,@ModelAttribute RequestBoard form,  Model model){
        commonProcess(bid, "write", model);

        //게시판 작성시 회원일 경우 작성자에 회원명 자동기입
        if(memberUtil.isLogin()){
            Member member = memberUtil.getMember();
            form.setPoster(member.getName());
        }

        return utils.tpl("board/write");
    }

    //게시글 수정
    @GetMapping("/update/{seq}")
    public String update(@PathVariable("seq") Long seq, Model model){
        commonProcess(seq, "update", model);

        return utils.tpl("board/update");
    }

    //게시글 등록 & 수정
    @PostMapping("/save")
    public String save(@Valid RequestBoard form, Errors errors, Model model){

        String bid = form.getBid();
        String mode = form.getMode();
        commonProcess(bid, mode, model);

        boardFormValidator.validate(form, errors); //비회원 비밀번호 검증

        //에러발생해도 파일,이미지 목록 유지
        if (errors.hasErrors()) {
            String gid = form.getGid();

            List<FileInfo> editorFiles = fileInfoService.getList(gid, "editor");
            List<FileInfo> attachFiles = fileInfoService.getList(gid, "attach");

            form.setEditorFiles(editorFiles);
            form.setAttachFiles(attachFiles);

            return utils.tpl("board/" + mode);
        }

        return null;
    }

    //비회원 글수정, 글삭제 비밀번호 확인
    @PostMapping("/password")
    public String passwordCheck(@RequestParam(name="password", required = false) String password, Model model){

        return "common/_execute_script";
    }


    //비회원 비밀번호 체크 접근 권한
    @Override
    @ExceptionHandler(Exception.class)
    public String errorHandler(Exception e, HttpServletResponse response, HttpServletRequest request, Model model) {

        if (e instanceof GuestPasswordCheckException) { //비회원 비밀번호랑 에러객체 e 가 동일하면
            //던져진 예외가 GuestPasswordCheckException 이면 템플릿을 보여준다
            return utils.tpl("board/password");
        }

        //동일하지않으면 원래 처리
        return ExceptionProcessor.super.errorHandler(e, response, request, model);
    }


    //게시판 공통 처리
    private void commonProcess(String bid, String mode, Model model){
        //모드가 없으면 기본값 list 목록
        mode = StringUtils.hasText(mode) ? mode : "list";



        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();

        List<String> addCommonCss = new ArrayList<>();
        List<String> addCss = new ArrayList<>();
        //게시판 설정처리
        board = configInfoService.get(bid); //bid로 조회해서 가져와

        //스킨별 css, js 추가
        String skin = board.getSkin(); //스킨 가져와서
        addCss.add("board/skin_" + skin);
        addScript.add("board/skin_" + skin);


        model.addAttribute("board", board);

        //게시판명이 기본 타이틀
        String pageTitle = board.getBName();

        //쓰기 & 수정
        if(mode.equals("write") || mode.equals("update")){
            //에디터 사용할 경우 추가
            if(board.isUseEditor()){
                addCommonScript.add("ckeditor5/ckeditor");
            }

            //이미지 또는 파일 첨부 사용할 경우 추가
            if(board.isUseUploadImage() || board.isUseUploadFile()){
                addCommonScript.add("fileManager");
            }

            //수정&등록은 자바스크립트가 필요함
            addScript.add("board/form");

            //제목
            pageTitle += " ";
            pageTitle += mode.equals("update") ? Utils.getMessage("글수정",
                    "commons") : Utils.getMessage("글쓰기", "commons");

        }

        //스킨에 따라 css , script 분리
        model.addAttribute("addCommonCss", addCommonCss);
        model.addAttribute("addCss", addCss);
        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("pageTitle", pageTitle);


    }

    private void commonProcess(Long seq, String mode, Model model){

    }

}
