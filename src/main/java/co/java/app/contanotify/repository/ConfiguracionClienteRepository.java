package co.java.app.contanotify.repository;

import co.java.app.contanotify.model.ConfiguracionCliente;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConfiguracionClienteRepository extends MongoRepository<ConfiguracionCliente, String> {

    Optional<ConfiguracionCliente> findByPublicId(UUID publicId);

    Optional<ConfiguracionCliente> findByUsuarioId(String id);

    Optional<ConfiguracionCliente> findByUsuarioClienteId(ObjectId id);

    Optional<ConfiguracionCliente> findByUsuarioIdAndUsuarioClienteId(ObjectId usuarioId, ObjectId clienteId);
}
