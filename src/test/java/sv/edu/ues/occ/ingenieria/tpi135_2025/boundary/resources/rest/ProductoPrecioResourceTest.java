package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoPrecioBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoPrecio;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class ProductoPrecioResourceTest {

    List<ProductoPrecio> TEST_TP = Arrays.asList(
            new ProductoPrecio(1L),
            new ProductoPrecio(2L),
            new ProductoPrecio(3L),
            new ProductoPrecio(4L),
            new ProductoPrecio(5L),
            new ProductoPrecio(6L));

    ProductoPrecioResource cut; // Clase bajo prueba
    ProductoPrecioBean mockPp;
    PersistenceException causaPe = new PersistenceException("eroor desde test");
    PersistenceException persistenceExcepcion = new PersistenceException("Error al consultar", causaPe);

    EntityNotFoundException causaEe = new EntityNotFoundException("eroor desde test");
    EntityNotFoundException entityNotFoundException = new EntityNotFoundException("Error al consultar", causaEe);


    @Test
    void findRange() {
        System.out.println("ProductoPrecio test findRange");
        cut = new ProductoPrecioResource();
        mockPp = Mockito.mock(ProductoPrecioBean.class);
        cut.productoPrecioBean= mockPp;

        Integer first=0;
        Integer max=10;

        //flujo normal
        Mockito.when(mockPp.findRange(first, max)).thenReturn(TEST_TP);
        Mockito.when(mockPp.count()).thenReturn((long) TEST_TP.size());
        Response response = cut.findAll(first, max);
        Assertions.assertEquals(200, response.getStatus());
        Mockito.verify(mockPp).findRange(first, max);

        //excepciones

        Mockito.reset(mockPp);
        Mockito.when(mockPp.findRange(first, max)).thenThrow(persistenceExcepcion);
        response = cut.findAll(first, max);
        Assertions.assertEquals(500, response.getStatus());

//        fail("Esta prueba no pasa quemado");
    }

    @Test
    void findById() {
        System.out.println("ProductoPrecio test findById");
        cut = new ProductoPrecioResource();
        mockPp = Mockito.mock(ProductoPrecioBean.class);
        Long id = 1L;
        cut.productoPrecioBean= mockPp;
        Integer first=0;
        Integer max=10;
        //flujo normal
        Mockito.when(mockPp.findById(id)).thenReturn(TEST_TP.stream().filter(p->p.getIdProductoPrecio().equals(id)).findFirst().get());
        Response response = cut.findById(id);
        Assertions.assertEquals(200, response.getStatus());
        //excepciones
        Mockito.reset(mockPp);
        Mockito.when(mockPp.findById(2L)).thenThrow(entityNotFoundException);
        response = cut.findById(2L);
        Assertions.assertEquals(404, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void create() {
        System.out.println("ProductoPrecio test create");
        cut = new ProductoPrecioResource();
        mockPp = Mockito.mock(ProductoPrecioBean.class);
        cut.productoPrecioBean = mockPp;

        UriInfo mockUriInfo = Mockito.mock(UriInfo.class);
        UriBuilder mockUriBuilder = Mockito.mock(UriBuilder.class);

        URI baseUri = URI.create("http://localhost:8080/api/ProductoPrecio");
        Mockito.when(mockUriInfo.getAbsolutePath()).thenReturn(baseUri); // <-- esta línea es nueva

        ProductoPrecio tp = new ProductoPrecio();
        tp.setIdProductoPrecio(1L);
        tp.setPrecioSugerido(BigDecimal.valueOf(99.99));
        Long idProducto = 2L;

        URI uri = URI.create("http://localhost:8080/api/ProductoPrecio/1");
        Mockito.when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.path(String.valueOf(tp.getIdProductoPrecio()))).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.build()).thenReturn(uri);
        Mockito.doNothing().when(mockPp).create(tp,idProducto);

        Response response = cut.create(idProducto, tp, mockUriInfo);
        Assertions.assertEquals(201, response.getStatus());

        // excepción
        Mockito.reset(mockPp);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockPp).create(tp,idProducto);
        response = cut.create(idProducto, tp, mockUriInfo);
        Assertions.assertEquals(500, response.getStatus());
    }


    @Test
    void delete() {
        System.out.println("ProductoPrecio test delete");
        cut = new ProductoPrecioResource();
        mockPp = Mockito.mock(ProductoPrecioBean.class);
        cut.productoPrecioBean= mockPp;
        Long id=1L;
        //flujo normal
        Mockito.doNothing().when(mockPp).delete(id);
        Response response = cut.delete(id);
        Assertions.assertEquals(200, response.getStatus());
        response = cut.delete(id);
        Assertions.assertEquals(200, response.getStatus());
        Mockito.doNothing().when(mockPp).delete(id);
        //excepciones
        Mockito.reset(mockPp);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockPp).delete(id);
        response = cut.delete(id);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void update() {
        System.out.println("ProductoPrecio test update");
        cut = new ProductoPrecioResource();
        mockPp = Mockito.mock(ProductoPrecioBean.class);
        cut.productoPrecioBean= mockPp;
        Long id=1L;
        UriInfo mockUriInfo = Mockito.mock(UriInfo.class);

        UriBuilder mockUriBuilder = Mockito.mock(UriBuilder.class);
        ProductoPrecio tp = new ProductoPrecio();
        tp.setIdProductoPrecio(1L);
        tp.setPrecioSugerido(BigDecimal.valueOf(99.99));
        //flujo normal
        URI uri = URI.create("http://localhost:8080/api/ProductoPrecio/1");
        Mockito.when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.path(String.valueOf(tp.getIdProductoPrecio()))).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.build()).thenReturn(uri);
        Mockito.when(mockPp.update(tp,id)).thenReturn(tp);
        Response response = cut.update(id,tp);
        Assertions.assertEquals(200, response.getStatus());
        //precio nulo
        response =cut.update(id,null);
        Assertions.assertEquals(404, response.getStatus());
        //precio nulo
        response =cut.update(200L,tp);
        Assertions.assertEquals(400, response.getStatus());
        //excepciones
        Mockito.reset(mockPp);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockPp).update(tp,id);
        response = cut.update(id,tp);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }


    @Test
    void findByIdProducto() {
        System.out.println("ProductoPrecio test findByIdProducto");
        cut = new ProductoPrecioResource();
        mockPp = Mockito.mock(ProductoPrecioBean.class);
        Long id = 1L;
        cut.productoPrecioBean= mockPp;
        //flujo normal
        Mockito.when(mockPp.findByIdProducto(id)).thenReturn(TEST_TP.stream().filter(p->p.getIdProductoPrecio().equals(id)).findFirst().get());
        Response response = cut.findByIdProducto(id);
        Assertions.assertEquals(200, response.getStatus());
        //error de id
        response=cut.findByIdProducto(null);
        Assertions.assertEquals(400, response.getStatus());
        //excepciones
        Mockito.reset(mockPp);
        Mockito.when(mockPp.findByIdProducto(2L)).thenThrow(entityNotFoundException);
        response = cut.findByIdProducto(2L);
        Assertions.assertEquals(404, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void findCount() {
        System.out.println("ProductoPrecio test count");
        cut = new ProductoPrecioResource();
        mockPp = Mockito.mock(ProductoPrecioBean.class);
        Long id = 1L;
        cut.productoPrecioBean= mockPp;
        //flujo normal con id
        Mockito.when(mockPp.countByIdProducto(id)).thenReturn((long) TEST_TP.subList(0,3).size());
         Response response = cut.count(id);
        Assertions.assertEquals(200, response.getStatus());
        response = cut.count(null);
        Assertions.assertEquals(400, response.getStatus());
        //excepciones
        Mockito.reset(mockPp);
        Mockito.when(mockPp.countByIdProducto(id)).thenThrow(persistenceExcepcion);
        response = cut.count(id);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }




}
