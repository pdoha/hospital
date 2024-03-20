package com.hospital.admin.board.controllers;

import com.hospital.board.repositories.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
@RequiredArgsConstructor
public class BoardConfigValidator implements Validator {

    private final BoardRepository boardRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        //설정 범위한정
        return clazz.isAssignableFrom(RequestBoardConfig.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        //게시판 아이디 중복 체크
        RequestBoardConfig form = (RequestBoardConfig) target;

        //게시판 아이디 하나 가져와서
        String bid = form.getBid();
        String gid = form.getGid();
        //게시판 아디는 기본키로
        // 중복되면안되고 추가될때만 필요함
        //수정할때는 필요없으니까 add모드일때만 중복체크
        String mode = StringUtils.hasText(form.getMode()) ? form.getMode() : "add";
        //있을때 중복되는지 체크
        if(StringUtils.hasText(bid) && mode.equals("add") && boardRepository.existsById(bid)){
            errors.rejectValue("bid", "Duplicated");
        }

    }
}
