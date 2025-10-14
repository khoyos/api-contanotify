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
        ConfiguracionCliente configurarCliente = new ConfiguracionCliente();

        configurarCliente.setUsuarioId(new ObjectId(configurarClienteDTO.getUsuarioId()));
        configurarCliente.setUsuarioClienteId(new ObjectId(configurarClienteDTO.getUsuarioClienteId()));
        configurarCliente.setEntidadId(new ObjectId(configurarClienteDTO.getEntidadId()));

        configurarCliente.setNotificarContador(configurarClienteDTO.isNotificarContador());
        configurarCliente.setNotificarCliente(configurarClienteDTO.isNotificarCliente());
        configurarCliente.setNotificarEmail(configurarClienteDTO.isNotificarEmail());
        configurarCliente.setNotificarWhatsapp(configurarClienteDTO.isNotificarWhatsapp());
        configurarCliente.setNotificarSms(configurarClienteDTO.isNotificarSms());

        configuracionClienteRepository.save(configurarCliente);

        Optional<Usuario> usuario=usuarioRepository.findById(configurarClienteDTO.getUsuarioId());
        Optional<Usuario> cliente=usuarioRepository.findById(configurarClienteDTO.getUsuarioClienteId());

        Map<String,Object> response = new HashMap<>();
        response.put("contador",usuario.get().getNombre());
        response.put("cliente",cliente.get().getNombre());
        response.put("emailContador",usuario.get().getEmail());

        //TODO: Se debe construir servicio para enviar mensajes por whatsapp a que horas?
        //TODO: Se debe construir servicio para enviar mesnajes cuantas veces al día
        //TODO: Se debe constuir servicio para enviar mensaje con anticipación

        return Optional.of(response);
    }
}
