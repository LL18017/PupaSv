package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoDetalleBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetalle;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductoDetalleResourceTest {
    List<ProductoDetalle> TEST_C = Arrays.asList(
            new ProductoDetalle(1,1L),
            new ProductoDetalle(1,2L),
            new ProductoDetalle(1,3L),
            new ProductoDetalle(1,4L),
            new ProductoDetalle(1,5L),
            new ProductoDetalle(1,6L));

    ProductoDetalleResource cut; // Clase bajo prueba
    ProductoDetalleBean mockC;
    PersistenceException causaPe = new PersistenceException("eroor desde test");
    PersistenceException persistenceExcepcion = new PersistenceException("Error al consultar", causaPe);

    EntityNotFoundException causaEe = new EntityNotFoundException("eroor desde test");
    EntityNotFoundException entityNotFoundException = new EntityNotFoundException("Error al consultar", causaEe);


    @Test
    void findRange() {
        System.out.println("ProductoDetalle test findRange");
        cut = new ProductoDetalleResource();
        mockC = Mockito.mock(ProductoDetalleBean.class);

        cut.pdBean= mockC;
        Integer first=0;
        Integer max=10;

        //flujo normal
        Mockito.when(mockC.findRange(first, max)).thenReturn(TEST_C);
        Mockito.when(mockC.count()).thenReturn((long) TEST_C.size());
        Response response = cut.find(first, max);
        Assertions.assertEquals(200, response.getStatus());
        Mockito.verify(mockC).findRange(first, max);
        Mockito.verify(mockC).count();

        //excepciones

        Mockito.reset(mockC);
        Mockito.when(mockC.findRange(first, max)).thenThrow(persistenceExcepcion);
        response = cut.find(first, max);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }


    @Test
    void findById() {
        System.out.println("ProductoDetalle test findById");
        cut = new ProductoDetalleResource();
        mockC = Mockito.mock(ProductoDetalleBean.class);
        Long idProducto = 1L;
        Integer idTipoProducto = 1;
        cut.pdBean= mockC;
        //flujo normal
        Mockito.when(mockC.findById(idTipoProducto,idProducto)).thenReturn(TEST_C.get(0));
        Response response = cut.findByIDs(idTipoProducto,idProducto);
        Assertions.assertEquals(200, response.getStatus());
        //excepciones
        Mockito.reset(mockC);
        Mockito.when(mockC.findById(1,2L)).thenThrow(entityNotFoundException);
        response = cut.findByIDs(1,2L);
        Assertions.assertEquals(404, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void create() {
        System.out.println("ProductoDetalle test create");
        cut = new ProductoDetalleResource();
        mockC = Mockito.mock(ProductoDetalleBean.class);
        cut.pdBean= mockC;
        Long idProducto = 1L;
        Integer idTipoProducto = 1;
        UriInfo mockUriInfo = Mockito.mock(UriInfo.class);
        UriBuilder mockUriBuilder = Mockito.mock(UriBuilder.class);
        ProductoDetalle tp = new ProductoDetalle();
        tp.setObservaciones("Test");
        //flujo normal
        URI uri = URI.create("http://localhost:8080/api/ProductoDetalle/1");
        Mockito.when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.build()).thenReturn(uri);
        Mockito.doNothing().when(mockC).create(tp);
        Response response = cut.create(tp,idTipoProducto,idProducto,mockUriInfo);
        Assertions.assertEquals(201, response.getStatus());
        //excepciones
        Mockito.reset(mockC);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockC).create(tp,idTipoProducto,idProducto);
        response = cut.create(tp,idTipoProducto,idProducto, mockUriInfo);
        Assertions.assertEquals(500, response.getStatus());



        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void delete() {
        System.out.println("ProductoDetalle test delete");
        cut = new ProductoDetalleResource();
        mockC = Mockito.mock(ProductoDetalleBean.class);
        cut.pdBean= mockC;
        Long id=1L;
        Integer idTipoProducto = 1;
        //flujo normal
        Mockito.doNothing().when(mockC).deleteByIdTipoProductoAndIdProducto(idTipoProducto,id);
        Response response = cut.delete(idTipoProducto,id);
        Assertions.assertEquals(200, response.getStatus());
        response = cut.delete(idTipoProducto,id);
        //excepciones
        Mockito.reset(mockC);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockC).deleteByIdTipoProductoAndIdProducto(idTipoProducto,id);
        response = cut.delete(idTipoProducto,id);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void update() {
        System.out.println("ProductoDetalle test update");
        cut = new ProductoDetalleResource();
        mockC = Mockito.mock(ProductoDetalleBean.class);
        cut.pdBean= mockC;
        Long id=1L;
        Integer idTipoProducto = 1;
        UriInfo mockUriInfo = Mockito.mock(UriInfo.class);

        UriBuilder mockUriBuilder = Mockito.mock(UriBuilder.class);
        ProductoDetalle tp = new ProductoDetalle();
        tp.setObservaciones("Test");
        //flujo normal
        URI uri = URI.create("http://localhost:8080/api/ProductoDetalle/1");
        Mockito.when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.build()).thenReturn(uri);
        Mockito.when(mockC.update(tp,idTipoProducto,id)).thenReturn(tp);
        Response response = cut.update(tp,id,idTipoProducto, mockUriInfo);
        Assertions.assertEquals(200, response.getStatus());
        //excepciones
        Mockito.reset(mockC);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockC).update(tp,idTipoProducto,id);
        response = cut.update(tp,id,idTipoProducto, mockUriInfo);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }
}