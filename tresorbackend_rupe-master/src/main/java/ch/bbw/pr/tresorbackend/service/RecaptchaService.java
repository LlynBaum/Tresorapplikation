package ch.bbw.pr.tresorbackend.service;

public interface RecaptchaService {
    boolean verifyRecaptcha(String recaptchaToken);
}

