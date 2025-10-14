package co.java.app.contanotify.dto;

public class ResetPasswordRequest {

    public String token;
    public String newPassword;
    public String fallos;
    public String bloqueo;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getFallos() {
        return fallos;
    }

    public void setFallos(String fallos) {
        this.fallos = fallos;
    }

    public String getBloqueo() {
        return bloqueo;
    }

    public void setBloqueo(String bloqueo) {
        this.bloqueo = bloqueo;
    }
}
