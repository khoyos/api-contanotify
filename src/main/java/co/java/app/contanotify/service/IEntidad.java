package co.java.app.contanotify.service;

import co.java.app.contanotify.dto.EntidadDTO;
import co.java.app.contanotify.dto.ObligacionDTO;

import java.util.List;
import java.util.Optional;

public interface IEntidad {
    Optional<EntidadDTO> findByName(String name);
    void save(EntidadDTO entidadDTO);
    List<EntidadDTO> findAll();
}
