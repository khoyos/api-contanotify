package co.java.app.contanotify.service.impl;

import co.java.app.contanotify.dto.*;
import co.java.app.contanotify.model.*;
import co.java.app.contanotify.repository.*;
import co.java.app.contanotify.service.IObligacionCliente;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ObligacionClienteImpl implements IObligacionCliente {

    private static final Logger log = LoggerFactory.getLogger(ObligacionClienteImpl.class);
    private final ObligacionClienteRepository obligacionClienteRepository;
    private final ConfiguracionClienteRepository configuracionClienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final CalendarioRepository calendarioRepository;
    private final ReminderProducer reminderProducer;
    private final ConfiguracionObligacionesRepository configuracionObligacionesRepository;
    private final ObligacionRepository obligacionRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public ObligacionClienteImpl(ObligacionClienteRepository obligacionClienteRepository,
                                 ConfiguracionClienteRepository configuracionClienteRepository,
                                 UsuarioRepository usuarioRepository,
                                 CalendarioRepository calendarioRepository,
                                 ReminderProducer reminderProducer,
                                 ConfiguracionObligacionesRepository configuracionObligacionesRepository,
                                 ObligacionRepository obligacionRepository) {
        this.obligacionClienteRepository = obligacionClienteRepository;
        this.configuracionClienteRepository = configuracionClienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.calendarioRepository = calendarioRepository;
        this.reminderProducer = reminderProducer;
        this.configuracionObligacionesRepository = configuracionObligacionesRepository;
        this.obligacionRepository = obligacionRepository;
    }

    @Override
    public List<Map<String, Object>>  save(ObligacionClienteDTO obligacionClienteDTO) {
        Optional<Usuario> optUsuarioCliente = usuarioRepository.findByPublicId(obligacionClienteDTO.getUsuarioClienteId());

        if(!optUsuarioCliente.isPresent()||optUsuarioCliente.isEmpty()){
            new RuntimeException("Error al guardar la información");
        }

        Optional<ConfiguracionCliente> configuracionCliente= configuracionClienteRepository.findByUsuarioClienteId(new ObjectId(optUsuarioCliente.get().getId()));
        if(!configuracionCliente.isPresent()|| configuracionCliente.isEmpty()){
            new RuntimeException("Error al guardar la información");
        }

        String documento = optUsuarioCliente.get().getDocumento();
        String ultimoDigitoNit = String.valueOf(documento.charAt(documento.length() - 1));
        String penultimosDigitosNit = String.valueOf(documento.charAt(documento.length() - 2));

        String dosUltimosDigitosNit = penultimosDigitosNit+ultimoDigitoNit;

        Optional<Obligacion> optionalObligacion = obligacionRepository.findByPublicId(obligacionClienteDTO.getObligacionRentaId());

        Optional<List<Calendario>> calendarios= calendarioRepository.findByObligacionId(optionalObligacion.get().getId());

        if(calendarios.isEmpty()|| !calendarios.isPresent()){
            new RuntimeException("Error al guardar la información");
        }

        List<Map<String, Object>> responses = new ArrayList<>();

        for(Calendario calendario:  calendarios.get()){

            for(Fecha fecha: calendario.getFechas()){

                if(fecha.getNit().contains("independiente")){

                    ObligacionCliente obligacionCliente = new ObligacionCliente();

                    obligacionCliente.setConfiguracionClienteId(new ObjectId(configuracionCliente.get().getId()));
                    obligacionCliente.setCalendarioId(new ObjectId(calendario.getId()));
                    obligacionCliente.setFecha(fecha.getFecha());

                    obligacionCliente.setPublicId(UUID.randomUUID().toString());
                    obligacionCliente = obligacionClienteRepository.save(obligacionCliente);

                    HashMap<String, Object> response = new HashMap<>();
                    response.put("fecha", fecha.getFecha());
                    response.put("obligacionClienteId", obligacionCliente.getPublicId());
                    response.put("nombrePago", calendario.getNombre());
                    response.put("periodo", calendario.getCalendario());

                    responses.add(response);
                }

                if(fecha.getNit().contains("-")){
                    //Registrar las fechas con respecto a la logica de los 2 nits
                    String valor = fecha.getNit();
                    String[] partes = valor.split("-");

                    String primera = partes[0];
                    String segunda = partes[1];

                    if(primera.equals(dosUltimosDigitosNit)||segunda.equals(dosUltimosDigitosNit)){
                        // registrar la fecha
                        ObligacionCliente obligacionCliente = new ObligacionCliente();

                        obligacionCliente.setConfiguracionClienteId(new ObjectId(configuracionCliente.get().getId()));
                        obligacionCliente.setCalendarioId(new ObjectId(calendario.getId()));
                        obligacionCliente.setFecha(fecha.getFecha());

                        obligacionCliente.setPublicId(UUID.randomUUID().toString());
                        obligacionCliente = obligacionClienteRepository.save(obligacionCliente);

                        HashMap<String, Object> response = new HashMap<>();
                        response.put("fecha", fecha.getFecha());
                        response.put("obligacionClienteId", obligacionCliente.getPublicId());
                        response.put("nombrePago", calendario.getNombre());
                        response.put("periodo", calendario.getCalendario());

                        responses.add(response);

                    }
                }else{
                    //es de un solo nit
                    if(ultimoDigitoNit.equals(fecha.getNit())){
                        //registrar la fecha
                        ObligacionCliente obligacionCliente = new ObligacionCliente();

                        obligacionCliente.setConfiguracionClienteId(new ObjectId(configuracionCliente.get().getId()));
                        obligacionCliente.setCalendarioId(new ObjectId(calendario.getId()));
                        obligacionCliente.setFecha(fecha.getFecha());

                        obligacionCliente.setPublicId(UUID.randomUUID().toString());
                        obligacionCliente = obligacionClienteRepository.save(obligacionCliente);

                        HashMap<String, Object> response = new HashMap<>();
                        response.put("fecha", fecha.getFecha());
                        response.put("obligacionClienteId", obligacionCliente.getPublicId());
                        response.put("nombrePago", calendario.getNombre());
                        response.put("periodo", calendario.getCalendario());

                        responses.add(response);

                    }
                }
            }
        }


        return responses;
    }

    @Override
    public Page<ObligacionTableDTO> getAll(Map<String, Object> filters, Pageable pageable) {
        Query query = new Query().with(pageable);
        List<Criteria> criterios = new ArrayList<>();

        // Obtener valores de filtros (evita NPE)
        String identidad = (String) filters.getOrDefault("identidadCliente", "");
        String nombre = (String) filters.getOrDefault("nombre", "");
        String entidad = (String) filters.getOrDefault("entidad", "");
        String renta = (String) filters.getOrDefault("renta", "");
        String pago = (String) filters.getOrDefault("pago", "");
        String fecha = (String) filters.getOrDefault("fecha", "");

        // 🔍 Agregar filtros dinámicos (regex = búsqueda parcial, 'i' = case insensitive)

        if (identidad!=null && !identidad.isBlank()) {
            criterios.add(Criteria.where("identidadCliente").regex(identidad, "i"));
        }
        if (nombre!=null && !nombre.isBlank()) {
            criterios.add(Criteria.where("nombreCliente").regex(nombre, "i"));
        }
        if (entidad != null && !entidad.isBlank()) {
            criterios.add(Criteria.where("entidad").regex(entidad, "i"));
        }
        if (renta!=null && !renta.isBlank()) {
            criterios.add(Criteria.where("renta").regex(renta, "i"));
        }
        if (pago!=null && !pago.isBlank()) {
            criterios.add(Criteria.where("pago").regex(pago, "i"));
        }
        if (fecha!=null && !fecha.isBlank()) {
            criterios.add(Criteria.where("fecha").regex(fecha, "i"));
        }

        // Aplica todos los filtros si existen
        if (!criterios.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criterios.toArray(new Criteria[0])));
        }

        //  Ejecuta la consulta
        List<ConfiguracionObligaciones> resultados = mongoTemplate.find(query, ConfiguracionObligaciones.class);

        //  Conteo total para la paginación
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), ConfiguracionObligaciones.class);

        // Mapear la entidad al DTO manualmente
        List<ObligacionTableDTO> dtos = resultados.stream().map(item -> {
            ObligacionTableDTO dto = new ObligacionTableDTO();
            dto.setId(item.getPublicId().toString());
            dto.setIdentidadCliente(item.getIdentidadCliente());
            dto.setNombreCliente(item.getNombreCliente());
            dto.setEntidad(item.getEntidad());
            dto.setRenta(item.getRenta());
            dto.setPago(item.getPago());
            dto.setFecha(item.getFecha());
            dto.setEstado(item.getEstado());
            dto.setObservacion(item.getObservacion());
            return dto;
        }).toList();

        // Retorna la página final
        return new PageImpl<>(dtos, pageable, total);
    }

    @Override
    public void saveConfiguracionObligacion(ConfiguracionObligacionesDTO configuracionObligacionesDTO) {
        ConfiguracionObligaciones configuracionObligaciones = new ConfiguracionObligaciones();

        configuracionObligaciones.setUsuarioId(configuracionObligacionesDTO.getUsuarioId());
        configuracionObligaciones.setClienteId(configuracionObligacionesDTO.getClienteId());
        configuracionObligaciones.setIdentidadCliente(configuracionObligacionesDTO.getIdentidadCliente());
        configuracionObligaciones.setNombreCliente(configuracionObligacionesDTO.getNombreCliente());
        configuracionObligaciones.setEntidad(configuracionObligacionesDTO.getEntidad());
        configuracionObligaciones.setRenta(configuracionObligacionesDTO.getRenta());
        configuracionObligaciones.setPago(configuracionObligacionesDTO.getPago());
        configuracionObligaciones.setFecha(configuracionObligacionesDTO.getFecha());
        configuracionObligaciones.setObligacionClienteId(configuracionObligacionesDTO.getObligacionClienteId());
        configuracionObligaciones.setEstado("");
        configuracionObligaciones.setObservacion("");
        configuracionObligaciones.setPeriodo(configuracionObligacionesDTO.getPeriodo());

        configuracionObligaciones.setPublicId(UUID.randomUUID().toString());
        configuracionObligacionesRepository.save(configuracionObligaciones);
    }

    @Override
    public void actualizarConfiguracionObligacion(String idConfiguracionObligacion, EstadoObligacionClienteDTO estadoObligacionClienteDTO) {
        Optional<ConfiguracionObligaciones> optObligacionCliente = configuracionObligacionesRepository.findByPublicId(idConfiguracionObligacion);

        if(optObligacionCliente.isEmpty()){
            throw new RuntimeException("No se pudo actualizar el estado");
        }

        optObligacionCliente.get().setEstado(estadoObligacionClienteDTO.getEstado());
        optObligacionCliente.get().setObservacion(estadoObligacionClienteDTO.getObservacion());

        configuracionObligacionesRepository.save(optObligacionCliente.get());

        //TODO: CUANDO CAMBIA DE ESTADO EL CLIENTE RECIBE INFORMACIÓN POR EMAIL
    }

}
