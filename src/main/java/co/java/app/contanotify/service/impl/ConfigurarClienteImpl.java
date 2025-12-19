package co.java.app.contanotify.service.impl;

import co.java.app.contanotify.dto.ConfigurarClienteDTO;
import co.java.app.contanotify.model.ConfiguracionCliente;
import co.java.app.contanotify.model.Entidad;
import co.java.app.contanotify.model.Usuario;
import co.java.app.contanotify.repository.ConfiguracionClienteRepository;
import co.java.app.contanotify.repository.EntidadRepository;
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
    private final EntidadRepository entidadRepository;
    private final ObligacionClienteRepository obligacionClienteRepository;

    public ConfigurarClienteImpl(ConfiguracionClienteRepository configuracionClienteRepository,
                                 UsuarioRepository usuarioRepository,
                                 ObligacionClienteRepository obligacionClienteRepository,
                                 EntidadRepository entidadRepository) {
        this.configuracionClienteRepository = configuracionClienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.obligacionClienteRepository = obligacionClienteRepository;
        this.entidadRepository = entidadRepository;
    }

    @Override
    public Optional<Map<String,Object>> save(ConfigurarClienteDTO configurarClienteDTO) {
        Optional<Usuario> optUsuarioContador = usuarioRepository.findByPublicId(configurarClienteDTO.getUsuarioId());
        Optional<Usuario> optUsuarioCliente = usuarioRepository.findByPublicId(configurarClienteDTO.getUsuarioClienteId());

        Optional<ConfiguracionCliente> responseConfiguracionCliente = configuracionClienteRepository.findByUsuarioIdAndUsuarioClienteId(new ObjectId(optUsuarioContador.get().getId()),
                new ObjectId(optUsuarioCliente.get().getId()));

        if(!responseConfiguracionCliente.isEmpty()){
            Map<String,Object> response = new HashMap<>();
            response.put("contador",optUsuarioContador.get().getNombre());
            response.put("cliente",optUsuarioCliente.get().getNombre());
            response.put("emailContador",optUsuarioContador.get().getEmail());

            return Optional.of(response);
        }

        ConfiguracionCliente configurarCliente = new ConfiguracionCliente();

        configurarCliente.setUsuarioId(new ObjectId(optUsuarioContador.get().getId()));
        configurarCliente.setUsuarioClienteId(new ObjectId(optUsuarioCliente.get().getId()));

        Optional<Entidad> optEntidad = entidadRepository.findByPublicId(configurarClienteDTO.getEntidadId());
        configurarCliente.setEntidadId(new ObjectId(optEntidad.get().getId()));

        configurarCliente.setNotificarContador(configurarClienteDTO.isNotificarContador());
        configurarCliente.setNotificarCliente(configurarClienteDTO.isNotificarCliente());
        configurarCliente.setNotificarEmail(configurarClienteDTO.isNotificarEmail());
        configurarCliente.setNotificarWhatsapp(configurarClienteDTO.isNotificarWhatsapp());
        configurarCliente.setNotificarSms(configurarClienteDTO.isNotificarSms());

        configurarCliente.setPublicId(UUID.randomUUID().toString());
        configuracionClienteRepository.save(configurarCliente);

        Map<String,Object> response = new HashMap<>();
        response.put("contador",optUsuarioContador.get().getNombre());
        response.put("cliente",optUsuarioCliente.get().getNombre());
        response.put("emailContador",optUsuarioContador.get().getEmail());

        return Optional.of(response);
    }
}
