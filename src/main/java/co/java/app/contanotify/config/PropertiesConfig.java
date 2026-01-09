package co.java.app.contanotify.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertiesConfig {

    @Value("${app.url.frotend}")
    private String urlFrotendPropertie;

    @Value("${app.url.backend}")
    private String urlMercadoPagoApi;

    @Value("${mercadopago.access-token}")
    private String mercadopagoAccessToken;

    private boolean isProduction;

    public String getUrlFrotendPropertie() {
        return urlFrotendPropertie;
    }

    public String getUrlMercadoPagoApi() {
        return urlMercadoPagoApi;
    }

    public String getMercadopagoAccessToken() { return mercadopagoAccessToken; }

    public boolean isProduction() { return isProduction; }
}
