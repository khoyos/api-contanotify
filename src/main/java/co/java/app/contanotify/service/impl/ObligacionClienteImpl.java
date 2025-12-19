package co.java.app.contanotify.service.impl;

import co.java.app.contanotify.dto.*;
import co.java.app.contanotify.model.*;
import co.java.app.contanotify.repository.*;
import co.java.app.contanotify.service.IObligacionCliente;
import co.java.app.contanotify.util.FechaLegible;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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
                    Optional<List<ObligacionCliente>> optObligacionCliente = obligacionClienteRepository.findByConfiguracionClienteIdAndCalendarioIdAndFecha(new ObjectId(configuracionCliente.get().getId()),
                            new ObjectId(calendario.getId()),
                            fecha.getFecha());
                    if(!optObligacionCliente.get().isEmpty()){
                        continue;
                    }else{
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

                if(fecha.getNit().contains("-")){
                    //Registrar las fechas con respecto a la logica de los 2 nits
                    String valor = fecha.getNit();
                    String[] partes = valor.split("-");

                    String primera = partes[0];
                    String segunda = partes[1];

                    if(primera.equals(dosUltimosDigitosNit)||segunda.equals(dosUltimosDigitosNit)){
                        // registrar la fecha
                        Optional<List<ObligacionCliente>> optObligacionCliente = obligacionClienteRepository.findByConfiguracionClienteIdAndCalendarioIdAndFecha(new ObjectId(configuracionCliente.get().getId()),
                                new ObjectId(calendario.getId()),
                                fecha.getFecha());
                        if(!optObligacionCliente.get().isEmpty()){
                            continue;
                        }else {
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
                }else{
                    //es de un solo nit
                    if(ultimoDigitoNit.equals(fecha.getNit())){
                        //registrar la fecha
                        Optional<List<ObligacionCliente>> optObligacionCliente = obligacionClienteRepository.findByConfiguracionClienteIdAndCalendarioIdAndFecha(new ObjectId(configuracionCliente.get().getId()),
                                new ObjectId(calendario.getId()),
                                fecha.getFecha());
                        if(!optObligacionCliente.get().isEmpty()){
                            continue;
                        }else {
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
        }

        return responses;
    }

    public Page<ObligacionTableDTO> getAll(Map<String, Object> filters, Pageable pageable, String idContador) {
        List<Criteria> criterios = new ArrayList<>();

        // 🔒 Extraer y limpiar valores de filtros de forma segura
        String identidad = getSafeString(filters.get("identidadCliente"));
        String nombre = getSafeString(filters.get("nombre"));
        String entidad = getSafeString(filters.get("entidad"));
        String renta = getSafeString(filters.get("renta"));
        String pago = getSafeString(filters.get("pago"));
        String fecha = getSafeString(filters.get("fecha"));
        String estado = getSafeString(filters.get("estado"));

        // ✅ Aplicar criterios solo cuando no están vacíos
        if (!identidad.isEmpty()) criterios.add(Criteria.where("identidadCliente").regex(identidad, "i"));
        if (!nombre.isEmpty()) criterios.add(Criteria.where("nombreCliente").regex(nombre, "i"));
        if (!entidad.isEmpty()) criterios.add(Criteria.where("entidad").regex(entidad, "i"));
        if (!renta.isEmpty()) criterios.add(Criteria.where("renta").regex(renta, "i"));
        if (!pago.isEmpty()) criterios.add(Criteria.where("pago").regex(pago, "i"));
        if (!fecha.isEmpty()) criterios.add(Criteria.where("fecha").regex(fecha, "i"));
        if (!estado.isEmpty()) criterios.add(Criteria.where("estado").regex(estado, "i"));

        // 📋 Construir query
        Query query = new Query();
        query.addCriteria(Criteria.where("usuarioId").is(idContador));
        if (!criterios.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criterios.toArray(new Criteria[0])));
        }

        // 🧮 Conteo total
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), ConfiguracionObligaciones.class);

        // 🧭 Paginación + orden estable
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "fecha").and(Sort.by("_id"))
        );
        query.with(sortedPageable);

        // 🔍 Consulta paginada
        List<ConfiguracionObligaciones> resultados = mongoTemplate.find(query, ConfiguracionObligaciones.class);

        // 🧱 Mapear DTOs
        List<ObligacionTableDTO> dtos = resultados.stream().map(item -> {
            ObligacionTableDTO dto = new ObligacionTableDTO();
            dto.setId(item.getPublicId().toString());
            dto.setIdentidadCliente(item.getIdentidadCliente());
            dto.setNombreCliente(item.getNombreCliente());
            dto.setEntidad(item.getEntidad());
            dto.setRenta(item.getRenta());
            dto.setPago(item.getPago());
            dto.setFecha(item.getFecha());
            dto.setPeriodo(item.getPeriodo());
            dto.setEstado(item.getEstado());
            dto.setObservacion(item.getObservacion());
            return dto;
        }).toList();

        // 📤 Retornar la página correctamente
        return new PageImpl<>(dtos, sortedPageable, total);
    }

    /**
     * 🔧 Utilidad para evitar nulls o espacios vacíos en filtros
     */
    private String getSafeString(Object value) {
        if (value == null) return "";
        String str = value.toString().trim();
        return str.isBlank() ? "" : str;
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
        configuracionObligaciones.setEstado("Por Hacer");
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
        if(optObligacionCliente.get().getEstado().equals("Por Hacer")){
            return;
        }

        Optional<Usuario> optionalUsuario = usuarioRepository.findByPublicId(optObligacionCliente.get().getClienteId());

        Map<String, Object> request = new HashMap<>();

        String msg = "Información de su Obligacion Tributaria: " + optionalUsuario.get().getNombre() + " vence el " + optObligacionCliente.get().getFecha();

        String estadoObservacion = optObligacionCliente.get().getObservacion().isEmpty()? "si":"no";
        String observacion = optObligacionCliente.get().getObservacion().isEmpty()?"":optObligacionCliente.get().getObservacion();

        String fechaLegible = FechaLegible.convertirFechaISOPorFechaLegible(optObligacionCliente.get().getFecha());

        request.put("to", optionalUsuario.get().getEmail());
        request.put("message", msg);
        request.put("name", optionalUsuario.get().getNombre());
        request.put("renta", optObligacionCliente.get().getRenta());
        request.put("pago", optObligacionCliente.get().getPago());
        request.put("fecha", fechaLegible.replace(",","") );
        request.put("estado", optObligacionCliente.get().getEstado());
        request.put("estadoObservacion", estadoObservacion);
        request.put("observacion", observacion.replace(","," * "));

        reminderProducer.sendReminderClient(request);

    }

}
