package com.hospital.member.service;

import com.hospital.member.entities.Member;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.Collection;


@Data
@Builder
public class MemberInfo implements UserDetails {
    //외부에서 세팅하면 쓸 수 있게 추가했음
    private String email;
    private String userId;
    private String password;
    private Member member;

    //권한 (인가)
    //마이페이지 -> 로그인한 사람만 / 관리자페이지는 관리자 권한이 있는 사람만
    //GrantedAuthority 의 하위 클래스가 전부 가능하니까 <?   >
    private Collection<? extends GrantedAuthority> authorities;

    //특정 페이지의 인가를 승인 (권한)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    //비밀번호
    @Override
    public String getPassword() {
        return password; //스프링 시큐리티 내의 userDetails의 구현체들에 값만 넣어주면 알아서 작동함
    }

    //userId 아이디
    @Override
    public String getUsername() {
        return StringUtils.hasText(email) ? email : userId; //이메일이 있으면 이메일 없으면 userId
    }

    //계정이 만료 상태인지 체크
    //false이면 로그인이 안되서 우선 true로 바꿔주고 추후 관리자 설정에서 바꾼다
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //비밀번호가 만료되지않은 상태인지
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //탈퇴
    //탈퇴시 기존정보는 유지하지만 로그인이 안되게끔 만들어준다
    @Override
    public boolean isEnabled() {
        return true;
    }
}
