package edu.kh.project.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Report {

    /* ================= 공통 ================= */
    private int reportNo;                // 신고 번호
    private String reportType;           // BOARD / COMMENT
    private int targetNo;                // BOARD_NO or COMMENT_NO
    private String reportReason;         // 신고 사유
    private String reportDate;           // 신고일
    private String reportStatus;         // 처리 여부 (Y / N)
    private String reporterNickname;     // 신고자 닉네임
    private String reportDetail;		 // 세부 신고 사유

    /* ================= 게시글 정보 ================= */
    private int boardNo;                 // 게시글 번호
    private String targetTitle;          // 게시글 제목 (신고 대상 제목)
}