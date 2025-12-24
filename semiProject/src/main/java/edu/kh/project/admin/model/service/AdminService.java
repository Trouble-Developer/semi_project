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

	

}
