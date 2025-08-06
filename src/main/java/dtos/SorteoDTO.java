package dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SorteoDTO {
    private Long idSorteo;
    private LocalDate fechaSorteo;
    private Double totalEnReserva;
    private List<NumeroSorteadoDTO> numerosSorteados;
}
