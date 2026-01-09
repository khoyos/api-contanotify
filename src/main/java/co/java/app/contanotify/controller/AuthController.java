package co.java.app.contanotify.controller;

import co.java.app.contanotify.dto.*;
import co.java.app.contanotify.enums.SubscriptionPlan;
import co.java.app.contanotify.enums.SubscriptionStatus;
import co.java.app.contanotify.model.Subscription;
import co.java.app.contanotify.model.TipoUsuario;
import co.java.app.contanotify.model.Usuario;
import co.java.app.contanotify.repository.SubscriptionRepository;
import co.java.app.contanotify.repository.UsuarioRepository;
import co.java.app.contanotify.service.ITipoUsuario;
import co.java.app.contanotify.service.impl.EmailServiceImpl;
import co.java.app.contanotify.service.impl.ReminderProducer;
import co.java.app.contanotify.service.impl.TipoUsuarioImpl;
import co.java.app.contanotify.service.impl.UserServiceImpl;
import co.java.app.contanotify.util.FechaLegible;
import co.java.app.contanotify.util.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final UserServiceImpl userService; // contine recordFailedAttempt/onSuccessfulLogin
    private final ITipoUsuario iTipoUsuario;
    private final SubscriptionRepository subscriptionRepository;
    private final ReminderProducer reminderProducer;

    public AuthController(UsuarioRepository repo,
                          PasswordEncoder encoder,
                          JwtUtil jwtUtil,
                          UserServiceImpl userService,
                          TipoUsuarioImpl tipoUsuarioImpl,
                          SubscriptionRepository subscriptionRepository,
                          ReminderProducer reminderProducer) {
        this.repo = repo; this.encoder = encoder; this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.iTipoUsuario = tipoUsuarioImpl;
        this.subscriptionRepository = subscriptionRepository;
        this.reminderProducer = reminderProducer;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Optional<TipoUsuarioDTO> optOptional = iTipoUsuario.findByName("contador");
        Optional<TipoUsuarioDTO> optTipoUsuario = iTipoUsuario.findByPublicId(optOptional.get().getPublicId());
        var userOpt = repo.findByEmailAndTipoUsuarioIdAndActive(req.getEmail(), new ObjectId(optTipoUsuario.get().getId()), true);
        if (userOpt.isEmpty()) return ResponseEntity.status(401).body(Map.of("error","Usuario o contraseña inválidos."));

        Usuario user = userOpt.get();
        // bloqueo vigente?
        if (user.getLockUntil() != null && Instant.now().isBefore(user.getLockUntil())) {
            return ResponseEntity.status(423).body(Map.of("error","Cuenta bloqueada hasta " + user.getLockUntil().toString()));
        }

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            userService.recordFailedAttempt(user);
            return ResponseEntity.status(401).body(Map.of("error","Usuario o contraseña inválidos."));
        }

        // login correcto
        userService.onSuccessfulLogin(user);
        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(Map.of("token", token,
                "user", user.getNombre(),
                "userId", user.getPublicId()));
    }

    @PostMapping("/request-reset")
    public ResponseEntity<?> requestReset(@RequestBody Map<String,String> body){
        Optional<TipoUsuarioDTO> optOptional = iTipoUsuario.findByName("contador");
        Optional<TipoUsuarioDTO> optTipoUsuario = iTipoUsuario.findByPublicId(optOptional.get().getPublicId());
        String email = body.get("email");
        var u = repo.findByEmailAndTipoUsuarioIdAndActive(email, new ObjectId(optTipoUsuario.get().getId()), true).orElse(null);
        if (u == null) {
            // NO filtrar si el email existe — por seguridad responde igual (pero aquí devolvemos OK)
            return ResponseEntity.ok(Map.of("message","Si existe una cuenta se envió el email"));
        }
        String token = UUID.randomUUID().toString();
        u.setResetPasswordToken(token);
        u.setResetPasswordExpiry(Instant.now().plusSeconds(3600)); // 1 hora
        repo.save(u);


        Map<String, Object> request = new HashMap<>();

        request.put("to", u.getEmail());
        request.put("clienteNombre", u.getNombre());
        // TODO: IMPORTANTE LINK hay que colocar esta variable como propertie para toda la applicación
        String frontendUrl = "http://localhost:5173";
        String resetLink = frontendUrl + "/reset-password?token=" + token;
        request.put("resetLink", resetLink);
        request.put("template", "reset-password");
        request.put("expiracion", "1 hora");

        reminderProducer.sendForgotPassowrdClient(request);

        return ResponseEntity.ok(Map.of("message","Si existe una cuenta se envió el email"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest req) {
        var uOpt = repo.findByResetPasswordToken(req.getToken());
        if (uOpt.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error","Token inválido"));
        Usuario u = uOpt.get();
        if (u.getResetPasswordExpiry() == null || Instant.now().isAfter(u.getResetPasswordExpiry())) {
            return ResponseEntity.ok(Map.of("message","Error",
                    "sucess",true));
        }
        u.setPassword(encoder.encode(req.getNewPassword()));
        u.setResetPasswordToken(null);
        u.setResetPasswordExpiry(null);
        u.setFallos(0);
        u.setBloqueo(0);
        u.setLockUntil(null);
        repo.save(u);
        return ResponseEntity.ok(Map.of("message","Contraseña actualizada",
                "sucess",true));
    }

    @PostMapping("/register")
        public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest req) {
        Optional<TipoUsuarioDTO> optOptional = iTipoUsuario.findByName("contador");
        Optional<TipoUsuarioDTO> optTipoUsuario = iTipoUsuario.findByPublicId(optOptional.get().getPublicId());
        if (repo.findByEmailAndTipoUsuarioIdAndActive(req.getEmail(), new ObjectId(optTipoUsuario.get().getId()), true).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El email ya está registrado"));
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(req.getEmail());
        usuario.setNombre(req.getNombre());
        usuario.setPassword(encoder.encode(req.getPassword()));
        usuario.setTipoDocumento(req.getTipoDocumento());
        usuario.setDocumento(req.getNumeroDocumento());
        usuario.setTelefono(req.getTelefono());
        usuario.setActive(true);

        String TIPO_USUARIO = "contador";
        ObjectId tipoUsuarioId = null;

        if (iTipoUsuario.findByName(TIPO_USUARIO.toLowerCase()).isPresent()) {
            TipoUsuarioDTO tipoUsuarioDTO = new TipoUsuarioDTO();
            tipoUsuarioDTO = iTipoUsuario.findByName(TIPO_USUARIO).get();
            tipoUsuarioId = new ObjectId(iTipoUsuario.findByPublicId(tipoUsuarioDTO.getPublicId()).get().getId());

        }else{
            TipoUsuarioDTO tipoUsuarioDTO = new TipoUsuarioDTO();
            tipoUsuarioDTO.setName(TIPO_USUARIO);

            iTipoUsuario.save(tipoUsuarioDTO);

            tipoUsuarioDTO = iTipoUsuario.findByName(TIPO_USUARIO).get();
            tipoUsuarioId = new ObjectId(iTipoUsuario.findByPublicId(tipoUsuarioDTO.getPublicId()).get().getId());

        }
        usuario.setTipoUsuarioId(tipoUsuarioId);
        usuario.setEstado(true);

        // registra subscription
        Subscription subscription = new Subscription();

        String PLAN_SUBSCRIPTION = req.getPlan();

        subscription.setPlan(SubscriptionPlan.valueOf(PLAN_SUBSCRIPTION));

        if(PLAN_SUBSCRIPTION.equals(SubscriptionPlan.BASIC.toString())){
            subscription.setStatus(SubscriptionStatus.TRIAL);
            subscription.setTrialEndDate(LocalDate.now().plusDays(7));
            subscription.setEndDate(LocalDate.now().plusDays(7));
        }

        if(PLAN_SUBSCRIPTION.equals(SubscriptionPlan.PRO.name())||
                PLAN_SUBSCRIPTION.equals(SubscriptionPlan.PREMIUM.name())){
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscription.setStartDate(LocalDate.now());
            subscription.setEndDate(LocalDate.now().plusDays(30));
        }

        subscription.setPublicId(UUID.randomUUID().toString());

        subscription = subscriptionRepository.save(subscription);

        usuario.setSubscriptionId(subscription.getPublicId());

        usuario.setPublicId(UUID.randomUUID().toString());
        repo.save(usuario);

        return ResponseEntity.status(201).body(Map.of(
                "message", "Usuario registrado exitosamente",
                "email", usuario.getEmail(),
                "user", usuario.getPublicId(),
                "subscription", Map.of(
                        "status", subscription.getStatus(),
                        "endDate", subscription.getEndDate())
        ));
    }

    @PostMapping("/me")
    public ResponseEntity<?> getMe(@RequestBody @Valid RegisterRequest req) {
        Optional<TipoUsuarioDTO> optOptional = iTipoUsuario.findByName("contador");
        Optional<TipoUsuarioDTO> optTipoUsuario = iTipoUsuario.findByPublicId(optOptional.get().getPublicId());
        var userOpt = repo.findByEmailAndTipoUsuarioIdAndActive(req.getEmail(), new ObjectId(optTipoUsuario.get().getId()), true);
        if (userOpt.isEmpty()) return ResponseEntity.status(401).body(Map.of("error","No sé encontro usuario"));
        Optional<Subscription> subscription = subscriptionRepository.findByPublicId(userOpt.get().getSubscriptionId());
        Usuario user = userOpt.get();
        return ResponseEntity.ok(Map.of(
                "email", user.getEmail(),
                "subscription", Map.of(
                        "status", subscription.get().getStatus()),
                        "endDate", subscription.get().getEndDate())
        );
    }

}
