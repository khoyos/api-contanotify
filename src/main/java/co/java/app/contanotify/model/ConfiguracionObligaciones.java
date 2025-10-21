package co.java.app.contanotify.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "configuracion_obligaciones")
public class ConfiguracionObligaciones {

    @Id
    private String id;
    private UUID publicId;
    private String usuarioId;
    private String clienteId;
    private String identidadCliente;
    private String nombreCliente;
    private String entidad;
    private String renta;
    private String pago;
    private String fecha;
    private String obligacionClienteId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public String getRenta() {
        return renta;
    }

    public void setRenta(String renta) {
        this.renta = renta;
    }

    public String getPago() {
        return pago;
    }

    public void setPago(String pago) {
        this.pago = pago;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getObligacionClienteId() {
        return obligacionClienteId;
    }

    public void setObligacionClienteId(String obligacionClienteId) {
        this.obligacionClienteId = obligacionClienteId;
    }

    public String getIdentidadCliente() {
        return identidadCliente;
    }

    public void setIdentidadCliente(String identidadCliente) {this.identidadCliente = identidadCliente;}

    public UUID getPublicId() { return publicId;}

    public void setPublicId(UUID publicId) { this.publicId = publicId;}
}
