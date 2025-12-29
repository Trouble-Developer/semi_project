package edu.kh.project.comment.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.kh.project.comment.dto.Comment;
import edu.kh.project.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 댓글 관련 REST API를 처리하는 컨트롤러
 * 
 * @RestController = @Controller + @ResponseBody
 * - 모든 메서드의 반환값이 응답 본문(body)으로 전달됨
 * - JSON 형태로 자동 변환되어 응답
 * 
 * REST API 설계:
 * - 하나의 자원(URI)에 대해 HTTP Method로 CRUD를 구분
 * - 자원: /comment
 * 
 * GET    /comment?boardNo=1  : 댓글 조회 (Read)
 * POST   /comment             : 댓글 등록 (Create)
 * PUT    /comment             : 댓글 수정 (Update)
 * DELETE /comment             : 댓글 삭제 (Delete)
 * 
 * @author 조창래 (오합지졸 팀)
 * @since 2024-12-29
 */
@RestController
@RequestMapping("comment")
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    
    private final CommentService service;

    /**
     * 댓글 목록 조회 (AJAX)
     * 
     * @param boardNo : 조회할 게시글 번호 (쿼리스트링으로 전달)
     * @return commentList : 조회된 댓글 목록 (JSON 형태로 자동 변환)
     * 
     * 요청 예시: GET /comment?boardNo=123
     * 응답 예시: [{commentNo:1, commentContent:"댓글1", ...}, {...}, ...]
     */
    @GetMapping("")
    public List<Comment> selectCommentList(
            @RequestParam("boardNo") int boardNo) {
        
        log.debug("댓글 조회 요청 - boardNo: {}", boardNo);
        
        return service.selectCommentList(boardNo);
    }
    
    /**
     * 댓글/답글 등록 (AJAX)
     * 
     * @param comment : 요청 본문(body)에 담긴 JSON 데이터를 Comment 객체로 변환
     *                  @RequestBody 어노테이션이 JSON -> Java 객체 변환 수행
     * @return result : 삽입된 행의 개수 (성공: 1, 실패: 0)
     * 
     * 요청 예시: POST /comment
     *           body: {"commentContent":"댓글 내용", "boardNo":123, "memberNo":1, "parentCommentNo":0}
     * 응답 예시: 1 (숫자)
     * 
     * ★ 답글 등록 시에는 parentCommentNo에 부모 댓글 번호를 포함하여 전송
     */
    @PostMapping("")
    public int insertComment(@RequestBody Comment comment) {
        
        log.debug("댓글 등록 요청 - comment: {}", comment);
        
        return service.insertComment(comment);
    }
    
    /**
     * 댓글 삭제 (AJAX)
     * 
     * @param commentNo : 삭제할 댓글 번호 (요청 본문에서 받아옴)
     * @return result : 삭제된 행의 개수 (성공: 1, 실패: 0)
     * 
     * 요청 예시: DELETE /comment
     *           body: 123 (숫자)
     * 응답 예시: 1 (숫자)
     * 
     * ★ 실제로는 물리적 삭제가 아닌 논리적 삭제 (COMMENT_DEL_FL을 'Y'로 변경)
     */
    @DeleteMapping("")
    public int deleteComment(@RequestBody int commentNo) {
        
        log.debug("댓글 삭제 요청 - commentNo: {}", commentNo);
        
        return service.deleteComment(commentNo);
    }
    
    /**
     * 댓글 수정 (AJAX)
     * 
     * @param comment : 수정할 댓글 정보 (commentNo, commentContent)
     * @return result : 수정된 행의 개수 (성공: 1, 실패: 0)
     * 
     * 요청 예시: PUT /comment
     *           body: {"commentNo":123, "commentContent":"수정된 내용"}
     * 응답 예시: 1 (숫자)
     */
    @PutMapping("")
    public int updateComment(@RequestBody Comment comment) {
        
        log.debug("댓글 수정 요청 - comment: {}", comment);
        
        return service.updateComment(comment);
    }
}