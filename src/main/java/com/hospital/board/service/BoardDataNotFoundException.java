package com.hospital.board.service;

import com.hospital.commons.Utils;
import com.hospital.commons.exceptions.AlertBackException;
import org.springframework.http.HttpStatus;

public class BoardDataNotFoundException extends AlertBackException {
    public BoardDataNotFoundException(){
        super(Utils.getMessage("NotFound.boardData", "errors"), HttpStatus.NOT_FOUND);
    }
}
