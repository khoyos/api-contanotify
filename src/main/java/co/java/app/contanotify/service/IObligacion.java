package co.java.app.contanotify.service;

import co.java.app.contanotify.dto.AlertasCriticasDTO;
import co.java.app.contanotify.dto.CardGeneralDTO;
import co.java.app.contanotify.dto.CorporativoPorEntidadGobiernoDTO;
import co.java.app.contanotify.dto.ObligacionDTO;

import java.util.List;
import java.util.Optional;

public interface IObligacion {
    Optional<ObligacionDTO> findByName(String name);
    void save(ObligacionDTO obligacionDTO);
    List<ObligacionDTO> findAll();
    List<AlertasCriticasDTO> dashboardAlertas(String usuarioId);
    CardGeneralDTO dashboardCardGeneral(String usuarioId);
    List<CorporativoPorEntidadGobiernoDTO> dashboardCorporativoPorEntidad(String usuarioId);
}
