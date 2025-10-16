package co.java.app.contanotify.service.impl;

import co.java.app.contanotify.dto.AlertasCriticasDTO;
import co.java.app.contanotify.dto.ObligacionDTO;
import co.java.app.contanotify.model.ConfiguracionObligaciones;
import co.java.app.contanotify.model.Obligacion;
import co.java.app.contanotify.model.ObligacionCliente;
import co.java.app.contanotify.repository.ConfiguracionObligacionesRepository;
import co.java.app.contanotify.repository.ObligacionClienteRepository;
import co.java.app.contanotify.repository.ObligacionRepository;
import co.java.app.contanotify.service.IObligacion;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ObligacionImpl implements IObligacion {

    private final ObligacionRepository obligacionRepository;
    private final ConfiguracionObligacionesRepository configuracionObligacionesRepository;
    private final ObligacionClienteRepository obligacionClienteRepository;

    public ObligacionImpl(ObligacionRepository obligacionRepository,
                          ConfiguracionObligacionesRepository configuracionObligacionesRepository,
                          ObligacionClienteRepository obligacionClienteRepository) {
        this.obligacionRepository = obligacionRepository;
        this.configuracionObligacionesRepository = configuracionObligacionesRepository;
        this.obligacionClienteRepository = obligacionClienteRepository;
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
            obligacionDTO.setId(obligacion.getId());
            obligacionDTO.setName(obligacion.getName());
            listObligaciones.add(obligacionDTO);
        }

        return listObligaciones;
    }

    @Override
    public List<AlertasCriticasDTO> dashboard(String usuarioId) {
        List<AlertasCriticasDTO> alertasCriticasList = new ArrayList<>();
        Optional<List<ConfiguracionObligaciones>> configuracionObligacionesList = configuracionObligacionesRepository.findByUsuarioId(usuarioId);

        if(configuracionObligacionesList.isEmpty()){
            throw new RuntimeException("Error al consultar dashboard");
        }



        for (ConfiguracionObligaciones co: configuracionObligacionesList.get()){
            Optional<ObligacionCliente> response = obligacionClienteRepository.findById(co.getObligacionClienteId());
            AlertasCriticasDTO alertasCriticasDTO = new AlertasCriticasDTO();

            DateTimeFormatter formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDate fecha = LocalDate.parse(co.getFecha(), formateador);

            alertasCriticasDTO.setFechaVencimiento(co.getFecha());
            alertasCriticasDTO.setNombreCliente(co.getNombreCliente());
            alertasCriticasDTO.setObligacionRenta(co.getRenta());
            alertasCriticasDTO.setObligacionPago(co.getPago());
            alertasCriticasDTO.setPerido(String.valueOf(fecha.getYear()));

            alertasCriticasDTO.setUrgente(response.get().isReminder1DaySent());
            alertasCriticasDTO.setAlta(response.get().isReminder3DaysSent());
            alertasCriticasDTO.setMedia(response.get().isReminder5DaysSent());

            alertasCriticasList.add(alertasCriticasDTO);
        }

        return alertasCriticasList;
    }
}
