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
	private int reportNo;		 // 신고글 번호
	private String reportReason; // 신고 사유
	private String reportTarget; // 신고 대상
	private String reportStatus; // 신고 처리 상태
}
