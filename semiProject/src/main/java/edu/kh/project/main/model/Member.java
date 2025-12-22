package edu.kh.project.main.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {
	private int memberNo;
	private String memberId;
	private String memberNickname;
	private String memberEmail;
	private String memberRrn;
	private String profileImg;
	private String enrollDate;
	private String memberTel;
	private String memberDelFl;
	private int authority;
}
