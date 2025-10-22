package co.java.app.contanotify.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "obligaciones")
public class Obligacion {

    @Id
    private String id;
    private String publicId;
    private String name;
    private boolean state;

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

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getPublicId() { return publicId; }

    public void setPublicId(String publicId) { this.publicId = publicId; }

}
