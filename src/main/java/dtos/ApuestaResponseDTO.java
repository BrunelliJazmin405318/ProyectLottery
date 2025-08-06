package dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApuestaResponseDTO {
    private Long idSorteo;
    private LocalDate fechaSorteo;
    private String idCliente;
    private String numero;
    private String resultado;

}
