package github.qziul.iopet.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "tutor")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tutor implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento Um-para-Muitos
    // cascade = CascadeType.ALL + orphanRemoval =  Se o tutor for excluído, todos os vínculos e os pets
    //                                              associados são eliminados em cascata.
    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Pet> pets = new ArrayList<>();

    // ID público para rotas API exposta
    @Column(nullable = false, unique = true)
    private UUID uuid = UUID.randomUUID();

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String senha;

    @Column(nullable = false, unique = true, length = 20)
    private String telefone;

    @Column(name = "url_foto")
    private String urlFoto;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "editado_em")
    private LocalDateTime editadoEm;

    @Column(name = "excluido_em")
    private LocalDateTime excluidoEm;

    @Column(nullable = false)
    private Boolean ativo;

    @PrePersist
    protected void onCreate() {
        this.ativo = true;
        this.criadoEm = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.editadoEm = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
