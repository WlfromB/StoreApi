package com.example.demo.service.template;

public interface TemplateManager {
    String PATH = "templates/";
    String VERIFICATION = "verification.html";
    String SUCCESS_VERIFICATION = "success-verify.html";
    
    String loadTemplate(String templateName);
}
