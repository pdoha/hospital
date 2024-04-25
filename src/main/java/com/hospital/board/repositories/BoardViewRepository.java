package com.hospital.board.repositories;

import com.hospital.board.entities.BoardView;
import com.hospital.board.entities.BoardViewId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface BoardViewRepository extends JpaRepository<BoardView, BoardViewId>,
        QuerydslPredicateExecutor<BoardView> {
}
