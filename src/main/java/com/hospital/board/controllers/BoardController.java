package com.hospital.board.controllers;

import com.hospital.board.entities.Board;
import com.hospital.board.service.config.BoardConfigInfoService;
import com.hospital.commons.ExceptionProcessor;
import com.hospital.commons.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController implements ExceptionProcessor {
    private final BoardConfigInfoService configInfoService;
    private final Utils utils;
    private Board board;

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
    public String write(@PathVariable("bid") String bid, Model model){
        commonProcess(bid, "write", model);

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
    public String save(Model model){

        return null;
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
