package edu.kh.project.email.model.service;

import java.util.Map;

public interface EmailService {
    String sendEmail(String string, String email);
    int checkAuthKey(Map<String, String> paramMap);
}