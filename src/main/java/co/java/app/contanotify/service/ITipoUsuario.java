package co.java.app.contanotify.service;

import co.java.app.contanotify.dto.TipoUsuarioDTO;
import co.java.app.contanotify.model.TipoUsuario;

import java.util.Optional;

public interface ITipoUsuario {
    Optional<TipoUsuarioDTO> findByName(String name);
    void save(TipoUsuarioDTO tipoUsuarioDTO);
}
