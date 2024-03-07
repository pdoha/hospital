package com.hospital.configs;

import com.hospital.member.service.LoginFailureHandler;
import com.hospital.member.service.LoginSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableMethodSecurity //인가를 애노테이션가지고 메서드 단위로 통제
@Configuration
public class SecurityConfig {


    @Bean
    //설정무력화
    public SecurityFilterChain filterChain(HttpSecurity http) throws  Exception{
        //설정하나도 안하고 무력화한 후에 나중에 하나씩 추가할거임

        //인증 설정 - 로그인
        http.formLogin(f -> {f.loginPage("/member/login")
                                .usernameParameter("userId")
                                .passwordParameter("password")
                                //스프링시큐리티가 대신해주는 기본값
//                              .defaultSuccessUrl("/") // 성공시 메인페이지로 이동
//                              .failureUrl("/member/login?error=true"); //실패시 url

                                //내가 직접 설정
                                .successHandler(new LoginSuccessHandler())
                                .failureHandler(new LoginFailureHandler());

        });

        //로그아웃
        http.logout(c -> {
            c.logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                    .logoutSuccessUrl("/member/login"); //로그아웃하면 로그인 페이지로 이동
        });

        //인가 설정 - 접근 통제
        http.authorizeHttpRequests(c -> {
            //개발하느라 잠시 주석걸어놈! 나중에 배포전에 풀자!!
            c//.requestMatchers("/mypage/**").authenticated() //회원전용
//                  .requestMatchers("/admin/**").hasAnyAuthority("ADMIN", "MANAGER") //관리자 & 매니저만 접근
                    .anyRequest().permitAll(); //그외 모든 페이지는 모두 접근 가능
        });

        //오류페이지 상세 설정
        //기본동작은 -> 로그인페이지로 이동
        //AuthenticationEntryPoint를 따로 정의 할 수 있음
        http.exceptionHandling(c -> {
            c.authenticationEntryPoint(( req,  res, e) -> {
                String URL = req.getRequestURI();
                if( URL.indexOf("/admin") != -1 ){ //관리자페이지에서만 다르게 동작
                    //관리자페이지면 접근 제한 응답코드
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED); //401
                }
                else{ //회원전용 페이지이면 로그인페이지로 이동
                    res.sendRedirect(req.getContextPath() + "member/login");

                }



            });
        });
        return http.build();
    }

    @Bean
    //비밀번호 해시화
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
