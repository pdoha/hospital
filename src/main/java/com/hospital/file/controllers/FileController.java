package com.hospital.file.controllers;

import com.hospital.commons.ExceptionProcessor;
import com.hospital.commons.exceptions.AlertBackException;
import com.hospital.commons.exceptions.CommonException;
import com.hospital.file.service.FileDeleteService;
import com.hospital.file.service.FileDownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController implements ExceptionProcessor {
    private final FileDeleteService deleteService;
    private final FileDownloadService downloadService;


    @GetMapping("/delete/{seq}")
    public String delete(@PathVariable("seq") Long seq, Model model){
        deleteService.delete(seq);

        //callbackFileDelete 함수가 정의되어있으면 실행하겠다
        String script = String.format("if(typeof parent.callbackFileDelete == 'function') " +
                "parent.callbackFileDelete(%d);", seq);

        model.addAttribute("script", script );
        return "common/_execute_script";

    }
    @ResponseBody
    @RequestMapping("/download/{seq}")
    public void download(@PathVariable("seq") Long seq){
        try {
            downloadService.download(seq);
        } catch (CommonException e){
            //파일없을때 자바스크립트로 출력
            throw new AlertBackException(e.getMessage(), HttpStatus.NOT_FOUND);

        }

    }


}
