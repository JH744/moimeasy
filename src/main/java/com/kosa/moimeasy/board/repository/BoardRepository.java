package com.kosa.moimeasy.board.repository;

import com.kosa.moimeasy.board.dto.BoardDTO;
import com.kosa.moimeasy.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("SELECT new com.kosa.moimeasy.board.dto.BoardDTO(b.boardId, b.title, b.content, b.isNotice, u.nickname, u.profileImage ,b.tag, b.createAt,b.updateAt) " +
            "FROM Board b " +
            "JOIN b.writer u "+ // Board와 User를 조인
            "ORDER BY b.boardId DESC")
    List<BoardDTO> findAllBoardsWithWriterName();


    @Query("SELECT new com.kosa.moimeasy.board.dto.BoardDTO(b.boardId, b.title, b.content, b.isNotice, u.nickname, u.profileImage ,b.tag, b.createAt,b.updateAt) " +
            "FROM Board b " +
            "JOIN b.writer u "+ // Board와 User를 조인
            "WHERE b.boardId = :boardId " +
            "ORDER BY b.boardId DESC")
    Optional<BoardDTO> findByIdWithWriterName(@Param("boardId") Long boardId);
}
