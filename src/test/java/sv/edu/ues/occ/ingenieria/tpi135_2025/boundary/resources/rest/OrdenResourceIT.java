package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Orden;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrdenResourceIT extends AbstractContainerTest {


    private static final Logger log = LoggerFactory.getLogger(OrdenResourceIT.class);

    @BeforeAll
    public void inicializar() {
        cliente = ClientBuilder.newClient();
        target = cliente.target(String.format("http://localhost:%d/PupaSv-1.0-SNAPSHOT/v1/", servidorDeAplicaion.getMappedPort(9080)));

    }

    Integer first = 0;
    Integer max = 10;
    Long cantidadEnScript = 3L;
    Long idBase = 12345L;

    @Order(1)
    @Test
    public void testGetBean() {
        System.out.println("testSI get");
        Assertions.assertTrue(servidorDeAplicaion.isRunning());
        Response respuesta = target.path("orden").request(MediaType.APPLICATION_JSON).get();

        //flujo bueno
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertNotNull(respuesta);
        List<Orden> registros = respuesta.readEntity(new GenericType<List<Orden>>() {
        });
        Assertions.assertEquals(cantidadEnScript, registros.size());

        //argumentos malos
        respuesta = target.path("orden").queryParam("first", first).queryParam("max", -80).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(400, respuesta.getStatus());
//        Assertions.fail("fallo exitosamente");
    }

    @Order(2)
    @Test
    public void testCreate() throws InterruptedException {
        System.out.println("testSI create");
        Orden registro = new Orden();
        registro.setFecha(new Date());
        registro.setSucursal("Zarsa");
        registro.setAnulada(false);
        //flujo bueno
        Response respuesta = target.path("orden").request(MediaType.APPLICATION_JSON).post(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(201, respuesta.getStatus());
        //Assertions.fail("fallo exitosamenet");
    }

    @Order(3)
    @Test
    public void testUpdate() {
        System.out.println("testSI update");
        String sucursal = "sa";
        boolean anulada = true;

        Orden registro = new Orden();
        registro.setIdOrden(idBase);
        registro.setSucursal(sucursal);
        registro.setAnulada(anulada);

        Response respuesta = target.path(String.format("orden/%d", idBase)).request(MediaType.APPLICATION_JSON).put(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(200, respuesta.getStatus());
        //fallo de argumentos
        respuesta = target.path(String.format("orden/%d", 0)).request(MediaType.APPLICATION_JSON).put(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(400, respuesta.getStatus());
        //fid No existe
        respuesta = target.path(String.format("orden/%d", 112233L)).request(MediaType.APPLICATION_JSON).put(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(404, respuesta.getStatus());

//        Assertions.fail("fallo exitosamente");
    }

    @Order(4)
    @Test
    public void testGetId() {
        // Esta prueba está vacía, así que agregaríamos la lógica de la prueba aquí
        System.out.println("testSI getById");

        //flujo bueno
        Response respuestaPeticion = target.path(String.format("orden/%d", idBase)).request(MediaType.APPLICATION_JSON).get();
        Orden respuesta = respuestaPeticion.readEntity(Orden.class);
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(200, respuestaPeticion.getStatus());
        Assertions.assertEquals(idBase, respuesta.getIdOrden());
        //fallo de argumentos
        respuestaPeticion = target.path(String.format("orden/%d", -90)).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(400, respuestaPeticion.getStatus());
        //id No existe
        respuestaPeticion = target.path(String.format("orden/%d", 112233L)).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(404, respuestaPeticion.getStatus());
//        Assertions.fail("fallo exitosamente");

    }

    @Order(5)
    @Test
    public void testDelete() {
        // Esta prueba está vacía, así que agregaríamos la lógica de la prueba aquí
        System.out.println("testSI delete");
        Long id = 1L;
        Orden registro = new Orden(id);
        Response respuestaPeticion = target.path(String.format("orden/%d", id)).
                request(MediaType.APPLICATION_JSON).delete();
        Assertions.assertEquals(200, respuestaPeticion.getStatus());
        Response comprobacion = target.path(String.format("orden/%d", id)).
                request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(404, comprobacion.getStatus());
//        Assertions.fail("fallo exitosamente");

    }

}
