package com.clinica.api.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    public void sendEmail(String to, String subject, String body) {
        // En un escenario real, aquí se usaría JavaMailSender
        log.info("Enviando correo a: {}, Asunto: {}", to, subject);
        log.info("Cuerpo: {}", body);
    }
}
