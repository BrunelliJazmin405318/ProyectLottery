package servicios;

import dtos.ApuestaCreateDTO;
import entidades.*;
import repositorios.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ApuestaService {
    private final ApuestaRepository apuestaRepository;
    private final SorteoRepository sorteoRepository;

    public ApuestaService(ApuestaRepository apuestaRepository, SorteoRepository sorteoRepository) {
        this.apuestaRepository = apuestaRepository;
        this.sorteoRepository = sorteoRepository;
    }

    public Apuesta registrarApuesta(ApuestaCreateDTO dto) {
        Optional<Sorteo> sorteoOpt = sorteoRepository.findByFechaSorteo(dto.getFecha_sorteo());
        if (sorteoOpt.isEmpty()) {
            throw new RuntimeException("No existe sorteo para la fecha " + dto.getFecha_sorteo());
        }
        Sorteo sorteo = sorteoOpt.get();


        Long maximoPermitido = sorteo.getTotalEnReserva() / 100;
        if (dto.getMontoApostado() > maximoPermitido) {
            throw new RuntimeException("Monto apostado supera el máximo permitido: " + maximoPermitido);
        }

        Apuesta apuesta = new Apuesta();
        apuesta.setIdCliente(dto.getId_cliente());
        apuesta.setNumero(dto.getNumero());
        apuesta.setMontoApostado(dto.getMontoApostado());
        apuesta.setSorteo(sorteo);
        apuesta.setPremio(0L);
        apuesta.setResultado("PERDEDOR"); // Por defecto


        if (sorteo.getNumerosSorteados() != null) {
            for (var numeroSorteado : sorteo.getNumerosSorteados()) {

                String numApostado = apuesta.getNumero();
                String numGanador = String.valueOf(numeroSorteado.getNumero());

                int aciertos = calcularAciertos(numApostado, numGanador);

                if (aciertos >= 2) {
                    apuesta.setResultado("GANADOR");

                    Long premio = calcularPremio(apuesta.getMontoApostado(), aciertos);
                    apuesta.setPremio(premio);
                    break;
                }
            }
        }


        return apuestaRepository.save(apuesta);
    }


    private int calcularAciertos(String apuesta, String ganador) {
        int aciertos = 0;
        int lenApuesta = apuesta.length();
        int lenGanador = ganador.length();


        while (aciertos < lenApuesta && aciertos < lenGanador) {
            if (apuesta.charAt(lenApuesta - 1 - aciertos) == ganador.charAt(lenGanador - 1 - aciertos)) {
                aciertos++;
            } else {
                break;
            }
        }
        return aciertos;
    }


    private Long calcularPremio(Long montoApostado, int aciertos) {
        switch (aciertos) {
            case 2:
                return montoApostado * 700L;
            case 3:
                return montoApostado * 7000L;
            case 4:
                return montoApostado * 60000L;
            case 5:
                return montoApostado * 350000L;
            default:
                return 0L;
        }
    }

    public List<Apuesta> obtenerApuestasGanadorasPorFecha(LocalDate fechaSorteo) {
        List<Apuesta> apuestas = apuestaRepository.findBySorteo_FechaSorteo(fechaSorteo);

        return apuestas.stream()
                .filter(apuesta -> "GANADOR".equalsIgnoreCase(apuesta.getResultado()))
                .toList();
    }
}
