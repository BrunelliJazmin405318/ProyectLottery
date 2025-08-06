package controllers;

import dtos.*;
import entidades.*;
import servicios.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/sorteo")
public class SorteoController {

    private final SorteoService sorteoService;
    private final ApuestaService apuestaService;

    public SorteoController(SorteoService sorteoService, ApuestaService apuestaService) {
        this.sorteoService = sorteoService;
        this.apuestaService= apuestaService;
    }

    @PostMapping
    public ResponseEntity<Sorteo> registrar(
            @RequestParam LocalDate fecha,
            @RequestBody SorteoCreateDTO dto) {
        Sorteo sorteo = sorteoService.registrarSorteo(fecha, dto);
        return new ResponseEntity<>(sorteo, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Sorteo> consultar(@RequestParam LocalDate fecha) {
        Sorteo sorteo = sorteoService.consultarPorFecha(fecha);
        return ResponseEntity.ok(sorteo);
    }

    @GetMapping("/loteria/sorteo/{id_sorteo}")
    public ResponseEntity<Sorteo> obtenerSorteoConApuestas(@PathVariable("id_sorteo") Long idSorteo) {
        Sorteo sorteo = sorteoService.obtenerSorteoConApuestas(idSorteo);
        return ResponseEntity.ok(sorteo);
    }
    @GetMapping("/totales")
    public ResponseEntity<TotalApuestasDTO> obtenerTotales(@RequestParam LocalDate fecha) {
        TotalApuestasDTO dto = sorteoService.obtenerTotalesPorSorteo(fecha);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/apuestas-ganadoras")
    public ResponseEntity<List<Apuesta>> obtenerApuestasGanadoras(@RequestParam LocalDate fecha) {
        List<Apuesta> ganadoras = apuestaService.obtenerApuestasGanadorasPorFecha(fecha);
        return ResponseEntity.ok(ganadoras);
    }
}

