package github.qziul.iopet.config;

import github.qziul.iopet.domain.repository.TutorRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final TutorRepository tutorRepository;

    public SecurityFilter(TokenService tokenService, TutorRepository tutorRepository) {
        this.tokenService = tokenService;
        this.tutorRepository = tutorRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException
    {
        var token = recuperarToken(request);

        if (token != null) {
            var email = tokenService.validarToken(token);
            UserDetails tutor = tutorRepository.findByEmail(email).orElse(null);

            if (tutor != null) {
                // Autentica o usuário no contexto do Spring Security
                var authentication = new UsernamePasswordAuthenticationToken(tutor, null, tutor.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continua o fluxo da requisição
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}
