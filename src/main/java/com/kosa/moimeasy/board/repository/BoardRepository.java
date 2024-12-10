package com.kosa.moimeasy.board.repository;

import com.kosa.moimeasy.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
