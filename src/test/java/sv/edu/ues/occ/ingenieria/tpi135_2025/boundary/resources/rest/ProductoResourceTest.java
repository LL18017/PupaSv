package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

class ProductoResourceTest {

    List<Producto> TEST_TP = Arrays.asList(
            new Producto(1L),
            new Producto(2L),
            new Producto(3L),
            new Producto(4L),
            new Producto(5L),
            new Producto(6L));

    ProductoResource cut; // Clase bajo prueba
    ProductoBean mockP;
    PersistenceException causaPe = new PersistenceException("eroor desde test");
    PersistenceException persistenceExcepcion = new PersistenceException("Error al consultar", causaPe);

    EntityNotFoundException causaEe = new EntityNotFoundException("eroor desde test");
    EntityNotFoundException entityNotFoundException = new EntityNotFoundException("Error al consultar", causaEe);


    @Test
    void findRange() {
        System.out.println("Producto test findRange");
        cut = new ProductoResource();
        mockP = Mockito.mock(ProductoBean.class);

        cut.pBean= mockP;
        Integer first=0;
        Integer max=10;
        Integer idTipoProducto=2;
        Boolean activo=true;

        //id cero y activo null
        Mockito.when(mockP.findRange(first, max)).thenReturn(TEST_TP);
        Mockito.when(mockP.count()).thenReturn((long) TEST_TP.size());
        Response response = cut.findRange(first, max,0,null);
        Assertions.assertEquals(200, response.getStatus());
        Mockito.verify(mockP).findRange(first, max);
        Mockito.verify(mockP).count();

        //busqueda por activos
        Mockito.when(mockP.findRangeProductoActivos(first, max,activo)).thenReturn(TEST_TP.subList(0, 3));
        Mockito.when(mockP.countProductoActivos(activo)).thenReturn((long) TEST_TP.subList(0,3).size());
        response = cut.findRange(first, max,0,activo);
        Assertions.assertEquals(200, response.getStatus());
        Mockito.verify(mockP).findRangeProductoActivos(first, max,activo);
        Mockito.verify(mockP).countProductoActivos(activo);

        //busqueda por activos y tp
        Mockito.when(mockP.findRangeByIdTipoProductosAndActivo(idTipoProducto,activo,first,max)).thenReturn(TEST_TP.subList(0, 2));
        Mockito.when(mockP.countByIdTipoProductosAndActivo(idTipoProducto,activo)).thenReturn((long) TEST_TP.subList(0,2).size());
        response = cut.findRange(first, max,idTipoProducto,null);
        Assertions.assertEquals(200, response.getStatus());
        Mockito.verify(mockP).findRangeByIdTipoProductosAndActivo(idTipoProducto,activo,first,max);
        Mockito.verify(mockP).countByIdTipoProductosAndActivo(idTipoProducto,activo);


        //excepciones

        Mockito.reset(mockP);
        Mockito.when(mockP.findRange(first, max)).thenThrow(persistenceExcepcion);
        response = cut.findRange(first, max,0,null);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void findById() {
        System.out.println("Producto test findById");
        cut = new ProductoResource();
        mockP = Mockito.mock(ProductoBean.class);
        Long id = 1L;
        cut.pBean= mockP;
        Integer first=0;
        Integer max=10;
        //flujo normal
        Mockito.when(mockP.findById(id)).thenReturn(TEST_TP.stream().filter(p->p.getIdProducto().equals(id)).findFirst().get());
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
        System.out.println("Producto test create");
        cut = new ProductoResource();
        mockP = Mockito.mock(ProductoBean.class);
        cut.pBean= mockP;
        UriInfo mockUriInfo = Mockito.mock(UriInfo.class);
        URI fakeUri = URI.create("http://localhost:9080/v1/Producto/");
        UriBuilder mockUriBuilder = Mockito.mock(UriBuilder.class);
        Producto tp = new Producto();
        tp.setIdProducto(1L);
        tp.setNombre("Test");
        //flujo normal
        URI uri = URI.create("http://localhost:8080/api/Producto/1");
        Mockito.when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.path(String.valueOf(tp.getIdProducto()))).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.build()).thenReturn(uri);
        Mockito.doNothing().when(mockP).create(tp);
        Response response = cut.create(tp,0, mockUriInfo);
        Assertions.assertEquals(201, response.getStatus());
        //flujo con detalle
        Mockito.doNothing().when(mockP).createProductoAndDetail(tp,2);
        response = cut.create(tp,2, mockUriInfo);
        Assertions.assertEquals(201, response.getStatus());

        //excepciones
        Mockito.reset(mockP);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockP).create(tp);
        response = cut.create(tp,0, mockUriInfo);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void delete() {
        System.out.println("Producto test delete");
        cut = new ProductoResource();
        mockP = Mockito.mock(ProductoBean.class);
        cut.pBean= mockP;
        Long id=1L;
        Integer idTipoProducto=2;
        //flujo normal
        Mockito.doNothing().when(mockP).delete(id);
        Response response = cut.delete(id,0);
        Assertions.assertEquals(200, response.getStatus());
        response = cut.delete(id,idTipoProducto);
        Assertions.assertEquals(200, response.getStatus());
        Mockito.doNothing().when(mockP).deleteProductoAndDetail(id,idTipoProducto);
        //excepciones
        Mockito.reset(mockP);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockP).delete(id);
        response = cut.delete(id,0);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void update() {
        System.out.println("Producto test update");
        cut = new ProductoResource();
        mockP = Mockito.mock(ProductoBean.class);
        cut.pBean= mockP;
        Long id=1L;
        UriInfo mockUriInfo = Mockito.mock(UriInfo.class);

        UriBuilder mockUriBuilder = Mockito.mock(UriBuilder.class);
        Producto tp = new Producto();
        tp.setIdProducto(1L);
        tp.setNombre("Test");
        //flujo normal
        URI uri = URI.create("http://localhost:8080/api/Producto/1");
        Mockito.when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.path(String.valueOf(tp.getIdProducto()))).thenReturn(mockUriBuilder);
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

    @Test
    void findRangeByName() {
        System.out.println("Producto test findRangeByName");
        cut = new ProductoResource();
        mockP = Mockito.mock(ProductoBean.class);

        cut.pBean= mockP;
        Integer first=0;
        Integer max=10;
        Integer idTipoProducto=2;
        Boolean activo=true;
        String nombre="test";

        //normal
        Mockito.when(mockP.findByNombre(nombre,first, max)).thenReturn(TEST_TP);
        Mockito.when(mockP.countProductoByName(nombre)).thenReturn((long) TEST_TP.size());
        Response response = cut.findRangeByName(first, max,nombre);
        Assertions.assertEquals(200, response.getStatus());
        Mockito.verify(mockP).findByNombre(nombre,first, max);
        Mockito.verify(mockP).countProductoByName(nombre);


        //excepciones

        Mockito.reset(mockP);
        Mockito.when(mockP.findByNombre(nombre,first, max)).thenThrow(persistenceExcepcion);
        response = cut.findRangeByName(first, max,nombre);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

}