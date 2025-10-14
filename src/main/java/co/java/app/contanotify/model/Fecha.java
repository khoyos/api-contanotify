package co.java.app.contanotify.model;

import java.time.LocalDateTime;

public class Fecha {

    private Integer id;
    private LocalDateTime fecha;
    private String nit;

    // Getters y setters


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }
}
