package com.kosa.moimeasy.board.controller;

import com.kosa.moimeasy.board.dto.BoardDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RestController;
import com.kosa.moimeasy.board.entity.Board;
import com.kosa.moimeasy.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="모임 게시판 페이지" , description="Board List API")
@RestController
@RequestMapping("/api/v1/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 모든 게시글 조회하기
    @Operation(summary = "게시글 목록 조회" ,description ="모든 게시글을 조회하기" )
    @GetMapping
    public ResponseEntity<List<BoardDTO>> getAllBoards() {
        return ResponseEntity.ok(boardService.getAllBoards());
    }

    //게시글 상세
    @Operation(summary = "게시글 상세 조회" ,description ="특정 게시글의 상세내용 조회하기" )
    @GetMapping("/{id}")
    public ResponseEntity<BoardDTO> getBoardById(@PathVariable("id") Long boardId) {
        return ResponseEntity.ok(boardService.getBoardById(boardId));
    }

    // 게시글 추가
    @Operation(summary = "게시글 상세 조회" ,description ="특정 게시글의 상세내용 조회하기" )
    @PostMapping("/{id}")
    public ResponseEntity<Board> createBoard(@RequestBody BoardDTO board, @PathVariable("id") Long userId) {
        return ResponseEntity.ok(boardService.createBoard(board,userId));
    }

    // 게시글 수정
    @Operation(summary = "게시글 수정" ,description ="특정 게시글의 내용 수정하기" )
    @PutMapping("/{id}")
    public ResponseEntity<Board> updateBoard(@RequestBody BoardDTO boardDTO, @PathVariable("id") Long userId) {
        return ResponseEntity.ok(boardService.updateBoard(boardDTO,userId));
    }

    // 게시글 삭제하기
    @Operation(summary = "게시글 삭제" ,description ="특정 게시글을 삭제하기" )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }
}