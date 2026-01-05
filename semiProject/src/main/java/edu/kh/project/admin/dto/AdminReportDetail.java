package edu.kh.project.admin.dto;

import java.util.Date;
import java.util.List;

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
public class AdminReportDetail {
	
    // 신고 정보
    private int reportNo;
    private String reportType;
    private String reportReason;
    private Date reportDate;
    private String reportStatus;
    private String reporterNickname;

    // 신고 대상 게시글
    private int boardNo;
    private String boardTitle;
    private String boardContent;

    // 댓글 목록 (신고용 전용 DTO)
    private List<AdminReportComment> commentList;

}
