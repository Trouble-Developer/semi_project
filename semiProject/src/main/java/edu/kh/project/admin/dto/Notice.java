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
public class Notice {
	
    private int noticeNo; 			// 공지사항 번호
    private String noticeTitle; 	// 공지사항 제목
    private String noticeContent;	// 공지사항 본문
    private String noticeCreateDate;// 공지사항 작성일
    private String noticeDelFl;		// 공지사항 삭제 여부
}
