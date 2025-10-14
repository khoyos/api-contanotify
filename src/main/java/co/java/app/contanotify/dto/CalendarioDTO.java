package co.java.app.contanotify.dto;


import java.time.LocalDateTime;
import java.util.List;

public class CalendarioDTO {

    private String id;

    private String nombre;

    private LocalDateTime hasta;

    private boolean estado;

    private String obligacionId;

    private int calendario;

    private List<FechaDTO> fechas;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDateTime getHasta() {
        return hasta;
    }

    public void setHasta(LocalDateTime hasta) {
        this.hasta = hasta;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getObligacionId() {
        return obligacionId;
    }

    public void setObligacionId(String obligacionId) {
        this.obligacionId = obligacionId;
    }

    public int getCalendario() {
        return calendario;
    }

    public void setCalendario(int calendario) {
        this.calendario = calendario;
    }

    public List<FechaDTO> getFechas() {
        return fechas;
    }

    public void setFechas(List<FechaDTO> fechas) {
        this.fechas = fechas;
    }
}
