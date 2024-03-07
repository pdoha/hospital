package com.hospital.board.entities;

import com.hospital.commons.entities.BaseMember;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class BoardData extends BaseMember {


    @Id @GeneratedValue //증감
    private Long seq;
    private String subject;
    private String content;
}
