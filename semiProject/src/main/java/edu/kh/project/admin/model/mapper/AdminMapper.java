package edu.kh.project.admin.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.admin.dto.AdminMember;
import edu.kh.project.admin.dto.AdminNotice;
import edu.kh.project.admin.dto.AdminReportComment;
import edu.kh.project.admin.dto.AdminSupport;
import edu.kh.project.admin.dto.Report;
import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.Comment;



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
	List<AdminNotice> selectNoticeList(Map<String, Object> paramMap);

	/** 공지사항 게시글 삭제/복구
	 * @param paramMap
	 * @return
	 */
	int updateNoticeStatus(Map<String, Object> paramMap);

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

	/** 신고글 전체 개수 조회
	 * @param paramMap
	 * @return
	 */
	int getReportCount(Map<String, Object> paramMap);

	/** 신고글 목록 조회
	 * @param paramMap
	 * @return
	 */
	List<Report> selectReportList(Map<String, Object> paramMap);
	
	/** 게시글 기각 처리
	 * @param reportNo
	 */
	void rejectBoardReport(int reportNo);
	
	/** 댓글 기각 처리
	 * @param reportNo
	 */
	void rejectCommentReport(int reportNo);
	
	/** 신고된 게시글 삭제
	 * @param paramMap
	 */
	void deleteReportedBoard(Map<String, Object> paramMap);

	/** 게시글 신고처리 완료
	 * @param paramMap
	 */
	void updateBoardReportProcess(Map<String, Object> paramMap);

	/** 신고된 댓글 삭제
	 * @param paramMap
	 */
	void deleteReportedComment(Map<String, Object> paramMap);

	/** 댓글 신고처리 완료
	 * @param paramMap
	 */
	void updateCommentReportProcess(Map<String, Object> paramMap);

	
	/** 신고글 상세 조횐
	 * @param reportNo
	 * @return
	 */
	Report selectReportDetail(int reportNo);

	/** 신고 대상(게시글 or 댓글)에 따른 게시글 번호
	 * @param targetNo
	 * @return
	 */
	Integer selectBoardNoByReportTarget(int targetNo);
	
	/** 게시글 상세
	 * @param boardNo
	 * @return
	 */
	Board selectBoardDetail(int boardNo);

	/** 댓글 목록
	 * @param boardNo
	 * @return
	 */
	List<Comment> selectCommentListByBoardNo(int boardNo);

	/** 이전 글
	 * @param reportNo
	 * @return
	 */
	Integer selectPrevReportNo(int reportNo);

	/** 다음 글
	 * @param reportNo
	 * @return
	 */
	Integer selectNextReportNo(int reportNo);

	/** 댓글 목록 (관리자 전용)
	 * @param boardNo
	 * @return
	 */
	List<AdminReportComment> selectCommentListForReport(int boardNo);

	/** 신고된 게시글용 제목
	 * @param reportNo
	 * @return
	 */
	String selectBoardReportTitle(int reportNo);
	
	/** 신고된 댓글용 제목
	 * @param reportNo
	 * @return
	 */
	String selectCommentReportTitle(int reportNo);

	/** 게시글 상세 조회
	 * @param reportNo
	 * @return
	 */
	Report selectBoardReportDetail(int reportNo);

	/** 댓글 상세 조회
	 * @param reportNo
	 * @return
	 */
	Report selectCommentReportDetail(int reportNo);


	

	



	
	
	
}
