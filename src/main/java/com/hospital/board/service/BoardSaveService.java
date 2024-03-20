package com.hospital.board.service;

import com.hospital.board.controllers.RequestBoard;
import com.hospital.board.entities.BoardData;
import com.hospital.board.repositories.BoardDataRepository;
import com.hospital.file.service.FileUploadService;
import com.hospital.member.MemberUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class BoardSaveService {
    private final BoardDataRepository boardDataRepository;
    private final FileUploadService fileUploadService;
    private final MemberUtil memberUtil; //로그인한 사용자 정보
    private final HttpServletRequest request;

    public BoardData save(RequestBoard form){
        String mode = form.getMode();
        mode = StringUtils.hasText(mode) ? mode : "write";

        Long seq = form.getSeq();
        BoardData data = null; //seq값이 없으면 안되니까

        if (seq != null && mode.equals("update")){ //글 수정
            //게시글 없을때는 수정하면 안되니까 예외 추가 BoardDataNotFoundException
            data = boardDataRepository.findById(seq).orElseThrow(BoardDataNotFoundException::new);

        } else{ //글작성시 새로운 엔티티를 만든다
            data = new BoardData();
            data.setGid(form.getGid());
            data.setIp(request.getRemoteAddr());
            data.setUa(request.getHeader("User-Agent"));
            data.setMember(memberUtil.getMember());

        }

        return data;

    }
}
