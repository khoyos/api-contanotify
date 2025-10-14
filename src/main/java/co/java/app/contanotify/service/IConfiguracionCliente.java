package co.java.app.contanotify.service;

import co.java.app.contanotify.dto.ConfigurarClienteDTO;

import java.util.Map;
import java.util.Optional;

public interface IConfiguracionCliente {
    Optional<Map<String,Object>> save(ConfigurarClienteDTO configurarClienteDTO);

}
