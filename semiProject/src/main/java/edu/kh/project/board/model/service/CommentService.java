package edu.kh.project.board.model.service;

import java.util.List;
import edu.kh.project.board.model.dto.Comment;
import edu.kh.project.board.model.dto.CommentReport;

public interface CommentService {

    /**
     * 댓글 목록 조회
     */
    List<Comment> selectCommentList(int boardNo);

    /**
     * 댓글 등록
     */
    int insertComment(Comment comment);

    /**
     * 댓글 수정
     */
    int updateComment(Comment comment);

    /**
     * 댓글 삭제
     */
    int deleteComment(int commentNo);

    /**
     * 게시글 작성자 번호 조회 (권한 체크용)
     * 
     * @param boardNo 게시글 번호
     * @param boardCode 게시판 코드
     * @return 게시글 작성자의 회원 번호
     */
    int selectBoardWriter(int boardNo, int boardCode);

    /* ============================================
     *           댓글 신고 관련 메서드
     * ============================================ */

    /**
     * 댓글 신고
     * @param report 신고 정보
     * @return 1: 성공, 0: 실패, -1: 중복 신고
     */
    int reportComment(CommentReport report);
}