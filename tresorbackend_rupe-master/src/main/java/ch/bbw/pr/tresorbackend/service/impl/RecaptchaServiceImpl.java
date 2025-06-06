package ch.bbw.pr.tresorbackend.service.impl;

import ch.bbw.pr.tresorbackend.service.RecaptchaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class RecaptchaServiceImpl implements RecaptchaService {
    private static final Logger logger = LoggerFactory.getLogger(RecaptchaServiceImpl.class);

    @Value("${CAPTCHA_SECRET_KEY}")
    private String recaptchaSecret;

    @Override
    public boolean verifyRecaptcha(String recaptchaToken) {
        String url = "https://www.google.com/recaptcha/api/siteverify";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String body = "secret=" + recaptchaSecret + "&response=" + recaptchaToken;
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        try {
            Map response = restTemplate.postForObject(url, request, Map.class);
            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                return true;
            }
        } catch (Exception e) {
            logger.error("Recaptcha verification failed", e);
        }
        return false;
    }
}

