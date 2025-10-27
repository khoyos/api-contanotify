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
        Map<String, String> map = getStringToMap(payload.replace("{","").replace("}",""));
        sendEmailAccountant(map);
    }

    @JmsListener(destination = "reminder-3days")
    public void process3DaysReminder(String payload) {
        System.out.println("[3 días antes] Enviando recordatorio: " + payload);
        Map<String, String> map = getStringToMap(payload.replace("{","").replace("}",""));
        sendEmailAccountant(map);
    }

    @JmsListener(destination = "reminder-1day")
    public void process1DayReminder(String payload) {
        System.out.println("[1 día antes] Enviando recordatorio: " + payload);
        Map<String, String> map = getStringToMap(payload.replace("{","").replace("}",""));
        sendEmailAccountant(map);
    }

    @JmsListener(destination = "reminder-today")
    public void processTodayReminder(String payload) {
        System.out.println("[Hoy] Enviando recordatorio: " + payload.replace("{","").replace("}",""));
        Map<String, String> map = getStringToMap(payload.replace("{","").replace("}",""));
        sendEmailAccountant(map);
    }

    @JmsListener(destination = "reminder-status-client")
    public void processStatusClientReminder(String payload) {
        System.out.println("[Hoy] Enviando recordatorio a cliente: " + payload.replace("{","").replace("}",""));
        Map<String, String> map = getStringToMap(payload.replace("{","").replace("}",""));
        sendEmailClient(map);
    }

    private void sendEmailAccountant(Map<String,String> payload){
        System.out.println("payload: "+ payload);
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("contadorNombre",payload.get("contadorNombre"));
            variables.put("clienteNombre",payload.get("clienteNombre"));
            variables.put("fecha",payload.get("fecha"));
            variables.put("renta", payload.get("renta"));
            variables.put("pago", payload.get("pago"));
            variables.put("colorClass", payload.get("colorClass"));
            variables.put("dias", payload.get("dias").equals("0")?"Es Hoy": payload.get("dias"));

            variables.toString();
            emailSender.sendHtmlEmail(payload.get("to"),"Contanotify te recuerda estar pendiente de tus obligaciones tributarias",payload.get("template"),variables);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sendEmailClient(Map<String,String> payload){
        System.out.println("payload: "+ payload);
        try {

            Map<String, Object> variables = new HashMap<>();
            variables.put("name",payload.get("name"));
            variables.put("fecha",payload.get("fecha"));
            variables.put("renta",payload.get("renta"));
            variables.put("pago",payload.get("pago"));
            variables.put("estado",payload.get("estado"));
            variables.put("estadoObservacion",payload.get("estadoObservacion"));
            variables.put("observacion",payload.get("observacion"));

            emailSender.sendHtmlEmail(payload.get("to"),"Contanotify te recuerda estar pendiente de tus obligaciones tributarias","reminder-obligation-status-client",variables);
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
