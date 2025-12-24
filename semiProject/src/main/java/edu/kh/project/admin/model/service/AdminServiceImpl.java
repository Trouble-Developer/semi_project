package edu.kh.project.admin.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import edu.kh.project.admin.dto.AdminMember;
import edu.kh.project.admin.model.mapper.AdminMapper;
import edu.kh.project.board.model.dto.Pagination;
import edu.kh.project.member.model.dto.Member;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{
	
	private final AdminMapper mapper;
	
	// 회원 관리
	@Override
	public int getMemberCount(Map<String, Object> paramMap) {
		return mapper.getMemberCount(paramMap);
	}
	
	@Override
	public List<AdminMember> selectMemberList(Map<String, Object> paramMap) {
		return mapper.selectMemberList(paramMap);
	}
	
	// 회원 관리 - 강제 탈퇴
	@Override
	public int updateMemberStatus(Map<String, Object> paramMap) {
		return mapper.updateMemberStatus(paramMap);
	}
	
	
	// 공지사항 (앞으로 만들어야 함)...
	
	
}
