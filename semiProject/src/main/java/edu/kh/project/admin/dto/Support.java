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
public class Support {
    private int supportNo; 			// 고객지원글 번호
    private String supportTitle;	// 고객지원글 제목
    private String supportContent;	// 고객지원글 본문
    private String supportReply;	// 관리자 답변
    private String supportStatus;	// 문의 처리 상태
}
