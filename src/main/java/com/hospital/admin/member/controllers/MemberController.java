package com.hospital.admin.member.controllers;

import com.hospital.admin.menus.Menu;
import com.hospital.admin.menus.MenuDetail;
import com.hospital.commons.ExceptionProcessor;
import com.hospital.commons.ListData;
import com.hospital.member.controllers.MemberSearch;
import com.hospital.member.entities.Member;
import com.hospital.member.service.MemberInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Objects;

@Controller("adminMemberController")
@RequestMapping("/admin/member")
@RequiredArgsConstructor
public class MemberController implements ExceptionProcessor {
    private final MemberInfoService infoService;


    //모든 템플릿영역에서 공유되어야하니까 @ModelAttribute
    // menuCode, subMenus 라는 속성값이 있으면 유지되게
    //컨트롤러내에서 모든 템플릿 영역에서 공유된다
    @ModelAttribute("menuCode")
    public String getMenuCode(){ //주메뉴 코드
        return "member";
    }

    @ModelAttribute("subMenus")
    public List<MenuDetail> getSubMenus(){
        return Menu.getMenus("member");
    }
    @GetMapping
    public String list(@ModelAttribute MemberSearch search, Model model){
        //on 클래스 추가
         commonProcess("list", model);

        ListData<Member> data = infoService.getList(search);

         model.addAttribute("items", data.getItems()); //목록
         model.addAttribute("pagination", data.getPagination()); //페이징

        return "admin/member/list";
    }

    //공통기능
    private void commonProcess(String mode, Model model) {
        //모드값이 널값일때 기본값 list
        mode = Objects.requireNonNullElse(mode, "list");
        String pageTitle = "회원 목록";

        model.addAttribute("subMenuCode", mode);
        model.addAttribute("pageTitle", pageTitle);
    }


}
