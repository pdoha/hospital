package com.hospital.member.entities;

import com.hospital.commons.entities.Base;
import com.hospital.file.entities.FileInfo;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor @AllArgsConstructor
public class Member extends Base{
    //기본키는 Wrapper class타입으로 입력
    @Id @GeneratedValue //자동증감 추가
    private Long seq;

    @Column(length = 65, nullable = false)
    private String gid;

    //이메일 & 아이디는 중복 X :  unique = true
    @Column(length = 80, nullable = false, unique = true)
    private String email;

    @Column(length = 40, nullable = false, unique = true)
    private String userId;

    @Column(length = 65, nullable = false)
    private String password;

    @Column(length = 40, nullable = false)
    private String name;

    @ToString.Exclude //순환참조 방지를 위해 toString 출력배제
    //한꺼번에 여러개 조회 @OneToMany : (fetch타입은 필요할때 조회할 수 있게)
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Authorities> authorities = new ArrayList<>();

    @Transient
    private FileInfo profileImage;
}
