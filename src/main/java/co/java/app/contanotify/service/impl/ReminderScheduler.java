package co.java.app.contanotify.service.impl;

import co.java.app.contanotify.model.Calendario;
import co.java.app.contanotify.model.ConfiguracionCliente;
import co.java.app.contanotify.model.ObligacionCliente;
import co.java.app.contanotify.model.Usuario;
import co.java.app.contanotify.repository.*;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class ReminderScheduler {
    private final ObligacionClienteRepository obligacionClienteRepository;
    private final CalendarioRepository calendarioRepository;
    private final ConfiguracionClienteRepository configuracionClienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReminderProducer reminderProducer;

    public ReminderScheduler(ObligacionClienteRepository obligacionClienteRepository,
                             ReminderProducer reminderProducer,
                             CalendarioRepository calendarioRepository,
                             ConfiguracionClienteRepository configuracionClienteRepository,
                             UsuarioRepository usuarioRepository) {
        this.obligacionClienteRepository = obligacionClienteRepository;
        this.reminderProducer = reminderProducer;
        this.calendarioRepository = calendarioRepository;
        this.configuracionClienteRepository= configuracionClienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Se ejecuta todos los días a las 8am
    //@Scheduled(cron = "0 0 8 * * ?")
    @Scheduled(fixedRate = 60000)
    public void scheduleReminders() {
        LocalDate today = LocalDate.now();
        System.out.println("* scheduleReminders leyendo cola");

        // Buscar tareas para 5 días adelante
        processRemindersPlusDays(today.plusDays(5),5);

        // Buscar tareas para 3 días adelante
        processRemindersPlusDays(today.plusDays(3), 3);

        // Buscar tareas para 1 día adelante
        processRemindersPlusDays(today.plusDays(1), 1);

        // Buscar tareas para hoy
        processRemindersPlusDays(today,0);

    }

    private void processRemindersPlusDays(LocalDate today, int daysToAdd) {
        Optional<List<ObligacionCliente>> tasksOpt = obligacionClienteRepository.findByFecha(today.atStartOfDay());

        if(tasksOpt.isEmpty()){
            return;
        }

        List<ObligacionCliente> tasks = tasksOpt.get();

        for(ObligacionCliente obligacionDelCliente: tasks){
           // long daysToDue = ChronoUnit.DAYS.between(today, obligacionDelCliente.getFecha());

            if (daysToAdd == 5 && !obligacionDelCliente.isReminder5DaysSent()) {
                // Buscar tareas para 5 días adelante
                sendReminders(obligacionDelCliente, 5);
                obligacionDelCliente.setReminder5DaysSent(true);

                obligacionDelCliente.setPublicId(UUID.randomUUID());
                obligacionClienteRepository.save(obligacionDelCliente);

                System.out.println("* Se ejecuto recordar 5 días antes envio email y se guardo obligacion cliente");
            } else if (daysToAdd == 3 && !obligacionDelCliente.isReminder3DaysSent()) {
                // Buscar tareas para 3 días adelante
                sendReminders(obligacionDelCliente, 3);
                obligacionDelCliente.setReminder3DaysSent(true);

                obligacionDelCliente.setPublicId(UUID.randomUUID());
                obligacionClienteRepository.save(obligacionDelCliente);

                System.out.println("* Se ejecuto recordar 3 días antes envio email y se guardo obligacion cliente");
            } else if (daysToAdd == 1 && !obligacionDelCliente.isReminder1DaySent()) {
                // Buscar tareas para 1 día adelante
                sendReminders(obligacionDelCliente, 1);
                obligacionDelCliente.setReminder1DaySent(true);

                obligacionDelCliente.setPublicId(UUID.randomUUID());
                obligacionClienteRepository.save(obligacionDelCliente);

                System.out.println("* Se ejecuto recordar 1 días antes envio email y se guardo obligacion cliente");
            }else if (daysToAdd == 0 && !obligacionDelCliente.isReminderToDaySent()) {
                // Buscar tareas para 1 día adelante
                sendReminders(obligacionDelCliente, 0);
                obligacionDelCliente.setReminderToDaySent(true);

                obligacionDelCliente.setPublicId(UUID.randomUUID());
                obligacionClienteRepository.save(obligacionDelCliente);

                System.out.println("* Se ejecuto recordar hoy días antes envio email y se guardo obligacion cliente");
            }

        }
    }

    private void sendReminders(ObligacionCliente obligacionCliente, int days) {
       Optional<Calendario> calendario = calendarioRepository.findById(String.valueOf(obligacionCliente.getCalendarioId()));
       Optional<ConfiguracionCliente> configuracionCliente = configuracionClienteRepository.findById(obligacionCliente.getConfiguracionClienteId().toString());
       Optional<Usuario> usuarioCliente = usuarioRepository.findById(String.valueOf(configuracionCliente.get().getUsuarioClienteId()));
        Optional<Usuario> usuarioContador = usuarioRepository.findById(String.valueOf(configuracionCliente.get().getUsuarioId()));

       boolean isNotificarCliente = configuracionCliente.get().isNotificarCliente();
       boolean isNotificarContador = configuracionCliente.get().isNotificarContador();

       if(isNotificarCliente){
           Map<String, Object> request = new HashMap<>();

           String msg = "Recordatorio: " + calendario.get().getNombre() + " vence el " + obligacionCliente.getFecha();

           request.put("to", usuarioCliente.get().getEmail());
           request.put("message", msg);
           request.put("name", usuarioCliente.get().getNombre());
           request.put("fecha", obligacionCliente.getFecha());
           if(days == 0){
               request.put("hoy", "Hoy es el día");
           }else{
               request.put("dias", days);
           }

           reminderProducer.sendReminder(request, days);
       }

        if(isNotificarContador){
            Map<String, Object> request = new HashMap<>();

            String msg = "Recordatorio: " + calendario.get().getNombre() + " vence el " + obligacionCliente.getFecha();

            request.put("to", usuarioContador.get().getEmail());
            request.put("message", msg);
            request.put("name", usuarioContador.get().getNombre());
            request.put("fecha", obligacionCliente.getFecha());

            if(days == 0){
                request.put("hoy", "Hoy es el día");
            }else{
                request.put("dias", days);
            }

            reminderProducer.sendReminder(request, days);
        }

    }

}
