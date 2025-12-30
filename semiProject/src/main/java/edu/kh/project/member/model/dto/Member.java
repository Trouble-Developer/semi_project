package edu.kh.project.member.model.dto;

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
public class Member {
    // DB 컬럼과 매칭되는 필드
    private int memberNo;           // MEMBER_NO
    private String memberName;		// MEMBER_NAME
    private String memberId;        // MEMBER_ID
    private String memberPw;        // MEMBER_PW
    private String memberNickname;  // MEMBER_NICKNAME
    private String memberEmail;     // MEMBER_EMAIL
    private String memberRrn;       // MEMBER_RRN (주민번호 전체: 000000-1234567)
    private String memberTel;       // MEMBER_TEL
    private String memberAddress;   // MEMBER_ADDRESS (주소 전체)
    private String profileImg;    	// PROFILE_IMG
    private String enrollDate;      // ENROLL_DATE
    private String memberDelFl;     // MEMBER_DEL_FL
    private int authority;          // AUTHORITY

    // --- DB에는 없지만 HTML 폼에서 입력받을 때 쓰는 임시 필드 ---
    private String memberRrn1;      // 주민번호 앞자리
    private String memberRrn2;      // 주민번호 뒷자리
}