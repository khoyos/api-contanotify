package co.java.app.contanotify.service;

import co.java.app.contanotify.dto.ConfiguracionObligacionesDTO;
import co.java.app.contanotify.dto.ObligacionClienteDTO;
import co.java.app.contanotify.dto.ObligacionTableDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface IObligacionCliente {

    Map<String, Object> save(ObligacionClienteDTO obligacionClienteDTO);

    Page<ObligacionTableDTO> getAll(Map<String,Object> filters, Pageable pageable);

    void saveConfiguracionObligacion(ConfiguracionObligacionesDTO configuracionObligacionesDTO);

}
