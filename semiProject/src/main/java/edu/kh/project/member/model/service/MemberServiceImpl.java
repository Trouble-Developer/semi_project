package edu.kh.project.member.model.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional // 트랜잭션 처리 (예외 발생 시 롤백)
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
}