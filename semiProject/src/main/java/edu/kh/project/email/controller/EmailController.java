package edu.kh.project.email.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.kh.project.email.model.service.EmailService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService service;

    @ResponseBody
    @PostMapping("signup")
    public int signup(@RequestBody String email) {
        // 서비스는 인증키(String)를 돌려줌
        String authKey = service.sendEmail("signup", email);
        
        // 인증키가 null이 아니면 성공(1), null이면 실패(0) 반환
        
        if(authKey != null) {
            return 1;
        } else {
            return 0;
        }
    }

    @ResponseBody
    @PostMapping("checkAuthKey")
    public int checkAuthKey(@RequestBody Map<String, String> paramMap) {
        // 여기도 Service에 맞춰서 <String, Object> -> <String, String>으로 변경
        return service.checkAuthKey(paramMap);
    }
}