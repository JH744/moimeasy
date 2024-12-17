package com.kosa.moimeasy.board.service;

import com.kosa.moimeasy.board.dto.BoardDTO;
import com.kosa.moimeasy.board.entity.Board;
import org.springframework.stereotype.Service;
import java.util.List;
import com.kosa.moimeasy.board.repository.BoardRepository;
import com.kosa.moimeasy.user.entity.User;
import com.kosa.moimeasy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // 모든 게시글 조회
    public List<BoardDTO> getAllBoards() {
        return boardRepository.findAllBoardsWithWriterName();
    }
    // 게시글 상세
    public BoardDTO getBoardById(Long boardId) {
        return boardRepository.findByIdWithWriterName(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + boardId));
    }

    // 게시글 추가 및 수정
    public Board createBoard(BoardDTO boardDTO,Long userId) {

        User writer = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("작성자를 찾을 수 없습니다."));
        Board board=boardDTO.toEntity(writer);
        return boardRepository.save(board);
    }

    // 게시글 수정
    public Board updateBoard( BoardDTO boardDTO,Long userId) {
            long boardId = boardDTO.getBoardId();
            Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + boardId));
            board.setTitle(boardDTO.getTitle());
            board.setContent(boardDTO.getContent());
            board.setIsNotice(boardDTO.getIsNotice());
        return boardRepository.save(board);
    }

    //게시글 삭제하기
    public void deleteBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + id));
        boardRepository.delete(board);
    }
}