package co.java.app.contanotify.service.impl;

import jakarta.jms.Queue;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class ReminderProducer {
    private final JmsTemplate jmsTemplate;
    private final Queue reminder5DaysQueue;
    private final Queue reminder3DaysQueue;
    private final Queue reminder1DayQueue;
    private final Queue reminderTodayQueue;
    private final Queue reminderStatusClientQueue;

    public ReminderProducer(JmsTemplate jmsTemplate,
                            Queue reminder5DaysQueue,
                            Queue reminder3DaysQueue,
                            Queue reminder1DayQueue,
                            Queue reminderTodayQueue,
                            Queue reminderStatusClientQueue) {
        this.jmsTemplate = jmsTemplate;
        this.reminder5DaysQueue = reminder5DaysQueue;
        this.reminder3DaysQueue = reminder3DaysQueue;
        this.reminder1DayQueue = reminder1DayQueue;
        this.reminderTodayQueue = reminderTodayQueue;
        this.reminderStatusClientQueue = reminderStatusClientQueue;
    }

    public void sendReminder(Map<String,String> payload, int days) {
        //System.out.println("* sendReminder");
        switch (days) {
            case 5 -> jmsTemplate.convertAndSend(reminder5DaysQueue, payload.toString());
            case 3 -> jmsTemplate.convertAndSend(reminder3DaysQueue, payload.toString());
            case 1 -> jmsTemplate.convertAndSend(reminder1DayQueue, payload.toString());
            case 0 -> jmsTemplate.convertAndSend(reminderTodayQueue, payload.toString());
        }
    }

    public void sendReminderClient(Map<String,Object> payload) {
        System.out.println("* sendReminder cliente");
        jmsTemplate.convertAndSend(reminderStatusClientQueue, payload.toString());

    }
}
