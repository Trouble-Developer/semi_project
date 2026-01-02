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
	
	 private int reportNo;            // 신고 번호
	 private String reportReason;     // 신고 유형 (욕설, 스팸 등)
	 private String reportStatus;     // 처리 상태 (Y/N)
	 private String reportType;       // "게시글" / "댓글"
	 private String reportTypeCode;   // "board" / "comment"
	 private int targetNo;            // BOARD_NO 또는 COMMENT_NO
	 private int boardNo;             // 게시글 번호
	 private String boardTitle;       // 게시글 제목
	 private String reportDate;       // 신고일
	 
	 private String processYn;
	 private String reporterNickname;

}
