package co.java.app.contanotify.service.impl;

import co.java.app.contanotify.dto.ConfigurarClienteDTO;
import co.java.app.contanotify.model.ConfiguracionCliente;
import co.java.app.contanotify.model.Usuario;
import co.java.app.contanotify.repository.ConfiguracionClienteRepository;
import co.java.app.contanotify.repository.ObligacionClienteRepository;
import co.java.app.contanotify.repository.UsuarioRepository;
import co.java.app.contanotify.service.IConfiguracionCliente;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConfigurarClienteImpl implements IConfiguracionCliente {

    private final ConfiguracionClienteRepository configuracionClienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObligacionClienteRepository obligacionClienteRepository;

    public ConfigurarClienteImpl(ConfiguracionClienteRepository configuracionClienteRepository,
                                 UsuarioRepository usuarioRepository,
                                 ObligacionClienteRepository obligacionClienteRepository) {
        this.configuracionClienteRepository = configuracionClienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.obligacionClienteRepository = obligacionClienteRepository;
    }

    @Override
    public Optional<Map<String,Object>> save(ConfigurarClienteDTO configurarClienteDTO) {

        Optional<ConfiguracionCliente> responseConfiguracionCliente = configuracionClienteRepository.findByUsuarioIdAndUsuarioClienteId(new ObjectId(configurarClienteDTO.getUsuarioId()),
                new ObjectId(configurarClienteDTO.getUsuarioClienteId()));

        if(!responseConfiguracionCliente.isEmpty()){
            Optional<Usuario> usuario=usuarioRepository.findByPublicId(UUID.fromString(configurarClienteDTO.getUsuarioId()));
            Optional<Usuario> cliente=usuarioRepository.findByPublicId(UUID.fromString(configurarClienteDTO.getUsuarioClienteId()));

            Map<String,Object> response = new HashMap<>();
            response.put("contador",usuario.get().getNombre());
            response.put("cliente",cliente.get().getNombre());
            response.put("emailContador",usuario.get().getEmail());

            return Optional.of(response);
        }

        ConfiguracionCliente configurarCliente = new ConfiguracionCliente();

        configurarCliente.setUsuarioId(new ObjectId(configurarClienteDTO.getUsuarioId()));
        configurarCliente.setUsuarioClienteId(new ObjectId(configurarClienteDTO.getUsuarioClienteId()));
        configurarCliente.setEntidadId(new ObjectId(configurarClienteDTO.getEntidadId()));

        configurarCliente.setNotificarContador(configurarClienteDTO.isNotificarContador());
        configurarCliente.setNotificarCliente(configurarClienteDTO.isNotificarCliente());
        configurarCliente.setNotificarEmail(configurarClienteDTO.isNotificarEmail());
        configurarCliente.setNotificarWhatsapp(configurarClienteDTO.isNotificarWhatsapp());
        configurarCliente.setNotificarSms(configurarClienteDTO.isNotificarSms());

        configurarCliente.setPublicId(UUID.randomUUID());
        configuracionClienteRepository.save(configurarCliente);

        Optional<Usuario> usuario=usuarioRepository.findByPublicId(UUID.fromString(configurarClienteDTO.getUsuarioId()));
        Optional<Usuario> cliente=usuarioRepository.findByPublicId(UUID.fromString(configurarClienteDTO.getUsuarioClienteId()));

        Map<String,Object> response = new HashMap<>();
        response.put("contador",usuario.get().getNombre());
        response.put("cliente",cliente.get().getNombre());
        response.put("emailContador",usuario.get().getEmail());

        return Optional.of(response);
    }
}
