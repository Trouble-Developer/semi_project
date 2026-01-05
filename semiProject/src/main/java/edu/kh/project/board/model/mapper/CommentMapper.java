package edu.kh.project.board.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import edu.kh.project.board.model.dto.Comment;
import edu.kh.project.board.model.dto.CommentReport;

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

    /**
     * 게시글 작성자 번호 조회
     * 
     * @param boardNo 게시글 번호
     * @param boardCode 게시판 코드
     * @return 게시글 작성자의 회원 번호
     */
    int selectBoardWriter(@Param("boardNo") int boardNo, 
                          @Param("boardCode") int boardCode);

    /* ============================================
     *           댓글 신고 관련 메서드
     * ============================================ */

    /**
     * 댓글 신고 중복 체크
     * @param commentNo 댓글 번호
     * @param memberNo 신고자 회원번호
     * @return 이미 신고했으면 1, 아니면 0
     */
    int checkCommentReport(@Param("commentNo") int commentNo, 
                           @Param("memberNo") int memberNo);

    /**
     * 댓글 신고 등록
     * @param report 신고 정보
     * @return 성공 시 1
     */
    int insertCommentReport(CommentReport report);
}