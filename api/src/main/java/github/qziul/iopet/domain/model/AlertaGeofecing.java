package github.qziul.iopet.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerta_geofecing")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertaGeofecing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento Muitos-para-Um com Pet
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    // Relacionamento Muitos-para-Um com HistoricoLocalizacao
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "historico_localizacao_id", referencedColumnName = "id", nullable = false)
    private HistoricoLocalizacao historicoLocalizacao;

    @Column(nullable = false, length = 255)
    private String mensagem;

    @Column(nullable = false)
    private boolean visualizado;

    @Column(nullable = false)
    private LocalDateTime data;

    @PrePersist
    public void onCreate() {this.data = LocalDateTime.now();}
}
