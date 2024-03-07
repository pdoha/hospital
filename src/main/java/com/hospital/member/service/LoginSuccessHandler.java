package com.hospital.member.service;

import com.hospital.member.MemberUtil;
import com.hospital.member.entities.Member;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException,
            ServletException {

        HttpSession session = request.getSession();
        //로그인 성공시에도 메세지데이터 삭제
        MemberUtil.clearLoginData(session);

        //회원정보 조회 편의 구현
        //getauthentication → 로그인 정보가 담겨있음( 로그인 성공했을 때 회원정보가 담겨있음)
        //이 값을 가지고 sesison쪽으로 유지하게하면
        //-> 편하게 sesison만 조회하면 회원정보 조회됨!
        MemberInfo memberInfo = (MemberInfo) authentication.getPrincipal();
        Member member = memberInfo.getMember();
        session.setAttribute("member", member);


        //-성공시 조회해서 이동할 수 있게 값이 없을때는 메인페이지로 이동-->
        String redirectURL = request.getParameter("redirectURL");
        redirectURL = StringUtils.hasText(redirectURL) ? redirectURL : "/";

        response.sendRedirect(request.getContextPath() + redirectURL);



    }
}
