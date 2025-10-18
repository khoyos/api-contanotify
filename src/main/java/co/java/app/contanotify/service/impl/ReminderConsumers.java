package co.java.app.contanotify.service.impl;

import co.java.app.contanotify.util.EmailSender;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReminderConsumers {

    @Autowired
    private EmailSender emailSender;

    @JmsListener(destination = "reminder-5days")
    public void process5DaysReminder(String payload) {
        System.out.println("[5 días antes] Enviando recordatorio: " + payload);
        // Aquí llamas al servicio de correo
        Map<String, String> map = getStringToMap(payload);
        sendEmail(map);
    }

    @JmsListener(destination = "reminder-3days")
    public void process3DaysReminder(String payload) {
        System.out.println("[3 días antes] Enviando recordatorio: " + payload);
        Map<String, String> map = getStringToMap(payload);
        sendEmail(map);
    }

    @JmsListener(destination = "reminder-1day")
    public void process1DayReminder(String payload) {
        System.out.println("[1 día antes] Enviando recordatorio: " + payload);
        Map<String, String> map = getStringToMap(payload);
        sendEmail(map);
    }

    @JmsListener(destination = "reminder-today")
    public void processTodayReminder(String payload) {
        System.out.println("[Hoy] Enviando recordatorio: " + payload.replace("{","").replace("}",""));
        Map<String, String> map = getStringToMap(payload.replace("{","").replace("}",""));
        sendEmail(map);
    }

    private void sendEmail(Map<String,String> payload){
        System.out.println("payload: "+ payload);
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("name",payload.get("name"));
            variables.put("fecha",payload.get("fecha"));
            emailSender.sendHtmlEmail(payload.get("to"),"Contanotify te recuerda estar pendiente de tus obligaciones tributarias","remember-obligation",variables);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, String> getStringToMap(String payload) {
        String[] keyValuePairs = payload.split(",");
        Map<String, String> map = new HashMap<>();
        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim(); // Trim whitespace
                String value = keyValue[1].trim(); // Trim whitespace
                map.put(key, value);
            }
        }
        System.out.println(map);
        return map;
    }
}
