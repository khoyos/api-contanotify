package co.java.app.contanotify.controller;

import co.java.app.contanotify.dto.*;
import co.java.app.contanotify.service.ICalendario;
import co.java.app.contanotify.service.IEntidad;
import co.java.app.contanotify.service.IObligacion;
import co.java.app.contanotify.service.ITipoUsuario;
import co.java.app.contanotify.service.impl.CalendarioImpl;
import co.java.app.contanotify.service.impl.EntidadImpl;
import co.java.app.contanotify.service.impl.ObligacionImpl;
import co.java.app.contanotify.service.impl.TipoUsuarioImpl;
import jakarta.validation.Valid;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/masters")
public class MasterController {

    private final ITipoUsuario iTipoUsuario;
    private final IObligacion iObligacion;
    private final IEntidad iEntidad;
    private final ICalendario iCalendario;

    public MasterController(TipoUsuarioImpl tipoUsuarioImpl,
                            ObligacionImpl obligacionImpl,
                            EntidadImpl entidadImpl,
                            CalendarioImpl calendarioImpl) {
        this.iTipoUsuario = tipoUsuarioImpl;
        this.iObligacion = obligacionImpl;
        this.iEntidad = entidadImpl;
        this.iCalendario = calendarioImpl;
    }

    @PostMapping("/tipousuario/register")
    public ResponseEntity<?> tipoUsuarioRegister(@RequestBody @Valid TipoUsuarioDTO req) {
        if (iTipoUsuario.findByName(req.getName().toLowerCase()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El tipo de usuario ya está registrado"));
        }

        iTipoUsuario.save(req);

        return ResponseEntity.status(201).body(Map.of(
                "message", "Tipo de Usuario registrado exitosamente",
                "name", req.getName()
        ));
    }

    @PostMapping("/obligacion/register")
    public ResponseEntity<?> obligacionesRegister(@RequestBody @Valid ObligacionDTO req) {
        if (iObligacion.findByName(req.getName().toLowerCase()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "La obligación ya está registrada"));
        }

        iObligacion.save(req);

        return ResponseEntity.status(201).body(Map.of(
                "message", "La obligación se ha registrado exitosamente",
                "name", req.getName()
        ));
    }

    @PostMapping("/entidades/register")
    public ResponseEntity<?> entidadesRegister(@RequestBody @Valid EntidadDTO req) {
        if (iEntidad.findByName(req.getName().toLowerCase()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "La entidad ya está registrada"));
        }
        iEntidad.save(req);

        return ResponseEntity.status(201).body(Map.of(
                "message", "La entidad se ha registrado exitosamente",
                "name", req.getName()
        ));
    }

    @PostMapping("/calendarios")
    public ResponseEntity<?> getCalendarioByObligacion(@RequestBody @Valid ObligacionDTO req) {
        Optional<CalendarioDTO> response = iCalendario.findByObligacionId(req.getId());
        if (!iCalendario.findByObligacionId(req.getId()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No hay calendario asociado a esta obligación"));
        }

        return ResponseEntity.status(201).body(Map.of(
                "message", "Calendario se ha consultado exitosamente",
                "calendario", response.get()
        ));
    }

    @PostMapping("/pagos")
    public ResponseEntity<?> getPagos(@RequestBody @Valid ObligacionDTO req) {
        List<CalendarioSimpleDTO> response = iCalendario.getPagos(req.getId());
        if (response.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No hay pagos asociado a esta obligación"));
        }

        return ResponseEntity.status(201).body(Map.of(
                "message", "Calendario se ha consultado exitosamente",
                "pagos", response
        ));
    }

    @GetMapping("/entidades")
    public ResponseEntity<?> getEntidades() {
        List<EntidadDTO> response = iEntidad.findAll();
        if (response.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No hay pagos asociado a esta obligación"));
        }

        return ResponseEntity.status(201).body(Map.of(
                "message", "Calendario se ha consultado exitosamente",
                "entidades", response
        ));
    }

    @PostMapping("/calendarios/register")
    public ResponseEntity<?> calendarioRegister(@RequestBody @Valid CalendarioDTO req) {
        if (iCalendario.findByNombre(req.getNombre().toLowerCase()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "La entidad ya está registrada"));
        }
        iCalendario.save(req);

        return ResponseEntity.status(201).body(Map.of(
                "message", "Se ha registrado para el calendario "+req.getCalendario()+" exitosamente",
                "name", req.getNombre()
        ));
    }

    @GetMapping("/obligaciones")
    public ResponseEntity<?> getObligaciones() {
        List<ObligacionDTO> obligaciones = iObligacion.findAll();

        return ResponseEntity.status(201).body(Map.of(
                "message", "Se ha consultado obligaciones correctamente",
                "obligaciones", obligaciones
        ));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(@RequestParam(required = true) String userId) {
        List<AlertasCriticasDTO> obligaciones = iObligacion.dashboard(userId);


        return ResponseEntity.status(201).body(Map.of(
                "message", "Se ha consultado obligaciones correctamente",
                "alertas", obligaciones
        ));
    }
}
