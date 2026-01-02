package edu.kh.project.member.model.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberMapper mapper;
    
    // SecurityConfig에서 Bean으로 등록되어 있어야 함 (필수!)
    private final BCryptPasswordEncoder bcrypt; 

    // 회원가입
    @Override
    public int signup(Member inputMember) {
        
        // 1. 비밀번호 암호화 (평문 -> 암호문)
        String encPw = bcrypt.encode(inputMember.getMemberPw());
        inputMember.setMemberPw(encPw);
        
        // 2. 주민번호 합치기
        if(inputMember.getMemberRrn1() != null && inputMember.getMemberRrn2() != null) {
            String rrn = inputMember.getMemberRrn1() + "-" + inputMember.getMemberRrn2();
            inputMember.setMemberRrn(rrn);
        }
        
        // 3. DAO(Mapper) 호출해서 DB에 저장
        return mapper.signup(inputMember);
    }
    
    // 로그인
    @Override
    public Member login(Member inputMember) {
        
        // 1. 아이디로 회원 정보 조회
        Member loginMember = mapper.login(inputMember.getMemberId());

        // 2. 일치하는 아이디가 없으면 null 리턴
        if (loginMember == null) {
            return null;
        }

        // 3. 비밀번호 비교
        if (!bcrypt.matches(inputMember.getMemberPw(), loginMember.getMemberPw())) {
            return null;
        }

        // 4. 비밀번호 제거 (보안)
        loginMember.setMemberPw(null);

        return loginMember;
    }

    // 아이디 중복 검사
    @Override
    public int checkId(String memberId) {
        return mapper.checkId(memberId);
    }

    // 닉네임 중복 검사
    @Override
    public int checkNickname(String memberNickname) {
        return mapper.checkNickname(memberNickname);
    }

    // 이메일 중복 검사
    @Override
    public int checkEmail(String memberEmail) {
        return mapper.checkEmail(memberEmail);
    }
    
    /**
     * 아이디 찾기 (이름 + 이메일)
     * 
     * @param memberName : 회원 이름
     * @param memberEmail : 회원 이메일
     * @return Member 객체 (아이디, 가입일자) 또는 null
     */
    @Override
    public Member findId(String memberName, String memberEmail) {  // ✅ memberRrn1 파라미터 제거
        
        // 입력값 검증
        if(memberName == null || memberName.trim().isEmpty()) {
            log.error("아이디 찾기 실패: 이름이 비어있음");
            return null;
        }
        
        if(memberEmail == null || memberEmail.trim().isEmpty()) {
            log.error("아이디 찾기 실패: 이메일이 비어있음");
            return null;
        }
        
        // Mapper 호출하여 DB 조회 (이름 + 이메일만 전달)
        return mapper.findId(memberName, memberEmail);  // ✅ memberRrn1 제거
    }

    /**
     * 비밀번호 찾기 - 본인 확인
     * 
     * @param memberId : 회원 아이디
     * @param memberName : 회원 이름
     * @param memberRrn1 : 주민번호 앞자리
     * @param memberEmail : 회원 이메일
     * @return Member 객체 또는 null
     */
    @Override
    public Member findPw(String memberId, String memberName, String memberRrn1, String memberEmail) {
        
        // Mapper 호출해서 DB 조회
        Member findMember = mapper.findPw(memberId, memberName, memberRrn1, memberEmail);
        
        return findMember;
    }
    
    /**
     * 비밀번호 재설정
     * 
     * @param memberId : 회원 아이디
     * @param newPw : 새 비밀번호 (평문)
     * @return result (1:성공, 0:실패)
     */
    @Override
    public int resetPw(String memberId, String newPw) {
        
        // 1. 새 비밀번호 암호화
        String encPw = bcrypt.encode(newPw);
        
        // 2. Mapper 호출하여 DB 업데이트
        int result = mapper.resetPw(memberId, encPw);
        
        return result;
    }
}