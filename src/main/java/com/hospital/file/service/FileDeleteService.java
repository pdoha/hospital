package com.hospital.file.service;

import com.hospital.commons.Utils;
import com.hospital.commons.exceptions.UnAuthorizedException;
import com.hospital.file.FileInfoRepository.FileInfoRepository;
import com.hospital.file.entities.FileInfo;
import com.hospital.member.MemberUtil;
import com.hospital.member.entities.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileDeleteService {

    //파일 정보
    private final FileInfoService infoService;
    //관리자일때 가능
    private final MemberUtil memberUtil;
    private final FileInfoRepository repository;

    public void delete(Long seq){
        FileInfo data = infoService.get(seq); //파일 정보가져오기

        //파일 삭제 권한 체크
        Member member = memberUtil.getMember(); //회원 정보 가져오기
        String createdBy = data.getCreatedBy(); //게시판 등록한 사람 가져오기
        if(!memberUtil.isAdmin() && StringUtils.hasText(createdBy)
                && !createdBy.equals(member.getUserId())){
            throw new UnAuthorizedException(Utils.getMessage("Not.your.file", "errors"));

        }

        //원래 파일 삭제 ( 없는 파일 삭제하면 오류 발생)
        File file = new File(data.getFilePath());
        if(file.exists()) file.delete();

        //썸네잉ㄹ 삭제
        List<String> thumbsPath = data.getThumbsPath();
        if(thumbsPath != null){
            for(String path : thumbsPath){
                File thumbFile = new File(path);
                if(thumbFile.exists()) thumbFile.delete();
            }
        }

        repository.delete(data);
        repository.flush();

    }
}
