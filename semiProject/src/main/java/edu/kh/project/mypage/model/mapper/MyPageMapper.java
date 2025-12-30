package edu.kh.project.mypage.model.mapper;

import org.apache.ibatis.annotations.Mapper;
import edu.kh.project.member.model.dto.Member;

@Mapper
public interface MyPageMapper {

	/**
	 * 회원 비밀번호 조회 (암호화된 값)
	 * @param memberNo
	 * @return encPw
	 */
	String selectEncryptedPw(int memberNo);

	/**
	 * 회원 정보 수정
	 * @param updateMember
	 * @return result
	 */
	int updateMember(Member updateMember);

}