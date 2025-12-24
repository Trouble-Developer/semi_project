package edu.kh.project.admin.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.admin.dto.AdminMember;



@Mapper
public interface AdminMapper {

	// 회원 관리
	int getMemberCount(Map<String, Object> paramMap);

	List<AdminMember> selectMemberList(Map<String, Object> paramMap);

	// 회원 관리 - 강제 탈퇴
	int updateMemberStatus(Map<String, Object> paramMap);

}
