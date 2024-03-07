package com.hospital.member.repositories;


import com.hospital.member.entities.Member;
import com.hospital.member.entities.QMember;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;
//Q클래스 (서비스를만들때 만드시 주입해줘야함)
//엔티티를 가지고 디비에 접근하기 위해 레포지티가 필요함
public interface MemberRepository extends JpaRepository<Member, Long>,
        QuerydslPredicateExecutor<Member> {




    //그냥 멤버보다 optional 형태로쓰자
    //멤버형태로 받아서
    //optional형태로 조회해서 반환값도 optional형태로 만들어짐
    //이메일과 유저 아이디 두가지 형태로 회원를 조회

    @EntityGraph(attributePaths = "authorities") //조회했을때 즉시 로딩
    Optional<Member> findByEmail(String email);

    @EntityGraph(attributePaths = "authorities") //조회했을때 즉시 로딩
    Optional<Member> findByUserId(String userId);


    default boolean existsByEmail(String email){
        QMember member = QMember.member;
        //이메일 존재하는지 체크
        return exists(member.email.eq(email));
    }

    default boolean existsByUserId(String userId){
        QMember member = QMember.member;

        return exists(member.userId.eq(userId));

    }

    //회원이없으면 회원이없다는 메세지 예외던져주기
}
