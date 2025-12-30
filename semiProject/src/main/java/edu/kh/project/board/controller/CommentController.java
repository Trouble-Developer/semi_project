package edu.kh.project.board.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.kh.project.board.model.dto.Comment;
import edu.kh.project.board.model.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("comment")
@RequiredArgsConstructor
public class CommentController {

    /* ============================================
     *              의존성 주입 (DI)
     * ============================================ */
    
    /** 댓글 관련 비즈니스 로직을 처리하는 Service */
    private final CommentService service;

    /* ============================================
     *           댓글 목록 조회 (GET - Read)
     * ============================================ */
    
    /**
     * 특정 게시글의 댓글 목록 조회 (AJAX)
     * 
     * @param boardNo : 조회할 게시글 번호 (쿼리스트링)
     * @return commentList : 댓글 목록 (JSON 배열로 자동 변환)
     */
    @GetMapping("")
    public List<Comment> selectCommentList(
            @RequestParam("boardNo") int boardNo) {
        
        log.debug("[댓글 목록 조회] boardNo: {}", boardNo);
        
        return service.selectCommentList(boardNo);
    }

    /* ============================================
     *           댓글 등록 (POST - Create)
     * ============================================ */
    
    /**
     * @param comment : 요청 본문의 JSON → Comment 객체로 변환
     *                  (@RequestBody가 JSON → Java 객체 변환 수행)
     * @return result : 삽입된 행의 수 (1: 성공, 0: 실패)
     */
    @PostMapping("")
    public int insertComment(@RequestBody Comment comment) {
        
        log.debug("[댓글 등록] comment: {}", comment);
        
        return service.insertComment(comment);
    }

    /* ============================================
     *           댓글 수정 (PUT - Update)
     * ============================================ */
    
    /**
     * 댓글 수정 (AJAX)
     * 
     * <p><b>요청 URL:</b> PUT /comment</p>
     * <p><b>요청 본문:</b> 수정할 댓글 정보 (commentNo, commentContent)</p>
     * 
     * <pre>
     * [요청 예시]
     * PUT /comment
     * Content-Type: application/json
     * Body: {
     *   "commentNo": 123,
     *   "commentContent": "수정된 댓글 내용"
     * }
     * 
     * [응답]
     * 1 (성공) 또는 0 (실패)
     * </pre>
     * 
     * @param comment : 수정할 댓글 정보 (commentNo, commentContent 필수)
     * @return result : 수정된 행의 수 (1: 성공, 0: 실패)
     */
    @PutMapping("")
    public int updateComment(@RequestBody Comment comment) {
        
        log.debug("[댓글 수정] commentNo: {}, content: {}", 
                  comment.getCommentNo(), comment.getCommentContent());
        
        return service.updateComment(comment);
    }

    /* ============================================
     *           댓글 삭제 (DELETE - Delete)
     * ============================================ */
    
    /**
     * 댓글 삭제 (AJAX) - 논리적 삭제
     * 
     * [요청 예시]
     * DELETE /comment
     * Content-Type: application/json
     * Body: 123    ← 삭제할 댓글 번호 (숫자)
     * 
     * [응답]
     * 1 (성공) 또는 0 (실패)
     * 
     * @param commentNo : 삭제할 댓글 번호 (요청 본문에서 수신)
     * @return result : 삭제 처리된 행의 수 (1: 성공, 0: 실패)
     */
    @DeleteMapping("")
    public int deleteComment(@RequestBody int commentNo) {
        
        log.debug("[댓글 삭제] commentNo: {}", commentNo);
        
        return service.deleteComment(commentNo);
    }
}