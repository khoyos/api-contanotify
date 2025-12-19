package co.java.app.contanotify.dto;

public class ObligacionEmailDTO {

    private String nombreRenta;
    private String nombrePago;
    private String fecha;

    public String getNombreRenta() {
        return nombreRenta;
    }

    public void setNombreRenta(String nombreRenta) {
        this.nombreRenta = nombreRenta;
    }

    public String getNombrePago() {
        return nombrePago;
    }

    public void setNombrePago(String nombrePago) {
        this.nombrePago = nombrePago;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
