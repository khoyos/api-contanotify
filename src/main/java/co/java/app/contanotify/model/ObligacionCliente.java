package co.java.app.contanotify.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "obligaciones_clientes")
public class ObligacionCliente {
    @Id
    private String id;
    private ObjectId configuracionClienteId;
    private ObjectId calendarioId;
    private LocalDateTime fecha;
    private boolean reminder5DaysSent = false;
    private boolean reminder3DaysSent = false;
    private boolean reminder1DaySent = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ObjectId getConfiguracionClienteId() {
        return configuracionClienteId;
    }

    public void setConfiguracionClienteId(ObjectId configuracionClienteId) {
        this.configuracionClienteId = configuracionClienteId;
    }

    public ObjectId getCalendarioId() {
        return calendarioId;
    }

    public void setCalendarioId(ObjectId calendarioId) {
        this.calendarioId = calendarioId;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public boolean isReminder5DaysSent() {
        return reminder5DaysSent;
    }

    public void setReminder5DaysSent(boolean reminder5DaysSent) {
        this.reminder5DaysSent = reminder5DaysSent;
    }

    public boolean isReminder3DaysSent() {
        return reminder3DaysSent;
    }

    public void setReminder3DaysSent(boolean reminder3DaysSent) {
        this.reminder3DaysSent = reminder3DaysSent;
    }

    public boolean isReminder1DaySent() {
        return reminder1DaySent;
    }

    public void setReminder1DaySent(boolean reminder1DaySent) {
        this.reminder1DaySent = reminder1DaySent;
    }
}
