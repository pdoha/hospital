package com.hospital.file.entities;

import com.hospital.commons.entities.BaseMember;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor @AllArgsConstructor
@Table(indexes = {
        @Index(name="idx_fInfo_gid", columnList = "gid"),
        @Index(name = "idx_fInfo_gid_loc", columnList = "gid,location")
})
public class FileInfo extends BaseMember {
    @Id @GeneratedValue
    private Long seq; //파일 등록번호 ( 서버에 업로드하는 파일명 기준)

    //게시글 하나당 파일 그룹아이디
    @Column(length = 65, nullable = false)
    private String gid = UUID.randomUUID().toString(); // 따로 설정하지 않아도 자동 생성

    @Column(length = 65)
    private String location; //역할별로 나누는 위치에 대한 그룹값

    @Column(length = 80)
    private String fileName;  //기존 파일명 (다운받을때 필요)

    @Column(length = 30)
    private String extension; //확장자

    @Column(length = 65)
    private String fileType;

    @Transient //DB에 저장 X
    private String filePath; //서버에 실제올라간 파일 경로
    @Transient
    private String fileUrl; //브라우저 주소창에 입력해서 접근할 수 있는 경로
    @Transient
    private List<String> thumbsPath; //썸네일 이미지 경로
    @Transient
    private List<String> thumbsUrl; //브라우저 주소창에 입력해서 접근할 수 있는 경로

    private boolean done;
}
