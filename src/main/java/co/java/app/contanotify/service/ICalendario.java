package co.java.app.contanotify.service;

import co.java.app.contanotify.dto.CalendarioDTO;
import co.java.app.contanotify.dto.CalendarioSimpleDTO;

import java.util.List;
import java.util.Optional;

public interface ICalendario {

    Optional<CalendarioDTO> findByObligacionId(String id);

    Optional<CalendarioDTO> findByNombre(String nombre);

    void save(CalendarioDTO calendarioDTO);

    List<CalendarioSimpleDTO> getPagos(String obligacionId);
}
