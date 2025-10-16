package co.java.app.contanotify.dto;

public class AlertasCriticasDTO {
    private String fechaVencimiento;
    private String nombreCliente;
    private String obligacionRenta;
    private String obligacionPago;
    private String perido;
    private boolean urgente;
    private boolean alta;
    private boolean media;

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public String getObligacionRenta() {
        return obligacionRenta;
    }

    public void setObligacionRenta(String obligacionRenta) {
        this.obligacionRenta = obligacionRenta;
    }

    public String getObligacionPago() {
        return obligacionPago;
    }

    public void setObligacionPago(String obligacionPago) {
        this.obligacionPago = obligacionPago;
    }

    public String getPerido() {
        return perido;
    }

    public void setPerido(String perido) {
        this.perido = perido;
    }

    public boolean isUrgente() {
        return urgente;
    }

    public void setUrgente(boolean urgente) {
        this.urgente = urgente;
    }

    public boolean isAlta() {
        return alta;
    }

    public void setAlta(boolean alta) {
        this.alta = alta;
    }

    public boolean isMedia() {
        return media;
    }

    public void setMedia(boolean media) {
        this.media = media;
    }
}
