package edu.kh.project.mypage.model.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.member.model.dto.Member;

public interface MyPageService {

  /**
   * 회원 정보 수정 서비스
   * @param updateMember (수정할 정보 + PK)
   * @param profileImg (업로드된 파일)
   * @param webPath (웹 접근 경로)
   * @param folderPath (서버 저장 경로)
   * @param currentPw (현재 비밀번호)
   * @return result (1:성공, 0:실패, -1:비번불일치)
   * @throws IOException
   */
  int updateProfile(Member updateMember, MultipartFile profileImg, String webPath, String folderPath, String currentPw) throws IOException;

  Map<String, Object> selectPostList(int memberNo, int cp, Map<String, Object> paramMap);
  
  Map<String, Object> selectCommentPostList(int memberNo, int cp, Map<String, Object> paramMap);

}