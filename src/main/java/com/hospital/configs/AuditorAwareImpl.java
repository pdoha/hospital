package com.hospital.configs;

import com.hospital.member.service.MemberInfo;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
//파일이랑 게시글은 보통 본인이 올린것만 수정 삭제가능하다
//→ 회원정보가 필요하다
//로그인한 사용자정보를 직접 자동으로 넣어준다

@Component //스프링관리 객체
public class AuditorAwareImpl implements AuditorAware<String> {//넣어줄 회원정보는 내가 정한다
    @Override
    public Optional<String> getCurrentAuditor() {
        String userId = null;
        //로그인 여부
        //1. authentication 객체의 값이 있어야한다
        //2. getPrincipal 의 UserDetail의  구현체가 있어야한다
        //SecurityContextHolder를 이용해서 회원 정보 가져오기 getContext . getAuthentication객체
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        //가져온 객체( getPrincipal )가 UserDetails의 구현체 (memberInfo의 구현 객체)인지 체크
        if(auth != null && auth.getPrincipal() instanceof MemberInfo){
            MemberInfo memberInfo = (MemberInfo)auth.getPrincipal();
            //optional형태로 값을 만들어서 넣어준다 위에서  userId로 가져오기로 정했으니까 가져와야지
            userId = memberInfo.getUserId();

        }

        return Optional.ofNullable(userId); //null값 허용 ofNullable
    }





}
