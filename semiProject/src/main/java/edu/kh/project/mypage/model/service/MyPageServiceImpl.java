package edu.kh.project.mypage.model.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.ibatis.session.RowBounds;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.Pagination;
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
    public int updateProfile(Member updateMember, MultipartFile profileImg, String webPath, String folderPath) throws IOException {
        
        // 1. 프로필 이미지 업로드 처리
        String rename = null; 
        
        if(profileImg.getSize() > 0) { // 업로드된 파일이 있다면
            
            // 1) 파일 이름 변경 (UUID)
            rename = UUID.randomUUID().toString();
            
            // 원본 파일 확장자 가져오기
            String originalName = profileImg.getOriginalFilename();
            String ext = originalName.substring(originalName.lastIndexOf("."));
            
            // 저장할 파일명 세팅
            rename += ext;
            
            // 2) DB에 저장할 경로 세팅
            updateMember.setProfileImg(webPath + rename);
            
        } else {
            // 업로드 안 했으면 null (Mapper XML에서 <if>로 처리)
            updateMember.setProfileImg(null);
        }
        
        // 2. DB 수정 (Mapper 호출)
        // ★ Mapper 메서드 이름도 updateProfile로 맞춰야 함
        int result = mapper.updateProfile(updateMember);
        
        
        // 3. DB 수정 성공 시, 실제 파일 서버에 저장
        if(result > 0 && rename != null) {
            File file = new File(folderPath + rename);
            
            if(!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            
            profileImg.transferTo(file);
        }
        
        return result;
    }

  @Override
  public Map<String, Object> selectPostList(int memberNo, int cp, Map<String, Object> paramMap) {

      // 1. MyBatis에 전달할 파라미터 맵 생성
      Map<String, Object> map = new HashMap<>();
      map.put("memberNo", memberNo);
      
      if(paramMap != null) {
          map.put("key", paramMap.get("key"));
          map.put("query", paramMap.get("query"));
      }

      // 2. 게시글 수 조회
      int listCount = mapper.getPostCount(map);
      
      // 3. 페이지네이션
      Pagination pagination = new Pagination(cp, listCount);
      
      // 4. 게시글 목록 조회
      int offset = (pagination.getCurrentPage() - 1) * pagination.getLimit();
      RowBounds rowBounds = new RowBounds(offset, pagination.getLimit());

      // 여기서 map 안에는 memberNo, key, query 다 들어있어야 함
      List<Board> boardList = mapper.selectPostList(map, rowBounds);
      
      // 5. 결과 리턴
      Map<String, Object> resultMap = new HashMap<>();
      resultMap.put("pagination", pagination);
      resultMap.put("boardList", boardList);
      
      return resultMap;
  }
  
  @Override
	public Map<String, Object> selectCommentPostList(int memberNo, int cp, Map<String, Object> paramMap) {
		
		Map<String, Object> map = new HashMap<>();
		map.put("memberNo", memberNo);
		
		if(paramMap != null) {
			map.put("key", paramMap.get("key"));
			map.put("query", paramMap.get("query"));
		}

		// 1. 내가 댓글 단 게시글 수 (중복 제거)
		int listCount = mapper.getCommentPostCount(map);
		
		Pagination pagination = new Pagination(cp, listCount);
		
		int offset = (pagination.getCurrentPage() - 1) * pagination.getLimit();
		RowBounds rowBounds = new RowBounds(offset, pagination.getLimit());

		// 2. 목록 조회
		List<Board> boardList = mapper.selectCommentPostList(map, rowBounds);
		
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("pagination", pagination);
		resultMap.put("boardList", boardList);
		
		return resultMap;
	}
  
	  @Override
	  public Map<String, Object> selectScrapList(int memberNo, int cp, Map<String, Object> paramMap) {
	      Map<String, Object> map = new HashMap<>();
	      map.put("memberNo", memberNo);
	      if(paramMap != null) {
	          map.put("key", paramMap.get("key"));
	          map.put("query", paramMap.get("query"));
	      }
	      
	      // 1. 전체 스크랩 수 조회
	      int listCount = mapper.getScrapCount(map);
	      
	      Pagination pagination = new Pagination(cp, listCount);
	      int offset = (pagination.getCurrentPage() - 1) * pagination.getLimit();
	      RowBounds rowBounds = new RowBounds(offset, pagination.getLimit());
	
	      // 2. 목록 조회
	      List<Board> boardList = mapper.selectScrapList(map, rowBounds);
	      
	      Map<String, Object> resultMap = new HashMap<>();
	      resultMap.put("pagination", pagination);
	      resultMap.put("boardList", boardList);
	      
	      return resultMap;
	  }

	@Override
	public int changePw(String currentPw, String newPw, int memberNo) {
		
		// 1. 현재 비밀번호(암호화된 상태) 가져오기
		String encPw = mapper.selectEncPw(memberNo);
		
		// 2. 입력한 현재 비밀번호(currentPw)랑 DB 비밀번호(encPw) 비교
		if( !bcrypt.matches(currentPw, encPw) ) {
			return 0; // 비밀번호 불일치 -> 0 반환
		}
		
		// 3. 일치하면 새 비밀번호 암호화
		String newEncPw = bcrypt.encode(newPw);
		
		// 4. DB 업데이트 (Map이나 DTO 사용)
		Member member = new Member();
		member.setMemberNo(memberNo);
		member.setMemberPw(newEncPw);
		
		return mapper.changePw(member);
	}
}