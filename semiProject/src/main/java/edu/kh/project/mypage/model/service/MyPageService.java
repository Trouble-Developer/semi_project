package edu.kh.project.mypage.model.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.member.model.dto.Member;

public interface MyPageService {

  Map<String, Object> selectPostList(int memberNo, int cp, Map<String, Object> paramMap);
  
  Map<String, Object> selectCommentPostList(int memberNo, int cp, Map<String, Object> paramMap);

  Map<String, Object> selectScrapList(int memberNo, int cp, Map<String, Object> paramMap);

  int changePw(String currentPw, String newPw, int memberNo);

  int updateProfile(Member updateMember, MultipartFile profileImg, String profileWebPath, String profileFolderPath) throws IOException;

}