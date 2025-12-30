package edu.kh.project.board.model.service;

import java.util.List;

import edu.kh.project.board.model.dto.Comment;

public interface CommentService {

    /* ============================================
     *              댓글 조회 (SELECT)
     * ============================================ */
    
    /**
     * 특정 게시글의 댓글 목록 조회
     * 
     * <p>삭제되지 않은 댓글만 조회 (COMMENT_DEL_FL = 'N')</p>
     * <p>작성일 오름차순 정렬</p>
     * 
     * @param boardNo : 조회할 게시글 번호
     * @return commentList : 조회된 댓글 목록 (작성자 정보 포함)
     *         - 댓글이 없으면 빈 리스트 반환
     */
    List<Comment> selectCommentList(int boardNo);

    /* ============================================
     *              댓글 등록 (INSERT)
     * ============================================ */
    
    /**
     * 댓글/답글 등록
     * 
     * <p>일반 댓글: parentCommentNo = 0</p>
     * <p>답글(대댓글): parentCommentNo = 부모 댓글 번호</p>
     * 
     * @param comment : 등록할 댓글 정보
     *        - boardNo : 게시글 번호 (필수)
     *        - memberNo : 작성자 회원번호 (필수)
     *        - commentContent : 댓글 내용 (필수)
     *        - parentCommentNo : 부모 댓글 번호 (답글인 경우)
     * @return result : 삽입된 행의 수
     *         - 성공 시 1
     *         - 실패 시 0
     */
    int insertComment(Comment comment);

    /* ============================================
     *              댓글 수정 (UPDATE)
     * ============================================ */
    
    /**
     * 댓글 수정
     * 
     * <p>댓글 내용과 수정일을 갱신</p>
     * 
     * @param comment : 수정할 댓글 정보
     *        - commentNo : 수정할 댓글 번호 (필수)
     *        - commentContent : 수정할 내용 (필수)
     * @return result : 수정된 행의 수
     *         - 성공 시 1
     *         - 실패 시 0
     */
    int updateComment(Comment comment);

    /* ============================================
     *              댓글 삭제 (DELETE)
     * ============================================ */
    
    /**
     * 댓글 삭제 (소프트 삭제)
     * 
     * <p>실제 데이터 삭제가 아닌 삭제 플래그 변경</p>
     * <p>COMMENT_DEL_FL = 'Y'로 UPDATE</p>
     * 
     * @param commentNo : 삭제할 댓글 번호
     * @return result : 삭제 처리된 행의 수
     *         - 성공 시 1
     *         - 실패 시 0
     */
    int deleteComment(int commentNo);
}