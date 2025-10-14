package co.java.app.contanotify.service.impl;

import co.java.app.contanotify.dto.TipoUsuarioDTO;
import co.java.app.contanotify.model.TipoUsuario;
import co.java.app.contanotify.repository.TipoUsuarioRepository;
import co.java.app.contanotify.service.ITipoUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TipoUsuarioImpl implements ITipoUsuario {

    private TipoUsuarioRepository tipoUsuarioRepository;

    @Autowired
    public TipoUsuarioImpl(TipoUsuarioRepository tipoUsuarioRepository) {
        this.tipoUsuarioRepository = tipoUsuarioRepository;
    }

    @Override
    public Optional<TipoUsuarioDTO> findByName(String name) {
        Optional<TipoUsuario> tipoUsuario = tipoUsuarioRepository.findByName(name);
        if(tipoUsuario.isEmpty()){
            return Optional.empty();
        }
        TipoUsuarioDTO tipoUsuarioDTO = new TipoUsuarioDTO();

        tipoUsuarioDTO.setId(tipoUsuario.get().getId());
        tipoUsuarioDTO.setName(tipoUsuario.get().getName());

        return Optional.of(tipoUsuarioDTO);
    }

    @Override
    public void save(TipoUsuarioDTO tipoUsuarioDTO) {
        TipoUsuario tipoUsuario = new TipoUsuario();
        tipoUsuario.setName(tipoUsuarioDTO.getName().toLowerCase());
        tipoUsuario.setState(true);

        tipoUsuarioRepository.save(tipoUsuario);

    }
}
