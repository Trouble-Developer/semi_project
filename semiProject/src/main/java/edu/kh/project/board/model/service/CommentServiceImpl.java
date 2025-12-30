package edu.kh.project.board.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.board.model.dto.Comment;
import edu.kh.project.board.model.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    /* ============================================
     *              의존성 주입 (DI)
     * ============================================ */
    
    /** 댓글 관련 DB 작업을 수행하는 Mapper */
    private final CommentMapper mapper;

    /* ============================================
     *              댓글 조회 (SELECT)
     * ============================================ */
    
    /**
     * 특정 게시글의 댓글 목록 조회
     * 
     * @param boardNo : 조회할 게시글 번호
     * @return commentList : 댓글 목록 (작성자 정보 포함)
     */
    @Override
    public List<Comment> selectCommentList(int boardNo) {
        
        log.debug("댓글 목록 조회 - boardNo: {}", boardNo);
        
        return mapper.selectCommentList(boardNo);
    }

    /* ============================================
     *              댓글 등록 (INSERT)
     * ============================================ */
    
    /**
     * 댓글/답글 등록
     * 
     * @param comment : 등록할 댓글 정보 (boardNo, memberNo, commentContent 필수)
     * @return result : 삽입 성공 시 1, 실패 시 0
     */
    @Override
    public int insertComment(Comment comment) {
        
        log.debug("댓글 등록 - comment: {}", comment);
        
        // TODO: 필요 시 XSS 필터링 처리
        // comment.setCommentContent(XSSFilter.filter(comment.getCommentContent()));
        
        return mapper.insertComment(comment);
    }

    /* ============================================
     *              댓글 수정 (UPDATE)
     * ============================================ */
    
    /**
     * 댓글 수정
     * 
     * @param comment : 수정할 댓글 정보 (commentNo, commentContent 필수)
     * @return result : 수정 성공 시 1, 실패 시 0
     */
    @Override
    public int updateComment(Comment comment) {
        
        log.debug("댓글 수정 - commentNo: {}, content: {}", 
                  comment.getCommentNo(), comment.getCommentContent());
        
        return mapper.updateComment(comment);
    }

    /* ============================================
     *              댓글 삭제 (DELETE)
     * ============================================ */
    
    /**
     * 댓글 삭제 (소프트 삭제)
     * 
     * <p>실제 DELETE가 아닌 COMMENT_DEL_FL = 'Y'로 UPDATE</p>
     * 
     * @param commentNo : 삭제할 댓글 번호
     * @return result : 삭제 성공 시 1, 실패 시 0
     */
    @Override
    public int deleteComment(int commentNo) {
        
        log.debug("댓글 삭제 (논리적 삭제) - commentNo: {}", commentNo);
        
        return mapper.deleteComment(commentNo);
    }
}