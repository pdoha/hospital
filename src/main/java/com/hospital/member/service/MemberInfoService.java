package com.hospital.member.service;

import com.hospital.member.entities.Authorities;
import com.hospital.member.entities.Member;
import com.hospital.member.repositories.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberInfoService implements UserDetailsService {

    //DB에서도 조회할 수 있게 의존성 추가
    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws
     UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username) //1번째는 이메일 조회
                .orElseGet(() -> memberRepository.findByUserId(username) //2번째는 아이디로 조회
                        .orElseThrow(() -> new UsernameNotFoundException(username))); //3번째에도 없으면 예외 던진다

        //authorities 데이터를 꺼내서 memberinfo쪽에 유지해주면 자동으로 회원 권한을 체크해줌
        List<SimpleGrantedAuthority> authorities = null;
        List<Authorities> tmp = member.getAuthorities(); //디비에서 조회했던 데이터를
        if (tmp != null) { //null이 아닐때
            //리스트형태로 값을 가공해서 넣는다
            //가져온 상수 데이터 -> GrantedAuthority객체로 변환
            authorities = tmp.stream() //tmp값이 있으면 stream 사용해서
                    .map(s -> new SimpleGrantedAuthority(s.getAuthority().name()))//변환메서드 map ( )
                    .toList(); //변환된 데이터를 리스트 형태로                        //넘어온 데이터는 엔티티 s
                                                                                 //넘어오면 SimpleGrantedAuthority에 넣는다(상수이므로 문자열로 바꿔서)
        }
        //->가져왔던 데이터에서 상수만 뽑아서 반환값으로 문자열로 사용
        //편하게 쓰려고 SimpleGrantedAuthority 객체를 사용해서 권한만 문자열로 넣으면됨

        //userDetails 구현체로 반환
        return MemberInfo.builder()
                .email(member.getEmail())
                .userId(member.getUserId())
                .password(member.getPassword())
                .member(member)
                .authorities(authorities)
                .build();
    }
}
