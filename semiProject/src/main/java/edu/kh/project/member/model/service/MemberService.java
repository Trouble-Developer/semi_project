package edu.kh.project.member.model.service;

import edu.kh.project.member.model.dto.Member;

public interface MemberService {

	int checkId(String memberId);

	int checkNickname(String memberNickname);

	int checkEmail(String memberEmail);

	int signup(Member inputMember);

	Member login(Member inputMember);

	Member findId(String memberName, String memberRrn1, String memberEmail);

	Member findPw(String memberId, String memberName, String memberRrn1, String memberEmail);

	int resetPw(String memberId, String newPw);

	
}
