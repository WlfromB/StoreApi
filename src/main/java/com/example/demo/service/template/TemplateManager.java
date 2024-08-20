package com.example.demo.service.template;

public interface TemplateManager {
    static final String PATH = "templates/";
    static final String VERIFICATION = "verification.html";
    static final String SUCCESS_VERIFICATION = "success-verify.html";
    
    String loadTemplate(String templateName);
}
