package com.hospital.member.service;

import com.hospital.commons.Utils;
import com.hospital.member.MemberUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException,
            ServletException {

        //실패시 다시 입력하게 유도 하귀 위해 로그인페이지로 이동
        //데이터 유지를 위해 redirect보다는 범위가 더 큰 session을 이용하자
        HttpSession session = request.getSession();

        //세션 로그인 실패 메세지 일괄 삭제
        MemberUtil.clearLoginData(session);

        String username = request.getParameter("userId");
        String password = request.getParameter("password");

        //검증실패시 아이디가 입력되어있으면 다음 요청에도 입력되어있게
        session.setAttribute("userId", username);


        //validations 메세지를 이용하자
        //username, password가 없는 경우 메세지 출력
        if(!StringUtils.hasText(username)){
            session.setAttribute("NotBlank_userId", Utils.getMessage("NotBlank.userId"));
        }

        if(!StringUtils.hasText(password)){
            session.setAttribute("NotBlank_password", Utils.getMessage("NotBlank_password"));
        }

        //아이디, 비번이 입력되어있지만 실패한 경우
        //아이디로 조회되는 회원이없거나, 비번이 일치 x
        if(StringUtils.hasText(username) && StringUtils.hasText(password)){
            session.setAttribute("Global_error", Utils.getMessage("Fail.login", "errors"));
        }

    }
}
