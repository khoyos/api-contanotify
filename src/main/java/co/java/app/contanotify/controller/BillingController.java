package co.java.app.contanotify.controller;

import co.java.app.contanotify.dto.PlanDTO;
import co.java.app.contanotify.enums.SubscriptionPlan;
import co.java.app.contanotify.enums.SubscriptionStatus;
import co.java.app.contanotify.model.Subscription;
import co.java.app.contanotify.model.Usuario;
import co.java.app.contanotify.repository.SubscriptionRepository;
import co.java.app.contanotify.repository.UsuarioRepository;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static co.java.app.contanotify.enums.SubscriptionPlan.*;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private UsuarioRepository usuarioRepository;
    private SubscriptionRepository subscriptionRepository;

    public BillingController(UsuarioRepository usuarioRepository,
                             SubscriptionRepository subscriptionRepository) {
        this.prices = prices;
        this.usuarioRepository = usuarioRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @PostMapping("/pay")
    public ResponseEntity createPayment(@AuthenticationPrincipal Usuario user,
                                        @RequestParam SubscriptionPlan plan) throws MPException, MPApiException {

        String FRONT_URL = "http://localhost:5173/";
        String API_URL = "https://localhost:3000";

        PreferenceItemRequest item =
                PreferenceItemRequest.builder()
                        .title("Suscripción " + plan.name())
                        .quantity(1)
                        .unitPrice(prices.get(plan))
                        .currencyId("COP")
                        .build();

        PreferenceRequest preference =
                PreferenceRequest.builder()
                        .items(List.of(item))
                        .externalReference(user.getPublicId())
                        .backUrls(PreferenceBackUrlsRequest.builder()
                                .success(FRONT_URL + "/success")
                                .failure(FRONT_URL + "/failure")
                                .pending(FRONT_URL + "/pending")
                                .build()
                        )
                        .notificationUrl(API_URL + "/webhooks/mercadopago")
                        .build();

        Preference result = new PreferenceClient().create(preference);

        return ResponseEntity.status(201).body(Map.of(
                "paymentResponse", result.getInitPoint()
        ));
    }

    @PostMapping("/webhooks/mercadopago")
    public ResponseEntity<Void> webhook(@RequestParam String type, @RequestParam String data_id) throws MPException, MPApiException {

        if ("payment".equals(type)) {
            processPayment(data_id);
        }

        return ResponseEntity.ok().build();
    }

    private void processPayment(String paymentId) throws MPException, MPApiException {

        Payment payment = new PaymentClient().get(Long.valueOf(paymentId));

        if ("approved".equals(payment.getStatus())) {

            Usuario usuario = usuarioRepository.findByPublicId(payment.getExternalReference()).get();
            Subscription subscription = subscriptionRepository.findByPublicId(usuario.getSubscriptionId()).get();

            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscription.setStartDate(LocalDate.now());
            subscription.setEndDate(LocalDate.now().plusMonths(1));
            subscription.setMercadoPagoPaymentId(paymentId);

            subscriptionRepository.save(subscription);
        }
    }



    public Map<SubscriptionPlan, BigDecimal> prices = Map.of(
            BASIC, new BigDecimal("29000")//,
            //PRO, new BigDecimal("59000")
    );

    @GetMapping("/plans")
    public List<PlanDTO> getPlans() {
        return List.of(
                new PlanDTO(FREE_TRIAL.name(), "Plan Básico", new BigDecimal("0")),
                new PlanDTO(BASIC.name(), "Plan Básico", new BigDecimal("29000")),
                new PlanDTO(PRO.name(), "Plan Pro", new BigDecimal("59000"))
        );
    }


}
