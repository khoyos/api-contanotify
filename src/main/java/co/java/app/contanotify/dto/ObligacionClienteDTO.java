package co.java.app.contanotify.dto;

import org.bson.types.ObjectId;

public class ObligacionClienteDTO {

    private String usuarioClienteId;
    private String pagoId;

    public String getUsuarioClienteId() {
        return usuarioClienteId;
    }

    public void setUsuarioClienteId(String usuarioClienteId) {
        this.usuarioClienteId = usuarioClienteId;
    }

    public String getPagoId() {
        return pagoId;
    }

    public void setPagoId(String pagoId) {
        this.pagoId = pagoId;
    }
}
