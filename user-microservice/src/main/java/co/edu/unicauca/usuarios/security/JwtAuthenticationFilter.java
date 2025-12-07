package co.edu.unicauca.usuarios.security;

import co.edu.unicauca.usuarios.models.Usuario;
import co.edu.unicauca.usuarios.services.IUsuarioService;
import co.edu.unicauca.usuarios.util.InvalidUserDataException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final IUsuarioService usuarioService;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, IUsuarioService usuarioService) {
        this.tokenProvider = tokenProvider;
        this.usuarioService = usuarioService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (tokenProvider.validateToken(token)) {
                String email = tokenProvider.getEmailFromToken(token);
                String rolesStr = tokenProvider.getRolFromToken(token); // Ahora contiene roles separados por coma

                try {
                    Usuario usuario = usuarioService.obtenerPorEmail(email);
                    if (usuario != null) {
                        List<GrantedAuthority> authorities = Arrays.stream(rolesStr.split(","))
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .collect(Collectors.toList());

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(email, null, authorities);

                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                } catch (InvalidUserDataException ex) {
                    // Usuario no encontrado o inválido → no se setea autenticación
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}