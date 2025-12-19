package co.java.app.contanotify.service.impl;

import co.java.app.contanotify.dto.TipoUsuarioDTO;
import co.java.app.contanotify.model.TipoUsuario;
import co.java.app.contanotify.model.Usuario;
import co.java.app.contanotify.repository.TipoUsuarioRepository;
import co.java.app.contanotify.repository.UsuarioRepository;
import org.bson.types.ObjectId;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {


    private final UsuarioRepository repo;
    private final TipoUsuarioRepository tipoUsuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository repo,
                                  TipoUsuarioRepository tipoUsuarioRepository) {
        this.repo = repo;
        this.tipoUsuarioRepository = tipoUsuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<TipoUsuario> opt = tipoUsuarioRepository.findByName("contador");
        Optional<TipoUsuario> optTipoUsuario = tipoUsuarioRepository.findByPublicId(opt.get().getPublicId());
        Usuario u = repo.findByEmailAndTipoUsuarioIdAndActive(username, new ObjectId(optTipoUsuario.get().getId()), true).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getPassword())
                .authorities("USER")
                .accountLocked(u.getLockUntil() != null && u.getLockUntil().isAfter(java.time.Instant.now()))
                .build();
    }
}
