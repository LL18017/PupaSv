package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.OrdenBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Orden;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Orden;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

class OrdenResourceTest {
    List<Orden> TEST_C = Arrays.asList(
            new Orden(1L),
            new Orden(2L),
            new Orden(3L),
            new Orden(4L),
            new Orden(5L),
            new Orden(6L));

    OrdenResource cut; // Clase bajo prueba
    OrdenBean mockC;
    PersistenceException causaPe = new PersistenceException("eroor desde test");
    PersistenceException persistenceExcepcion = new PersistenceException("Error al consultar", causaPe);

    EntityNotFoundException causaEe = new EntityNotFoundException("eroor desde test");
    EntityNotFoundException entityNotFoundException = new EntityNotFoundException("Error al consultar", causaEe);


    @Test
    void findRange() {
        System.out.println("Orden test findRange");
        cut = new OrdenResource();
        mockC = Mockito.mock(OrdenBean.class);

        cut.oBean= mockC;
        Integer first=0;
        Integer max=10;

        //flujo normal
        Mockito.when(mockC.findRange(first, max)).thenReturn(TEST_C);
        Mockito.when(mockC.count()).thenReturn((long) TEST_C.size());
        Response response = cut.findRange(first, max);
        Assertions.assertEquals(200, response.getStatus());
        Mockito.verify(mockC).findRange(first, max);
        Mockito.verify(mockC).count();

        //excepciones

        Mockito.reset(mockC);
        Mockito.when(mockC.findRange(first, max)).thenThrow(persistenceExcepcion);
        response = cut.findRange(first, max);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }


    @Test
    void findById() {
        System.out.println("Orden test findById");
        cut = new OrdenResource();
        mockC = Mockito.mock(OrdenBean.class);
        Long id = 1L;
        cut.oBean= mockC;
        Integer first=0;
        Integer max=10;
        //flujo normal
        Mockito.when(mockC.findById(id)).thenReturn(TEST_C.stream().filter(p->p.getIdOrden().equals(id)).findFirst().get());
        Response response = cut.findById(id);
        Assertions.assertEquals(200, response.getStatus());
        //excepciones
        Mockito.reset(mockC);
        Mockito.when(mockC.findById(2L)).thenThrow(entityNotFoundException);
        response = cut.findById(2L);
        Assertions.assertEquals(404, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void create() {
        System.out.println("Orden test create");
        cut = new OrdenResource();
        mockC = Mockito.mock(OrdenBean.class);
        cut.oBean= mockC;
        UriInfo mockUriInfo = Mockito.mock(UriInfo.class);
        UriBuilder mockUriBuilder = Mockito.mock(UriBuilder.class);
        Orden tp = new Orden();
        tp.setIdOrden(1L);
        tp.setSucursal("Test");
        //flujo normal
        URI uri = URI.create("http://localhost:8080/api/Orden/1");
        Mockito.when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.path(String.valueOf(tp.getIdOrden()))).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.build()).thenReturn(uri);
        Mockito.doNothing().when(mockC).create(tp);
        Response response = cut.create(tp, mockUriInfo);
        Assertions.assertEquals(201, response.getStatus());
        //excepciones
        Mockito.reset(mockC);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockC).create(tp);
        response = cut.create(tp, mockUriInfo);
        Assertions.assertEquals(500, response.getStatus());



        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void delete() {
        System.out.println("Orden test delete");
        cut = new OrdenResource();
        mockC = Mockito.mock(OrdenBean.class);
        cut.oBean= mockC;
        Long id=1L;
        //flujo normal
        Mockito.doNothing().when(mockC).delete(id);
        Response response = cut.delete(id);
        Assertions.assertEquals(200, response.getStatus());
        response = cut.delete(id);
        //excepciones
        Mockito.reset(mockC);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockC).delete(id);
        response = cut.delete(id);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void update() {
        System.out.println("Orden test update");
        cut = new OrdenResource();
        mockC = Mockito.mock(OrdenBean.class);
        cut.oBean= mockC;
        Long id=1L;
        UriInfo mockUriInfo = Mockito.mock(UriInfo.class);

        UriBuilder mockUriBuilder = Mockito.mock(UriBuilder.class);
        Orden tp = new Orden();
        tp.setIdOrden(1L);
        tp.setSucursal("Test");
        //flujo normal
        URI uri = URI.create("http://localhost:8080/api/Orden/1");
        Mockito.when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.path(String.valueOf(tp.getIdOrden()))).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.build()).thenReturn(uri);
        Mockito.when(mockC.update(tp,id)).thenReturn(tp);
        Response response = cut.update(tp,id, mockUriInfo);
        Assertions.assertEquals(200, response.getStatus());
        //excepciones
        Mockito.reset(mockC);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockC).update(tp,id);
        response = cut.update(tp,id, mockUriInfo);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }
}