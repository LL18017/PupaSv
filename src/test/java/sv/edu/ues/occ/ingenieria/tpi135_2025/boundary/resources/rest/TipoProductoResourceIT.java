package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.TipoProducto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TipoProductoResourceIT extends AbstractContainerTest{
    Long totalEnScript=3L;
    Integer idParaTest=1001;
    Integer idCreado=1001;

    @BeforeAll
    public void inicializar() {
        cliente = ClientBuilder.newClient();
        target = cliente.target(String.format("http://localhost:%d/PupaSv-1.0-SNAPSHOT/v1/", servidorDeAplicaion.getMappedPort(9080)));

    }

    @Order(1)
   // @Test
    public void testCreate() throws InterruptedException {
        System.out.println("tipoProducto testSI create");
        TipoProducto registro = new TipoProducto();
        registro.setNombre("bebidas test");

        Response respuesta = target.path("tipoProducto").request(MediaType.APPLICATION_JSON).post(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(201, respuesta.getStatus());
        totalEnScript++;
        String[] id = respuesta.getLocation().toString().split("/");
        idCreado=Integer.parseInt(id[id.length-1]);
        respuesta = target.path("tipoProducto").request(MediaType.APPLICATION_JSON).post(Entity.entity(null, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(500, respuesta.getStatus());


//        Assertions.fail("fallo exitosamente");
    }
    @Order(2)
   // @Test
    public void testGetBean() {
        System.out.println("TipoProducto  testSI getBean");
        Assertions.assertTrue(servidorDeAplicaion.isRunning());
        Response respuesta = target.path("tipoProducto").request(MediaType.APPLICATION_JSON).get();

        //buscar por defecto
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertNotNull(respuesta);
        List<TipoProducto> registros = respuesta.readEntity(new GenericType<List<TipoProducto>>() {
        });
        Assertions.assertEquals(totalEnScript, registros.size());
        //argumentos malos
        respuesta = target.path("tipoProducto").queryParam("first", first).queryParam("max", -80).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(400, respuesta.getStatus());
        //fallo de argumentos
        respuesta=target.path("tipoProducto").queryParam("first",-9).queryParam("max",max).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(400, respuesta.getStatus());
//        Assertions.fail("fallo exitosamente");
    }
    @Order(3)
   // @Test
    public void testUpdate() {
        System.out.println("TipoProducto  testSI update");
        Integer id = 4;
        String nombre = "nombre de prueba test";
        String observeacion = "observacion de prueba test";

        TipoProducto registro = new TipoProducto();
        registro.setIdTipoProducto(id);
        registro.setNombre(nombre);
        registro.setObservaciones(observeacion);

        Response respuesta = target.path(String.format("tipoProducto/%d", id)).request(MediaType.APPLICATION_JSON).put(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Response respuestaPeticion = target.path(String.format("tipoProducto/%d", id)).request(MediaType.APPLICATION_JSON).get();
        TipoProducto rpp = respuestaPeticion.readEntity(TipoProducto.class);
        Assertions.assertNotNull(rpp);
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertEquals(nombre, rpp.getNombre());
        assertEquals(observeacion, rpp.getObservaciones());
        Assertions.assertEquals(id, rpp.getIdTipoProducto());

        //error de argumentos

        //fallo de argumentos
        respuesta = target.path(String.format("tipoProducto/%d", 12345)).request(MediaType.APPLICATION_JSON).put(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(404, respuesta.getStatus());


    }

    @Order(4)
   // @Test
    public void testGetId() {
        System.out.println("TipoProducto  testSI getById");

        Response respuesta = target.path(String.format("tipoProducto/%d", idParaTest))  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(idParaTest, respuesta.readEntity(TipoProducto.class).getIdTipoProducto());

        //fallo de argumentos
        respuesta = target.path(String.format("tipoProducto/%d", null))  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(404, respuesta.getStatus());

//        Assertions.fail("fallo exitosamente");

    }

    @Order(5)
   // @Test
    public void testDelete() {
        // Esta prueba está vacía, así que agregaríamos la lógica de la prueba aquí
        System.out.println("TipoProducto  testSIdelete");

        TipoProducto registro = new TipoProducto(idCreado);
        Response respuestaPeticion = target.path(String.format("tipoProducto/%d", idCreado)).
                request(MediaType.APPLICATION_JSON).delete();
        Assertions.assertEquals(200, respuestaPeticion.getStatus());
        Response comprobacion = target.path(String.format("tipoProducto/%d", idCreado)).
                request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(404, comprobacion.getStatus());


        //fallo de argumentos
        respuestaPeticion = target.path(String.format("tipoProducto/%d", 0))
                .request(MediaType.APPLICATION_JSON).delete();
        Assertions.assertEquals(400, respuestaPeticion.getStatus());

        respuestaPeticion = target.path(String.format("tipoProducto/%d", 1000))
                .request(MediaType.APPLICATION_JSON).delete();
        Assertions.assertEquals(404, respuestaPeticion.getStatus());


//        Assertions.fail("fallo exitosamente");

    }

}