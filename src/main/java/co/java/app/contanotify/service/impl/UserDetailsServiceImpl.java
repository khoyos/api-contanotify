package co.java.app.contanotify.service.impl;

import co.java.app.contanotify.model.Usuario;
import co.java.app.contanotify.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {


    private final UsuarioRepository repo;

    public UserDetailsServiceImpl(UsuarioRepository repo) { this.repo = repo; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario u = repo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getPassword())
                .authorities("USER")
                .accountLocked(u.getLockUntil() != null && u.getLockUntil().isAfter(java.time.Instant.now()))
                .build();
    }
}
