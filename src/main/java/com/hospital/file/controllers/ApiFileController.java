package com.hospital.file.controllers;

import com.hospital.commons.ExceptionRestProcessor;
import com.hospital.commons.rests.JSONData;
import com.hospital.file.entities.FileInfo;
import com.hospital.file.service.FileDeleteService;
import com.hospital.file.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class ApiFileController implements ExceptionRestProcessor {

    private final FileUploadService uploadService;
    private final FileDeleteService deleteService;

    @PostMapping
    //파일 올라가면 파일데이터
    public JSONData<List<FileInfo>> upload(@RequestParam("file")MultipartFile[] files,
                                           @RequestParam(name="gid", required = false) String gid,
                                           @RequestParam(name="location", required = false) String location,
                                           @RequestParam(name="imageOnly", required = false) boolean imageOnly
                                           ) {



        //업로드
        List<FileInfo> uploadedFiles = uploadService.upload(files, gid, location, imageOnly);

        return new JSONData<>(uploadedFiles);

    }

    @GetMapping("/{seq}")
    public void delete(@PathVariable("seq") Long seq){
        deleteService.delete(seq);

    }
}
