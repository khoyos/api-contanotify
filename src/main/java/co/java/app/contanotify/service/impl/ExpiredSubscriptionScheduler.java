package co.java.app.contanotify.service.impl;

import co.java.app.contanotify.model.Usuario;
import co.java.app.contanotify.repository.SubscriptionRepository;
import co.java.app.contanotify.repository.UsuarioRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ExpiredSubscriptionScheduler {

    private final UsuarioRepository usuarioRepository;
    private final SubscriptionRepository subscriptionRepository;

    public ExpiredSubscriptionScheduler(UsuarioRepository usuarioRepository,
                                        SubscriptionRepository subscriptionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void checkExpiredSubscriptions() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        usuarios.stream()
                .filter(u -> LocalDate.now().isAfter(subscriptionRepository.findByPublicId(u.getSubscriptionId()).get().getEndDate()))
                .forEach(u -> block(u));
    }

    public void block(Usuario usuario){
        usuario.setBlock(true);
        usuarioRepository.save(usuario);
    }

}
