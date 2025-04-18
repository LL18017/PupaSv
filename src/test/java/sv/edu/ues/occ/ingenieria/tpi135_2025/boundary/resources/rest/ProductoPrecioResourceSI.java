
package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductoPrecioResourceSI extends AbstractContainerTest {

    private Client client;
    private WebTarget target;
    private Long createdId;
    private Long idProducto = 1001l;
    private Long noexisteIdProducto = 999l;


    @BeforeAll
    public void inicializar() {
        cliente = ClientBuilder.newClient();
        target = cliente.target(String.format("http://%s:%d/PupaSv-1.0-SNAPSHOT/v1/productoPrecio", servidorDeAplicaion.getHost(), servidorDeAplicaion.getMappedPort(9080)));
    }

    @AfterAll
    public void finalizar() {
        if (client != null) {
            client.close();
        }
    }

    @Order(1)
    @Test
    void testCrearProductoPrecio() {
        System.out.println("ProductoPrecio testSI CrearProductoPrecio");
        Map<String, Object> nuevoPrecio = new HashMap<>();
        nuevoPrecio.put("precioSugerido", 25.50);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        nuevoPrecio.put("fechaDesde", LocalDate.now().format(formatter));
        Map<String, Object> producto = new HashMap<>();
        producto.put("idProducto", idProducto);
        nuevoPrecio.put("producto", producto);

        Response response = target.request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(nuevoPrecio, MediaType.APPLICATION_JSON));

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        // Extraer el ID de la cabecera "Location"
        String location = response.getHeaderString("Location");
        assertNotNull(location);
        String[] parts = location.split("/");
        createdId = Long.parseLong(parts[parts.length - 1]);
        assertTrue(createdId > 0);
    }


    @Order(2)
    @Test
    void testObtenerProductoPrecioPorIdExistente() {
        System.out.println("ProductoPrecio testSI ObtenerProductoPrecioPorIdExistente");
        Response response = target.path(createdId.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Map<String, Object> precio = response.readEntity(Map.class);
        assertNotNull(precio);
        assertEquals(createdId.longValue(), ((Number) precio.get("idProductoPrecio")).longValue());
        // fail("Esta prueba no pasa quemado");
    }


    @Test
    @Order(3)
    void testObtenerTodosProductoPrecios() {
        System.out.println("ProductoPrecio testSI ObtenerProductoPrecios");
        Response response = target.request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<Map<String, Object>> precios = (List<Map<String, Object>>) response.readEntity(List.class);
        assertNotNull(precios);
        assertTrue(precios.size() >= 1);
        //fail("Esta prueba no pasa quemado");
    }

    @Order(4)
    @Test
    void testActualizarProductoPrecioExistente() {
        System.out.println("ProductoPrecio testSI ActualizarProductoPrecioExistente - createdId: " + createdId);
        Map<String, Object> precioActualizado = new HashMap<>();
        precioActualizado.put("idProductoPrecio", createdId);
        precioActualizado.put("precioSugerido", 30.00);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        precioActualizado.put("fechaDesde", LocalDate.now().plusDays(1).format(formatter));
        Map<String, Object> producto = new HashMap<>();
        producto.put("idProducto", idProducto);
        precioActualizado.put("producto", producto);

        Response response = target.path(createdId.toString())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(precioActualizado, MediaType.APPLICATION_JSON));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        // Puedes agregar más aserciones para verificar que los datos se actualizaron correctamente
    }

    @Test
    @Order(5)
    void testEliminarProductoPrecioExistente() {
        System.out.println("testEliminarProductoPrecioExistente - createdId: " + createdId);
        Response response = target.path(createdId.toString())
                .request(MediaType.APPLICATION_JSON)
                .delete();

        assertEquals(200, response.getStatus());
        Response getResponse = target.path(createdId.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(404, getResponse.getStatus()); // Cambiado a 500
    }

    @Test
    @Order(6)
    void testContarProductoPreciosPorProductoIdExistente() {
        System.out.println("ProductoPrecio testSI ContarProductoPreciosPorProductoIdExistente");
        Response response = target.path("producto/" + idProducto + "/count")
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        String count = response.readEntity(String.class);
        assertNotNull(count);
        assertTrue(Integer.parseInt(count) >= 0);
        //fail("Esta prueba no pasa quemado");
    }
}