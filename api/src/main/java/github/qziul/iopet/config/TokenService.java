package github.qziul.iopet.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import github.qziul.iopet.domain.model.Tutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String gerarToken(Tutor tutor) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(jwtSecret);
            return JWT.create()
                    .withIssuer("IoPet API")
                    .withSubject(tutor.getEmail()) // Define quem é o dono do token
                    .withClaim("idPublico", tutor.getUuid().toString()) // Guarda o UUID no token
                    .withExpiresAt(dataExpiracao())
                    .sign(algoritmo);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String validarToken(String token) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(jwtSecret);
            return JWT.require(algoritmo)
                    .withIssuer("IoPet API")
                    .build()
                    .verify(token)
                    .getSubject(); // Devolve o e-mail se o token for válido
        } catch (JWTVerificationException exception) {
            return "";
        }
    }

    private Instant dataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
