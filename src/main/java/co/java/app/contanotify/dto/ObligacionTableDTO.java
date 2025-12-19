package co.java.app.contanotify.dto;

public class ObligacionTableDTO {

    private String id;
    private String nombreCliente;
    private String entidad;
    private String renta;
    private String pago;
    private String fecha;
    private String identidadCliente;
    private String estado;
    private String observacion;
    private String periodo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getIdentidadCliente() {
        return identidadCliente;
    }

    public void setIdentidadCliente(String identidadCliente) {
        this.identidadCliente = identidadCliente;
    }

    public String getEstado() { return estado; }

    public void setEstado(String estado) { this.estado = estado; }

    public String getObservacion() { return observacion; }

    public void setObservacion(String observacion) { this.observacion = observacion; }

    public String getPeriodo() {return periodo; }

    public void setPeriodo(String periodo) { this.periodo = periodo; }
}
