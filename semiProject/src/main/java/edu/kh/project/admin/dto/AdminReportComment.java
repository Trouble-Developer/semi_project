package edu.kh.project.admin.dto;

import java.util.Date;

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
public class AdminReportComment { // 신고 상세 전요 dto
    private int commentNo;
    private String commentContent;
    private String memberNickname;
    private String profileImg;

    // ⭐ 핵심: Date로 고정
    private Date commentWriteDate;

    private String commentDelFl;
    private Integer parentCommentNo;

}
