package edu.kh.project.mypage.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import edu.kh.project.board.model.dto.Board;
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


    int getPostCount(Map<String, Object> map);


    List<Board> selectPostList(Map<String, Object> map, RowBounds rowBounds);
    

 	int getCommentPostCount(Map<String, Object> map);


 	List<Board> selectCommentPostList(Map<String, Object> map, RowBounds rowBounds);

}