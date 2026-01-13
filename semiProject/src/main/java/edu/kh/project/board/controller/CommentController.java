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
import org.springframework.web.bind.annotation.SessionAttribute;

import edu.kh.project.board.model.dto.Comment;
import edu.kh.project.board.model.dto.CommentReport;
import edu.kh.project.board.model.service.CommentService;
import edu.kh.project.member.model.dto.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("comment")
@RequiredArgsConstructor
public class CommentController {

    /* ============================================
     *              상수 선언
     * ============================================ */
    
    /** 고객지원 게시판 코드 */
    private static final int BOARD_CODE_SUPPORT = 5;
    
    /** 관리자 권한 코드 */
    private static final int AUTHORITY_ADMIN = 2;

    /* ============================================
     *              의존성 주입 (DI)
     * ============================================ */
    
    private final CommentService service;

    /* ============================================
     *           댓글 목록 조회 (GET - Read)
     * ============================================ */
    
    @GetMapping("")
    public List<Comment> selectCommentList(@RequestParam("boardNo") int boardNo) {
        log.debug("[댓글 목록 조회] boardNo: {}", boardNo);
        return service.selectCommentList(boardNo);
    }

    /* ============================================
     *           댓글 등록 (POST - Create)
     * ============================================ */
    
    /**
     * 댓글 등록 (AJAX)
     * 
     * [보안 강화]
     * - 클라이언트가 보낸 memberNo, boardWriter 값은 무시
     * - 세션에서 memberNo를 가져와서 덮어씀
     * - DB에서 실제 boardWriter를 조회해서 비교
     * 
     * [고객지원 게시판 권한]
     * - 관리자(authority=2) → 허용
     * - 게시글 작성자 본인 → 허용
     * - 그 외 일반 사용자 → 차단
     * 
     * @param comment 댓글 정보 (클라이언트 전송)
     * @param loginMember 로그인 회원 정보 (세션)
     * @return 1: 성공, 0: 실패, -1: 권한 없음
     */
    @PostMapping("")
    public int insertComment(
            @RequestBody Comment comment,
            @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        
        log.debug("[댓글 등록 요청] comment: {}", comment);
        
        // ===== 1) 로그인 체크 =====
        if (loginMember == null) {
            log.warn("[권한 없음] 로그인이 필요합니다");
            return -1;
        }
        
        // ===== 2) 작성자 번호는 무조건 세션 값으로 덮어쓰기 (보안!) =====
        // 클라이언트가 보낸 memberNo는 무시!
        comment.setMemberNo(loginMember.getMemberNo());
        
        log.debug("[보안] 댓글 작성자를 세션 값으로 설정: {}", loginMember.getMemberNo());
        
        // ===== 3) 고객지원 게시판 권한 체크 =====
        if (comment.getBoardCode() == BOARD_CODE_SUPPORT) {
            
            // DB에서 실제 게시글 작성자 조회 (클라이언트 값 신뢰 안 함!)
            int boardWriterNo = service.selectBoardWriter(
                comment.getBoardNo(), 
                comment.getBoardCode()
            );
            
            log.debug("[권한 체크] 게시글 작성자(DB): {}, 로그인 회원: {}, 권한: {}", 
                      boardWriterNo, loginMember.getMemberNo(), loginMember.getAuthority());
            
            // 관리자면 통과
            if (loginMember.getAuthority() == AUTHORITY_ADMIN) {
                log.debug("[권한 확인] 관리자 - 댓글 작성 허용");
                return service.insertComment(comment);
            }
            
            // 게시글 작성자 본인이면 통과
            if (loginMember.getMemberNo() == boardWriterNo) {
                log.debug("[권한 확인] 게시글 작성자 본인 - 댓글 작성 허용");
                return service.insertComment(comment);
            }
            
            // 그 외 차단
            log.warn("[권한 없음] 고객지원 게시글은 작성자와 관리자만 댓글 작성 가능");
            return -1;
        }
        
        // ===== 4) 일반 게시판은 모두 허용 =====
        return service.insertComment(comment);
    }

    /* ============================================
     *           댓글 수정 (PUT - Update)
     * ============================================ */
    
    @PutMapping("")
    public int updateComment(@RequestBody Comment comment) {
        log.debug("[댓글 수정] commentNo: {}, content: {}", 
                  comment.getCommentNo(), comment.getCommentContent());
        return service.updateComment(comment);
    }

    /* ============================================
     *           댓글 삭제 (DELETE - Delete)
     * ============================================ */
    
    @DeleteMapping("")
    public int deleteComment(@RequestBody int commentNo) {
        log.debug("[댓글 삭제] commentNo: {}", commentNo);
        return service.deleteComment(commentNo);
    }

    /* ============================================
     *           댓글 신고 (POST)
     * ============================================ */
    
    /**
     * 댓글 신고 (AJAX)
     * 
     * @param report 신고 정보 (commentNo, reportReason)
     * @param loginMember 로그인 회원 정보 (세션)
     * @return 1: 성공, 0: 실패, -1: 중복 신고, -2: 로그인 필요
     */
    @PostMapping("/report")
    public int reportComment(
            @RequestBody CommentReport report,
            @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        
        log.debug("[댓글 신고 요청] commentNo: {}, reason: {}", 
                  report.getCommentNo(), report.getReportReason());
        
        // 1) 로그인 체크
        if (loginMember == null) {
            log.warn("[댓글 신고] 로그인 필요");
            return -2;
        }
        
        // 2) 신고자 번호는 세션에서 가져오기 (보안!)
        report.setMemberNo(loginMember.getMemberNo());
        
        // 3) 신고 처리
        return service.reportComment(report);
    };

    /* ============================================
     *           댓글 좋아요 (POST)
     * ============================================ */
    
    /**
     * 댓글 좋아요 (AJAX)
     * 
     * @param commentNo 댓글 번호
     * @param loginMember 로그인 회원 정보 (세션)
     * @return 좋아요 수 (성공) / -1 (로그인 필요)
     */
    @PostMapping("/like")
    public int toggleCommentLike(
            @RequestBody int commentNo,
            @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        
        log.debug("[댓글 좋아요 요청] commentNo: {}", commentNo);
        
        // 로그인 체크
        if (loginMember == null) {
            log.warn("[댓글 좋아요] 로그인 필요");
            return -1;
        }
        
        // 좋아요 누른 후 현재 좋아요 수 반환
        return service.toggleCommentLike(commentNo, loginMember.getMemberNo());
    }
}