package edu.kh.project.board.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.board.model.dto.Comment;
import edu.kh.project.board.model.dto.CommentReport;
import edu.kh.project.board.model.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper mapper;

    @Override
    public List<Comment> selectCommentList(int boardNo) {
        return mapper.selectCommentList(boardNo);
    }

    @Override
    public int insertComment(Comment comment) {
        return mapper.insertComment(comment);
    }

    @Override
    public int updateComment(Comment comment) {
        return mapper.updateComment(comment);
    }

    @Override
    public int deleteComment(int commentNo) {
        return mapper.deleteComment(commentNo);
    }

    /**
     * 게시글 작성자 번호 조회 (권한 체크용)
     */
    @Override
    public int selectBoardWriter(int boardNo, int boardCode) {
        return mapper.selectBoardWriter(boardNo, boardCode);
    }

    /* ============================================
     *           댓글 신고 관련 메서드
     * ============================================ */

    /**
     * 댓글 신고
     * @param report 신고 정보
     * @return 1: 성공, 0: 실패, -1: 중복 신고
     */
    @Override
    public int reportComment(CommentReport report) {
        
        // 1. 중복 신고 체크
        int check = mapper.checkCommentReport(report.getCommentNo(), report.getMemberNo());
        
        if (check > 0) {
            log.debug("[댓글 신고] 중복 신고 - commentNo: {}, memberNo: {}", 
                      report.getCommentNo(), report.getMemberNo());
            return -1; // 이미 신고함
        }
        
        // 2. 신고 등록
        return mapper.insertCommentReport(report);
    }

    /* ============================================
     *           댓글 좋아요 관련 메서드
     * ============================================ */

    /**
     * 댓글 좋아요 토글
     * @param commentNo 댓글 번호
     * @param memberNo 회원 번호
     * @return 좋아요 수
     */
    @Override
    public int toggleCommentLike(int commentNo, int memberNo) {
        
        // 1. 이미 좋아요 눌렀는지 체크
        int check = mapper.checkCommentLike(commentNo, memberNo);
        
        if (check > 0) {
            // 좋아요 취소
            mapper.deleteCommentLike(commentNo, memberNo);
            log.debug("[댓글 좋아요] 취소 - commentNo: {}, memberNo: {}", commentNo, memberNo);
        } else {
            // 좋아요 등록
            mapper.insertCommentLike(commentNo, memberNo);
            log.debug("[댓글 좋아요] 등록 - commentNo: {}, memberNo: {}", commentNo, memberNo);
        }
        
        // 2. 현재 좋아요 수 반환
        return mapper.countCommentLike(commentNo);
    }
}