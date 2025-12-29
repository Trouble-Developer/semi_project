package edu.kh.project.comment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.comment.dto.Comment;
import edu.kh.project.comment.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;

/**
 * 댓글 관련 비즈니스 로직을 구현하는 Service 구현 클래스
 * 
 * - @Service: Service 계층임을 명시하고 Bean으로 등록
 * - @Transactional: DML(INSERT, UPDATE, DELETE) 수행 시 트랜잭션 처리
 * - @RequiredArgsConstructor: final 필드에 대한 생성자 자동 생성 (의존성 주입)
 * 
 * @author 조창래 (오합지졸 팀)
 * @since 2024-12-29
 */
@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    
    private final CommentMapper mapper;  // 생성자를 통한 의존성 주입

    /**
     * 특정 게시글의 댓글 목록 조회
     */
    @Override
    public List<Comment> selectCommentList(int boardNo) {
        return mapper.selectCommentList(boardNo);
    }

    /**
     * 댓글/답글 등록
     */
    @Override
    public int insertComment(Comment comment) {
        return mapper.insertComment(comment);
    }

    /**
     * 댓글 삭제 (논리적 삭제 - COMMENT_DEL_FL을 'Y'로 변경)
     */
    @Override
    public int deleteComment(int commentNo) {
        return mapper.deleteComment(commentNo);
    }

    /**
     * 댓글 수정
     */
    @Override
    public int updateComment(Comment comment) {
        return mapper.updateComment(comment);
    }
}