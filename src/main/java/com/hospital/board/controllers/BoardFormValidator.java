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

    private final MemberUtil memberUtil; //비회원 체크

    @Override
    public boolean supports(Class<?> clazz) {
        //검증 대상 지정
        return clazz.isAssignableFrom(RequestBoard.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        //검증에 필요한거 커맨드 객체 가져오기
        RequestBoard form = (RequestBoard) target;
        String guestPw = form.getGuestPw();
        /**
         * 1. 비회원 비밀번호 -
         *  - 필수여부
         *  - 6자이상
         *  - 알파벳 + 숫자 비밀번호 복잡성 체크
         */

        if (!memberUtil.isLogin()) { //로그인이 안되어있고

            //guestPw 없을때
            if (!StringUtils.hasText(guestPw)) {
                errors.rejectValue("guestPw", "NotBlank");
            }
            // 비밀번호 필수 처리
            if (StringUtils.hasText(guestPw) && guestPw.length() < 6) { //guestPw값이있는데 6자 이하
                errors.rejectValue("guestPw", "Size");
            }
            // 비밀번호 복잡성 실패 처리
            if (StringUtils.hasText(guestPw) //값이 있는데 알파벳 + 숫자 복잡성 체크가 안되어있을때
                    && (!alphaCheck(guestPw, true) || !numberCheck(guestPw))) {
                errors.rejectValue("guestPw", "Complexity");
            }
        }
    }
}
