package co.java.app.contanotify.repository;

import co.java.app.contanotify.model.ConfiguracionCliente;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ConfiguracionClienteRepository extends MongoRepository<ConfiguracionCliente, String> {
    Optional<ConfiguracionCliente> findByUsuarioId(String id);
    Optional<ConfiguracionCliente> findByUsuarioClienteId(ObjectId id);
    Optional<ConfiguracionCliente> findByUsuarioIdAndUsuarioClienteId(ObjectId usuarioId, ObjectId clienteId);
}
