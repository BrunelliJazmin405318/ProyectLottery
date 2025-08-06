package repositorios;
import entidades.Sorteo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SorteoRepository extends JpaRepository<Sorteo, Long> {
    Optional<Sorteo> findByFechaSorteo(LocalDate fechaSorteo);
}
