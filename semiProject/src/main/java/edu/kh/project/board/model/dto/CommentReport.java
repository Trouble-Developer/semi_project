package edu.kh.project.board.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 댓글 신고 DTO
 * - COMMENT_REPORT 테이블과 매핑
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentReport {

    private int reportNo;        // 댓글 신고 번호 (PK)
    private String reportReason; // 신고 사유
    private String reportDate;   // 신고 날짜
    private int memberNo;        // 신고자 회원번호 (FK)
    private int commentNo;       // 신고된 댓글 번호 (FK)
    private String processYn;    // 처리 여부 (Y/N)
    
    // 조회용 추가 필드
    private String reporterNickname;  // 신고자 닉네임
    private String commentContent;    // 신고된 댓글 내용
    private String commentWriter;     // 댓글 작성자 닉네임
}