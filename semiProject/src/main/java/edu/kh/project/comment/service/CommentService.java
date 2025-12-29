package edu.kh.project.comment.service;

import java.util.List;

import edu.kh.project.comment.dto.Comment;

/**
 * 댓글 관련 비즈니스 로직을 정의하는 Service 인터페이스
 * 
 */
public interface CommentService {

    /**
     * 특정 게시글의 댓글 목록 조회
     * @param boardNo : 조회할 게시글 번호
     * @return commentList : 조회된 댓글 목록
     */
    List<Comment> selectCommentList(int boardNo);

    /**
     * 댓글/답글 등록
     * @param comment : 등록할 댓글 정보
     * @return result : 삽입 성공 시 1, 실패 시 0
     */
    int insertComment(Comment comment);

    /**
     * 댓글 삭제
     * @param commentNo : 삭제할 댓글 번호
     * @return result : 삭제 성공 시 1, 실패 시 0
     */
    int deleteComment(int commentNo);

    /**
     * 댓글 수정
     * @param comment : 수정할 댓글 정보
     * @return result : 수정 성공 시 1, 실패 시 0
     */
    int updateComment(Comment comment);
}