package co.java.app.contanotify.repository;

import co.java.app.contanotify.model.ConfiguracionObligaciones;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConfiguracionObligacionesRepository extends MongoRepository<ConfiguracionObligaciones, String> {

    Optional<ConfiguracionObligaciones> findByPublicId(UUID publicId);

    Optional<ConfiguracionObligaciones> findByNombreCliente(String name);

    Optional<List<ConfiguracionObligaciones>> findByUsuarioId(String usuarioId);
}
