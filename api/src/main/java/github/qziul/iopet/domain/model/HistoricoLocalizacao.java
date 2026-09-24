package github.qziul.iopet.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

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
    // Chave Estrangeira (FK) que apontará para a PK natural (endereco_mac) de
    // DispositivoIot
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "localizador_endereco_mac", referencedColumnName = "endereco_mac", nullable = false)
    private DispositivoIot dispositivoIot;

    // Relacionamento Muitos-para-Um com AlertaGeofecing
    @OneToMany(mappedBy = "historicoLocalizacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AlertaGeofencing> alertaGeofencing = new ArrayList<>();

    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false, name = "ponto")
    private Point posicao;

    @Column(nullable = false)
    private float latitude;

    @Column(nullable = false)
    private float longitude;

    @Column(nullable = false, name = "data_registro")
    private LocalDateTime data;

    @PrePersist
    public void onCreate() {
        this.data = LocalDateTime.now();
    }

}
