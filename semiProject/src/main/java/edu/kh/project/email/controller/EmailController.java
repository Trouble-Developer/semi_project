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
        return service.sendEmail("signup", email);
    }

    @ResponseBody
    @PostMapping("checkAuthKey")
    public int checkAuthKey(@RequestBody Map<String, Object> paramMap) {
        return service.checkAuthKey(paramMap);
    }
}