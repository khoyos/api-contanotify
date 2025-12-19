package co.java.app.contanotify.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "dias.alerta")
public class DiasAlertaProperties {

    private int urgente;
    private int alta;
    private int media;
    private int hoy;

    // Getters y setters

    public int getUrgente() {
        return urgente;
    }

    public void setUrgente(int urgente) {
        this.urgente = urgente;
    }

    public int getAlta() {
        return alta;
    }

    public void setAlta(int alta) {
        this.alta = alta;
    }

    public int getMedia() {
        return media;
    }

    public void setMedia(int media) {
        this.media = media;
    }

    public int getHoy() {
        return hoy;
    }

    public void setHoy(int hoy) {
        this.hoy = hoy;
    }
}

