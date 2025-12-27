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
public class MemberServiceImpl implements MemberService {

    private final MemberMapper mapper;
    
    // SecurityConfig에서 Bean으로 등록되어 있어야 함 (필수!)
    private final BCryptPasswordEncoder bcrypt; 

    // 회원가입
    @Override
    public int signup(Member inputMember) {
        
        // 1. 비밀번호 암호화 (평문 -> 암호문)
        // 사용자가 입력한 "1234"를 "$2a$10$..." 이런 식으로 바꿔서 세팅
        String encPw = bcrypt.encode(inputMember.getMemberPw());
        inputMember.setMemberPw(encPw);
        
        // 2. 주민번호 합치기 (123456 - 1234567)
        // HTML에서 memberRrn1, memberRrn2로 따로 들어옴 -> DB에는 MEMBER_RRN 하나로 저장
        if(inputMember.getMemberRrn1() != null && inputMember.getMemberRrn2() != null) {
            String rrn = inputMember.getMemberRrn1() + "-" + inputMember.getMemberRrn2();
            inputMember.setMemberRrn(rrn);
        }
        
        // 3. DAO(Mapper) 호출해서 DB에 저장
        return mapper.signup(inputMember);
    }
    
    // ----------------------------------------------------------------
    // [로그인 서비스 구현]
    // ----------------------------------------------------------------
    @Override
    public Member login(Member inputMember) {
        
        // 1. 아이디를 이용해서 DB에서 회원 정보 조회
        // (비밀번호 비교를 위해 DB에 저장된 암호화된 비밀번호가 필요함)
        Member loginMember = mapper.login(inputMember.getMemberId());

        // 2. 일치하는 아이디가 없으면 null 리턴
        if (loginMember == null) {
            return null;
        }

        // 3. 비밀번호 비교
        // DB에 저장된 비번(암호화O) vs 입력된 비번(암호화X)
        // bcrypt.matches(평문, 암호문) -> 일치하면 true
        if (!bcrypt.matches(inputMember.getMemberPw(), loginMember.getMemberPw())) {
            return null; // 비밀번호 틀림
        }

        // 4. 로그인 성공 시, 보안을 위해 비밀번호 제거
        // (세션에 비밀번호까지 둥둥 떠다니면 위험하니까)
        loginMember.setMemberPw(null);

        // 5. 최종 로그인 정보 리턴
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
    
    // 아이디 찾기
    @Override
    public Member findId(String memberName, String memberRrn1, String memberEmail) {
        return mapper.findId(memberName, memberRrn1, memberEmail);
    }
}