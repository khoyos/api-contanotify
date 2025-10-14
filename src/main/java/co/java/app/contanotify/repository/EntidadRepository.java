package co.java.app.contanotify.repository;

import co.java.app.contanotify.model.Entidad;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface EntidadRepository extends MongoRepository<Entidad,String> {
    Optional<Entidad> findByName(String name);
}
