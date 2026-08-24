package github.qziul.iopet.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.locationtech.jts.geom.Geometry;
import lombok.*;

import java.util.*;
import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@Table(name = "pet")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Chave pública exposta
    @Column(nullable = false, unique = true)
    private UUID uuid = UUID.randomUUID();

    // Relacionamento Muitos-para-Um com Tutor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    // Relacionamento Um-para-Um com DispositivoIot
    // DispositivoIot recebe ID de Pet
    @OneToOne(mappedBy = "pet")
    private DispositivoIot dispositivoIot;

    // Relacionamento Um-para-Muitos com AlertaGeofecing
    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AlertaGeofecing> alertaGeofecing = new ArrayList<>();

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String raca;

    @Column(nullable = false)
    private String especie;

    @Column(nullable = false)
    private String porte;

    @Column(name = "url_foto", nullable = false)
    private String urlFoto;

    @Column( name = "data_nascimento", nullable = false)
    private Date dataNascimento;

    @Column(length = 500)
    private String descricao;

    // Campo geométrico PostGIS
    @Column(name = "zona_seguranca", columnDefinition = "geometry(Polygon, 4326)")
    private Geometry zonaSeguranca;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "editado_em")
    private LocalDateTime editadoEm;

    @Column(name = "excluido_em")
    private LocalDateTime excluidoEm;

    @Column(nullable = false)
    private Boolean ativo = true;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.editadoEm = LocalDateTime.now();
    }
}
