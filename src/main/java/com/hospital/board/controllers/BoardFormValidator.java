package com.hospital.board.controllers;

import com.hospital.commons.validators.PasswordValidator;
import com.hospital.member.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class BoardFormValidator implements Validator, PasswordValidator {
    private final MemberUtil memberUtil; //권한 체크
    @Override
    public boolean supports(Class<?> clazz) {
        //검증 대상 지정
        return clazz.isAssignableFrom(RequestBoard.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        //검증에 필요한거 커맨드 객체 가져오기
        RequestBoard form = (RequestBoard) target;

        /**
         * 1. 비회원 비밀번호 -
         *  - 필수여부
         *  - 6자이상
         *  - 알파벳 + 숫자 비밀번호 복잡성 체크
         */

        String guestPw = form.getGuestPw();
        if (!memberUtil.isLogin()) {

            if (!StringUtils.hasText(guestPw)) { //guestPw 없을때
                errors.rejectValue("guestPw", "NotBlank");
                // 비밀번호 필수 처리
            } else if (StringUtils.hasText(guestPw) && //비밀번호 입력됐고
                    (guestPw.length() < 6) && //6자 이하
                    (!alphaCheck(guestPw, true) || !numberCheck(guestPw))) {
                // 비밀번호 복잡성 실패 처리
                errors.rejectValue("guestPw", "complexity");
            }


        }



    }
}
