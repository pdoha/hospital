package com.hospital.member.controllers;

import com.hospital.commons.validators.PasswordValidator;
import com.hospital.member.repositories.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * 추가검증
 */
@Component //스프링 관리 객체 등록
@RequiredArgsConstructor
public class JoinValidator implements Validator, PasswordValidator {
    private final MemberRepository memberRepository;

    //검증 대상 : RequestJoin.class
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(RequestJoin.class); //검증 대상을 한정
    }

    @Override
    public void validate(Object target, Errors errors) {
            //필요한것 이메일, 아이디, 등등 form에서 가져오기
            RequestJoin form = (RequestJoin)target;
            String email = form.getEmail();
            String userId = form.getUserId();
            String password = form.getPassword();
            String confirmPassword = form.getConfirmPassword();

            //1. 이메일과 아이디 중복 여부 체크 (별도 메서드 추가하면 편함)
            //이메일 값이 있고 && DB에 이메일가 이미 존재하면 에러메세지
            if(StringUtils.hasText(email) && memberRepository.existsByEmail(email)){

                errors.rejectValue("email", "Duplicated");
            }

            //아이디 중복 여부 체크
            if(StringUtils.hasText(userId) && memberRepository.existsByUserId(userId)){
                errors.rejectValue("userId", "Duplicated");
            }

            //비밀번호, 비밀번화 확인 일치 여부
            //비밀번호가 존재하고 && 비밀번호확인이 존재하고 && 둘이 일치하지않을때
            if(StringUtils.hasText(password) && StringUtils.hasText(confirmPassword)
                && !password.equals(confirmPassword)){
                errors.rejectValue("confirmPassword", "Mismatch.password");
            }

            //비밀번호 복잡성 체크
            //-대소문자 1개 각각 포함, 숫자 1개 이상 포함, 특수문자도 1개 이상 포함
            if(StringUtils.hasText(password) &&
                    (!alphaCheck(password, true))){
                errors.rejectValue("password", "Complexity");
            }
    }

}
