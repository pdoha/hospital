package com.hospital.board.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor @AllArgsConstructor
public class BoardView {

    @Id
    private Long seq; //게시글 번호
    @Id
    private int uid;
}
