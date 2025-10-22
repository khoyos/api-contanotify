package co.java.app.contanotify.repository;

import co.java.app.contanotify.model.Obligacion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface ObligacionRepository extends MongoRepository<Obligacion, String> {

    Optional<Obligacion> findByPublicId(String publicId);

    Optional<Obligacion> findByName(String name);

}
