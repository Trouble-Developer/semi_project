package edu.kh.project.board.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardReport {
//	CREATE TABLE "BOARD_REPORT" (
//			"REPORT_NO"	NUMBER		NOT NULL,
//			"MEMBER_NO"	NUMBER		NOT NULL,
//			"BOARD_NO"	NUMBER		NOT NULL,
//			"REPORT_REASON"	NVARCHAR2(100)		NOT NULL,
//			"REPORT_DATE"	DATE	DEFAULT SYSDATE	NOT NULL,
//			"PROCESS_YN"	CHAR(1)	DEFAULT 'N'	NOT NULL,
//			"REPORT_REASON_DETAIL" NVARCHAR2(1000)
//		);
	private int reportNo;
	private int memberNo;
	private int boardNo;
	private String reportReason;
	private String reportDate;
	private char processYn;
	private String reportReasonDetail;
}
