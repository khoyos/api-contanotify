package co.java.app.contanotify.controller;

import co.java.app.contanotify.config.PropertiesConfig;
import co.java.app.contanotify.dto.PlanDTO;
import co.java.app.contanotify.enums.SubscriptionPlan;
import co.java.app.contanotify.enums.SubscriptionStatus;
import co.java.app.contanotify.model.Subscription;
import co.java.app.contanotify.model.Usuario;
import co.java.app.contanotify.repository.SubscriptionRepository;
import co.java.app.contanotify.repository.UsuarioRepository;
import com.mercadopago.MercadoPagoConfig;
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
    private PropertiesConfig propertiesConfig;

    public BillingController(UsuarioRepository usuarioRepository,
                             SubscriptionRepository subscriptionRepository,
                             PropertiesConfig propertiesConfig) {
        this.prices = prices;
        this.usuarioRepository = usuarioRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.propertiesConfig = propertiesConfig;
    }

    @PostMapping("/pay")
    public ResponseEntity<?> createPayment(@RequestParam String user,
                                           @RequestParam String planCode)
            throws MPException {

        BigDecimal price = prices.get(planCode);
        if (price == null) {
            return ResponseEntity.badRequest().body("Plan inválido");
        }

        String FRONT_URL = propertiesConfig.getUrlFrotendPropertie();
        String BACKEND_URL = "http://localhost:8080";

        MercadoPagoConfig.setAccessToken(propertiesConfig.getMercadopagoAccessToken());
        System.out.println("PLAN CODE: " + planCode);
        System.out.println("PRICE: " + prices.get(planCode));

        PreferenceItemRequest item =
                PreferenceItemRequest.builder()
                        .title("Suscripción " + planCode)
                        .quantity(1)
                        .unitPrice(price)
                        .currencyId("COP")
                        .build();

        PreferenceRequest.PreferenceRequestBuilder preferenceBuilder =
                PreferenceRequest.builder()
                        .items(List.of(item))
                        .externalReference(user)
                        .backUrls(
                                PreferenceBackUrlsRequest.builder()
                                        .success(FRONT_URL + "/success")
                                        .failure(FRONT_URL + "/failure")
                                        .pending(FRONT_URL + "/pending")
                                        .build()
                        )
                        .notificationUrl(BACKEND_URL + "/webhooks/mercadopago");


        if (propertiesConfig.isProduction()) {
            preferenceBuilder.autoReturn("approved");
        }

        PreferenceRequest preference = preferenceBuilder.build();

        try {
            Preference result = new PreferenceClient().create(preference);
            return ResponseEntity.status(201).body(
                    Map.of("url", result.getInitPoint())
            );
        } catch (MPApiException e) {
            System.out.println("STATUS: " + e.getStatusCode());
            System.out.println("RESPONSE: " + e.getApiResponse().getContent());
            System.out.println("FRONT_URL = " + FRONT_URL);
            System.out.println("SUCCESS_URL = " + FRONT_URL + "/success");
            return ResponseEntity.status(500).body(e.getApiResponse().getContent());
        }
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

    public Map<String, BigDecimal> prices = Map.of(
            FREE_TRIAL.name(), new BigDecimal("0"),
            PRO.name(), new BigDecimal("29999"),
            PREMIUM.name(), new BigDecimal("59999")
            //PRO, new BigDecimal("59000")
    );

    @GetMapping("/plans")
    public List<PlanDTO> getPlans() {
        return List.of(
                new PlanDTO(FREE_TRIAL.name(), "Plan Básico", new BigDecimal("0")),
                new PlanDTO(PRO.name(), "Plan Pro", new BigDecimal("29000")),
                new PlanDTO(PREMIUM.name(), "Plan Premium", new BigDecimal("59000"))
        );
    }


}
