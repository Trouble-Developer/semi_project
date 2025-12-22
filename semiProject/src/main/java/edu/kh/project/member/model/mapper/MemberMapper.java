package edu.kh.project.member.model.mapper;

import org.apache.ibatis.annotations.Mapper;
import edu.kh.project.member.model.dto.Member;

@Mapper // 이거 없으면 스프링이 못 찾는다 (MyBatis 빈 등록)
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

}