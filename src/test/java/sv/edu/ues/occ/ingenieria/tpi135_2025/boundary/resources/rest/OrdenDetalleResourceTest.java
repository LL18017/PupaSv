package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest.plantillas.DatosMixtosDTO;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.OrdenDetalleBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.OrdenDetalle;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrdenDetalleResourceTest {

    List<OrdenDetalle> TEST_TP = Arrays.asList(
            new OrdenDetalle(1L, 1L),
            new OrdenDetalle(1L, 2L),
            new OrdenDetalle(1L, 3L),
            new OrdenDetalle(1L, 4L),
            new OrdenDetalle(1L, 5L),
            new OrdenDetalle(1L, 6L));

    OrdenDetalleResource cut; // Clase bajo prueba
    OrdenDetalleBean mockOd;
    PersistenceException causaPe = new PersistenceException("eroor desde test");
    PersistenceException persistenceExcepcion = new PersistenceException("Error al consultar", causaPe);

    EntityNotFoundException causaEe = new EntityNotFoundException("eroor desde test");
    EntityNotFoundException entityNotFoundException = new EntityNotFoundException("Error al consultar", causaEe);


    @Test
    void testFindRangeByIdOrden() {
        System.out.println("OrdenDetalle test findRange");
        cut = new OrdenDetalleResource();
        mockOd = Mockito.mock(OrdenDetalleBean.class);

        cut.odBean = mockOd;
        Integer first = 0;
        Integer max = 10;
        Long idOrden = 2L;

        //id cero y activo null
        Mockito.when(mockOd.findRangeByIdOrden(idOrden, first, max)).thenReturn(TEST_TP);
        Mockito.when(mockOd.countByIdOrden(idOrden)).thenReturn((long) TEST_TP.size());
        Response response = cut.findRangeByIdOrden(first, max, idOrden);
        Assertions.assertEquals(200, response.getStatus());
        Mockito.verify(mockOd).findRangeByIdOrden(idOrden, first, max);
        Mockito.verify(mockOd).countByIdOrden(idOrden);

        //excepciones

        Mockito.reset(mockOd);
        Mockito.when(mockOd.findRangeByIdOrden(idOrden, first, max)).thenThrow(persistenceExcepcion);
        response = cut.findRangeByIdOrden(first, max, idOrden);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void testFindByIdOrdenAndIdProductoPrecio() {
        System.out.println("OrdenDetalle test findByIdOrdenAndIdProductoPrecio");
        cut = new OrdenDetalleResource();
        mockOd = Mockito.mock(OrdenDetalleBean.class);

        cut.odBean = mockOd;
        Long idOrden = 1L;
        Long idProductoPrecio = 3L;

        //flujo normal
        Mockito.when(mockOd.findByIdOrdenAndIdPrecioProducto(idOrden, idProductoPrecio)).thenReturn(TEST_TP.get(0));
        Response response = cut.findByIdOrdenAndIdProductoPrecio(idOrden, idProductoPrecio);
        Assertions.assertEquals(200, response.getStatus());
        Mockito.verify(mockOd).findByIdOrdenAndIdPrecioProducto(idOrden, idProductoPrecio);

        //excepciones
        Mockito.reset(mockOd);
        Mockito.when(mockOd.findByIdOrdenAndIdPrecioProducto(idOrden, idProductoPrecio)).thenThrow(persistenceExcepcion);
        response = cut.findByIdOrdenAndIdProductoPrecio(idOrden, idProductoPrecio);
        Assertions.assertEquals(500, response.getStatus());
        //fail("Esta prueba no pasa quemado");


    }

    @Test
    void testDelete() throws Exception {
        System.out.println("OrdenDetalle test delete");
        cut = new OrdenDetalleResource();
        mockOd = Mockito.mock(OrdenDetalleBean.class);

        cut.odBean = mockOd;
        Long idOrden = 1L;
        Long idProductoPrecio = 3L;
        //flujo normal
        Mockito.doNothing().when(mockOd).delete(idOrden, idProductoPrecio);
        Response response = cut.delete(idOrden, idProductoPrecio);
        Assertions.assertEquals(200, response.getStatus());
        Mockito.verify(mockOd).delete(idOrden, idProductoPrecio);
        //excepciones
        Mockito.reset(mockOd);

        Mockito.doThrow(persistenceExcepcion)
                .when(mockOd).delete(idOrden, idProductoPrecio);
        response = cut.delete(idOrden, idProductoPrecio);
        Assertions.assertEquals(500, response.getStatus());
        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void testupdate() {
        System.out.println("OrdenDetalle test update");
        cut = new OrdenDetalleResource();
        mockOd = Mockito.mock(OrdenDetalleBean.class);
        cut.odBean = mockOd;
        Long idOrden = 1L;
        Long idProductoPrecio = 2L;
        UriInfo mockUriInfo = Mockito.mock(UriInfo.class);

        UriBuilder mockUriBuilder = Mockito.mock(UriBuilder.class);
        OrdenDetalle tp = new OrdenDetalle(1L, 2L);
        tp.setObservaciones("Test");
        //flujo normal
        URI uri = URI.create("http://localhost:8080/api/ordenDetalle");
        Mockito.when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.build()).thenReturn(uri);
        Mockito.when(mockOd.update(tp, idOrden, idProductoPrecio)).thenReturn(tp);
        Response response = cut.update(tp, mockUriInfo, idOrden, idProductoPrecio);
        Assertions.assertEquals(200, response.getStatus());
        //excepciones
        Mockito.reset(mockOd);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockOd).update(tp, idOrden, idProductoPrecio);
        response = cut.update(tp, mockUriInfo, idOrden, idProductoPrecio);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void generarOrdenDetalleProducto() {
        System.out.println("Pago test generarOrdenDetalleProducto");
        cut = new OrdenDetalleResource();
        mockOd = Mockito.mock(OrdenDetalleBean.class);
        cut.odBean = mockOd;
        UriInfo mockUriInfo = Mockito.mock(UriInfo.class);
        UriBuilder mockUriBuilder = Mockito.mock(UriBuilder.class);
        Long idOrden = 1L;
        Long idProductoPrecio = 2L;
        Integer cantidad = 1;
        //flujo normal
        URI uri = URI.create("http://localhost:8080/api/pago/1");
        Mockito.when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.build()).thenReturn(uri);
        Mockito.doNothing().when(mockOd).generarOrdenDetalleProducto(idOrden, idProductoPrecio, cantidad);
        Response response = cut.generarOrdenDetalleProducto(idOrden, idProductoPrecio, cantidad);
        Assertions.assertEquals(200, response.getStatus());
        //excepciones
        Mockito.reset(mockOd);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockOd).generarOrdenDetalleProducto(idOrden, idProductoPrecio, cantidad);
        response = cut.generarOrdenDetalleProducto(idOrden, idProductoPrecio, cantidad);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void generarOrdenDetalleDesdeCombo() {
        System.out.println("Pago test generarOrdenDetalleDesdeCombo");
        cut = new OrdenDetalleResource();
        mockOd = Mockito.mock(OrdenDetalleBean.class);
        cut.odBean = mockOd;
        UriInfo mockUriInfo = Mockito.mock(UriInfo.class);
        UriBuilder mockUriBuilder = Mockito.mock(UriBuilder.class);
        Long idOrden = 1L;
        Long idcombo = 2L;
        Integer cantidad = 1;
        //flujo normal
        URI uri = URI.create("http://localhost:8080/api/pago/1");
        Mockito.when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.build()).thenReturn(uri);
        Mockito.doNothing().when(mockOd).generarOrdenDetalleDesdeCombo(idOrden, idcombo, cantidad);
        Response response = cut.generarOrdenDetalleDesdeCombo(idOrden, idcombo, cantidad);
        Assertions.assertEquals(200, response.getStatus());
        //argumnetos malos
        Mockito.doNothing().when(mockOd).generarOrdenDetalleDesdeCombo(0L, idcombo, cantidad);
        response = cut.generarOrdenDetalleDesdeCombo(0L, idcombo, cantidad);
        Assertions.assertEquals(400, response.getStatus());
        //excepciones
        Mockito.reset(mockOd);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockOd).generarOrdenDetalleDesdeCombo(idOrden, idcombo, cantidad);
        response = cut.generarOrdenDetalleDesdeCombo(idOrden, idcombo, cantidad);
        Assertions.assertEquals(500, response.getStatus());
    }

