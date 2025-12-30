package co.java.app.contanotify.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "usuarios")
public class Usuario {

    @Id
    private String id;
    private String publicId;
    private String nombre;
    private String documento;
    private String email;
    private String password; // bcrypt
    private String telefono;
    private boolean estado;
    private ObjectId tipoUsuarioId;
    private String usuarioContadorId;
    private String razonSocial;
    private String tipoDocumento;
    private String subscriptionId;
    private boolean block;
    private boolean active;

    // bloqueo/seguridad
    private int bloqueo = 0; // contador de fallos actuales (se resetea en ciertas condiciones)
    private int fallos = 0; // 0 = none, 1 = 10min, 2 = 1h, 3 = 24h
    private Instant lockUntil; // si no es null y es > now -> cuenta bloqueada

    // reset de contraseña
    private String resetPasswordToken;
    private Instant resetPasswordExpiry;

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

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getBloqueo() {
        return bloqueo;
    }

    public void setBloqueo(int bloqueo) {
        this.bloqueo = bloqueo;
    }

    public int getFallos() {
        return fallos;
    }

    public void setFallos(int fallos) {
        this.fallos = fallos;
    }

    public Instant getLockUntil() {
        return lockUntil;
    }

    public void setLockUntil(Instant lockUntil) {
        this.lockUntil = lockUntil;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public Instant getResetPasswordExpiry() {
        return resetPasswordExpiry;
    }

    public void setResetPasswordExpiry(Instant resetPasswordExpiry) {
        this.resetPasswordExpiry = resetPasswordExpiry;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public ObjectId getTipoUsuarioId() {
        return tipoUsuarioId;
    }

    public void setTipoUsuarioId(ObjectId tipoUsuarioId) {
        this.tipoUsuarioId = tipoUsuarioId;
    }

    public String getRazonSocial() {return razonSocial;}

    public void setRazonSocial(String razonSocial) {this.razonSocial = razonSocial;}

    public String getTipoDocumento() {return tipoDocumento;}

    public void setTipoDocumento(String tipoDocumento) {this.tipoDocumento = tipoDocumento;}

    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }

    public String getPublicId() { return publicId; }

    public void setPublicId(String publicId) { this.publicId = publicId; }

    public String getUsuarioContadorId() { return usuarioContadorId; }

    public void setUsuarioContadorId(String usuarioContadorId) { this.usuarioContadorId = usuarioContadorId; }

    public String getSubscriptionId() { return subscriptionId; }

    public void setSubscriptionId(String subscriptionId) { this.subscriptionId = subscriptionId; }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }
}
