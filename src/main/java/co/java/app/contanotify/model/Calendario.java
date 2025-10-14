package co.java.app.contanotify.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "calendarios")
public class Calendario {

    @Id
    private String id;

    private String nombre;

    private LocalDateTime hasta;

    private boolean estado;

    private String obligacionId;

    private int calendario;

    private List<Fecha> fechas;

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
    public List<Fecha> getFechas() {
        return fechas;
    }
    public void setFechas(List<Fecha> fechas) {
        this.fechas = fechas;
    }

}
