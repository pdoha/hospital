package com.hospital.admin.board.controllers;

import com.hospital.admin.menus.Menu;
import com.hospital.admin.menus.MenuDetail;
import com.hospital.board.service.config.BoardConfigSaveService;
import com.hospital.commons.ExceptionProcessor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller("adminBoardController")
@RequestMapping("/admin/board")
@RequiredArgsConstructor
public class BoardController implements ExceptionProcessor {

    private final BoardConfigSaveService configSaveService;
    private final BoardConfigValidator configValidator;

    //주메뉴 코드
    @ModelAttribute("menuCode")
    public String getMenuCode(){
        return "board";
    }

    //서브메뉴
    @ModelAttribute("subMenus")
    public List<MenuDetail> getSubMenus(){
        return Menu.getMenus("board");
    }

    //게시판 목록
    @GetMapping
    public String list(Model model){
        commonProcess("list", model);
        return "admin/board/list";
    }

    //게시판 등록
    @GetMapping("/add")
    public String add(@ModelAttribute RequestBoardConfig config, Model model){
        commonProcess("add", model);
        return "admin/board/add";
    }

    @GetMapping("/adit/{bid}")
    public String edit(@PathVariable("bid") String bid, Model model){
        commonProcess("edit", model);

        return "admin/board/edit";
    }

    //(게시판 등록/수정 동시에 공유
    @PostMapping("/save")
    public String save(@Valid RequestBoardConfig config, Errors errors, Model model){
        String mode = config.getMode();

        commonProcess("mode", model);

        configValidator.validate(config, errors);
        //에러있으면 처리안한다
        if(errors.hasErrors()){
            return "admin/board/" + mode;
        }

        //오류없으면
        configSaveService.save(config);
        return "redirect:/admin/board";

    }

    //게시글 관리
    @GetMapping("/posts")
    public String posts(Model model){
        commonProcess("posts", model);
        return "admin/board/posts";
    }

    //공통처리 (페이지제목, css, js 등)
    private void commonProcess(String mode, Model model){
        String pageTitle = "게시판 목록";
        //모드값이 없을때 기본값 list
        mode = StringUtils.hasText(mode) ? mode : "list";


        //페이지이름
        if(mode.equals("add")){
            pageTitle = "게시판 등록";
        } else if(mode.equals("edit")){
            pageTitle = "게시글 수정";
        } else if (mode.equals("posts")){
            pageTitle = "게시글 관리";
        }

        //공통


        //양식에 필요한 스크립트 ( 게시판 등록, 수정에 다 필요하니까 form)
        List<String> addScript = new ArrayList<>();

        //추가 등록 수정에 필요한 공통적인 자바스크립트 (관리자쪽에 필요한 설정
        List<String> addCommonScript = new ArrayList<>();

        //게시판 등록 또는 수정일때만 추가할 script
        if (mode.equals("add") || mode.equals("edit")){
            addCommonScript.add("ckeditor5/ckeditor"); //에디더 추가
            addCommonScript.add("fileManager"); //파일업로드 ( 필요할때만 추가)
            addScript.add("board/form"); //보드 양식에 필요한 스크립트

        }

        model.addAttribute("pageTitle", pageTitle); //페이지제목
        model.addAttribute("subMenuCode", mode); //서브메뉴코드는 모드값과 동일하게
        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);

    }
}


