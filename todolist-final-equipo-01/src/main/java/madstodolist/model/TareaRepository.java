package madstodolist.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface TareaRepository extends CrudRepository<Tarea, Long> {
    // List<Tarea> findByAsignado_id(Long id);
}
