package edu.kh.project.admin.model.service;

import java.util.List;
import java.util.Map;

import edu.kh.project.admin.dto.AdminMember;
import edu.kh.project.board.model.dto.Pagination;

public interface AdminService {

	/** 회원 수 조회
	 * @param paramMap
	 * @return
	 */
	int getMemberCount(Map<String, Object> paramMap);

	/** 회원 목록 조회
	 * @param paramMap
	 * @return
	 */
	List<AdminMember> selectMemberList(Map<String, Object> paramMap);

	/** 회원 강제 탈퇴
	 * @param paramMap
	 * @return
	 */
	int updateMemberStatus(Map<String, Object> paramMap);

	/** 공지사항 조회
	 * @param cp : 현재 페이지 번호. 이하 게시글 메서드에서도 동일한 의미.
	 * @param paramMap : 검색 조건. 이하 게시글 메서드에서도 동일한 의미.
	 * @return
	 */
	Map<String, Object> selectNoticeList(int cp, Map<String, Object> paramMap);

	/** 공지사항 삭제
	 * @param boardNo
	 * @return
	 */
	int deleteNotice(int boardNo);

	/** 고객지원 게시글 목록 조회
	 * @param cp 
	 * @param paramMap
	 * @return
	 */
	Map<String, Object> selectSupportList(int cp, Map<String, Object> paramMap);

	

}
