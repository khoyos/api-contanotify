package co.java.app.contanotify.repository;

import co.java.app.contanotify.model.TipoUsuario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TipoUsuarioRepository extends MongoRepository<TipoUsuario, String> {
    Optional<TipoUsuario> findByName(String name);
}
