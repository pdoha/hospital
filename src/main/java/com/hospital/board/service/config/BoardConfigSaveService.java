package com.hospital.board.service.config;

import com.hospital.admin.board.controllers.RequestBoardConfig;
import com.hospital.board.entities.Board;
import com.hospital.board.repositories.BoardRepository;
import com.hospital.member.Authority;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class BoardConfigSaveService {

    private final BoardRepository boardRepository;
//    private final FileUploadService fileUploadService;
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
//      fileUploadService.processDone(board.getGid());
    }
}
