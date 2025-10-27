package co.java.app.contanotify.service.impl;

import co.java.app.contanotify.dto.ObligacionDTO;
import co.java.app.contanotify.model.*;
import co.java.app.contanotify.repository.*;
import co.java.app.contanotify.util.FechaLegible;
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
    private final ObligacionRepository obligacionRepository;

    public ReminderScheduler(ObligacionClienteRepository obligacionClienteRepository,
                             ReminderProducer reminderProducer,
                             CalendarioRepository calendarioRepository,
                             ConfiguracionClienteRepository configuracionClienteRepository,
                             UsuarioRepository usuarioRepository,
                             ObligacionRepository obligacionRepository) {
        this.obligacionClienteRepository = obligacionClienteRepository;
        this.reminderProducer = reminderProducer;
        this.calendarioRepository = calendarioRepository;
        this.configuracionClienteRepository= configuracionClienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.obligacionRepository = obligacionRepository;
    }

    // Se ejecuta todos los días a las 8am
    //@Scheduled(cron = "0 0 8 * * ?")
    @Scheduled(fixedRate = 60000)
    public void scheduleReminders() {
        LocalDate today = LocalDate.now();
        //System.out.println("* scheduleReminders leyendo cola");

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

                obligacionClienteRepository.save(obligacionDelCliente);

                System.out.println("* Se ejecuto recordar 5 días antes envio email y se guardo obligacion cliente");
            } else if (daysToAdd == 3 && !obligacionDelCliente.isReminder3DaysSent()) {
                // Buscar tareas para 3 días adelante
                sendReminders(obligacionDelCliente, 3);
                obligacionDelCliente.setReminder3DaysSent(true);

                obligacionClienteRepository.save(obligacionDelCliente);

                System.out.println("* Se ejecuto recordar 3 días antes envio email y se guardo obligacion cliente");
            } else if (daysToAdd == 1 && !obligacionDelCliente.isReminder1DaySent()) {
                // Buscar tareas para 1 día adelante
                sendReminders(obligacionDelCliente, 1);
                obligacionDelCliente.setReminder1DaySent(true);

                obligacionClienteRepository.save(obligacionDelCliente);

                System.out.println("* Se ejecuto recordar 1 días antes envio email y se guardo obligacion cliente");
            }else if (daysToAdd == 0 && !obligacionDelCliente.isReminderToDaySent()) {
                // Buscar tareas para 1 día adelante
                sendReminders(obligacionDelCliente, 0);
                obligacionDelCliente.setReminderToDaySent(true);

                obligacionClienteRepository.save(obligacionDelCliente);

                System.out.println("* Se ejecuto recordar hoy días antes envio email y se guardo obligacion cliente");
            }

        }
    }

    private void sendReminders(ObligacionCliente obligacionCliente, int days) {
       Optional<Calendario> calendario = calendarioRepository.findById(String.valueOf(obligacionCliente.getCalendarioId()));
       Optional<Obligacion> obligacion = obligacionRepository.findById(calendario.get().getObligacionId());
       Optional<ConfiguracionCliente> configuracionCliente = configuracionClienteRepository.findById(obligacionCliente.getConfiguracionClienteId().toString());
       Optional<Usuario> usuarioCliente = usuarioRepository.findById(String.valueOf(configuracionCliente.get().getUsuarioClienteId()));
        Optional<Usuario> usuarioContador = usuarioRepository.findById(String.valueOf(configuracionCliente.get().getUsuarioId()));

       boolean isNotificarCliente = configuracionCliente.get().isNotificarCliente();
       boolean isNotificarContador = configuracionCliente.get().isNotificarContador();

       String colorClass = switch (days) {
            case 0, 1 -> "obligation-red";
            case 3 -> "obligation-orange";
            case 5 -> "obligation-yellow";
            default -> "";
       };

       if(isNotificarCliente){
           Map<String, String> request = new HashMap<>();

           String msg = "Recordatorio: " + calendario.get().getNombre() + " vence el " + obligacionCliente.getFecha();

           request.put("to", usuarioCliente.get().getEmail());
           request.put("message", msg);
           request.put("contadorNombre", usuarioContador.get().getNombre());
           request.put("clienteNombre", usuarioCliente.get().getNombre());
           String fechaLegible = FechaLegible.convertirFechaISOPorFechaLegible(obligacionCliente.getFecha().toString());
           request.put("fecha", fechaLegible.replace(",",""));
           request.put("renta", obligacion.get().getName());
           request.put("pago", calendario.get().getNombre());

           request.put("dias", String.valueOf(days));
           request.put("colorClass", colorClass);


           request.put("template", "reminder-for-client");

           reminderProducer.sendReminder(request, days);
       }

        if(isNotificarContador){
            Map<String, String> request = new HashMap<>();

            String msg = "Recordatorio: " + calendario.get().getNombre() + " vence el " + obligacionCliente.getFecha();

            request.put("to", usuarioContador.get().getEmail());
            request.put("message", msg);
            request.put("contadorNombre", usuarioContador.get().getNombre());
            request.put("clienteNombre", usuarioCliente.get().getNombre());
            String fechaLegible = FechaLegible.convertirFechaISOPorFechaLegible(obligacionCliente.getFecha().toString());
            request.put("fecha", fechaLegible.replace(",",""));
            request.put("renta", obligacion.get().getName());
            request.put("pago", calendario.get().getNombre());

            request.put("dias", String.valueOf(days));
            request.put("colorClass", colorClass);


            request.put("template", "reminder-for-accountant");

            reminderProducer.sendReminder(request, days);
        }

    }

}
