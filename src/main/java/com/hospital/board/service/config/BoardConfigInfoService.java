package com.hospital.board.service.config;

import com.hospital.admin.board.controllers.RequestBoardConfig;
import com.hospital.board.entities.Board;
import com.hospital.board.repositories.BoardRepository;
import com.hospital.file.entities.FileInfo;
import com.hospital.file.service.FileInfoService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class BoardConfigInfoService {
    private final BoardRepository boardRepository;
    private final FileInfoService fileInfoService;

    //개별조회
    public Board get(String bid){
        Board board = boardRepository.findById(bid).orElseThrow(BoardNotFoundException::new);
        addBoardInfo(board);  //추가정보 - 첨부파일 목록

        return board;

    }

    //양식형태 커맨드객체 형태로 바꿔서 가져오자
    public RequestBoardConfig getForm(String bid){
        Board board = get(bid);
        RequestBoardConfig form = new ModelMapper().map(board, RequestBoardConfig.class);
        //상수는 수동으로 바꿔줘야함
        form.setListAccessType(board.getListAccessType().name());
        form.setViewAccessType(board.getViewAccessType().name());
        form.setWriteAccessType(board.getWriteAccessType().name());
        form.setReplyAccessType(board.getReplyAccessType().name());
        form.setCommentAccessType(board.getCommentAccessType().name());

        form.setMode("edit");
        return form;
    }

    //게시판 설정 추가 정보
    //      - 에디터 첨부파일 목록
    public void addBoardInfo(Board board){
        //업로드가 완료된 파일 가져오자
        String gid = board.getGid();


        List<FileInfo> htmlTopImages = fileInfoService.getListDone(gid, "html_top");
        List<FileInfo> htmlBottomImages = fileInfoService.getListDone(gid, "html_bottom");

        board.setHtmlTopImages(htmlTopImages);
        board.setHtmlBottomImages(htmlBottomImages);
    }

    //게시판 설정 목록
//    public ListData<Board> getList(BoardSearch search){
//        int page =Utils.onlyPosition search.getPage();
//    }
}
