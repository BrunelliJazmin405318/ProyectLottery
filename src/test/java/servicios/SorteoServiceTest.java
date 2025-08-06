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
import static org.mockito.Mockito.*;

public class SorteoServiceTest {

    @Mock
    private SorteoRepository sorteoRepository;

    @Mock
    private ApuestaRepository apuestaRepository;

    @InjectMocks
    private SorteoService sorteoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registrarSorteo_ActualizaApuestas() {
        LocalDate fecha = LocalDate.of(2025, 7, 22);

        NumeroSorteado ns1 = new NumeroSorteado();
        ns1.setOrden(0);
        ns1.setNumero("12345");

        NumeroSorteado ns2 = new NumeroSorteado();
        ns2.setOrden(1);
        ns2.setNumero("54321");

        List<NumeroSorteado> numeros = List.of(ns1, ns2);

        SorteoCreateDTO dto = SorteoCreateDTO.builder()
                .fechaSorteo(fecha)
                .numerosSorteados(numeros)
                .build();

        Sorteo sorteoGuardado = new Sorteo();
        sorteoGuardado.setId(1L);
        sorteoGuardado.setFechaSorteo(fecha);
        sorteoGuardado.setNumerosSorteados(numeros);
        sorteoGuardado.setTotalEnReserva(100000L);

        Apuesta apuesta1 = new Apuesta();
        apuesta1.setNumero("12345"); // gana - índice 0
        apuesta1.setResultado(null);

        Apuesta apuesta2 = new Apuesta();
        apuesta2.setNumero("99999"); // pierde
        apuesta2.setResultado(null);

        List<Apuesta> apuestas = new ArrayList<>();
        apuestas.add(apuesta1);
        apuestas.add(apuesta2);

        when(sorteoRepository.save(any(Sorteo.class))).thenReturn(sorteoGuardado);
        when(apuestaRepository.findBySorteo_FechaSorteo(fecha)).thenReturn(apuestas);
        when(apuestaRepository.save(any(Apuesta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Sorteo resultado = sorteoService.registrarSorteo(fecha, dto);

        assertNotNull(resultado);
        assertEquals(fecha, resultado.getFechaSorteo());

        assertEquals("GANADOR", apuesta1.getResultado());
        assertTrue(apuesta1.getPremio() > 0);


        assertEquals("PERDEDOR", apuesta2.getResultado());
        assertEquals(0L, apuesta2.getPremio());


        long esperadoReserva = 100000L - apuesta1.getPremio();
        assertEquals(esperadoReserva, resultado.getTotalEnReserva());
    }

    @Test
    void consultarPorFecha_SorteoExiste_RetornaSorteo() {
        LocalDate fecha = LocalDate.of(2025, 7, 22);
        Sorteo sorteo = new Sorteo();
        sorteo.setId(1L);
        sorteo.setFechaSorteo(fecha);

        when(sorteoRepository.findByFechaSorteo(fecha)).thenReturn(Optional.of(sorteo));

        Sorteo resultado = sorteoService.consultarPorFecha(fecha);

        assertNotNull(resultado);
        assertEquals(sorteo.getId(), resultado.getId());
    }

    @Test
    void consultarPorFecha_SorteoNoExiste_LanzaExcepcion() {
        LocalDate fecha = LocalDate.of(2025, 7, 22);
        when(sorteoRepository.findByFechaSorteo(fecha)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            sorteoService.consultarPorFecha(fecha);
        });

        assertEquals("No existe sorteo para la fecha " + fecha, ex.getMessage());
    }

    @Test
    void obtenerSorteoConApuestas_Existe_RetornaSorteo() {
        Sorteo sorteo = new Sorteo();
        sorteo.setId(1L);

        when(sorteoRepository.findById(1L)).thenReturn(Optional.of(sorteo));

        Sorteo resultado = sorteoService.obtenerSorteoConApuestas(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void obtenerSorteoConApuestas_NoExiste_LanzaExcepcion() {
        when(sorteoRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            sorteoService.obtenerSorteoConApuestas(1L);
        });

        assertEquals("No existe sorteo con id 1", ex.getMessage());
    }

    @Test
    void obtenerTotalesPorSorteo_RetornaDTO() {
        LocalDate fecha = LocalDate.of(2025, 7, 22);

        Sorteo sorteo = new Sorteo();
        sorteo.setId(1L);
        sorteo.setFechaSorteo(fecha);
        sorteo.setTotalEnReserva(100000L);

        Apuesta apuesta1 = new Apuesta();
        apuesta1.setPremio(1000L);

        Apuesta apuesta2 = new Apuesta();
        apuesta2.setPremio(2000L);

        List<Apuesta> apuestas = List.of(apuesta1, apuesta2);


        Sorteo spySorteo = spy(sorteo);
        doReturn(apuestas).when(spySorteo).getApuestas();

        when(sorteoRepository.findByFechaSorteo(fecha)).thenReturn(Optional.of(spySorteo));

        TotalApuestasDTO dto = sorteoService.obtenerTotalesPorSorteo(fecha);

        assertNotNull(dto);
        assertEquals(1L, dto.getIdSorteo());
        assertEquals(fecha, dto.getFechaSorteo());
        assertEquals(2, dto.getTotalDeApuestas());
        assertEquals(3000L, dto.getTotalPagado());
        assertEquals(100000L, dto.getTotalEnReserva());
    }

    @Test
    void obtenerApuestasPorFecha_DevuelveLista() {
        LocalDate fecha = LocalDate.of(2025, 7, 22);

        Apuesta a1 = new Apuesta();
        Apuesta a2 = new Apuesta();

        List<Apuesta> apuestas = List.of(a1, a2);

        when(apuestaRepository.findBySorteo_FechaSorteo(fecha)).thenReturn(apuestas);

        List<Apuesta> resultado = sorteoService.obtenerApuestasPorFecha(fecha);

        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(a1));
        assertTrue(resultado.contains(a2));
    }
}


