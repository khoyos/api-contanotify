package co.java.app.contanotify.repository;

import co.java.app.contanotify.model.ObligacionCliente;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ObligacionClienteRepository extends MongoRepository<ObligacionCliente,String> {

    Optional<ObligacionCliente> findByPublicId(String publicId);

    Optional<List<ObligacionCliente>> findByFecha(LocalDateTime date);

    Optional<List<ObligacionCliente>> findByConfiguracionClienteIdAndCalendarioIdAndFecha(ObjectId configClienteId, ObjectId calendarioId, LocalDateTime fecha);

    List<ObligacionCliente> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

}
