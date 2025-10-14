package co.java.app.contanotify.service;

import co.java.app.contanotify.dto.UsuarioDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface IUsuario {

    UsuarioDTO save(UsuarioDTO usuarioDTO);

    Page<UsuarioDTO> getAll(String nombre, String documento, String email, Pageable pageable);

    UsuarioDTO findByTipoDocumentoAndDocumento(String tipoDocumento, String documento);

    UsuarioDTO findById(String id);

    void update(UsuarioDTO usuarioDTO);

    void delete(UsuarioDTO usuarioDTO);

}
