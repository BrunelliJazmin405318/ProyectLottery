package controllers;

import dtos.*;
import entidades.*;
import servicios.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loteria/apuesta")
public class ApuestaController {
    private final ApuestaService apuestaService;

    public ApuestaController(ApuestaService apuestaService) {
        this.apuestaService = apuestaService;
    }

    @PostMapping
    public ResponseEntity<ApuestaResponseDTO> registrarApuesta(@RequestBody ApuestaCreateDTO dto) {
        Apuesta apuesta = apuestaService.registrarApuesta(dto);

        ApuestaResponseDTO responseDTO = ApuestaResponseDTO.builder()
                .idSorteo(apuesta.getSorteo().getId())
                .fechaSorteo(apuesta.getSorteo().getFechaSorteo())
                .idCliente(apuesta.getIdCliente())
                .numero(apuesta.getNumero())
                .resultado(apuesta.getResultado())
                .build();

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
}
