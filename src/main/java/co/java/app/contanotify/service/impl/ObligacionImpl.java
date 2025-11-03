package co.java.app.contanotify.service.impl;

import co.java.app.contanotify.dto.AlertasCriticasDTO;
import co.java.app.contanotify.dto.CardGeneralDTO;
import co.java.app.contanotify.dto.CorporativoPorEntidadGobiernoDTO;
import co.java.app.contanotify.dto.ObligacionDTO;
import co.java.app.contanotify.model.ConfiguracionObligaciones;
import co.java.app.contanotify.model.Obligacion;
import co.java.app.contanotify.model.ObligacionCliente;
import co.java.app.contanotify.model.Usuario;
import co.java.app.contanotify.repository.ConfiguracionObligacionesRepository;
import co.java.app.contanotify.repository.ObligacionClienteRepository;
import co.java.app.contanotify.repository.ObligacionRepository;
import co.java.app.contanotify.repository.UsuarioRepository;
import co.java.app.contanotify.service.IObligacion;
import co.java.app.contanotify.util.FechaLegible;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ObligacionImpl implements IObligacion {

    private final ObligacionRepository obligacionRepository;
    private final ConfiguracionObligacionesRepository configuracionObligacionesRepository;
    private final ObligacionClienteRepository obligacionClienteRepository;
    private final UsuarioRepository usuarioRepository;

    public ObligacionImpl(ObligacionRepository obligacionRepository,
                          ConfiguracionObligacionesRepository configuracionObligacionesRepository,
                          ObligacionClienteRepository obligacionClienteRepository,
                          UsuarioRepository usuarioRepository) {
        this.obligacionRepository = obligacionRepository;
        this.configuracionObligacionesRepository = configuracionObligacionesRepository;
        this.obligacionClienteRepository = obligacionClienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Optional<ObligacionDTO> findByName(String name) {
        Optional<Obligacion> obligacion = obligacionRepository.findByName(name);
        if(obligacion.isEmpty()){
            return Optional.empty();
        }
        ObligacionDTO obligacionDTO = new ObligacionDTO();

        obligacionDTO.setId(obligacion.get().getId());
        obligacionDTO.setName(obligacion.get().getName());

        return Optional.of(obligacionDTO);
    }

    @Override
    public void save(ObligacionDTO obligacionDTO) {
        Obligacion obligacion = new Obligacion();

        obligacion.setName(obligacionDTO.getName().toLowerCase());
        obligacion.setState(true);

        obligacion.setPublicId(UUID.randomUUID().toString());
        obligacionRepository.save(obligacion);
    }

    @Override
    public List<ObligacionDTO> findAll() {
        List<ObligacionDTO> listObligaciones = new ArrayList<>();
        List<Obligacion> obligaciones = obligacionRepository.findAll();
        if(obligaciones.isEmpty()){
            throw new RuntimeException("No se encontro obligaciones");
        }
        for(Obligacion obligacion: obligaciones){
            ObligacionDTO obligacionDTO = new ObligacionDTO();
            obligacionDTO.setId(obligacion.getPublicId().toString());
            obligacionDTO.setName(obligacion.getName());
            listObligaciones.add(obligacionDTO);
        }

        return listObligaciones;
    }

    @Override
    public List<AlertasCriticasDTO> dashboardAlertas(String usuarioId) {
        List<AlertasCriticasDTO> alertasCriticasList = new ArrayList<>();

        Optional<List<ConfiguracionObligaciones>> configuracionObligacionesList = configuracionObligacionesRepository.findByUsuarioIdAndEstadoNot(usuarioId, "Declarado y Presentado");

        if(configuracionObligacionesList.isEmpty()){
            throw new RuntimeException("Error al consultar dashboard");
        }

        for (ConfiguracionObligaciones co: configuracionObligacionesList.get()){
            Optional<ObligacionCliente> response = obligacionClienteRepository.findByPublicId(co.getObligacionClienteId());
            AlertasCriticasDTO alertasCriticasDTO = new AlertasCriticasDTO();

            DateTimeFormatter formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDate fecha = LocalDate.parse(co.getFecha(), formateador);

            alertasCriticasDTO.setFechaVencimiento(co.getFecha());
            alertasCriticasDTO.setNombreCliente(co.getNombreCliente());
            alertasCriticasDTO.setObligacionRenta(co.getRenta());
            alertasCriticasDTO.setObligacionPago(co.getPago());
            alertasCriticasDTO.setPerido(String.valueOf(fecha.getYear()));

            if(fecha.isBefore(LocalDate.now())){
                alertasCriticasDTO.setVencida(true);
                String diasPasados = FechaLegible.formatoPeriodo(fecha, LocalDate.now());
                alertasCriticasDTO.setMensaje("Vencido: ".concat(diasPasados));
                alertasCriticasList.add(alertasCriticasDTO);
                continue;
            }

            if(response.get().isReminderToDaySent()){
                alertasCriticasDTO.setUrgente(response.get().isReminderToDaySent());
                alertasCriticasDTO.setMensaje("Vence Hoy ");
                alertasCriticasList.add(alertasCriticasDTO);
                continue;
            }
            if(response.get().isReminder1DaySent()){
                alertasCriticasDTO.setUrgente(response.get().isReminder1DaySent());
                alertasCriticasDTO.setMensaje("Vence Mañana ");
                alertasCriticasList.add(alertasCriticasDTO);
                continue;
            }
            if(response.get().isReminder3DaysSent()){
                alertasCriticasDTO.setAlta(response.get().isReminder3DaysSent());
                alertasCriticasDTO.setMensaje("Vence Dentro de 3 Días");
                alertasCriticasList.add(alertasCriticasDTO);
                continue;
            }
            if(response.get().isReminder5DaysSent()){
                alertasCriticasDTO.setMedia(response.get().isReminder5DaysSent());
                alertasCriticasDTO.setMensaje("Vence Dentro de 5 Días");
                alertasCriticasList.add(alertasCriticasDTO);
                continue;
            }

        }

        return alertasCriticasList;
    }

    @Override
    public CardGeneralDTO dashboardCardGeneral(String usuarioId) {
        CardGeneralDTO response = new CardGeneralDTO();

        Optional<List<ConfiguracionObligaciones>> obligacionesPorHacerList = configuracionObligacionesRepository.findByUsuarioIdAndEstado(usuarioId, "Por Hacer");
        if(obligacionesPorHacerList.isEmpty()){
            response.setPorHacer(0);
        }
        response.setPorHacer(obligacionesPorHacerList.get().size());

        Optional<List<ConfiguracionObligaciones>> obligacionesElaboracionList = configuracionObligacionesRepository.findByUsuarioIdAndEstado(usuarioId, "Elaboración");
        if(obligacionesElaboracionList.isEmpty()){
            response.setElaboracion(0);
        }
        response.setElaboracion(obligacionesElaboracionList.get().size());

        Optional<List<ConfiguracionObligaciones>> obligacionesPendientePorDocumentosList = configuracionObligacionesRepository.findByUsuarioIdAndEstado(usuarioId, "Pendiente por Documentos.");
        if(obligacionesPendientePorDocumentosList.isEmpty()){
            response.setPendientePorDocs(0);
        }
        response.setPendientePorDocs(obligacionesPendientePorDocumentosList.get().size());

        Optional<List<ConfiguracionObligaciones>> obligacionesDeclaradoPresentadoList = configuracionObligacionesRepository.findByUsuarioIdAndEstado(usuarioId, "Declarado y Presentado");
        if(obligacionesDeclaradoPresentadoList.isEmpty()){
            response.setDeclaradoPresentado(0);
        }
        response.setDeclaradoPresentado(obligacionesDeclaradoPresentadoList.get().size());

        Optional<List<ConfiguracionObligaciones>> obligacionesList = configuracionObligacionesRepository.findByUsuarioIdAndEstadoNot(usuarioId, "Declarado y Presentado");
        int totalVencidas = 0;
        if(obligacionesList.isEmpty()){
            response.setVencidas(totalVencidas);
        }

        for (ConfiguracionObligaciones co: obligacionesList.get()) {
            DateTimeFormatter formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDate fecha = LocalDate.parse(co.getFecha(), formateador);

            if (fecha.isBefore(LocalDate.now())) {
                totalVencidas++;
                continue;
            }
        }
        response.setVencidas(totalVencidas);

        return response;
    }

    @Override
    public List<CorporativoPorEntidadGobiernoDTO> dashboardCorporativoPorEntidad(String usuarioId) {

        Optional<List<ConfiguracionObligaciones>> obligacionesPorHacerList =
                configuracionObligacionesRepository.findByUsuarioId(usuarioId);

        // Si no hay obligaciones, devolvemos los 12 meses con valor 0
        if (obligacionesPorHacerList.isEmpty() || obligacionesPorHacerList.get().isEmpty()) {
            return IntStream.rangeClosed(1, 12)
                    .mapToObj(mes -> {
                        CorporativoPorEntidadGobiernoDTO dto = new CorporativoPorEntidadGobiernoDTO();
                        dto.setName(FechaLegible.nombreMes(mes));
                        dto.setValue(0);
                        return dto;
                    })
                    .collect(Collectors.toList());
        }

        List<ConfiguracionObligaciones> obligaciones = obligacionesPorHacerList.get();

        // Agrupar por mes (extraído de la fecha)
        Map<Integer, Long> conteoPorMes = obligaciones.stream()
                .filter(o -> o.getFecha() != null && !o.getFecha().isEmpty())
                .collect(Collectors.groupingBy(
                        o -> FechaLegible.obtenerMes(o.getFecha()), // debe retornar 1-12
                        Collectors.counting()
                ));

        // Crear lista con los 12 meses, asignando 0 si no existe, y ordenada enero→diciembre
        return IntStream.rangeClosed(1, 12)
                .mapToObj(mes -> {
                    CorporativoPorEntidadGobiernoDTO dto = new CorporativoPorEntidadGobiernoDTO();
                    dto.setName(FechaLegible.nombreMesAbreviado(mes)); // Ej: Enero, Febrero, etc.
                    dto.setValue(conteoPorMes.getOrDefault(mes, 0L).intValue());
                    return dto;
                })
                // El .sorted no es estrictamente necesario porque ya generamos en orden 1..12,
                // pero lo dejo comentado por si prefieres asegurar el orden por nombre:
                // .sorted(Comparator.comparingInt(e -> FechaLegible.obtenerNumeroMesPorNombre(e.getName())))
                .collect(Collectors.toList());
    }


}
