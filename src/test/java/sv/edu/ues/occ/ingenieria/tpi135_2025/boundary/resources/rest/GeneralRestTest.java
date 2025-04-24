package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.json.bind.JsonbException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GeneralRestTest {

    @Test
    void responseExcepcions() {
        System.out.println("GeneralRest test responseExcepcions");
        GeneralRest cut = new GeneralRest();
        Long idError = 10L;
        PersistenceException causaPe = new PersistenceException("eroor desde test");
        PersistenceException persistenceExcepcion = new PersistenceException("Error al consultar", causaPe);
        PersistenceException persistenceExcepcion2 = new PersistenceException("No se encontro resultado para esto", causaPe);

        EntityNotFoundException causaEe = new EntityNotFoundException("eroor desde test");
        EntityNotFoundException entityNotFoundException = new EntityNotFoundException("Error al consultar", causaEe);

        IllegalArgumentException causaIa = new IllegalArgumentException("eroor desde test");
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Error al consultar", causaIa);

        NoResultException noResultException = new NoResultException("Error al consultar");

        Set<ConstraintViolation<?>> violations = Collections.emptySet(); // o tu conjunto real de violaciones
        ConstraintViolationException constraintViolationException = new ConstraintViolationException("Error al consultar", violations);

        IllegalStateException causaIE = new IllegalStateException("eroor desde test");
        IllegalStateException illegalStateException = new IllegalStateException("Error al consultar", causaIE);

        JsonbException causaJE = new JsonbException("eroor desde test");
        JsonbException jsonbException = new JsonbException("Error al consultar", causaJE);

        Exception causa = new Exception("eroor desde test");
        Exception exception = new Exception("Error al consultar", causa);

        Exception exception2 = new Exception("No se encontro resultado", causa);



       Response response = cut.responseExcepcions(illegalArgumentException, idError);
        assertEquals(400, response.getStatus());

        response = cut.responseExcepcions(noResultException, idError);
        assertEquals(404, response.getStatus());

        response = cut.responseExcepcions(entityNotFoundException, idError);
        assertEquals(404, response.getStatus());

        response = cut.responseExcepcions(illegalStateException, idError);
        assertEquals(500, response.getStatus());

        response = cut.responseExcepcions(constraintViolationException, idError);
        assertEquals(500, response.getStatus());

        response = cut.responseExcepcions(jsonbException, idError);
        assertEquals(500, response.getStatus());


        response = cut.responseExcepcions(exception, idError);
        assertEquals(500, response.getStatus());

        response = cut.responseExcepcions(exception2, idError);
        assertEquals(404, response.getStatus());

        response = cut.responseExcepcions(persistenceExcepcion, idError);
        assertEquals(500, response.getStatus());

        response = cut.responseExcepcions(persistenceExcepcion2, idError);
        assertEquals(404, response.getStatus());

//        fail("fallo existosamente");
    }
}