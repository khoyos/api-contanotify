package co.java.app.contanotify.repository;

import co.java.app.contanotify.model.ConfiguracionObligaciones;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ConfiguracionObligacionesRepository extends MongoRepository<ConfiguracionObligaciones, String> {
    Optional<ConfiguracionObligaciones> findByNombreCliente(String name);
}
