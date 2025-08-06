package dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApuestaCreateDTO {
    private LocalDate fecha_sorteo;
    private String id_cliente;
    private String numero;
    private Long montoApostado;
}
