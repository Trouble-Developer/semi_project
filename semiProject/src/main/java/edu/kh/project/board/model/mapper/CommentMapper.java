package edu.kh.project.board.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import edu.kh.project.board.model.dto.Comment;

@Mapper
public interface CommentMapper {

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

    // ⭐⭐⭐ 이 메서드 추가! ⭐⭐⭐
    /**
     * 게시글 작성자 번호 조회
     * 
     * @param boardNo 게시글 번호
     * @param boardCode 게시판 코드
     * @return 게시글 작성자의 회원 번호
     */
    int selectBoardWriter(@Param("boardNo") int boardNo, 
                          @Param("boardCode") int boardCode);
}