package co.java.app.contanotify.repository;

import co.java.app.contanotify.model.Obligacion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ObligacionRepository extends MongoRepository<Obligacion, String> {
    Optional<Obligacion> findByName(String name);

}
