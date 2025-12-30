package edu.kh.project.board.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.board.model.dto.Comment;

/**
 * 댓글 관련 SQL을 수행하는 Mapper 인터페이스
 * - MyBatis가 자동으로 구현체를 생성하여 Bean으로 등록
 * 
 */
@Mapper
public interface CommentMapper {

	/** 
	 * 특정 게시글의 댓글 목록 조회
	 * @param boardNo : 조회할 게시글 번호
	 * @return commentList : 조회된 댓글 목록
	 */
	List<Comment> selectCommentList(int boardNo);
	
	
	
	/**
	 * 댓글/답글 등록
	 * @param comment : 등록할 댓글 정보
	 * @return result : 삽입된 행의 갯수
	 */
	int insertComment(Comment comment);
	
	/**
	 * @param commentNo : 삭제할 댓글 번호
	 * @return result : 수정된 행의 개수
	 */
	int deleteComment(int commentNo);
	
	
	/**
	 * @param comment : 수정할 댓글 정보
	 * @return result : 수정된 행의 개수
	 */
	int updateComment(Comment comment);
}
