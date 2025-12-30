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
	 * @param memberName : 이름
	 * @param memberRrn1 : 주민번호 앞자리
	 * @param memberEmail : 이메일
	 * @return Member 객체 (조회 성공) 또는 null (조회 실패)
	 */
	 Member findId(
		        @Param("memberName") String memberName,
		        @Param("memberRrn1") String memberRrn1,
		        @Param("memberEmail") String memberEmail
		 );

	 /** 비밀번호 찾기 - 본인 확인
	  * 
	  * <p>회원의 아이디, 이름, 주민번호 앞자리, 이메일을 모두 확인하여
	  * 일치하는 회원이 있는지 조회</p>
	  * 
	  * @param memberId : 회원 아이디
	  * @param memberName : 회원 이름
	  * @param memberRrn1 : 주민번호 앞 6자리 (생년월일)
	  * @param memberEmail : 회원 이메일
	  * @return Member 객체 (조회 성공) 또는 null (조회 실패)
	  */
	 Member findPw(
		 @Param("memberId") String memberId,
		 @Param("memberName") String memberName,
		 @Param("memberRrn1") String memberRrn1,
		 @Param("memberEmail") String memberEmail
	 );
	 
	 /** 비밀번호 재설정
	  * 
	  * <p>본인 확인 완료 후 새로운 비밀번호로 업데이트</p>
	  * 
	  * @param memberId : 회원 아이디
	  * @param encPw : 암호화된 새 비밀번호
	  * @return result (1:성공, 0:실패)
	  */
	 int resetPw(
		 @Param("memberId") String memberId,
		 @Param("encPw") String encPw
	 );
}