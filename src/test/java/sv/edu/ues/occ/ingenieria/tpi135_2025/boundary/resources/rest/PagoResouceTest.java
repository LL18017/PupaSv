package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.PagoBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Orden;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Pago;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PagoResouceTest {
    List<Pago> TEST_TP = Arrays.asList(
            new Pago(1L),
            new Pago(2L),
            new Pago(3L),
            new Pago(4L),
            new Pago(5L),
            new Pago(6L));

    PagoResouce cut; // Clase bajo prueba
    PagoBean mockP;
    PersistenceException causaPe = new PersistenceException("eroor desde test");
    PersistenceException persistenceExcepcion = new PersistenceException("Error al consultar", causaPe);

    EntityNotFoundException causaEe = new EntityNotFoundException("eroor desde test");
    EntityNotFoundException entityNotFoundException = new EntityNotFoundException("Error al consultar", causaEe);


    @Test
    void findRange() {
        System.out.println("Pago test findRange");
        cut = new PagoResouce();
        mockP = Mockito.mock(PagoBean.class);

        cut.pBean= mockP;
        Integer first=0;
        Integer max=10;

        //id cero y activo null
        Mockito.when(mockP.findRange(first, max)).thenReturn(TEST_TP);
        Mockito.when(mockP.count()).thenReturn((long) TEST_TP.size());
        Response response = cut.findRange(first, max);
        Assertions.assertEquals(200, response.getStatus());
        Mockito.verify(mockP).findRange(first, max);
        Mockito.verify(mockP).count();

        //excepciones

        Mockito.reset(mockP);
        Mockito.when(mockP.findRange(first, max)).thenThrow(persistenceExcepcion);
        response = cut.findRange(first, max);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }
    @Test
    void findRangeByIdOrden() {
        System.out.println("Pago test findRange");
        cut = new PagoResouce();
        mockP = Mockito.mock(PagoBean.class);

        cut.pBean= mockP;
        Integer first=0;
        Integer max=10;
        Long idOrden=2L;

        //id cero y activo null
        Mockito.when(mockP.findByIdOrden(idOrden,first, max)).thenReturn(TEST_TP);
        Mockito.when(mockP.countByIdOrden(idOrden)).thenReturn((long) TEST_TP.size());
        Response response = cut.findRangeByIdOrden(first, max,idOrden);
        Assertions.assertEquals(200, response.getStatus());
        Mockito.verify(mockP).findByIdOrden(idOrden,first, max);
        Mockito.verify(mockP).countByIdOrden(idOrden);

        //excepciones

        Mockito.reset(mockP);
        Mockito.when(mockP.findByIdOrden(idOrden,first, max)).thenThrow(persistenceExcepcion);
        response = cut.findRangeByIdOrden(first, max,idOrden);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void findById() {
        System.out.println("Pago test findById");
        cut = new PagoResouce();
        mockP = Mockito.mock(PagoBean.class);
        Long id = 1L;
        cut.pBean= mockP;
        Integer first=0;
        Integer max=10;
        //flujo normal
        Mockito.when(mockP.findById(id)).thenReturn(TEST_TP.stream().filter(p->p.getIdPago().equals(id)).findFirst().get());
        Response response = cut.findById(id);
        Assertions.assertEquals(200, response.getStatus());
        //excepciones
        Mockito.reset(mockP);
        Mockito.when(mockP.findById(2L)).thenThrow(entityNotFoundException);
        response = cut.findById(2L);
        Assertions.assertEquals(404, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void create() {
        System.out.println("Pago test create");
        cut = new PagoResouce();
        mockP = Mockito.mock(PagoBean.class);
        cut.pBean= mockP;
        UriInfo mockUriInfo = Mockito.mock(UriInfo.class);
        UriBuilder mockUriBuilder = Mockito.mock(UriBuilder.class);
        Pago tp = new Pago();
        tp.setIdPago(1L);
        tp.setIdOrden(new Orden(1L));
        tp.setMetodoPago("test");
        //flujo normal
        URI uri = URI.create("http://localhost:8080/api/pago/1");
        Mockito.when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.path(String.valueOf(tp.getIdPago()))).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.build()).thenReturn(uri);
        Mockito.doNothing().when(mockP).create(tp);
        Response response = cut.create(tp, mockUriInfo);
        Assertions.assertEquals(201, response.getStatus());
        //excepciones
        Mockito.reset(mockP);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockP).create(tp);
        response = cut.create(tp, mockUriInfo);
        Assertions.assertEquals(500, response.getStatus());
        tp.setIdOrden(null);
        response = cut.create(tp, mockUriInfo);
        Assertions.assertEquals(500, response.getStatus());



        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void delete() {
        System.out.println("Pago test delete");
        cut = new PagoResouce();
        mockP = Mockito.mock(PagoBean.class);
        cut.pBean= mockP;
        Long id=1L;
        //flujo normal
        Mockito.doNothing().when(mockP).delete(id);
        Response response = cut.delete(id);
        Assertions.assertEquals(200, response.getStatus());
        response = cut.delete(id);
        //excepciones
        Mockito.reset(mockP);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockP).delete(id);
        response = cut.delete(id);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void update() {
        System.out.println("Pago test update");
        cut = new PagoResouce();
        mockP = Mockito.mock(PagoBean.class);
        cut.pBean= mockP;
        Long id=1L;
        UriInfo mockUriInfo = Mockito.mock(UriInfo.class);

        UriBuilder mockUriBuilder = Mockito.mock(UriBuilder.class);
        Pago tp = new Pago();
        tp.setIdPago(1L);
        tp.setMetodoPago("Test");
        //flujo normal
        URI uri = URI.create("http://localhost:8080/api/pago/1");
        Mockito.when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.path(String.valueOf(tp.getIdPago()))).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.build()).thenReturn(uri);
        Mockito.when(mockP.update(tp,id)).thenReturn(tp);
        Response response = cut.update(tp,id, mockUriInfo);
        Assertions.assertEquals(200, response.getStatus());
        //excepciones
        Mockito.reset(mockP);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockP).update(tp,id);
        response = cut.update(tp,id, mockUriInfo);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }
}