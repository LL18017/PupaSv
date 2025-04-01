package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLOutput;

import static org.junit.jupiter.api.Assertions.*;

class ResourceTest {

    @Test
    void verificarId() {
        System.out.println("test verificarId");
        Integer idINT = 10;
        Long idLong = 10L;
        int esperado=200;
        Resource cut=new Resource();


        //todo perfecto
        Response response = cut.verificarId(idINT, "idInt");
        assertEquals(esperado, response.getStatus());

       response = cut.verificarId(idLong, "idLong");
        assertEquals(esperado, response.getStatus());
        //fallo de argumentos

        //fallo de proceso
        response = cut.verificarId(null, "idInt");
        assertEquals(400, response.getStatus());

//        fail("fallo existosamente");
    }

    @Test
    void veririficarMaxAndFirst() {
        System.out.println("test verificarMaxAndFirst");
        Integer max = 10;
        Integer first = 0;
        int esperado=200;
        Resource cut=new Resource();


        //todo perfecto
        Response response = cut.veririficarMaxAndFirst(first, max);
        assertEquals(esperado, response.getStatus());
        //fallo de argumentos
        response = cut.veririficarMaxAndFirst(first, 90);
        assertEquals(400, response.getStatus());

        //fallo de proceso
        response = cut.veririficarMaxAndFirst(null, null);
        assertEquals(500, response.getStatus());

//        fail("fallo existosamente");
    }
}