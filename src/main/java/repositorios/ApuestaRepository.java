package repositorios;

import entidades.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ApuestaRepository extends JpaRepository<Apuesta, Long> {
    List<Apuesta> findBySorteo(Sorteo sorteo);
    List<Apuesta> findBySorteo_FechaSorteo(LocalDate fechaSorteo);

}
