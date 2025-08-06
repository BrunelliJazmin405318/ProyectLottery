package servicios;

import dtos.*;
import entidades.*;
import repositorios.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class ApuestaServiceTest {

    @Mock
    private ApuestaRepository apuestaRepository;

    @Mock
    private SorteoRepository sorteoRepository;

    @InjectMocks
    private ApuestaService apuestaService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void registrarApuesta_SorteoNoExiste_LanzaExcepcion() {
        LocalDate fecha = LocalDate.of(2025, 7, 16);
        ApuestaCreateDTO dto = new ApuestaCreateDTO();
        dto.setFecha_sorteo(fecha);

        when(sorteoRepository.findByFechaSorteo(fecha)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            apuestaService.registrarApuesta(dto);
        });

        assertEquals("No existe sorteo para la fecha " + fecha, ex.getMessage());
    }

    @Test
    public void registrarApuesta_MontoExcedeMaximo_LanzaExcepcion() {
        LocalDate fecha = LocalDate.of(2025, 7, 16);
        ApuestaCreateDTO dto = new ApuestaCreateDTO();
        dto.setFecha_sorteo(fecha);
        dto.setMontoApostado(200L);

        Sorteo sorteo = new Sorteo();
        sorteo.setTotalEnReserva(10000L); // 1% = 100
        when(sorteoRepository.findByFechaSorteo(fecha)).thenReturn(Optional.of(sorteo));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            apuestaService.registrarApuesta(dto);
        });

        assertTrue(ex.getMessage().contains("Monto apostado supera el máximo"));
    }

    @Test
    public void registrarApuesta_Ganadora() {
        LocalDate fecha = LocalDate.of(2025, 7, 16);
        ApuestaCreateDTO dto = new ApuestaCreateDTO();
        dto.setFecha_sorteo(fecha);
        dto.setId_cliente("Cliente1");
        dto.setNumero("56789");
        dto.setMontoApostado(10L);


        Sorteo sorteo = new Sorteo();
        sorteo.setTotalEnReserva(100000L);

        NumeroSorteado ns = new NumeroSorteado();
        ns.setOrden(1);
        ns.setNumero("56789"); // mismo número

        List<NumeroSorteado> numeros = new ArrayList<>();
        numeros.add(ns);
        sorteo.setNumerosSorteados(numeros);

        when(sorteoRepository.findByFechaSorteo(fecha)).thenReturn(Optional.of(sorteo));
        when(apuestaRepository.save(any(Apuesta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Apuesta resultado = apuestaService.registrarApuesta(dto);

        assertNotNull(resultado);
        assertEquals("GANADOR", resultado.getResultado());
        assertTrue(resultado.getPremio() > 0);
        assertEquals(dto.getId_cliente(), resultado.getIdCliente());
        assertEquals(dto.getNumero(), resultado.getNumero());
        assertEquals(dto.getMontoApostado(), resultado.getMontoApostado());
    }

    @Test
    public void registrarApuesta_Perdedora() {
        LocalDate fecha = LocalDate.of(2025, 7, 16);
        ApuestaCreateDTO dto = new ApuestaCreateDTO();
        dto.setFecha_sorteo(fecha);
        dto.setId_cliente("Cliente2");
        dto.setNumero("12345");
        dto.setMontoApostado(10L);

        // Sorteo con numeros sorteados diferentes para no coincidir
        Sorteo sorteo = new Sorteo();
        sorteo.setTotalEnReserva(100000L);

        NumeroSorteado ns = new NumeroSorteado();
        ns.setOrden(1);
        ns.setNumero("99999");

        List<NumeroSorteado> numeros = new ArrayList<>();
        numeros.add(ns);
        sorteo.setNumerosSorteados(numeros);

        when(sorteoRepository.findByFechaSorteo(fecha)).thenReturn(Optional.of(sorteo));
        when(apuestaRepository.save(any(Apuesta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Apuesta resultado = apuestaService.registrarApuesta(dto);

        assertNotNull(resultado);
        assertEquals("PERDEDOR", resultado.getResultado());
        assertEquals(0L, resultado.getPremio());
    }

    @Test
    public void obtenerApuestasGanadorasPorFecha_FiltraSoloGanadoras() {
        LocalDate fecha = LocalDate.of(2025, 7, 16);

        Apuesta ganadora = new Apuesta();
        ganadora.setResultado("GANADOR");

        Apuesta perdedora = new Apuesta();
        perdedora.setResultado("PERDEDOR");

        List<Apuesta> todas = List.of(ganadora, perdedora);

        when(apuestaRepository.findBySorteo_FechaSorteo(fecha)).thenReturn(todas);

        List<Apuesta> resultado = apuestaService.obtenerApuestasGanadorasPorFecha(fecha);

        assertEquals(1, resultado.size());
        assertEquals("GANADOR", resultado.get(0).getResultado());
    }
}

