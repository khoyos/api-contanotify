package co.java.app.contanotify.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertiesConfig {

    @Value("${app.url.external-frotend}")
    private String urlFrotendPropertie;

    @Value("${app.url.mercado-pago-api}")
    private String urlMercadoPagoApi;

    public String getUrlFrotendPropertie() {
        return urlFrotendPropertie;
    }

    public String getUrlMercadoPagoApi() {
        return urlMercadoPagoApi;
    }
}
