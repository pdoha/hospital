package com.hospital.main.controllers;

import com.hospital.commons.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class IndexController {

    private final Utils utils;

    @GetMapping
    public String index() {
        return utils.tpl("main/index");
    }
}
