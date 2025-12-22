package edu.kh.project.email.model.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import edu.kh.project.email.model.mapper.EmailMapper;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j // 로그 찍을 때 필요함
@RequiredArgsConstructor // 생성자 주입 (final 필드 자동 주입)
public class EmailServiceImpl implements EmailService {

    private final EmailMapper mapper;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    public String sendEmail(String htmlName, String email) {
        
        // 1. 인증키 생성 (6자리 UUID)
        String authKey = createAuthKey();
        
        // 2. DB 저장용 Map 생성
        Map<String, String> map = new HashMap<>();
        map.put("authKey", authKey);
        map.put("email", email);
        
        // 3. DB에 인증키 저장 (실패하면 null 반환해서 컷)
        if (!storeAuthKey(map)) {
            return null;
        }
        
        // 4. 이메일 발송 객체 생성
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        
        try {
            // true : 파일 첨부 가능 설정, "UTF-8" : 인코딩
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(email); // 수신자
            helper.setSubject("회원 가입 인증번호 입니다."); // 제목
            
            // 타임리프 템플릿 처리 (htmlName: signup 등)
            String htmlContent = loadHtml(authKey, htmlName);
            helper.setText(htmlContent, true); // true: HTML 형식 사용

            // 5. 실제 메일 전송
            mailSender.send(mimeMessage);
            
            return authKey; // 성공 시 인증번호 반환

        } catch (Exception e) {
            log.error("메일 발송 중 에러 발생 : {}", e.getMessage());
            e.printStackTrace();
            return null; // 실패 시 null 반환
        }
    }

    /**
     * 타임리프 엔진을 이용해 HTML 본문 생성
     */
    private String loadHtml(String authKey, String htmlName) {
        Context context = new Context();
        context.setVariable("authKey", authKey);
        
        // templates/email 폴더 안의 html 파일을 읽음
        return templateEngine.process("email/" + htmlName, context);
    }

    /**
     * 인증키 DB 저장 (Update 시도 후 실패 시 Insert)
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean storeAuthKey(Map<String, String> map) {
        // 기존 이메일이 있으면 업데이트
        int result = mapper.updateAuthKey(map);
        
        // 업데이트 된 게 없으면 새로 삽입
        if (result == 0) {
            result = mapper.insertAuthKey(map);
        }
        
        return result > 0;
    }

    /**
     * 인증번호 생성 (UUID 6자리)
     */
    private String createAuthKey() {
        return UUID.randomUUID().toString().substring(0, 6);
    }

    /**
     * 인증번호 확인 로직
     */
    @Override
    public int checkAuthKey(Map<String, String> paramMap) {
        // paramMap에는 보통 "email"과 "authKey"가 담겨서 넘어옴
        return mapper.checkAuthKey(paramMap);
    }
}