package servicios;

import dtos.*;
import entidades.*;
import repositorios.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SorteoService {

    private final SorteoRepository sorteoRepository;
    private final ApuestaRepository apuestaRepository;

    public SorteoService(SorteoRepository sorteoRepository , ApuestaRepository apuestaRepository) {
        this.sorteoRepository = sorteoRepository;
        this.apuestaRepository = apuestaRepository;
    }

    public Sorteo registrarSorteo(LocalDate fecha, SorteoCreateDTO dto) {
        Sorteo sorteo = new Sorteo();
        sorteo.setFechaSorteo(dto.getFechaSorteo());

        List<NumeroSorteado> numerosSorteados = dto.getNumerosSorteados().stream()
                .map(nsDto -> {
                    NumeroSorteado ns = new NumeroSorteado();
                    ns.setOrden(nsDto.getOrden());
                    ns.setNumero(nsDto.getNumero());
                    return ns;
                })
                .toList();

        sorteo.setNumerosSorteados(numerosSorteados);
        sorteo.setTotalEnReserva(0L);

        sorteo = sorteoRepository.save(sorteo);

        // Buscar apuestas para esta fecha
        List<Apuesta> apuestas = apuestaRepository.findBySorteo_FechaSorteo(fecha);

        for (Apuesta apuesta : apuestas) {
            int index = -1;

            for (int i = 0; i < sorteo.getNumerosSorteados().size(); i++) {
                if (sorteo.getNumerosSorteados().get(i).getNumero().equals(apuesta.getNumero())) {
                    index = i;
                    break;
                }
            }

            if (index != -1) {
                apuesta.setResultado("GANADOR");

                long porcentaje;
                if (index == 0) porcentaje = 50;
                else if (index == 1) porcentaje = 30;
                else porcentaje = 20;

                long premio = (sorteo.getTotalEnReserva() * porcentaje) / 100;

                apuesta.setPremio(premio);

                sorteo.setTotalEnReserva(sorteo.getTotalEnReserva() - premio);
            } else {
                apuesta.setResultado("PERDEDOR");
                apuesta.setPremio(0L);
            }

            apuestaRepository.save(apuesta);
        }

        return sorteo;
    }



    public Sorteo consultarPorFecha(LocalDate fecha) {
        Optional<Sorteo> sorteoOpt = sorteoRepository.findByFechaSorteo(fecha);
        if (sorteoOpt.isPresent()) {
            return sorteoOpt.get();
        } else {
            throw new RuntimeException("No existe sorteo para la fecha " + fecha);
        }
    }
    public Sorteo obtenerSorteoConApuestas(Long idSorteo) {
        Optional<Sorteo> sorteoOpt = sorteoRepository.findById(idSorteo);
        if (sorteoOpt.isEmpty()) {
            throw new RuntimeException("No existe sorteo con id " + idSorteo);
        }
        return sorteoOpt.get();
    }
    public TotalApuestasDTO obtenerTotalesPorSorteo(LocalDate fecha) {
        Sorteo sorteo = sorteoRepository.findByFechaSorteo(fecha)
                .orElseThrow(() -> new RuntimeException("No existe sorteo para la fecha " + fecha));

        List<Apuesta> apuestas = sorteo.getApuestas();

        long totalDeApuestas = apuestas.size();

        long totalPagado = apuestas.stream()
                .mapToLong(apuesta -> apuesta.getPremio() != null ? apuesta.getPremio() : 0L)
                .sum();

        Long totalEnReserva = sorteo.getTotalEnReserva();

        return TotalApuestasDTO.builder()
                .idSorteo(sorteo.getId())
                .fechaSorteo(sorteo.getFechaSorteo())
                .totalDeApuestas(totalDeApuestas)
                .totalPagado(totalPagado)
                .totalEnReserva(totalEnReserva)
                .build();
    }
    public List<Apuesta> obtenerApuestasPorFecha(LocalDate fechaSorteo) {
        return apuestaRepository.findBySorteo_FechaSorteo(fechaSorteo);
    }
}
