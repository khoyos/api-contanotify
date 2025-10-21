package co.java.app.contanotify.service.impl;

import co.java.app.contanotify.dto.EntidadDTO;
import co.java.app.contanotify.dto.ObligacionDTO;
import co.java.app.contanotify.model.Entidad;
import co.java.app.contanotify.model.Obligacion;
import co.java.app.contanotify.repository.EntidadRepository;
import co.java.app.contanotify.service.IEntidad;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EntidadImpl implements IEntidad {

    private final EntidadRepository entidadRepository;

    public EntidadImpl(EntidadRepository entidadRepository) {
        this.entidadRepository = entidadRepository;
    }

    @Override
    public Optional<EntidadDTO> findByName(String name) {
        Optional<Entidad> entidad = entidadRepository.findByName(name);
        if(entidad.isEmpty()){
            return Optional.empty();
        }
        EntidadDTO entidadDTO = new EntidadDTO();

        entidadDTO.setId(entidad.get().getId());
        entidadDTO.setName(entidad.get().getName());

        return Optional.of(entidadDTO);
    }

    @Override
    public void save(EntidadDTO entidadDTO) {
        Entidad entidad = new Entidad();

        entidad.setName(entidadDTO.getName().toLowerCase());
        entidad.setState(true);

        entidad.setPublicId(UUID.randomUUID());
        entidadRepository.save(entidad);
    }

    @Override
    public List<EntidadDTO> findAll() {
        List<Entidad> entidades = entidadRepository.findAll();
        List<EntidadDTO> listaDto = new ArrayList<>();

        if (entidades.isEmpty()) {
            return new ArrayList<>();
        }

            for (Entidad entidad : entidades) {
                EntidadDTO dto = new EntidadDTO();
                dto.setId(entidad.getPublicId().toString());
                dto.setName(entidad.getName());
                listaDto.add(dto);
            }

        return listaDto;
    }
}
