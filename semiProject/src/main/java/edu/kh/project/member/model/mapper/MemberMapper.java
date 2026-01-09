package edu.kh.project.member.model.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import edu.kh.project.member.model.dto.Member;

@Mapper
public interface MemberMapper {

    // 로그인
    Member login(String memberId);
    
    // 회원가입
    int signup(Member inputMember);
    
    // 중복 검사
    int checkId(String memberId);
    int checkNickname(String memberNickname);
    int checkEmail(String memberEmail);
    int checkTel(String memberTel);
    
    // 아이디 찾기
    Member findId(@Param("memberName") String memberName, 
                  @Param("memberEmail") String memberEmail);
    
    // 비밀번호 찾기 - 본인 확인
    Member findPw(@Param("memberId") String memberId, 
                  @Param("memberName") String memberName, 
                  @Param("memberEmail") String memberEmail);
    
    // 비밀번호 재설정
    int resetPw(@Param("memberId") String memberId, 
                @Param("encPw") String encPw);
    
}