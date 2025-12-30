package co.java.app.contanotify.config;

import co.java.app.contanotify.enums.SubscriptionStatus;
import co.java.app.contanotify.model.Subscription;
import co.java.app.contanotify.repository.UsuarioRepository;
import co.java.app.contanotify.util.JwtUtil;
import co.java.app.contanotify.service.impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDate;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final SubscriptionFilter subscriptionFilter;

    public JwtTokenFilter(JwtUtil jwtUtil,
                          UserDetailsServiceImpl uds,
                          UsuarioRepository usuarioRepository,
                          SubscriptionFilter subscriptionFilter) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = uds;
        this.subscriptionFilter = subscriptionFilter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        String header = req.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsername(token);

                if (username != null && subscriptionFilter.isBlocked(username)) {
                    res.setStatus(HttpStatus.PAYMENT_REQUIRED.value());
                    res.getWriter().write("Suscripción requerida");
                    return;
                }

                UserDetails ud = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                ud,
                                null,
                                ud.getAuthorities()
                        );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        chain.doFilter(req, res);
    }

}
