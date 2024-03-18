package com.hospital.board.service.config;

import com.hospital.admin.board.controllers.RequestBoardConfig;
import com.hospital.board.entities.Board;
import com.hospital.board.repositories.BoardRepository;
import com.hospital.commons.Utils;
import com.hospital.commons.exceptions.AlertException;
import com.hospital.file.service.FileUploadService;
import com.hospital.member.Authority;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardConfigSaveService {

    private final BoardRepository boardRepository;
    private final FileUploadService fileUploadService;
    private final Utils utils;

    public void save(RequestBoardConfig form){

        // bid, mode 불러와서
        String bid = form.getBid();
        String mode = form.getMode();
        // mode값이 있을때는 mode 없을때는 기본값 고정
        mode = StringUtils.hasText(mode) ? mode : "add";

        //있으면 값넣어주고, 없으면 새로 추가
        Board board = boardRepository.findById(bid).orElseGet(Board::new);

        if(mode.equals("add")){ //게시판 등록시 gid, bid 등록
            board.setBid(bid);
            board.setGid(form.getGid());
        }
        board.setBName(form.getBName());
        board.setActive(form.isActive()); //boolean
        board.setRowsPerPage(form.getRowsPerPage());
        board.setPageCountPc(form.getPageCountPc());
        board.setPageCountMobile(form.getPageCountMobile());
        board.setUseReply(form.isUseReply());
        board.setUseComment(form.isUseComment());
        board.setUseEditor(form.isUseEditor());
        board.setUseUploadImage(form.isUseUploadImage());
        board.setUseUploadFile(form.isUseUploadFile());
        board.setLocationAfterWriting(form.getLocationAfterWriting());
        board.setSkin(form.getSkin());
        board.setCategory(form.getCategory());

        //권한은 상수로 넣었으니까  valueOf
        board.setListAccessType(Authority.valueOf(form.getListAccessType()));
        board.setViewAccessType(Authority.valueOf(form.getViewAccessType()));
        board.setWriteAccessType(Authority.valueOf(form.getWriteAccessType()));
        board.setReplyAccessType(Authority.valueOf(form.getReplyAccessType()));
        board.setCommentAccessType(Authority.valueOf(form.getCommentAccessType()));

        board.setHtmlTop(form.getHtmlTop());
        board.setHtmlBottom(form.getHtmlBottom());

        boardRepository.saveAndFlush(board);

        //파일 업로드 완료처리 ( 파일 목록 유지)
        fileUploadService.processDone(board.getGid());
    }

    public void saveList(List<Integer> chks){
        if(chks == null || chks.isEmpty()){
            throw new AlertException("수정할 게시판을 선택하세요.", HttpStatus.BAD_REQUEST);
        }

        for(int chk : chks){
            String bid = utils.getParam("bid_" + chk);
            Board board = boardRepository.findById(bid).orElse(null);
            if(board == null) continue;

            //활성화 여부
            boolean active = Boolean.parseBoolean(utils.getParam("active_" + chk));
            board.setActive(active);
        }

        boardRepository.flush();

    }
}
