package dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApuestaDTO {
    private Long idSorteo;
    private LocalDate fechaSorteo;
    private String idCliente;
    private String numero;
    private String resultado;
}

