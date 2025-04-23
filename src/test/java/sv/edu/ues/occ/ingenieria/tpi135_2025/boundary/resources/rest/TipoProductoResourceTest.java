package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoPrecioBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.TipoProductoBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoPrecio;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.TipoProducto;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TipoProductoResourceTest {

    List<TipoProducto> TEST_TP = Arrays.asList(
            new TipoProducto(1),
            new TipoProducto(2),
            new TipoProducto(3),
            new TipoProducto(4),
            new TipoProducto(5),
            new TipoProducto(6));

    TipoProductoResource cut; // Clase bajo prueba
    TipoProductoBean mockTp;
    PersistenceException causaPe = new PersistenceException("eroor desde test");
    PersistenceException persistenceExcepcion = new PersistenceException("Error al consultar", causaPe);

    EntityNotFoundException causaEe = new EntityNotFoundException("eroor desde test");
    EntityNotFoundException entityNotFoundException = new EntityNotFoundException("Error al consultar", causaEe);


    @Test
    void findRange() {
        System.out.println("TipoProducto test findRange");
        cut = new TipoProductoResource();
        mockTp = Mockito.mock(TipoProductoBean.class);

        cut.tpBean=mockTp;
        Integer first=0;
        Integer max=10;
        //flujo normal
        Mockito.when(mockTp.findRange(first, max)).thenReturn(TEST_TP);
        Mockito.when(mockTp.count()).thenReturn((long) TEST_TP.size());
        Response response = cut.findRange(first, max);
        Assertions.assertEquals(200, response.getStatus());
        Mockito.verify(mockTp).findRange(first, max);
        Mockito.verify(mockTp).count();
        //excepciones

        Mockito.reset(mockTp);
        Mockito.when(mockTp.findRange(first, max)).thenThrow(persistenceExcepcion);
        response = cut.findRange(first, max);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void findById() {
        System.out.println("TipoProducto test findById");
        cut = new TipoProductoResource();
        mockTp = Mockito.mock(TipoProductoBean.class);
        Integer id = 1;
        cut.tpBean=mockTp;
        Integer first=0;
        Integer max=10;
        //flujo normal
        Mockito.when(mockTp.findById(id)).thenReturn(TEST_TP.stream().filter(p->p.getIdTipoProducto().equals(id)).findFirst().get());
        Response response = cut.findById(id);
        Assertions.assertEquals(200, response.getStatus());
        //excepciones
        Mockito.reset(mockTp);
        Mockito.when(mockTp.findById(2)).thenThrow(entityNotFoundException);
        response = cut.findById(2);
        Assertions.assertEquals(404, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void create() {
        System.out.println("TipoProducto test create");
        cut = new TipoProductoResource();
        mockTp = Mockito.mock(TipoProductoBean.class);
        cut.tpBean=mockTp;
        UriInfo mockUriInfo = Mockito.mock(UriInfo.class);
        URI fakeUri = URI.create("http://localhost:9080/v1/tipoProducto/");
        UriBuilder mockUriBuilder = Mockito.mock(UriBuilder.class);
        TipoProducto tp = new TipoProducto();
        tp.setIdTipoProducto(1);
        tp.setNombre("Test");
        //flujo normal
        URI uri = URI.create("http://localhost:8080/api/tipoproducto/1");
        Mockito.when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.path(String.valueOf(tp.getIdTipoProducto()))).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.build()).thenReturn(uri);
        Response response = cut.create(tp, mockUriInfo);
        Assertions.assertEquals(201, response.getStatus());
        //excepciones
        Mockito.reset(mockTp);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockTp).create(tp);
        response = cut.create(tp, mockUriInfo);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void delete() {
        System.out.println("TipoProducto test delete");
        cut = new TipoProductoResource();
        mockTp = Mockito.mock(TipoProductoBean.class);
        cut.tpBean=mockTp;
        UriInfo mockUriInfo = Mockito.mock(UriInfo.class);
        Integer id=1;
        //flujo normal
        Mockito.doNothing().when(mockTp).delete(id);
        Response response = cut.delete(id);
        Assertions.assertEquals(200, response.getStatus());
        //excepciones
        Mockito.reset(mockTp);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockTp).delete(id);
        response = cut.delete(id);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void update() {
        System.out.println("TipoProducto test update");
        cut = new TipoProductoResource();
        mockTp = Mockito.mock(TipoProductoBean.class);
        cut.tpBean=mockTp;
        Integer id=1;
        UriInfo mockUriInfo = Mockito.mock(UriInfo.class);

        UriBuilder mockUriBuilder = Mockito.mock(UriBuilder.class);
        TipoProducto tp = new TipoProducto();
        tp.setIdTipoProducto(1);
        tp.setNombre("Test");
        //flujo normal
        URI uri = URI.create("http://localhost:8080/api/tipoproducto/1");
        Mockito.when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.path(String.valueOf(tp.getIdTipoProducto()))).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.build()).thenReturn(uri);
        Mockito.when(mockTp.update(tp,id)).thenReturn(tp);
        Response response = cut.update(tp, mockUriInfo,id);
        Assertions.assertEquals(200, response.getStatus());
        //excepciones
        Mockito.reset(mockTp);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockTp).update(tp,id);
        response = cut.update(tp, mockUriInfo,id);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }
}