//
//    @Test
//    void testGenerarOrdenDetalleMixto(){
//        System.out.println("testGenerarOrdenDetalleMixto");
//        cut = new OrdenDetalleResource();
//        mockOd = Mockito.mock(OrdenDetalleBean.class);
//        cut.odBean = mockOd;
//        Long idOrden = 1L;
//        UriInfo mockUriInfo = Mockito.mock(UriInfo.class);
//        UriBuilder mockUriBuilder = Mockito.mock(UriBuilder.class);
//        List<DatosMixtosDTO> listDatos = new ArrayList<>();
//        listDatos.add(new DatosMixtosDTO(1L,5,2L,3));
//        listDatos.add(new DatosMixtosDTO(2L,5,9L,1));
//
//        //flujo normal
//        URI uri = URI.create("http://localhost:8080/api/pago/1");
//        Mockito.when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
//        Mockito.when(mockUriBuilder.build()).thenReturn(uri);
//        Mockito.doNothing().when(mockOd).generarOrdenDetalleMixto(Mockito.eq(idOrden), Mockito.anyList(), Mockito.anyList());
//        Response response = cut.generarOrdenDetalleMixto(listDatos,idOrden);
//        Assertions.assertEquals(200, response.getStatus());
//        //lista nula
//        response = cut.generarOrdenDetalleMixto(null,idOrden);
//        Assertions.assertEquals(400, response.getStatus());
//        //id Orden invalido
//        response = cut.generarOrdenDetalleMixto(listDatos,0L);
//        Assertions.assertEquals(400, response.getStatus());
//        //excepciones
//        Mockito.reset(mockOd);
//        Mockito.doThrow(persistenceExcepcion)
//                .when(mockOd).generarOrdenDetalleMixto(Mockito.eq(idOrden), Mockito.anyList(), Mockito.anyList());
//        response = cut.generarOrdenDetalleMixto(listDatos, idOrden);
//        Assertions.assertEquals(500, response.getStatus());
//        //fail("Esta prueba no pasa quemado");
//
//
//    }


}

