package co.java.app.contanotify.config;

import jakarta.jms.Queue;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActiveMQConfig {
    @Bean
    public Queue reminder5DaysQueue() {
        return new ActiveMQQueue("reminder-5days");
    }

    @Bean
    public Queue reminder3DaysQueue() {
        return new ActiveMQQueue("reminder-3days");
    }

    @Bean
    public Queue reminder1DayQueue() {
        return new ActiveMQQueue("reminder-1day");
    }

    @Bean
    public Queue reminderTodayQueue() {
        return new ActiveMQQueue("reminder-today");
    }

    @Bean
    public Queue reminderStatusClientQueue() {
        return new ActiveMQQueue("reminder-status-client");
    }

    @Bean
    public Queue forgotPasswordQueue() {
        return new ActiveMQQueue("forgot-password");
    }
}
