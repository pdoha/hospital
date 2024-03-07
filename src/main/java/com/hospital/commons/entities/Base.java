package com.hospital.commons.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

//공통으로 사용할 속성
@Getter @Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class) //날짜 & 시간
public abstract class Base {
    @CreatedDate
    @Column(updatable = false) //-> 처음에만 값이 들어가야함
    private LocalDateTime createdAt; //작성일자 or 가입일자

    @LastModifiedDate
    @Column(insertable = false) //-> 수정시에만 값이 들어가야함
    private LocalDateTime modigiedAt;
}
