package co.java.app.contanotify.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class EmailSender {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailSender(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendResetEmail(String to, String token) {
        String resetUrl = "https://tu-dominio.com/reset?token=" + token;
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Reset de contraseña");
        msg.setText("Sigue este link para restablecer tu contraseña: " + resetUrl);
        mailSender.send(msg);
    }

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("lvillarrealcool@gmail.com"); // mismo que spring.mail.username
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables)
            throws MessagingException {

        // Crear contexto con variables dinámicas
        Context context = new Context();
        context.setVariables(variables);

        // Procesar plantilla
        String htmlContent = templateEngine.process("email/" + templateName, context);

        // Preparar el correo
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true = HTML

        mailSender.send(message);
    }

}
