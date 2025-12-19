package co.java.app.contanotify.controller;

import co.java.app.contanotify.dto.UsuarioDTO;
import co.java.app.contanotify.service.IUsuario;
import co.java.app.contanotify.service.impl.UsuarioImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final IUsuario iUsuario;

    public ClienteController(UsuarioImpl usuarioImpl) {
        this.iUsuario = usuarioImpl;
    }

    @PostMapping
    public ResponseEntity<?> createClient(@RequestBody UsuarioDTO client) {
        try {
            UsuarioDTO usuarioDTO = iUsuario.save(client);
            return ResponseEntity.status(201).body(Map.of(
                    "message", "Usuario registrado exitosamente",
                    "email", usuarioDTO.getEmail()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "El cliente no pudo guardarse con exito"
            ));
        }
    }

    //  Obtener clientes con paginación
    @GetMapping
    public ResponseEntity<?> getClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String idContador) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
            Page<UsuarioDTO> usuariosPage = iUsuario.getAll(nombre, documento, email, pageable, idContador);

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

    //  Obtener clientes con paginación
    @GetMapping("/by-identity")
    public ResponseEntity<?> getClientByIdentity(
            @RequestParam String tipoDocumento,
            @RequestParam String documento) {
        try {

            UsuarioDTO usuariosDto = iUsuario.findByTipoDocumentoAndDocumento(tipoDocumento.toLowerCase(), documento);

            return ResponseEntity.status(201).body(Map.of(
                    "message", "La obligación se ha registrado exitosamente",
                    "cliente", usuariosDto));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Error al hacer la consulta",
                    "details", e.getMessage()
            ));
        }
    }

    @GetMapping("/{idCliente}")
    public ResponseEntity<?> getClientById(
            @PathVariable String idCliente) {
        try {

            UsuarioDTO usuariosDto = iUsuario.findById(idCliente);

            return ResponseEntity.status(201).body(Map.of(
                    "message", "se consulto el cliente exitosamente",
                    "cliente", usuariosDto));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Error al hacer la consulta",
                    "details", e.getMessage()
            ));
        }
    }


    @PutMapping("/{idCliente}")
    public ResponseEntity<?> update(
            @PathVariable String idCliente,
            @RequestBody UsuarioDTO client) {
        try {

            UsuarioDTO usuarioDTO = iUsuario.findById(idCliente);

            usuarioDTO.setId(idCliente);
            usuarioDTO.setNombre(client.getNombre());
            usuarioDTO.setDocumento(client.getDocumento());
            usuarioDTO.setTelefono(client.getTelefono());
            usuarioDTO.setEmail(client.getEmail());
            usuarioDTO.setTipoDocumento(client.getTipoDocumento());
            usuarioDTO.setRazonSocial(client.getRazonSocial());

            iUsuario.update(usuarioDTO);

            return ResponseEntity.status(201).body(Map.of(
                    "message", "se consulto el cliente exitosamente",
                    "cliente", usuarioDTO));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Error al hacer la consulta",
                    "details", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{idCliente}")
    public ResponseEntity<?> delete(
            @PathVariable String idCliente) {
        try {

            UsuarioDTO usuarioDTO = new UsuarioDTO();
            usuarioDTO.setId(idCliente);

            iUsuario.delete(usuarioDTO);

            return ResponseEntity.status(201).body(Map.of(
                    "message", "La operacions se hizo con exito"));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Error al hacer la consulta",
                    "details", e.getMessage()
            ));
        }
    }




}
