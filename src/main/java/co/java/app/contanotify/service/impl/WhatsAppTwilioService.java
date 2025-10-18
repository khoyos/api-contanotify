package co.java.app.contanotify.service.impl;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppTwilioService {

    /*@Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;

    @Value("${twilio.phoneNumber}")
    private String from;

    public void sendMessage(String to, String body) {
        Twilio.init(accountSid, authToken);
        System.out.println("to = " + to + ", body = " + body + ", from = " + from);
        Message message = Message.creator(
                new com.twilio.type.PhoneNumber("whatsapp:" + to),
                new com.twilio.type.PhoneNumber("whatsapp:" + from),
                body
        ).create();

        System.out.println("Mensaje enviado con SID: " + message.getSid());
    }*/
}
