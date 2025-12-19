package co.java.app.contanotify.repository;

import co.java.app.contanotify.model.Calendario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CalendarioRepository extends MongoRepository<Calendario,String> {

    Optional<Calendario> findByPublicId(UUID publicId);

    Optional<List<Calendario>> findByObligacionId(String s);

    Optional<Calendario> findByNombre(String name);
}
