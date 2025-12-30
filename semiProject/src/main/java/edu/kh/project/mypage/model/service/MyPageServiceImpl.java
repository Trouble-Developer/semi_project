package edu.kh.project.mypage.model.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.mypage.model.mapper.MyPageMapper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Exception.class) // 예외 발생 시 롤백
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService{

  private final MyPageMapper mapper;
  private final BCryptPasswordEncoder bcrypt;

  @Override
  public int updateProfile(Member updateMember, MultipartFile profileImg, String webPath, String folderPath, String currentPw) throws IOException {
    
    // 1. 현재 비밀번호 일치 여부 확인
    // DB에서 암호화된 비밀번호를 조회해와야 함
    String encPw = mapper.selectEncryptedPw(updateMember.getMemberNo());
    
    // 입력한 비번(currentPw)과 DB비번(encPw) 비교
    if( !bcrypt.matches(currentPw, encPw) ) {
      return -1; // 불일치 시 -1 리턴
    }
    
    
    // 2. 새 비밀번호 수정 여부 확인
    // 비밀번호 입력란에 값이 있으면 -> 암호화해서 세팅
    if( updateMember.getMemberPw().length() > 0 ) {
      String newPw = bcrypt.encode(updateMember.getMemberPw());
      updateMember.setMemberPw(newPw);
    } else {
      // 값이 없으면 null로 세팅 (Mapper XML에서 if문으로 제외시키기 위함)
      updateMember.setMemberPw(null);
    }
    
    
    // 3. 프로필 이미지 업로드 처리
    String rename = null; // 변경된 파일명 저장 변수
    
    if(profileImg.getSize() > 0) { // 업로드된 파일이 있다면
      
      // 1) 파일 이름 변경 (UUID)
      // 예: "abc.jpg" -> "20241230_asdf-asdf-asdf.jpg"
      rename = UUID.randomUUID().toString();
      
      // 원본 파일 확장자(.jpg 등) 가져오기
      String originalName = profileImg.getOriginalFilename();
      String ext = originalName.substring(originalName.lastIndexOf("."));
      
      // 저장할 파일명 세팅 (UUID + 확장자)
      rename += ext;
      
      // 2) DB에 저장할 경로 세팅 (웹 접근 경로 + 변경된 파일명)
      updateMember.setProfileImg(webPath + rename);
      
    } else {
      // 업로드 안 했으면 null 세팅 (Mapper에서 제외)
      updateMember.setProfileImg(null);
    }
    
    
    // 4. DB 수정 (Mapper 호출)
    int result = mapper.updateMember(updateMember);
    
    
    // 5. DB 수정 성공 시, 실제 파일 서버에 저장
    if(result > 0 && rename != null) {
      // 파일 객체 생성
      File file = new File(folderPath + rename);
      
      // 폴더 없으면 생성
      if(!file.getParentFile().exists()) {
        file.getParentFile().mkdirs();
      }
      
      // 파일 저장
      profileImg.transferTo(file);
    }
    
    return result;
  }
}