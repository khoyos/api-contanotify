package co.java.app.contanotify.service;

import co.java.app.contanotify.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IObligacionCliente {

    List<Map<String, Object>> save(ObligacionClienteDTO obligacionClienteDTO);

    Page<ObligacionTableDTO> getAll(Map<String,Object> filters, Pageable pageable, String idContador);

    void saveConfiguracionObligacion(ConfiguracionObligacionesDTO configuracionObligacionesDTO);

    void actualizarConfiguracionObligacion(String idConfiguracionObligacion, EstadoObligacionClienteDTO estadoObligacionClienteDTO);

}
