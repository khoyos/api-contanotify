package co.java.app.contanotify.service.impl;

import co.java.app.contanotify.model.Usuario;
import co.java.app.contanotify.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class UserServiceImpl {

    private static final int MAX_ATTEMPTS = 3;

    private final UsuarioRepository repo;

    public UserServiceImpl(UsuarioRepository repo) {
        this.repo = repo;
    }

    public void recordFailedAttempt(Usuario user) {
        if (user.getLockUntil() != null && user.getLockUntil().isAfter(Instant.now())) {
            // sigue bloqueado, no incrementar más
            return;
        }
        user.setFallos(user.getFallos() + 1);
        if (user.getFallos() >= MAX_ATTEMPTS) {
            // aplicar bloqueo y resetear contador
            int nextLevel = user.getBloqueo() + 1;
            user.setBloqueo(Math.min(nextLevel, 3));
            Instant until;
            if (user.getBloqueo() == 1) until = Instant.now().plus(10, ChronoUnit.MINUTES);
            else if (user.getBloqueo() == 2) until = Instant.now().plus(1, ChronoUnit.HOURS);
            else until = Instant.now().plus(24, ChronoUnit.HOURS);
            user.setLockUntil(until);
            user.setFallos(0);
        }
        repo.save(user);
    }

    public void onSuccessfulLogin(Usuario user) {
        user.setFallos(0);
        user.setBloqueo(0);
        user.setLockUntil(null);
        repo.save(user);
    }
}
