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
public class TotalApuestasDTO {
    private Long idSorteo;
    private LocalDate fechaSorteo;
    private Long totalDeApuestas;  // Puede ser Integer si querés
    private Long totalPagado;
    private Long totalEnReserva;
}
