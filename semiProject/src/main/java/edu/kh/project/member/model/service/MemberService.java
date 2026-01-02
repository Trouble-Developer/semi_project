package edu.kh.project.member.model.service;

import edu.kh.project.member.model.dto.Member;

public interface MemberService {

    int checkId(String memberId);

    int checkNickname(String memberNickname);

    int checkEmail(String memberEmail);

    int signup(Member inputMember);

    Member login(Member inputMember);

    /**
     * 아이디 찾기 (이름 + 이메일)
     * @param memberName : 회원 이름
     * @param memberEmail : 회원 이메일
     * @return Member 객체 (아이디, 가입일자)
     */
    Member findId(String memberName, String memberEmail);  

    Member findPw(String memberId, String memberName, String memberEmail);

    int resetPw(String memberId, String newPw);

}