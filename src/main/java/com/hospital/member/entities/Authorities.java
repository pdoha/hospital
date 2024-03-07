package com.hospital.member.entities;


import com.hospital.member.Authority;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
//동일한 권한은 한개씩만 있게
//두개를 묶어서 unique로 만든다 ( 권한, 회원번호를 묶어서)
//유니크 제약조건 부여
@Table(
        indexes = @Index(name="uq_member_authority", columnList = "member_seq, authority" , unique = true)
)
public class Authorities {
    //권한을 상세하게 설정하기 위해 엔티티 하나 추가
    //권한이 여러개일때는 별도의 테이블을 만드는게 좋다)

    @Id
    @GeneratedValue
    private long seq; //회원번호 (일련번호)

    //한명의 회원이 여러개 권한 ( ManyToOne )
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="member_seq")
    private Member member;

    //회원당 여러개 권한 = 이넘클래스 (이넘타입)
    @Enumerated(EnumType.STRING)
    @Column(length = 15, nullable = false)
    private Authority authority;
}

//이 데이터를 꺼내서 memberinfo쪽에 유지해주면 자동으로 회원 권한을 체크해줌