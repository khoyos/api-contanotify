package co.java.app.contanotify.repository;

import co.java.app.contanotify.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByResetPasswordToken(String token);
    Optional<Usuario> findByTipoDocumentoAndDocumento(String tipoDocumento, String documento);
}
