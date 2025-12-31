package edu.kh.project.admin.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.admin.dto.AdminMember;
import edu.kh.project.admin.dto.AdminSupport;
import edu.kh.project.board.model.dto.Board;



@Mapper
public interface AdminMapper {

	// 회원 관리
	/** 회원 수 전체 조회
	 * @param paramMap
	 * @return
	 */
	int getMemberCount(Map<String, Object> paramMap);

	/** 회원 목록 조회(페이징 & 검색)
	 * @param paramMap
	 * @return
	 */
	List<AdminMember> selectMemberList(Map<String, Object> paramMap);

	/** 회원 관리 - 강제 탈퇴 & 복구
	 * @param paramMap
	 * @return
	 */
	int updateMemberStatus(Map<String, Object> paramMap);

	/** 공지사항 전체 개수 조회 (검색 조건 포함)
	 * @param paramMap
	 * @return
	 */
	int getNoticeCount(Map<String, Object> paramMap);

	/** 공지사항 목록 조회
	 * @param paramMap
	 * @return
	 */
	List<Board> selectNoticeList(Map<String, Object> paramMap);

	/** 공지사항 삭제(UPDATE)
	 * @param boardNo
	 * @return
	 */
	int deleteNotice(int boardNo);

	/** 고객지원 게시글 전체 개수 조회
	 * @param paramMap
	 * @return
	 */
	int getSupportCount(Map<String, Object> paramMap);

	/** 고객지원 게시글 목록 조회
	 * @param paramMap
	 * @return
	 */
	List<AdminSupport> selectSupportList(Map<String, Object> paramMap);

	/** 고객지원 게시글 삭제/복구
	 * @param paramMap
	 * @return
	 */
	int updateSupportStatus(Map<String, Object> paramMap);

}
