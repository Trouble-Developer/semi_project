package edu.kh.project.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSupport {
    private int boardNo;
    private String boardTitle;
    private String memberNickname;
    private String boardWriteDate;
    private String boardDelFl;

}
