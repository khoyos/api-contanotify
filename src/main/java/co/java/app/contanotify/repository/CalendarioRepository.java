package co.java.app.contanotify.repository;

import co.java.app.contanotify.model.Calendario;
import co.java.app.contanotify.model.Entidad;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CalendarioRepository extends MongoRepository<Calendario,String> {

    Optional<List<Calendario>> findByObligacionId(String s);
    Optional<Calendario> findByNombre(String name);
}
