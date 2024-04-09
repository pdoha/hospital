package com.hospital.board.entities;

import com.hospital.commons.entities.Base;
import com.hospital.member.entities.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor @AllArgsConstructor
@Table(indexes = { //공지사항 등록순대로 정렬조건
        @Index(name="idx_boardData_basic", columnList = "notice DESC, createdAt DESC")
})
public class BoardData extends Base {


    @Id @GeneratedValue //증감
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="boardSeq")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="memberSeq")
    private Member member;

    @Column(length = 65, nullable = false)
    private String gid = UUID.randomUUID().toString();

    //카테고리
    @Column(length = 60)
    private String category; //분류

    @Column(length = 40, nullable = false)
    private String poster; //작성자

    private String guestPw; //비회원 비밀번호
    private boolean notice; //공지글 여부  -true

    @Column(nullable = false)
    private String subject;

    @Lob
    @Column(nullable = false)
    private String content;

    private int viewCount; //조회수

    @Column(length = 20)
    private String ip; //ip 주소 - 이상한 글 차단

    private String ua; //User-Agent : 브라우저 정보

    private Long num1; //추가필드 : 정수
    private Long num2; //추가필드 : 정수
    private Long num3; //추가필드 : 정수

    @Column(length = 100)
    private String text1; //추가필드 : 한줄 텍스트

    @Column(length = 100)
    private String text2; //추가필드 : 한줄 텍스트

    @Column(length = 100)
    private String text3; //추가필드 : 한줄 텍스트

    @Lob
    private String longText1; //추가필드 : 여러줄 텍스트

    @Lob
    private String longText2; //추가필드 : 여러줄 텍스트

    @Lob
    private String longText3; //추가필드 : 여러줄 텍스트

}
