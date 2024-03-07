package com.hospital.admin.center.controllers;

import com.hospital.admin.menus.Menu;
import com.hospital.admin.menus.MenuDetail;
import com.hospital.commons.ExceptionProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/center")
@RequiredArgsConstructor
public class CenterController implements ExceptionProcessor {


    //주메뉴
    @ModelAttribute("menuCode")
    public String getMenuCode(){
        return "center";
    }

    //서브메뉴
    @ModelAttribute("subMenus")
    public List<MenuDetail> getSubMenus(){
        return Menu.getMenus("center");
    }

    @GetMapping("/list")
    public String list(){
        return "admin/center/list";
    }
    @GetMapping("/add")
    public String add(){
        return "admin/center/add";
    }
}
