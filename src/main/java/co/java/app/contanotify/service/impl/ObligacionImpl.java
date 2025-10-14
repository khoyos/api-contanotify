package co.java.app.contanotify.service.impl;

import co.java.app.contanotify.dto.ObligacionDTO;
import co.java.app.contanotify.model.Obligacion;
import co.java.app.contanotify.repository.ObligacionRepository;
import co.java.app.contanotify.service.IObligacion;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ObligacionImpl implements IObligacion {

    private final ObligacionRepository obligacionRepository;

    public ObligacionImpl(ObligacionRepository obligacionRepository) {
        this.obligacionRepository = obligacionRepository;
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
}
