package github.qziul.iopet.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "dispositivo_iot")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DispositivoIot {

    @Id
    @Column(name = "endereco_mac", length = 17, updatable = false)
    @NotBlank
    // Valida o formato padrão de endereço MAC (ex: AA:BB:CC:DD:EE:FF)
    @Pattern(regexp = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")
    private String enderecoMac;

    // Relacionamento Um-para-Um com Pet
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pet_id", referencedColumnName = "id")
    private Pet pet;

    @OneToMany(mappedBy = "dispositivo_iot", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<HistoricoLocalizacao> historicoLocalizacao = new ArrayList<>();

    @Column( name = "bateria_nivel", nullable = false)
    private int bateriaNivel;

    @Column(nullable = false)
    private Boolean ativo;

    @Column(name = "ultima_localizacao", nullable = false)
    private LocalDateTime ultimaLocalizacao;

    @PrePersist
    protected void onCreate() {
        this.ultimaLocalizacao = LocalDateTime.now();
    }
}
