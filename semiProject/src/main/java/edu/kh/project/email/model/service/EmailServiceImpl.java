package edu.kh.project.email.model.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import edu.kh.project.email.model.mapper.EmailMapper;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{

    private final EmailMapper mapper;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    public int sendEmail(String htmlName, String email) {
        
        // 1. 인증키 생성 (6자리 난수)
        String authKey = createAuthKey();
        
        try {
            // 2. 이메일 제목 설정
            String subject = null;
            switch(htmlName){
                case "signup" : subject = "[boardProject] 회원 가입 인증번호 입니다."; break;
            }

            // 3. 이메일 전송 객체 생성
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(email); // 받는 사람
            helper.setSubject(subject); // 제목
            
            // 4. 타임리프를 이용해서 HTML 내용 읽어오기
            Context context = new Context();
            context.setVariable("authKey", authKey);
            String htmlContent = templateEngine.process("email/" + htmlName, context);
            
            helper.setText(htmlContent, true); // HTML 내용 설정

            // 5. 이메일 전송
            mailSender.send(mimeMessage);

            // 6. DB에 인증키 저장 (Map 이용)
            Map<String, String> map = new HashMap<>();
            map.put("email", email);
            map.put("authKey", authKey);
            
            // 기존에 인증번호가 있는지 확인 후 업데이트 or 삽입
            int result = mapper.updateAuthKey(map);
            if(result == 0) {
                result = mapper.insertAuthKey(map);
            }
            
            return 1; // 성공

        } catch (Exception e) {
            e.printStackTrace();
            return 0; // 실패
        }
    }

    // 인증번호 생성 메서드
    public String createAuthKey() {
        String key = "";
        for(int i=0 ; i< 6 ; i++) {
            int sel1 = (int)(Math.random() * 3); // 0:숫자 / 1,2:영어
            if(sel1 == 0) {
                int num = (int)(Math.random() * 10); // 0~9
                key += num;
            }else {
                char ch = (char)(Math.random() * 26 + 65); // A~Z
                int temp = (int)(Math.random() * 2);
                if(temp == 1) ch = (char)(ch + 32); // 소문자 변환
                key += ch;
            }
        }
        return key;
    }

    // 인증번호 확인
    @Override
    public int checkAuthKey(Map<String, Object> paramMap) {
        return mapper.checkAuthKey(paramMap);
    }
}