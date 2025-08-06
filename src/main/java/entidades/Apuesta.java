package entidades;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "apuestas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Apuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String idCliente;

    private String numero;

    private Long montoApostado;

    private String resultado;

    private Long premio;

    @ManyToOne
    @JoinColumn(name = "sorteo_id")
    private Sorteo sorteo;
}
