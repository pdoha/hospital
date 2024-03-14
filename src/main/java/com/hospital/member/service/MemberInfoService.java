package com.hospital.member.service;

import com.hospital.commons.ListData;
import com.hospital.commons.Pagination;
import com.hospital.commons.Utils;
import com.hospital.file.entities.FileInfo;
import com.hospital.file.service.FileInfoService;
import com.hospital.member.controllers.MemberSearch;
import com.hospital.member.entities.Authorities;
import com.hospital.member.entities.Member;
import com.hospital.member.entities.QMember;
import com.hospital.member.repositories.MemberRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
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
    private final FileInfoService fileInfoService; //프로필 이미지
    private final HttpServletRequest request;
    private final EntityManager em;
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

        //프로필 이미지 처리
        //넘어온 데이터는 list 형태, 완료된것만 가져온다
        List<FileInfo> files = fileInfoService.getListDone(member.getGid());
        if(files != null && !files.isEmpty()){
            member.setProfileImage(files.get(0));
        }

        //userDetails 구현체로 반환
        return MemberInfo.builder()
                .email(member.getEmail())
                .userId(member.getUserId())
                .password(member.getPassword())
                .member(member)
                .authorities(authorities)
                .build();
    }

    //회원목록
    public ListData<Member> getList(MemberSearch search){
        //페이지는 음수가 되면안되므로 기본값 고정
        int page = Utils.onlyPositiveNumber(search.getPage(), 1); //페이지 번호
        int limit = Utils.onlyPositiveNumber(search.getLimit(),20); //1페이지당 레코드 개수
        int offset = (page -1) * limit; //페이지별 레코드 시작위치

        BooleanBuilder andBuilder = new BooleanBuilder(); //조건식
        QMember member = QMember.member; //회원관리니까 필요함

        //정렬 PathBuilder
        PathBuilder<Member> pathBuilder = new PathBuilder<>(Member.class, "member");

        List<Member> items = new JPAQueryFactory(em) //쿼리 DSL 사용
                .selectFrom(member) //멤버로부터 조회
                .leftJoin(member.authorities) //권한 부분 출력
                .fetchJoin() //즉시로딩
                .where(andBuilder) //조건식
                .limit(limit)
                .offset(offset)
                .orderBy(new OrderSpecifier(Order.DESC, pathBuilder.get("createdAt")))
                .fetch();

        //페이징 처리
        //검색 조건에 따라서 개수가 달라지므로 조건식 Predicate
        int total = (int)memberRepository.count(andBuilder); //총 레코드 개수
        Pagination pagination = new Pagination(page, total, 10, limit, request);

        return new ListData<>(items, pagination);


    }
}
