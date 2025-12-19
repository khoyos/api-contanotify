package co.java.app.contanotify.dto;

import java.util.UUID;

public class TipoUsuarioDTO {
    private String id;
    private String publicId;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublicId() { return publicId; }

    public void setPublicId(String publicId) { this.publicId = publicId; }
}
