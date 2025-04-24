package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.PagoDetalleBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Pago;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.PagoDetalle;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

class PagoDetalleResourceTest {
    List<PagoDetalle> TEST_C = Arrays.asList(
            new PagoDetalle(1L),
            new PagoDetalle(2L),
            new PagoDetalle(3L),
            new PagoDetalle(4L),
            new PagoDetalle(5L),
            new PagoDetalle(6L));

    PagoDetalleResource cut; // Clase bajo prueba
    PagoDetalleBean mockPd;
    PersistenceException causaPe = new PersistenceException("eroor desde test");
    PersistenceException persistenceExcepcion = new PersistenceException("Error al consultar", causaPe);

    EntityNotFoundException causaEe = new EntityNotFoundException("eroor desde test");
    EntityNotFoundException entityNotFoundException = new EntityNotFoundException("Error al consultar", causaEe);
    @Test
    void findRange() {
        System.out.println("PagoDetalle test findRange");
        cut = new PagoDetalleResource();
        mockPd = Mockito.mock(PagoDetalleBean.class);

        cut.pdBean= mockPd;
        Integer first=0;
        Integer max=10;
        Long id=1L;

        //flujo normal
        Mockito.when(mockPd.findRange(first, max)).thenReturn(TEST_C);
        Mockito.when(mockPd.count()).thenReturn((long) TEST_C.size());
        Response response = cut.findRange(first, max,null);
        Assertions.assertEquals(200, response.getStatus());
        Mockito.verify(mockPd).findRange(first, max);
        Mockito.verify(mockPd).count();

        Mockito.when(mockPd.findRangeByIdPago(id,first, max)).thenReturn(TEST_C);
        Mockito.when(mockPd.countByIdPago(id)).thenReturn((long) TEST_C.size());
        response = cut.findRange(first, max,id);
        Assertions.assertEquals(200, response.getStatus());
        Mockito.verify(mockPd).findRange(first, max);
        Mockito.verify(mockPd).count();

        //excepciones

        Mockito.reset(mockPd);
        Mockito.when(mockPd.findRange(first, max)).thenThrow(persistenceExcepcion);
        response = cut.findRange(first, max,null);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }


    @Test
    void findById() {
        System.out.println("PagoDetalle test findById");
        cut = new PagoDetalleResource();
        mockPd = Mockito.mock(PagoDetalleBean.class);
        Long idPagoDetalle = 1L;
        Integer idTipoPago = 1;
        cut.pdBean= mockPd;
        //flujo normal
        Mockito.when(mockPd.findById(idPagoDetalle)).thenReturn(TEST_C.get(0));
        Response response = cut.findById(idPagoDetalle);
        Assertions.assertEquals(200, response.getStatus());
        //excepciones
        Mockito.reset(mockPd);
        Mockito.when(mockPd.findById(idPagoDetalle)).thenThrow(entityNotFoundException);
        response = cut.findById(idPagoDetalle);
        Assertions.assertEquals(404, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void create() {
        System.out.println("PagoDetalle test create");
        cut = new PagoDetalleResource();
        mockPd = Mockito.mock(PagoDetalleBean.class);
        EntityManager mockEm=Mockito.mock(EntityManager.class);
        cut.pdBean= mockPd;

        Long idPago = 1L;
        Integer idTipoPago = 1;
        UriInfo mockUriInfo = Mockito.mock(UriInfo.class);
        UriBuilder mockUriBuilder = Mockito.mock(UriBuilder.class);
        PagoDetalle tp = new PagoDetalle();
        tp.setObservaciones("Test");

        //flujo normal
        URI uri = URI.create("http://localhost:8080/api/PagoDetalle/1");
        Mockito.when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.build()).thenReturn(uri);
        Mockito.doNothing().when(mockPd).create(tp);
        Mockito.when(mockPd.getEntityManager()).thenReturn(mockEm);
        Mockito.when(mockEm.find(Pago.class, idPago)).thenReturn(new Pago(idPago));
        Response response = cut.create(tp,mockUriInfo,idPago);
        Assertions.assertEquals(201, response.getStatus());

        //id nulo
         response = cut.create(tp,mockUriInfo,null);
        Assertions.assertEquals(400, response.getStatus());
        //entidda no existe
        Mockito.when(mockEm.find(Pago.class, 112233L)).thenReturn(null);
        response = cut.create(tp,mockUriInfo,112233L);
        Assertions.assertEquals(404, response.getStatus());



        //excepciones
        Mockito.reset(mockPd);
        Mockito.when(mockPd.getEntityManager()).thenReturn(mockEm);
        Mockito.when(mockEm.find(Pago.class, idPago)).thenReturn(new Pago(idPago));
        Mockito.doThrow(persistenceExcepcion)
                .when(mockPd).create(tp);
        response = cut.create(tp,mockUriInfo,idPago);
        Assertions.assertEquals(500, response.getStatus());
        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void delete() {
        System.out.println("PagoDetalle test delete");
        cut = new PagoDetalleResource();
        mockPd = Mockito.mock(PagoDetalleBean.class);
        cut.pdBean= mockPd;
        Long id=1L;
        Integer idTipoPago = 1;
        //flujo normal
        Mockito.doNothing().when(mockPd).delete(id);
        Response response = cut.delete(id);
        Assertions.assertEquals(200, response.getStatus());
        response = cut.delete(id);
        //excepciones
        Mockito.reset(mockPd);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockPd).delete(id);
        response = cut.delete(id);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void update() {
        System.out.println("PagoDetalle test update");
        cut = new PagoDetalleResource();
        mockPd = Mockito.mock(PagoDetalleBean.class);
        cut.pdBean= mockPd;
        Long id=1L;
        Integer idTipoPago = 1;
        UriInfo mockUriInfo = Mockito.mock(UriInfo.class);

        UriBuilder mockUriBuilder = Mockito.mock(UriBuilder.class);
        PagoDetalle tp = new PagoDetalle();
        tp.setObservaciones("Test");
        //flujo normal
        URI uri = URI.create("http://localhost:8080/api/PagoDetalle/1");
        Mockito.when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.build()).thenReturn(uri);
        Mockito.when(mockPd.update(tp,id)).thenReturn(tp);
        Response response = cut.update(tp, mockUriInfo,id);
        Assertions.assertEquals(200, response.getStatus());
        //excepciones
        Mockito.reset(mockPd);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockPd).update(tp,id);
        response = cut.update(tp, mockUriInfo,id);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }
}