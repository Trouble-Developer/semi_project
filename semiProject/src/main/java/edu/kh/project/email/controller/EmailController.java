package edu.kh.project.email.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.kh.project.email.model.service.EmailService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService service;

    // ========== 회원가입용 (기존 코드) ==========
    
    @ResponseBody
    @PostMapping("signup")
    public int signup(@RequestBody String email) {
        String authKey = service.sendEmail("signup", email);
        
        if(authKey != null) {
            return 1;
        } else {
            return 0;
        }
    }

    @ResponseBody
    @PostMapping("checkAuthKey")
    public int checkAuthKey(@RequestBody Map<String, String> paramMap) {
        return service.checkAuthKey(paramMap);
    }


    // ========== 아이디 찾기용 (추가) ==========
    
    /** 아이디 찾기 - 인증번호 발송 */
    @ResponseBody
    @PostMapping("sendAuthKey")
    public int sendAuthKey(@RequestBody Map<String, String> params) {
        String email = params.get("email");
        String authKey = service.sendEmail("findId", email);
        
        if(authKey != null) {
            return 1;
        } else {
            return 0;
        }
    }

    /** 아이디 찾기 - 인증번호 확인 (GET) */
    @ResponseBody
    @GetMapping("checkAuthKey")
    public int checkAuthKeyGet(
        @RequestParam("email") String email,
        @RequestParam("authKey") String authKey
    ) {
        Map<String, String> paramMap = Map.of("email", email, "authKey", authKey);
        return service.checkAuthKey(paramMap);
    }
}