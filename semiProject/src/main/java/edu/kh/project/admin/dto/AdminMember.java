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
public class AdminMember {
    // DB 컬럼과 매칭되는 필드
    private int memberNo;           // MEMBER_NO
    private String memberEmail;     // MEMBER_EMAIL
    private String memberNickname;  // MEMBER_NICKNAME
    private String memberDelFl;     // MEMBER_DEL_FL
    private String enrollDate;      // ENROLL_DATE

}