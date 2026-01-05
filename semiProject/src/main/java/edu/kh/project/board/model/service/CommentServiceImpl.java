package edu.kh.project.board.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.board.model.dto.Comment;
import edu.kh.project.board.model.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper mapper;

    @Override
    public List<Comment> selectCommentList(int boardNo) {
        return mapper.selectCommentList(boardNo);
    }

    @Override
    public int insertComment(Comment comment) {
        return mapper.insertComment(comment);
    }

    @Override
    public int updateComment(Comment comment) {
        return mapper.updateComment(comment);
    }

    @Override
    public int deleteComment(int commentNo) {
        return mapper.deleteComment(commentNo);
    }

    // ⭐⭐⭐ 이 메서드 추가! ⭐⭐⭐
    /**
     * 게시글 작성자 번호 조회 (권한 체크용)
     */
    @Override
    public int selectBoardWriter(int boardNo, int boardCode) {
        return mapper.selectBoardWriter(boardNo, boardCode);
    }
}