package entidades;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sorteo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaSorteo;

    @OneToMany
    private List<NumeroSorteado> numerosSorteados = new ArrayList<>();

    @OneToMany
    private List<Apuesta> apuestas = new ArrayList<>();

    private Long totalEnReserva;
}
