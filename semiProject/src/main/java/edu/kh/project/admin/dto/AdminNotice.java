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
public class AdminNotice {

    private int boardNo;          // 게시글 번호
    private String boardTitle;    // 제목
    private String memberNickname;// 작성자
    private int readCount;        // 조회수
    private String boardWriteDate;// 작성일
    private String boardDelFl;    // 삭제 여부 (N / Y)
}
