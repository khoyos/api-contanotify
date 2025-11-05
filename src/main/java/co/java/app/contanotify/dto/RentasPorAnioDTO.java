package co.java.app.contanotify.dto;

public class RentasPorAnioDTO {

    private int anio;
    private long rentas;

    public RentasPorAnioDTO(int anio, long rentas) {
        this.anio = anio;
        this.rentas = rentas;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public long getRentas() {
        return rentas;
    }

    public void setRentas(long rentas) {
        this.rentas = rentas;
    }
}
