package com.hospital.board.service.config;

import com.hospital.commons.Utils;
import com.hospital.commons.exceptions.AlertBackException;
import org.springframework.http.HttpStatus;

public class BoardNotFoundException extends AlertBackException {
    public BoardNotFoundException(){
        //게시판 없으면 메세지 고정해서 넣어준다
        super(Utils.getMessage("NotFound.board", "errors"), HttpStatus.NOT_FOUND);
    }
}
