package co.java.app.contanotify.service.impl;

import co.java.app.contanotify.config.DiasAlertaProperties;
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
    private final DiasAlertaProperties diasAlerta;

    public ReminderScheduler(ObligacionClienteRepository obligacionClienteRepository,
                             ReminderProducer reminderProducer,
                             CalendarioRepository calendarioRepository,
                             ConfiguracionClienteRepository configuracionClienteRepository,
                             UsuarioRepository usuarioRepository,
                             ObligacionRepository obligacionRepository,
                             DiasAlertaProperties diasAlerta) {
        this.obligacionClienteRepository = obligacionClienteRepository;
        this.reminderProducer = reminderProducer;
        this.calendarioRepository = calendarioRepository;
        this.configuracionClienteRepository= configuracionClienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.obligacionRepository = obligacionRepository;
        this.diasAlerta = diasAlerta;
    }

    // Se ejecuta todos los días a las 8am
    //@Scheduled(cron = "0 0 8 * * ?")
    @Scheduled(fixedRate = 60000)
    public void scheduleReminders() {
        LocalDate today = LocalDate.now();
        //System.out.println("* scheduleReminders leyendo cola");

        // Buscar tareas para hoy
        LocalDate localHoy = today.minusDays(1);
        processRemindersPlusDays(localHoy, localHoy.plusDays(diasAlerta.getUrgente() + 1), diasAlerta.getHoy());

        // Buscar tareas para ALTAS días adelante
        LocalDate localAlta = today.plusDays(diasAlerta.getUrgente());
        processRemindersPlusDays(localAlta, localAlta.plusDays(diasAlerta.getAlta() + 1), diasAlerta.getAlta());

        // Buscar tareas para MEDIA días adelante
        LocalDate localMedia = today.plusDays(diasAlerta.getAlta());
        processRemindersPlusDays(localMedia, localMedia.plusDays(diasAlerta.getMedia() + 1),diasAlerta.getMedia());


        // Buscar tareas para 1 día adelante
        //processRemindersPlusDays(today.plusDays(diasAlerta.getAlta()), today.plusDays(diasAlerta.getUrgente()), diasAlerta.getUrgente());


    }

    private void processRemindersPlusDays(LocalDate today,LocalDate todayPlusDays, int daysToAdd) {
        List<ObligacionCliente> tasksOpt = obligacionClienteRepository.findByFechaBetween(today.atStartOfDay(), todayPlusDays.atStartOfDay());

        if(tasksOpt.isEmpty()){
            return;
        }

        for(ObligacionCliente obligacionDelCliente: tasksOpt){
           // long daysToDue = ChronoUnit.DAYS.between(today, obligacionDelCliente.getFecha());

            if (daysToAdd == diasAlerta.getMedia() && !obligacionDelCliente.isReminder5DaysSent()) {
                // Buscar tareas para 5 días adelante
                sendReminders(obligacionDelCliente, diasAlerta.getMedia());
                obligacionDelCliente.setReminder5DaysSent(true);

                obligacionClienteRepository.save(obligacionDelCliente);

                System.out.println("* Se ejecuto recordar 5 días antes envio email y se guardo obligacion cliente");
            } else if (daysToAdd == diasAlerta.getAlta() && !obligacionDelCliente.isReminder3DaysSent()) {
                // Buscar tareas para 3 días adelante
                sendReminders(obligacionDelCliente, diasAlerta.getAlta());
                obligacionDelCliente.setReminder3DaysSent(true);

                obligacionClienteRepository.save(obligacionDelCliente);

                System.out.println("* Se ejecuto recordar 3 días antes envio email y se guardo obligacion cliente");
            } else if (daysToAdd == diasAlerta.getUrgente() && !obligacionDelCliente.isReminder1DaySent()) {
                // Buscar tareas para 1 día adelante
                sendReminders(obligacionDelCliente, diasAlerta.getUrgente());
                obligacionDelCliente.setReminder1DaySent(true);

                obligacionClienteRepository.save(obligacionDelCliente);

                System.out.println("* Se ejecuto recordar 1 días antes envio email y se guardo obligacion cliente");
            }else if (daysToAdd == diasAlerta.getHoy() && !obligacionDelCliente.isReminderToDaySent()) {
                // Buscar tareas para 1 día adelante
                sendReminders(obligacionDelCliente, diasAlerta.getHoy());
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
