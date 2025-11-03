package co.java.app.contanotify.controller;

import co.java.app.contanotify.dto.ConfigurarClienteDTO;
import co.java.app.contanotify.service.IConfiguracionCliente;
import co.java.app.contanotify.service.IObligacionCliente;
import co.java.app.contanotify.service.impl.ConfigurarClienteImpl;
import co.java.app.contanotify.service.impl.EmailServiceImpl;
import co.java.app.contanotify.service.impl.ObligacionClienteImpl;
import co.java.app.contanotify.service.impl.WhatsappService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/configurarcliente")
public class ConfigurarClienteController {

    private final IConfiguracionCliente iConfiguracionCliente;
    private final IObligacionCliente iObligacionCliente;

    private final EmailServiceImpl emailServiceImpl;
    private final WhatsappService whatsAppService;

    public ConfigurarClienteController(ConfigurarClienteImpl configurarClienteImpl,
                                       ObligacionClienteImpl obligacionClienteImpl,
                                       WhatsappService whatsAppService,
                                       EmailServiceImpl emailServiceImpl) {
        this.iConfiguracionCliente = configurarClienteImpl;
        this.whatsAppService = whatsAppService;
        this.iObligacionCliente = obligacionClienteImpl;
        this.emailServiceImpl = emailServiceImpl;
    }

    @PostMapping("/register")
    public ResponseEntity<?> configurarClienteRegister(@RequestBody @Valid ConfigurarClienteDTO req) {

        Optional<Map<String,Object>> response=iConfiguracionCliente.save(req);
        //TODO: CODIGO QUE PERMITE ENVIAR CORREO O MENSAJE DE WASSAP A CLIENTES.

        return ResponseEntity.status(201).body(Map.of(
                "message", "Configuración para cliente registrado exitosamente",
                "respuesta", response.get().toString()
        ));
    }
}
