package co.java.app.contanotify.controller;

import co.java.app.contanotify.dto.*;
import co.java.app.contanotify.service.IObligacionCliente;
import co.java.app.contanotify.service.impl.ObligacionClienteImpl;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/obligacioncliente")
public class ObligacionClienteController {

    private final IObligacionCliente iObligacionCliente;


    public ObligacionClienteController(ObligacionClienteImpl obligacionClienteImpl) {
        this.iObligacionCliente = obligacionClienteImpl;
    }

    @PostMapping("/register")
    public ResponseEntity<?> obligacionClienteRegister(@RequestBody @Valid ObligacionClienteDTO req) {

        List<Map<String, Object>> responses = iObligacionCliente.save(req);


        /*String fecha = response.get("fecha").toString();
        String obligacionClienteId = response.get("obligacionClienteId").toString();*/

        return ResponseEntity.status(201).body(Map.of(
                "message", "La obligacion cliente se guardo con exito",
                "pagos", responses
        ));
    }

    @PostMapping("/configuracion-obligaciones/register")
    public ResponseEntity<?> saveConfiguracionObligaciones(@RequestBody @Valid ConfiguracionObligacionesDTO req) {

        iObligacionCliente.saveConfiguracionObligacion(req);

        return ResponseEntity.status(201).body(Map.of(
                "message", "La obligacion tabla se cliente se guardo con exito"
        ));
    }


    @GetMapping("/obligaciones")
    public ResponseEntity<?> getObligaciones(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String identidadCliente,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String entidad,
            @RequestParam(required = false) String renta,
            @RequestParam(required = false) String pago,
            @RequestParam(required = false) String fecha) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
            Map<String, Object> filters = new HashMap<>();
            filters.put("identidadCliente", identidadCliente);
            filters.put("nombre", nombre);
            filters.put("entidad", entidad);
            filters.put("renta", renta);
            filters.put("pago", pago);
            filters.put("fecha", fecha);

            Page<ObligacionTableDTO> usuariosPage = iObligacionCliente.getAll(filters, pageable);

            return ResponseEntity.ok(Map.of(
                    "content", usuariosPage.getContent(),
                    "totalPages", usuariosPage.getTotalPages(),
                    "totalElements", usuariosPage.getTotalElements(),
                    "number", usuariosPage.getNumber(),
                    "size", usuariosPage.getSize(),
                    "first", usuariosPage.isFirst(),
                    "last", usuariosPage.isLast()
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Error al hacer la consulta",
                    "details", e.getMessage()
            ));
        }
    }

}
