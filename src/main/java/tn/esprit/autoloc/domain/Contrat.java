package tn.esprit.autoloc.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "contrat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idContrat;

    @Column(nullable = false)
    private LocalDate dateSignature;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTotal;

    private boolean valide;

    // Contrat 1 ---- 1 Reservation (cote proprietaire)
    @OneToOne
    @JoinColumn(name = "reservation_id", unique = true, nullable = false)
    private Reservation reservation;

    // Contrat 1 <>---- * Paiement : COMPOSITION
    // un paiement n'existe pas sans son contrat -> cascade ALL + orphanRemoval
    @OneToMany(mappedBy = "contrat", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("contrat-paiements")
    private List<Paiement> paiements = new ArrayList<>();
}
