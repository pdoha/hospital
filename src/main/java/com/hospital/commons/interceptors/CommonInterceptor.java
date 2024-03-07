package com.hospital.commons.interceptors;


import com.hospital.admin.config.controllers.BasicConfig;
import com.hospital.admin.config.service.ConfigInfoService;
import com.hospital.member.MemberUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class CommonInterceptor implements HandlerInterceptor {
    private final ConfigInfoService infoService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        checkDevice(request);

        //로그인페이지가 아닌 페이지일때는 메세지관련 세션내용을 비우자
        clearLoginData(request);
        loadSiteConfig(request); //인티셉터 설정

        return true;
    }

    //설정값 인터셉터
    //혹시 값이 없으면 비어있는 객체 만들기
    private void loadSiteConfig(HttpServletRequest request){
        //인터셉터는 모든 url에 적용되서 정적자원들은 쿼리가 여러번 수행됨
        //불필요한곳에는 배제할 수있게 설정
        String[] excludes = {".js", ".css", ".png", ".jpg", ".jpeg", "gif", ".pdf", ".xls", ".xlxs", ".ppt"};
        //특정 url이 포함되어있으면 밑에 로드하지않는방향으로처리
        String URL = request.getRequestURI().toLowerCase();

        //넘어왔을때 url에서 s가 포함되어어있는지
        boolean isIncluded = Arrays.stream(excludes).anyMatch(s -> URL.contains(s));

        //한개라도 매칭이되면 통과되지않음
        if(isIncluded){
            return;
        }
        BasicConfig config = infoService.get("basic", BasicConfig.class).orElseGet(BasicConfig::new);

        //request에서 설정값 넣어주기
        request.setAttribute("siteConfig", config);
    }

    /**
     * pc, mobile 수동 변경 처리
     *
     * url로 수동전환
     * ?device=pc  → pc 뷰
     * ?device=mobile → mobile 뷰
     *
     * @param request
     */
    //session가지고 등록
    private void checkDevice(HttpServletRequest request){
        //파라미터값 가져오기
        String device = request.getParameter("device");

        //값이 없을 경우
        if(!StringUtils.hasText(device)){
            return;
        }

        //값이 있을 경우
        //모바일 값이 아니면 -> pc
        //대소문자 구분 없음
        device = device.toUpperCase().equals("MOBILE") ? "MOBILE" : "PC";

        //session 가져와서 넣자
        HttpSession session = request.getSession();

        //값이 있으면 고정
        session.setAttribute("device", device);

    }

    //로그인페이지가 아닌 페이지일때는 메세지관련 세션내용을 비우자
    //->접속한 주소가 뭔지알아야함 getRequestURI
    private void clearLoginData(HttpServletRequest request){
        String URL = request.getRequestURI();
        //주소 url member/login 주소가 아닌 페이지면 전부 제거
        if(URL.indexOf("/member/login") == -1){  //문자열이 포함되지않을때  indexof
            //세션객체 가져와서 getSession
            HttpSession session = request.getSession();
            //만들어놓은  MemberUtil 이용 (공통처리를 위해 넣어준 인터셉터)
            MemberUtil.clearLoginData(session);
        }
    }
}
