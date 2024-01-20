package com.contacts.agenda.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class MailManager {

    @Value("${spring.mail.username}")
    private String sender;
    @Autowired
    JavaMailSender javaMailSender;

    public void sendEmail(String email, String subject,  String msg) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            message.setSubject(subject);
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message , true);
            // File file = new File();
            // mimeMessageHelper.addAttachment("Mi archivo a enviar", file);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setText(msg);
            mimeMessageHelper.setFrom(sender);
            javaMailSender.send(message);
        } catch (Exception e){
            log.info("Email enviando a => "+email);
            throw new RuntimeException("ERROR ENVIANDO EMAIL");
        }
    }

    public void sendEmailToRestorePassword(String email, String token) {
        // TODO CREAR SISTEMA PARA RESTAURAR PASSWORD MEDIANTE
        /*
        String template = """
                <form method=POST action= "http://localhost:8080/auth/setNewPassword">
                  <div class="mb-3">
                    <label for="password1" class="form-label">Password</label>
                    <input type="password" class="form-control" id="password1">
                  </div>
                  <div class="mb-3">
                    <label for="password2" class="form-label">Confirmar password</label>
                    <input type="password" class="form-control" id="password2">
                  </div>
                  <div>
                    <input hidden type="password" class="form-control" id="token" value= """;
        template.concat(token).concat("""
                  >
                  </div>
                  <button type="submit" class="btn btn-primary">Restarurar contraseña</button>
                </form>
                """);

         */
        String template = """
                <form method=POST action= "http://localhost:8080/auth/setNewPassword">
                  <div class="mb-3">
                    <label for="password1" class="form-label">Password</label>
                    <input type="password" class="form-control" id="password1">
                  </div>
                  <div class="mb-3">
                    <label for="password2" class="form-label">Confirmar password</label>
                    <input type="password" class="form-control" id="password2">
                  </div>
                  <div>
                    <input type="hidden" class="form-control" id="token" value="%s" >
                  </div>
                  <button type="submit" class="btn btn-primary">Restarurar contraseña</button>
                </form>
                """.formatted(token);
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            message.setSubject("Restauracion de password");
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message , true);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setText(template, true);
            mimeMessageHelper.setFrom(sender);
            javaMailSender.send(message);
        } catch (Exception e){
            log.info("Email enviando a => "+email);
            throw new RuntimeException("ERROR ENVIANDO EMAIL");
        }
    }
    public void sendEmailTemplate(String email, String token) {
        // TODO CREAR TEMPLATE PARA ENVIAR EMAILS BONITOS
        // TODO CREAR TEMPLATE PARA ENVIAR EMAILS BONITOS
        // TODO CREAR TEMPLATE PARA ENVIAR EMAILS BONITOS
        // TODO CREAR TEMPLATE PARA ENVIAR EMAILS BONITOS
        /*
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            message.setSubject(subject);
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message , true);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setText(msg);
            mimeMessageHelper.setFrom(sender);
            javaMailSender.send(message);
        } catch (Exception e){
            log.info("Email enviando a => "+email);
            throw new RuntimeException("ERROR ENVIANDO EMAIL");
        }

         */
    }
}
