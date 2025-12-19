package co.java.app.contanotify.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "configuraciones_clientes")
public class ConfiguracionCliente {
    @Id
    private String id;
    private String publicId;
    private ObjectId usuarioId;
    private ObjectId usuarioClienteId;
    private ObjectId entidadId;
    private boolean notificarCliente;
    private boolean notificarContador;
    private boolean notificarEmail;
    private boolean notificarWhatsapp;
    private boolean notificarSms;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ObjectId getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(ObjectId usuarioId) {
        this.usuarioId = usuarioId;
    }

    public ObjectId getUsuarioClienteId() {
        return usuarioClienteId;
    }

    public void setUsuarioClienteId(ObjectId usuarioClienteId) {
        this.usuarioClienteId = usuarioClienteId;
    }

    public ObjectId getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(ObjectId entidadId) {
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

    public String getPublicId() {return publicId;}

    public void setPublicId(String publicId) {this.publicId = publicId;}
}
