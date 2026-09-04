package github.qziul.iopet.controller;

import github.qziul.iopet.config.TokenService;
import github.qziul.iopet.controller.dto.request.DadosAuthRequestDTO;
import github.qziul.iopet.controller.dto.request.TutorRequestDTO;
import github.qziul.iopet.controller.dto.response.AuthResponseDTO;
import github.qziul.iopet.controller.dto.response.TutorResponseDTO;
import github.qziul.iopet.domain.model.Tutor;
import github.qziul.iopet.service.ITutorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager manager;
    private final TokenService tokenService;
    private final ITutorService tutorService;

    public AuthController(AuthenticationManager manager, TokenService tokenService, ITutorService tutorService) {
        this.manager = manager;
        this.tokenService = tokenService;
        this.tutorService = tutorService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> efetuarLogin(@RequestBody DadosAuthRequestDTO dados) {
        var tokenAutenticacao = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());

        // Verifica se o e-mail e a senha batem com o banco de dados
        var authentication = manager.authenticate(tokenAutenticacao);

        // Se bateu, gera o token JWT
        var tokenJWT = tokenService.gerarToken((Tutor) Objects.requireNonNull(authentication.getPrincipal()));

        return ResponseEntity.ok(tokenJWT);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> efetuarCadastroTutor(@RequestBody TutorRequestDTO dados) {
        if(this.tutorService.encontrarPorEmail(dados.email()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Tutor novoTutor = new Tutor();
        novoTutor.setNome(dados.nome());
        novoTutor.setEmail(dados.email());
        novoTutor.setSenha(dados.senha());
        novoTutor.setUrlFoto(dados.urlFoto());

        Tutor tutorSalvo = tutorService.cadastrar(novoTutor);

        // Gera o token JWT para o tutor recém-cadastrado
        var tokenJWT = tokenService.gerarToken(tutorSalvo);

        // Retornar status 201 (Created) com o token e os dados públicos do Tutor
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AuthResponseDTO(tokenJWT, new TutorResponseDTO(tutorSalvo)));
    }
}
