package co.java.app.contanotify.repository;

import co.java.app.contanotify.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByPublicId(String publicId);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByDocumento(String documento);

    Optional<Usuario> findByResetPasswordToken(String token);

    Optional<Usuario> findByTipoDocumentoAndDocumento(String tipoDocumento, String documento);
}
