
package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoPrecio;

import java.math.BigDecimal;
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

    private WebTarget target;
    private Long createdId=1001L;
    Long idProductoPrecioPrueba=1003L;
    Long totalRegistros=20L;


    @BeforeAll
    public void inicializar() {
        cliente = ClientBuilder.newClient();
        target = cliente.target(String.format("http://%s:%d/PupaSv-1.0-SNAPSHOT/v1/", servidorDeAplicaion.getHost(), servidorDeAplicaion.getMappedPort(9080)));
    }

    @Order(1)
    @Test
    public void testCreate() throws InterruptedException {
        System.out.println("ProductoPresio testSI create");
        ProductoPrecio registro = new ProductoPrecio();
        Long idProducto = 1004L;
        registro.setIdProducto(new Producto(idProducto));
        registro.setPrecioSugerido(BigDecimal.valueOf(1.50));

        //FLUJO BUENO
        String formatoPath = String.format("productoPrecio/%d", idProducto);
        Response respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).post(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertNotNull(respuesta);

        Assertions.assertEquals(201, respuesta.getStatus());
        totalRegistros++;
        String[] id=respuesta.getLocation().getPath().split("/");
        createdId=Long.parseLong(id[id.length-1]);

        //probar error de argumento ENTIDAD NULA
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).post(Entity.entity(null, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(500, respuesta.getStatus());


        //probar error de argumento IdProducto INEXISTENTE
        formatoPath = String.format("productoPrecio/%d", 112233L);
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).post(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(404, respuesta.getStatus());

        //probar error de argumento IdProducto INEXISTENTE
        formatoPath = String.format("productoPrecio/%d", -90);
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).post(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(400, respuesta.getStatus());


//        Assertions.fail("fallo exitosamente");
    }


    @Order(2)
    @Test
    void testObtenerProductoPrecioPorIdExistente() {
        System.out.println("ProductoPrecio testSI ObtenerProductoPrecioPorIdExistente");
        String formatoPath = String.format("productoPrecio/%d", idProductoPrecioPrueba);
        Response response = target.path(formatoPath)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        ProductoPrecio precio = response.readEntity(ProductoPrecio.class);
        assertNotNull(precio);
        assertEquals(idProductoPrecioPrueba,  precio.getIdProductoPrecio());
        // fail("Esta prueba no pasa quemado");
    }


    @Test
    @Order(3)
    void testObtenerTodosProductoPrecios() {
        System.out.println("ProductoPrecio testSI ObtenerProductoPrecios");
        String formatoPath = "productoPrecio";
        Response response = target.path(formatoPath).request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(200, response.getStatus());
        List<ProductoPrecio> resultados = response.readEntity(new GenericType<List<ProductoPrecio>>() {});


        Assertions.assertEquals(totalRegistros>20?20:totalRegistros, resultados.size());
        //fail("Esta prueba no pasa quemado");
    }

    @Order(4)
    @Test
    void testActualizarProductoPrecioExistente() {
        System.out.println("ProductoPrecio testSI ActualizarProductoPrecioExistente - createdId: " + createdId);
        BigDecimal precioActualizadoValor = BigDecimal.valueOf(30.00);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate formatoFechaActualizada = LocalDate.now().plusDays(1);

        ProductoPrecio registro = new ProductoPrecio();
        registro.setPrecioSugerido(precioActualizadoValor);
        registro.setFechaDesde(formatoFechaActualizada);
        registro.setIdProductoPrecio(createdId);
        String formatoPath = String.format("productoPrecio/%d", createdId);
        Response response = target.path(formatoPath)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(registro, MediaType.APPLICATION_JSON));
        ProductoPrecio resultado = response.readEntity(ProductoPrecio.class);
        assertEquals(200, response.getStatus());
        assertEquals(formatoFechaActualizada,resultado.getFechaDesde());
        assertEquals(precioActualizadoValor,resultado.getPrecioSugerido());
        assertEquals(createdId,resultado.getIdProductoPrecio());
        //fail("Esta prueba no pasa quemado");
    }

    @Test
    @Order(5)
    void testEliminarProductoPrecioExistente() {
        System.out.println("testEliminarProductoPrecioExistente - createdId: " + createdId);
        String formatoPath = String.format("productoPrecio/%d", createdId);
        Response response = target.path(formatoPath)
                .request(MediaType.APPLICATION_JSON)
                .delete();

        assertEquals(200, response.getStatus());
        Response getResponse = target.path(createdId.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(404, getResponse.getStatus());
        //fail("Esta prueba no pasa quemado");
    }

}