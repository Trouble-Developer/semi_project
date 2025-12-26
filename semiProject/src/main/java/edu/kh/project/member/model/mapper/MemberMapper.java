package edu.kh.project.member.model.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import edu.kh.project.member.model.dto.Member;

@Mapper
public interface MemberMapper {

	/** 회원가입
	 * @param inputMember
	 * @return result (1:성공, 0:실패)
	 */
	int signup(Member inputMember);

	/** 아이디 중복 검사
	 * @param memberId
	 * @return count (1:중복, 0:사용가능)
	 */
	int checkId(String memberId);

	/** 닉네임 중복 검사
	 * @param memberNickname
	 * @return count
	 */
	int checkNickname(String memberNickname);

	/** 이메일 중복 검사
	 * @param memberEmail
	 * @return count
	 */
	int checkEmail(String memberEmail);

	/** 로그인
	 * @param memberId
	 * @return
	 */
	Member login(String memberId);

	/** 아이디 찾기
	 * @param memberNickname : 이름
	 * @param memberRrn1 : 주민번호 앞자리
	 * @param memberEmail : 이메일
	 * @return Member 객체 (조회 성공) 또는 null (조회 실패)
	 */
	 Member findId(
		        @Param("memberNickname") String memberNickname,
		        @Param("memberRrn1") String memberRrn1,
		        @Param("memberEmail") String memberEmail
		 );       
}