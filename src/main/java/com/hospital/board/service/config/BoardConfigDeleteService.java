package com.hospital.board.service.config;

import com.hospital.board.entities.Board;
import com.hospital.board.repositories.BoardRepository;
import com.hospital.commons.exceptions.AlertException;
import com.hospital.file.service.FileDeleteService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.hospital.commons.Utils;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardConfigDeleteService {
    private final BoardRepository boardRepository;
    private final BoardConfigInfoService configInfoService;
    private final FileDeleteService fileDeleteService;
    private final Utils utils;

    //게시판ID를 가지고 개별삭제
    public void delete(String bid){
        //게시판 설정 불러오기
        Board board = configInfoService.get(bid);
        //파일그룹
        String gid = board.getGid();
        //설정 삭제
        boardRepository.delete(board);
        //저장
        boardRepository.flush();
        //연결되어있는 파일데이터 삭제
        fileDeleteService.delete(gid);
    }

    //일련번호 chk로 게시판 id를 찾아서 (복수개)삭제
    public void deleteList(List<Integer> chks){
        //선택을 하야함
        if(chks == null || chks.isEmpty()){
            throw new AlertException("삭제할 게시판을 선택하세요.", HttpStatus.BAD_REQUEST);
        }

        for(int chk : chks){
            //게시판 아이디 찾아서 목록삭제처리
            String bid = utils.getParam("bid_" + chk);
            delete(bid);
        }

    }

}
