package co.java.app.contanotify.service.impl;

import co.java.app.contanotify.config.DiasAlertaProperties;
import co.java.app.contanotify.dto.*;
import co.java.app.contanotify.model.*;
import co.java.app.contanotify.repository.*;
import co.java.app.contanotify.service.IObligacion;
import co.java.app.contanotify.util.FechaLegible;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ObligacionImpl implements IObligacion {

    private final ObligacionRepository obligacionRepository;
    private final ConfiguracionObligacionesRepository configuracionObligacionesRepository;
    private final ObligacionClienteRepository obligacionClienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final EntidadRepository entidadRepository;
    private final DiasAlertaProperties diasAlertaProperties;

    public ObligacionImpl(ObligacionRepository obligacionRepository,
                          ConfiguracionObligacionesRepository configuracionObligacionesRepository,
                          ObligacionClienteRepository obligacionClienteRepository,
                          UsuarioRepository usuarioRepository,
                          EntidadRepository entidadRepository,
                          DiasAlertaProperties diasAlertaProperties) {
        this.obligacionRepository = obligacionRepository;
        this.configuracionObligacionesRepository = configuracionObligacionesRepository;
        this.obligacionClienteRepository = obligacionClienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.entidadRepository = entidadRepository;
        this.diasAlertaProperties = diasAlertaProperties;
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
                alertasCriticasDTO.setDays("Hoy");
                alertasCriticasList.add(alertasCriticasDTO);
                continue;
            }
            if(response.get().isReminder1DaySent()){
                alertasCriticasDTO.setUrgente(response.get().isReminder1DaySent());
                alertasCriticasDTO.setMensaje("Vence Mañana ");
                alertasCriticasDTO.setDays(String.valueOf(diasAlertaProperties.getUrgente()));
                alertasCriticasList.add(alertasCriticasDTO);
                continue;
            }
            if(response.get().isReminder3DaysSent()){
                alertasCriticasDTO.setAlta(response.get().isReminder3DaysSent());
                alertasCriticasDTO.setMensaje("Vence Dentro de Días");
                alertasCriticasDTO.setDays(String.valueOf(diasAlertaProperties.getAlta()));
                alertasCriticasList.add(alertasCriticasDTO);
                continue;
            }
            if(response.get().isReminder5DaysSent()){
                alertasCriticasDTO.setMedia(response.get().isReminder5DaysSent());
                alertasCriticasDTO.setMensaje("Vence Dentro de 5 Días");
                alertasCriticasDTO.setDays(String.valueOf(diasAlertaProperties.getMedia()));
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

        // Si no hay obligaciones, devolvemos los 12 meses con valor 0 y sin entidad
        if (obligacionesPorHacerList.isEmpty() || obligacionesPorHacerList.get().isEmpty()) {
            return IntStream.rangeClosed(1, 12)
                    .mapToObj(mes -> {
                        CorporativoPorEntidadGobiernoDTO dto = new CorporativoPorEntidadGobiernoDTO();
                        dto.setName(FechaLegible.nombreMesAbreviado(mes));
                        dto.setValue(0);
                        dto.setType(null); //No hay entidad asociada
                        return dto;
                    })
                    .collect(Collectors.toList());
        }

        List<ConfiguracionObligaciones> obligaciones = obligacionesPorHacerList.get();

        // Agrupamos por mes y entidad (porque varias entidades pueden tener registros en distintos meses)
        Map<Integer, Map<String, Long>> conteoPorMesYEntidad = obligaciones.stream()
                .filter(o -> o.getFecha() != null && !o.getFecha().isEmpty())
                .collect(Collectors.groupingBy(
                        o -> FechaLegible.obtenerMes(o.getFecha()), // clave: mes (1–12)
                        Collectors.groupingBy(
                                o -> Optional.ofNullable(o.getEntidad()).orElse("Desconocida"), // clave: entidad
                                Collectors.counting()
                        )
                ));

        // Generamos la lista de DTOs (puede haber varios por mes si hay varias entidades)
        List<CorporativoPorEntidadGobiernoDTO> resultado = new ArrayList<>();

        IntStream.rangeClosed(1, 12).forEach(mes -> {
            Map<String, Long> entidades = conteoPorMesYEntidad.getOrDefault(mes, Collections.emptyMap());

            if (entidades.isEmpty()) {
                // Si no hay entidades ese mes, agregamos el mes con valor 0
                CorporativoPorEntidadGobiernoDTO dto = new CorporativoPorEntidadGobiernoDTO();
                dto.setName(FechaLegible.nombreMesAbreviado(mes));
                dto.setValue(0);
                dto.setType(null);
                resultado.add(dto);
            } else {
                // Si hay entidades, creamos un DTO por cada una
                entidades.forEach((entidad, conteo) -> {
                    CorporativoPorEntidadGobiernoDTO dto = new CorporativoPorEntidadGobiernoDTO();
                    dto.setName(FechaLegible.nombreMesAbreviado(mes));
                    dto.setValue(conteo.intValue());
                    dto.setType(entidad); //Aquí asignamos el valor del atributo "entidad"
                    resultado.add(dto);
                });
            }
        });

        return resultado;
    }

    @Override
    public List<ClientesConRentaPorMesDTO> dashboardCorporativoClientesConRentaPorMes(String usuarioId) {

        Optional<List<ConfiguracionObligaciones>> obligacionesOpt =
                configuracionObligacionesRepository.findByUsuarioId(usuarioId);

        if (obligacionesOpt.isEmpty() || obligacionesOpt.get().isEmpty()) {
            return IntStream.rangeClosed(1, 12)
                    .mapToObj(mes -> new ClientesConRentaPorMesDTO(
                            FechaLegible.nombreMesAbreviado(mes),
                            0
                    ))
                    .collect(Collectors.toList());
        }

        List<ConfiguracionObligaciones> obligaciones = obligacionesOpt.get();

        List<ConfiguracionObligaciones> filtradas = obligaciones.stream()
                .filter(o -> o.getRenta() != null && !o.getRenta().isEmpty())
                .filter(o -> o.getPago() != null && !o.getPago().isEmpty())
                .filter(o -> o.getFecha() != null && !o.getFecha().isEmpty())
                .collect(Collectors.toList());

        Map<Integer, Long> conteoPorMes = filtradas.stream()
                .collect(Collectors.groupingBy(
                        o -> FechaLegible.obtenerMes(o.getFecha()), // mes 1–12
                        Collectors.mapping(ConfiguracionObligaciones::getClienteId, Collectors.toSet())
                ))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> (long) e.getValue().size()
                ));

        return IntStream.rangeClosed(1, 12)
                .mapToObj(mes -> new ClientesConRentaPorMesDTO(
                        FechaLegible.nombreMesAbreviado(mes),
                        conteoPorMes.getOrDefault(mes, 0L).intValue()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<RentasPorAnioDTO> dashboardCorporativoRentasPorAnio(String usuarioId) {

        // 1️⃣ Consultar las obligaciones por estado "Declarado y Presentado"
        Optional<List<ConfiguracionObligaciones>> obligacionesOpt =
                configuracionObligacionesRepository.findByUsuarioIdAndEstado(usuarioId, "Declarado y Presentado");

        // 2️⃣ Validar si hay resultados
        if (obligacionesOpt.isEmpty() || obligacionesOpt.get().isEmpty()) {
            return Collections.emptyList();
        }

        List<ConfiguracionObligaciones> obligaciones = obligacionesOpt.get();

        // 3️⃣ Agrupar por año obtenido de la fecha
        Map<Integer, Long> conteoPorAnio = obligaciones.stream()
                .map(o -> FechaLegible.obtenerAnio(o.getFecha())) // obtiene el año de forma segura
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // 4️⃣ Convertir a DTO ordenado ascendentemente por año
        return conteoPorAnio.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new RentasPorAnioDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

}
