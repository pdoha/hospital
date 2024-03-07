package com.hospital.member.controllers;


import com.hospital.commons.ExceptionProcessor;
import com.hospital.commons.Utils;
import com.hospital.member.MemberUtil;
import com.hospital.member.entities.Member;
import com.hospital.member.service.JoinService;
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
@RequestMapping("/member") //기본주소
@RequiredArgsConstructor //자동의존성 주입
public class MemberController implements ExceptionProcessor { //발생한 에러 유입될 수 있게
    //의존성
    private final Utils utils;
    private final JoinService joinService;
    private final MemberUtil memberUtil;

    //회원가입
    @GetMapping("/join")
    public String join(@ModelAttribute RequestJoin form, Model model){
        //제목
        commonProcess("join", model);

        //템플릿 연동
        return utils.tpl("member/join");
    }
    //회원가입처리
    @PostMapping("/join")
    public String joinPs(@Valid RequestJoin form, Errors errors, Model model){ //커맨드 검증 연동 @Valid
                                                                        //검증 실패시 errors 객체에 담김
        commonProcess("join", model);
        joinService.process(form, errors);

        if(errors.hasErrors()){ //hasErrors가 참이면
            //입력한 값을 보여준다 (수정할수있게)

            errors.getAllErrors().forEach(System.out::println);
            return utils.tpl("member/join");
        }
        //회원가입 완료되면 로그인페이지로 이동
        return "redirect:/member/login";

    }

    //로그인 페이지
    @GetMapping("/login")
    public String login(Model model){
        commonProcess("login", model);


        return utils.tpl("member/login");
    }

    //회원정보 조회
    //데이터를 받지않고 회원데이터만 조회하는 방법 : SecurityContextHolder
    //세개의 메서드를 호출
   /* @ResponseBody
    @GetMapping("/info")
    public void info(){
        MemberInfo memberInfo = (MemberInfo) SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();
        System.out.println(memberInfo);

    }*/

    //다른방식으로 회원 조회
    @ResponseBody
    @GetMapping("/info")
    public void info(){
        if(memberUtil.isLogin()){
            Member member = memberUtil.getMember();
            System.out.println(member);
        } else {
            System.out.println("미로그인 상태");
        }
    }

    private void commonProcess(String mode, Model model){
        mode = StringUtils.hasText(mode) ? mode : "join";
        String pageTitle = Utils.getMessage("회원가입", "commons");

        List<String> addCommonScript = new ArrayList<>(); //공통 자바스크립트

        if(mode.equals("login")){
            pageTitle = Utils.getMessage("로그인", "commons");

        //회원가입할때
        } else if (mode.equals("join")){
            addCommonScript.add("fileManager"); //프로필이미지 업로드

        }

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("addCommonScript", addCommonScript);
    }

}
