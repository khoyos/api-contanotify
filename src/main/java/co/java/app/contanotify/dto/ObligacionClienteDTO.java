package co.java.app.contanotify.dto;

public class ObligacionClienteDTO {

    private String usuarioClienteId;
    private String obligacionRentaId;

    public String getUsuarioClienteId() {
        return usuarioClienteId;
    }

    public void setUsuarioClienteId(String usuarioClienteId) {
        this.usuarioClienteId = usuarioClienteId;
    }

    public String getObligacionRentaId() {
        return obligacionRentaId;
    }

    public void setObligacionRentaId(String obligacionRentaId) {
        this.obligacionRentaId = obligacionRentaId;
    }
}
