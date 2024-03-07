package com.hospital.admin.config.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

@Data
@Entity
public class Configs {

    @Id //기본키
    @Column(length = 60)
    private String code;

    @Lob //데이터 많아서
    private String data;
}
