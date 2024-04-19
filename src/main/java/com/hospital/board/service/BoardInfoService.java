package com.hospital.board.service;

import com.hospital.board.controllers.BoardDataSearch;
import com.hospital.board.entities.Board;
import com.hospital.board.entities.BoardData;
import com.hospital.board.entities.QBoardData;
import com.hospital.board.repositories.BoardDataRepository;
import com.hospital.board.service.config.BoardConfigInfoService;
import com.hospital.commons.ListData;
import com.hospital.commons.Utils;
import com.hospital.file.entities.FileInfo;
import com.hospital.file.service.FileInfoService;
import com.querydsl.core.BooleanBuilder;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardInfoService {
    private final BoardDataRepository boardDataRepository;
    private final EntityManager em;
    private final FileInfoService fileInfoService;
    private final BoardConfigInfoService configInfoService; //게시판 설정

    //게시글 개별 조회
    public BoardData get(Long seq){
        BoardData boardData = boardDataRepository.findById(seq).orElseThrow
                (BoardDataNotFoundException::new);
        //추가 정보는 가져와서 넣어준다 2차가공
        //에디터이미지, 첨부파일
        addBoardData(boardData);
        return boardData;
    }

    //게시판 목록 조회
    //bid :  게시판 ID
    public ListData<BoardData> getList(String bid, BoardDataSearch search){
        Board board = configInfoService.get(bid); //게시판 설정 가져오기
        int page = Utils.onlyPositiveNumber(search.getPage(), 1);
        int limit = Utils.onlyPositiveNumber(search.getLimit(), board.getRowsPerPage());

        QBoardData boardData = QBoardData.boardData;
        BooleanBuilder andBuilder = new BooleanBuilder();

    }

    //게시글 추가 정보처리
    public void addBoardData(BoardData boardData){
        String gid = boardData.getGid();

        //파일정보 가져오기
        List<FileInfo> editorFiles = fileInfoService.getListDone(gid, "editor"); //에디터 이미지파일
        List<FileInfo> attachFiles = fileInfoService.getListDone(gid, "attach"); //첨부파일
        //추가
        boardData.setEditorFiles(editorFiles);
        boardData.setAttachFiles(attachFiles);
    }
}
