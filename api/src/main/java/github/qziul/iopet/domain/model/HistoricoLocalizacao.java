package github.qziul.iopet.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "historico_localizacao")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoricoLocalizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento Muitos-para-Um com DispositivoIoT
    // Chave Estrangeira (FK) que apontará para a PK natural (endereco_mac) de DispositivoIot
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "localizador_endereco_mac", referencedColumnName = "endereco_mac", nullable = false)
    private DispositivoIot dispositivoIot;

    // Relacionamento Muitos-para-Um com AlertaGeofecing
    @OneToMany(mappedBy = "historicoLocalizacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AlertaGeofecing> alertaGeofecing = new ArrayList<>();

    @Column(nullable = false)
    private float latitude;

    @Column(nullable = false)
    private float longitude;

    @Column(nullable = false)
    private LocalDateTime data;

    @PrePersist
    public void onCreate() {this.data = LocalDateTime.now();}

}
