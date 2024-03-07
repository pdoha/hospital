package com.hospital.board.service.config;

import com.hospital.board.repositories.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class BoardConfigInfoService {
    private final BoardRepository boardRepository;
//  private final FileInfoService fileInfoService;

    //개별조회
//    public Board get(String bid){
//        Board board = boardRepository.findById(bid).orElseThrow(BoardNotFoundException::new);
//
//
//    }
}
