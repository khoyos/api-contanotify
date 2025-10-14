package co.java.app.contanotify.dto;

import org.bson.types.ObjectId;

public class ConfigurarClienteDTO {

    private String usuarioId;
    private String usuarioClienteId;
    private String entidadId;
    private boolean notificarCliente;
    private boolean notificarContador;
    private boolean notificarEmail;
    private boolean notificarWhatsapp;
    private boolean notificarSms;
    private String obligacionId;

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioClienteId() {
        return usuarioClienteId;
    }

    public void setUsuarioClienteId(String usuarioClienteId) {
        this.usuarioClienteId = usuarioClienteId;
    }

    public String getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(String entidadId) {
        this.entidadId = entidadId;
    }

    public boolean isNotificarCliente() {
        return notificarCliente;
    }

    public void setNotificarCliente(boolean notificarCliente) {
        this.notificarCliente = notificarCliente;
    }

    public boolean isNotificarContador() {
        return notificarContador;
    }

    public void setNotificarContador(boolean notificarContador) {
        this.notificarContador = notificarContador;
    }

    public boolean isNotificarEmail() {
        return notificarEmail;
    }

    public void setNotificarEmail(boolean notificarEmail) {
        this.notificarEmail = notificarEmail;
    }

    public boolean isNotificarWhatsapp() {
        return notificarWhatsapp;
    }

    public void setNotificarWhatsapp(boolean notificarWhatsapp) {
        this.notificarWhatsapp = notificarWhatsapp;
    }

    public boolean isNotificarSms() {
        return notificarSms;
    }

    public void setNotificarSms(boolean notificarSms) {
        this.notificarSms = notificarSms;
    }

    public String getObligacionId() {
        return obligacionId;
    }

    public void setObligacionId(String obligacionId) {
        this.obligacionId = obligacionId;
    }
}
