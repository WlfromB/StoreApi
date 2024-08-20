package com.example.demo.service.mail;

import com.example.demo.service.activation_codes_cache.ActivationCodeCache;
import com.example.demo.service.template.TemplateManager;
import com.example.demo.service.user.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class MailSenderImpl implements MailSender {
    private final JavaMailSender javaMailSender;
    private final ActivationCodeCache cache;
    private final UserService userService;
    private final TemplateManager templateManager;
    
    private static final String PARAM_EMAIL = "email";
    private static final String PARAM_CODE = "code";

    @Value("${spring.mail.username}")
    private String from;

    @Value("${application.protocol}")
    private String protocol;

    @Value("${application.host}")
    private String host;

    @Value("${server.port}")
    private String port;

    public void sendMail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        javaMailSender.send(message);
    }

    public void sendCode(String to) throws NoSuchAlgorithmException, MessagingException {
        String content = cache.generateVerificationCode(to);
        cache.setVerificationCode(to, content);
        sendMail(to, ACTIVATION_CODE, generateContentCode(to, content));
    }

    private String generateContentCode(String to, String code) {
        String template = templateManager.loadTemplate(TemplateManager.VERIFICATION);
        String link = UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(host)
                .port(port)
                .path(VERIFY_URI)
                .queryParam(PARAM_EMAIL, to)
                .queryParam(PARAM_CODE, code)
                .build()
                .toUriString();
        
        return template.replace("{{link}}", link);
    }


    public void sendSuccess(String to) throws Exception {
        String template = templateManager.loadTemplate(TemplateManager.SUCCESS_VERIFICATION);
        sendMail(to, ACTIVATION_SUCCESS, template);
        userService.setActivatedMail(to);
    }
}
