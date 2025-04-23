package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoPrecioBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoPrecio;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProductoPrecioResourceTest {

    @Mock
    private ProductoPrecioBean productoPrecioBean;
    @Mock
    private UriInfo uriInfo;
    @InjectMocks
    private ProductoPrecioResource productoPrecioResource;

    @BeforeEach
    public void setUp() {
        productoPrecioResource = new ProductoPrecioResource();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        System.out.println("Test findAll");
        ProductoPrecio precio1 = new ProductoPrecio();
        ProductoPrecio precio2 = new ProductoPrecio();
        when(productoPrecioBean.findRange(0, 20)).thenReturn(List.of(precio1, precio2));

        Response response = productoPrecioResource.findAll(0, 20);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(((List<?>) response.getEntity()).size() > 0);
        try {
            when(productoPrecioBean.findRange(0, 20)).thenThrow(new RuntimeException("Error al obtener precios"));

            response = productoPrecioResource.findAll(0, 20);

            assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
            assertNotNull(response.getEntity());
        } catch (RuntimeException e) {
        }
        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void testFindById() {
        System.out.println("Test findById");
        Long id = 1L;

        ProductoPrecio productoPrecio = new ProductoPrecio();
        when(productoPrecioBean.findById(id)).thenReturn(productoPrecio);

        Response response = productoPrecioResource.findById(id);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());

        when(productoPrecioBean.findById(id)).thenReturn(null);
        response = productoPrecioResource.findById(id);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());

        try {
            when(productoPrecioBean.findById(id)).thenThrow(new RuntimeException("Error al obtener el ProductoPrecio"));
            response = productoPrecioResource.findById(id);
            assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
            assertNotNull(response.getEntity());
        } catch (RuntimeException e) {
        }
        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void testCreate() {
        System.out.println("Test create");
        Long idProducto = 1L;
        ProductoPrecio precio = new ProductoPrecio();
        precio.setIdProductoPrecio(123L);

        String uriString = "http://localhost:8080/api/productoPrecio/1";
        when(uriInfo.getAbsolutePath()).thenReturn(URI.create(uriString));
        doNothing().when(productoPrecioBean).create(precio, idProducto);
        Response response = productoPrecioResource.create(idProducto, precio);


        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        String location = response.getHeaderString("Location");
        assertNotNull(location);
        assertTrue(location.contains("/123"));

        try {
            doThrow(new RuntimeException("Error en la creación")).when(productoPrecioBean).create(precio, idProducto);
            response = productoPrecioResource.create(idProducto, precio);
            assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        } catch (RuntimeException e) {
        }
        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void testUpdate() {
        System.out.println("Test update");
        Long id = 1L;
        ProductoPrecio precio = new ProductoPrecio();
        precio.setIdProductoPrecio(id);
        precio.setPrecioSugerido(new BigDecimal("29.99"));
        // Caso 1: El cuerpo de la solicitud está vacío
        Response response = productoPrecioResource.update(id, null);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("El cuerpo de la solicitud no puede estar vacio", response.getEntity());
        // Caso 2: El ID en la ruta no coincide con el ID del cuerpo de la solicitud
        ProductoPrecio precioConIdDiferente = new ProductoPrecio();
        precioConIdDiferente.setIdProductoPrecio(2L);
        response = productoPrecioResource.update(id, precioConIdDiferente);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("El ID en la ruta no coincide con el ID de la solicitud", response.getEntity());
        // Caso 3: Actualización exitosa
        when(productoPrecioBean.update(precio, id)).thenReturn(null);
        response = productoPrecioResource.update(id, precio);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(precio, response.getEntity());
        // Caso 4: Excepción durante la actualización
        try {
            doThrow(new RuntimeException("Error en la actualización")).when(productoPrecioBean).update(precio, id);
            response = productoPrecioResource.update(id, precio);
            assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        } catch (Exception e) {
        }
       // fail("Esta prueba no pasa queamdo");
    }

    @Test
    void testDelete() {
        System.out.println("Test delete");
        Response response = null;
        try {
            response = productoPrecioResource.delete(1L);
        } catch (Exception ex) {
            if (ex.getCause() != null) {
                System.out.println("Excepción capturada (causa): " + ex.getCause().toString());
            } else {
                System.out.println("Excepción capturada (sin causa específica): " + ex.toString());
            }
            fail("Excepción inesperada durante delete: " + ex);
        }
        assertNotNull("La respuesta no debe ser null", response);
        assertEquals("El código de respuesta debe ser 200 OK", Response.Status.OK.getStatusCode(), response.getStatus());
        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void testFindByIdProducto() throws Exception {
        System.out.println("Test findByIdProducto");
        Long validId = 123L;
        ProductoPrecio expectedPrecio = new ProductoPrecio();
        when(productoPrecioBean.findByIdProducto(validId)).thenReturn(expectedPrecio);

        // Caso 1: ID válido
        Response response = productoPrecioResource.findByIdProducto(validId, 0, 20);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(expectedPrecio, response.getEntity());
        verify(productoPrecioBean, times(1)).findByIdProducto(validId);

        // Caso 2: Excepción lanzada
        Long errorId = 456L;
        RuntimeException exception = new RuntimeException("Error al buscar el producto");
        when(productoPrecioBean.findByIdProducto(errorId)).thenThrow(exception);

        ProductoPrecioResource mockResource = spy(productoPrecioResource);
        Response expectedErrorResponse = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Ocurrió un error inesperado al procesar la solicitud para el producto con ID: 456")
                .build();
        doReturn(expectedErrorResponse).when(mockResource).responseExcepcions(exception, errorId);

        Response errorResponse = mockResource.findByIdProducto(errorId, 0, 20);
        assertEquals(expectedErrorResponse.getStatus(), errorResponse.getStatus());
        assertEquals(expectedErrorResponse.getEntity(), errorResponse.getEntity());
        verify(productoPrecioBean, times(2)).findByIdProducto(anyLong());
        verify(mockResource, times(1)).responseExcepcions(exception, errorId);

        //Caso 3: ID nulo
        Response nullIdResponse = productoPrecioResource.findByIdProducto(null, 0, 20);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), nullIdResponse.getStatus());
        assertEquals("El ID del producto no puede ser nulo o menor que 0.", nullIdResponse.getEntity());
        verify(productoPrecioBean, times(2)).findByIdProducto(anyLong());

        //Caso 4: ID negativo
        Response negativeIdResponse = productoPrecioResource.findByIdProducto(-5L, 0, 20);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), negativeIdResponse.getStatus());
        assertEquals("El ID del producto no puede ser nulo o menor que 0.", negativeIdResponse.getEntity());
        verify(productoPrecioBean, times(2)).findByIdProducto(anyLong());
        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void testCount() throws Exception {
        System.out.println("Test count");
        Long validId = 123L;
        Long expectedCount = 10L;
        when(productoPrecioBean.countByIdProducto(validId)).thenReturn(expectedCount);
        Response response = productoPrecioResource.count(validId);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(expectedCount, response.getEntity());
        verify(productoPrecioBean, times(1)).countByIdProducto(validId);

        // Caso 2: ID nulo
        Response nullResponse = productoPrecioResource.count(null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), nullResponse.getStatus());
        assertEquals("El ID del producto no puede ser nulo o menor que 0.", nullResponse.getEntity());
        verify(productoPrecioBean, times(1)).countByIdProducto(validId);

        // Caso 3: ID negativo
        Long negativeId = -5L;
        Response negativeResponse = productoPrecioResource.count(negativeId);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), negativeResponse.getStatus());
        assertEquals("El ID del producto no puede ser nulo o menor que 0.", negativeResponse.getEntity());
        verify(productoPrecioBean, times(1)).countByIdProducto(validId);

        // Caso 4: Excepción lanzada
        Long exceptionId = 456L;
        RuntimeException exception = new RuntimeException("Error al contar productos");
        when(productoPrecioBean.countByIdProducto(exceptionId)).thenThrow(exception);
        ProductoPrecioResource mockResource = spy(productoPrecioResource);
        Response expectedExceptionResponse = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Ocurrió un error inesperado al procesar la solicitud para el producto con ID: 456")
                .build();
        doReturn(expectedExceptionResponse).when(mockResource).responseExcepcions(exception, exceptionId);
        Response exceptionResponse = mockResource.count(exceptionId);
        assertEquals(expectedExceptionResponse.getStatus(), exceptionResponse.getStatus());
        assertEquals(expectedExceptionResponse.getEntity(), exceptionResponse.getEntity());
        verify(productoPrecioBean, times(1)).countByIdProducto(exceptionId);
        verify(mockResource, times(1)).responseExcepcions(exception, exceptionId);
        //fail("Esta prueba no pasa quemado");
    }
}
