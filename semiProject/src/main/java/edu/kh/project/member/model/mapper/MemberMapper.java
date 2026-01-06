package edu.kh.project.member.model.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import edu.kh.project.member.model.dto.Member;

@Mapper
public interface MemberMapper {
    
    /**
     * 로그인
     * @param memberId : 회원 아이디
     * @return Member 객체
     */
    Member login(String memberId);
    
    /**
     * 아이디 중복 검사
     * @param memberId : 회원 아이디
     * @return count (1:중복, 0:사용가능)
     */
    int checkId(String memberId);
    
    /**
     * 닉네임 중복 검사
     * @param memberNickname : 회원 닉네임
     * @return count (1:중복, 0:사용가능)
     */
    int checkNickname(String memberNickname);
    
    /**
     * 이메일 중복 검사
     * @param memberEmail : 회원 이메일
     * @return count (1:중복, 0:사용가능)
     */
    int checkEmail(String memberEmail);
    
	/**
	 * 전화번호 중복 검사
	 * @param memberTel
	 * @return
	 */
	int checkTel(String memberTel);
    
    /**
     * 회원가입
     * @param inputMember : 입력받은 회원 정보
     * @return result (1: 성공, 0: 실패)
     */
    int signup(Member inputMember);
    
    /**
     * 아이디 찾기 (이름 + 이메일)
     * @param memberName : 회원 이름
     * @param memberEmail : 회원 이메일
     * @return Member 객체 (아이디, 가입일자) 또는 null
     */
    Member findId(
        @Param("memberName") String memberName, 
        @Param("memberEmail") String memberEmail
    );
    
    /**
     * 비밀번호 찾기 - 본인 확인 (아이디 + 이름 + 이메일)
     * @param memberId : 회원 아이디
     * @param memberName : 회원 이름
     * @param memberEmail : 회원 이메일
     * @return Member 객체 또는 null
     */
    Member findPw(
        @Param("memberId") String memberId,
        @Param("memberName") String memberName,
        @Param("memberEmail") String memberEmail
    );
    
    /**
     * 비밀번호 재설정
     * @param memberId : 회원 아이디
     * @param encPw : 암호화된 새 비밀번호
     * @return result (1: 성공, 0: 실패)
     */
    int resetPw(
        @Param("memberId") String memberId,
        @Param("encPw") String encPw
    );


}