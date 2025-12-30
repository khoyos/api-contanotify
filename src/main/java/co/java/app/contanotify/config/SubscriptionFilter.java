package co.java.app.contanotify.config;

import co.java.app.contanotify.enums.SubscriptionStatus;
import co.java.app.contanotify.model.Subscription;
import co.java.app.contanotify.model.TipoUsuario;
import co.java.app.contanotify.model.Usuario;
import co.java.app.contanotify.repository.SubscriptionRepository;
import co.java.app.contanotify.repository.TipoUsuarioRepository;
import co.java.app.contanotify.repository.UsuarioRepository;
import com.mercadopago.MercadoPagoConfig;
import jakarta.annotation.PostConstruct;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class SubscriptionFilter {

    private final UsuarioRepository usuarioRepository;
    private final TipoUsuarioRepository tipoUsuarioRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Value("${mercadopago.access-token}")
    private String mercadoPagoAccessToken;

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(mercadoPagoAccessToken);
    }

    public SubscriptionFilter(UsuarioRepository usuarioRepository,
                              TipoUsuarioRepository tipoUsuarioRepository,
                              SubscriptionRepository subscriptionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.tipoUsuarioRepository = tipoUsuarioRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    public boolean isBlocked(String username) {
        Optional<TipoUsuario> opt = tipoUsuarioRepository.findByName("contador");
        Optional<TipoUsuario> optTipoUsuario = tipoUsuarioRepository.findByPublicId(opt.get().getPublicId());
        Usuario user = usuarioRepository.findByEmailAndTipoUsuarioIdAndActive(username, new ObjectId(optTipoUsuario.get().getId()), true).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Subscription subscription = subscriptionRepository.findByPublicId(user.getSubscriptionId()).get();
        if (subscription.getStatus() == SubscriptionStatus.TRIAL &&
                LocalDate.now().isAfter(subscription.getTrialEndDate())) {
            return true;
        }

        return subscription.getStatus() != SubscriptionStatus.ACTIVE;
    }
}
