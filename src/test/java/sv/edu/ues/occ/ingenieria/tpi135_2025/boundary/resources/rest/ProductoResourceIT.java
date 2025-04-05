package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.persistence.EntityManagerFactory;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.xml.bind.SchemaOutputResolver;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductoResourceIT extends AbstractContainerTest {

    Long totalEnScript = 4L;
    Long idParaTest = 1001L;
    Integer idTipoProductoCreado = 1003;
    Long idCreadoConRelacion = 0L;
    Long idCreadoSinRelacion = 0L;

    @BeforeAll
    public void inicializar() {
        cliente = ClientBuilder.newClient();
        target = cliente.target(String.format("http://localhost:%d/PupaSv-1.0-SNAPSHOT/v1/", servidorDeAplicaion.getMappedPort(9080)));

    }

    @Order(1)
    @Test

    public void testCreate() throws InterruptedException {
        System.out.println("Producto testSI create");
        Producto registro = new Producto();
        registro.setNombre("bebidas test");
        registro.setActivo(false);
        String path = String.format("producto");

        //flujo bueno con idTipoProducto
        Response respuesta = target.path(path)
                .queryParam("idTipoProducto", idTipoProductoCreado)
                .request(MediaType.APPLICATION_JSON).
                post(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(201, respuesta.getStatus());

        String[] id = respuesta.getLocation().toString().split("/");
        idCreadoConRelacion = Long.valueOf(id[id.length - 1]);
        totalEnScript++;

        //flujo bueno sin idTipoProducto
        respuesta = target.path(path)
                .request(MediaType.APPLICATION_JSON).
                post(Entity.entity(registro, MediaType.APPLICATION_JSON));

        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(201, respuesta.getStatus());
        id = respuesta.getLocation().toString().split("/");
        idCreadoSinRelacion = Long.valueOf(id[id.length - 1]);
        totalEnScript++;

        //probar error de argumento malo
        respuesta = target.path(path).request(MediaType.APPLICATION_JSON).post(Entity.entity(null, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(500, respuesta.getStatus());

        //probar error de argumento idTipoProducto malo
        respuesta = target.path(path).queryParam("idTipoProducto", -90).request(MediaType.APPLICATION_JSON).post(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(400, respuesta.getStatus());


        //probar error de argumento idTipoProducto inexistenete
        respuesta = target.path(path).queryParam("idTipoProducto", 12345).request(MediaType.APPLICATION_JSON).post(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(404, respuesta.getStatus());


//        Assertions.fail("fallo exitosamente");
    }

    @Order(2)
    @Test
    public void testGetRange() {
        System.out.println("Producto  testSI getRange");
        Assertions.assertTrue(servidorDeAplicaion.isRunning());
        Integer idTipoProducto = 1001;//dos registros en db
        Long cantidadTotalActivos = 3L;//3 en db + 1  the create()
        Integer cantidadEsperadasActicos = 1;

        String path = "producto";

        //todos los registros
        Response respuesta = target.path(path).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertNotNull(respuesta);
        List<Producto> registros = respuesta.readEntity(new GenericType<List<Producto>>() {
        });

        Assertions.assertEquals(totalEnScript, registros.size());

        //todos los activos
        respuesta = target.path(path).queryParam("activo", true).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertNotNull(respuesta);
        registros = respuesta.readEntity(new GenericType<List<Producto>>() {
        });
        Assertions.assertEquals(cantidadTotalActivos, registros.size());

        //para idTipoProducto 1001 y con activo false deberia ser 0
        respuesta = target.path(path).queryParam("idTipoProducto", 1001).queryParam("activo", false).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertNotNull(respuesta);
        registros = respuesta.readEntity(new GenericType<List<Producto>>() {
        });
        Assertions.assertEquals(0, registros.size());

        //fallo de argumentos first max
        respuesta = target.path(path).queryParam("first", first).queryParam("max", 0).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(400, respuesta.getStatus());
//
//        //fallo de argumentos id Inexistente
        respuesta = target.path(path).queryParam("idTipoProducto", 112233).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(404, respuesta.getStatus());
//        Assertions.fail("fallo exitosamente");
    }

    @Order(3)
    @Test
    public void testUpdate() {
        System.out.println("Producto  testSI update");
        Long idProdcuto = 1L;
        Integer idTipoProducto = 1001;
        String path = String.format("producto/%d", idParaTest);
        String nombre = "producto de prueba test";
        String observeacion = "observacion de prueba test";

        Producto registro = new Producto();
        registro.setNombre(nombre);
        registro.setObservaciones(observeacion);

        Response respuesta = target.path(path).request(MediaType.APPLICATION_JSON).put(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Response respuestaPeticion = target.path(path).request(MediaType.APPLICATION_JSON).get();


        Assertions.assertEquals(200, respuesta.getStatus());
        Producto rpp = respuestaPeticion.readEntity(Producto.class);
        Assertions.assertNotNull(rpp);
        Assertions.assertEquals(nombre, rpp.getNombre());
        assertEquals(observeacion, rpp.getObservaciones());
        Assertions.assertEquals(idParaTest , rpp.getIdProducto());

        //fallo de argumentos id inexistentes
        path = String.format("producto/%d", 12345);
        respuestaPeticion = target.path(path)
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(404, respuestaPeticion.getStatus());

        //fallo de argumentos  idProducto menor a 0
        path = String.format("producto/%d", -90);
        respuestaPeticion = target.path(path)
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(400, respuestaPeticion.getStatus());

    }

    @Order(4)
    @Test
    public void testGetById() {
        System.out.println("Producto  testSI getById");
        Long idBuscado = 1001L;//ya existe en el scrip de db
        String formato = String.format("producto/%d", idBuscado);

        Response respuesta = target.path(formato)  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(idBuscado, respuesta.readEntity(Producto.class).getIdProducto());

        //fallo de argumentos id inexistentes
        respuesta = target.path(String.format("producto/%d", 12345))  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(404, respuesta.getStatus());
        //fallo de argumentos  idProducto menor a 0
        respuesta = target.path(String.format("producto/%d", -89))  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(400, respuesta.getStatus());

//        Assertions.fail("fallo exitosamente");

    }

    @Order(5)
    @Test
    public void testDelete() {
        // Esta prueba está vacía, así que agregaríamos la lógica de la prueba aquí
        System.out.println("Producto  testSIdelete");


        //fallo de argumentos id inexistentes
        Response respuestaPeticion = target.path(String.format("producto/%d", 12345))  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(404, respuestaPeticion.getStatus());
        //fallo de argumentos  idProducto menor a 0
        respuestaPeticion = target.path(String.format("/producto/%d", -89))  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(400, respuestaPeticion.getStatus());

        //fallo por relaciomn

        String formato = String.format("producto/%d", idCreadoConRelacion);
        respuestaPeticion = target.path(String.format(formato)).
                request(MediaType.APPLICATION_JSON).delete();
        Assertions.assertEquals(500, respuestaPeticion.getStatus());

        //eliminacion sin relacion
        formato = String.format("producto/%d", idCreadoSinRelacion);
        respuestaPeticion = target.path(String.format(formato)).
                request(MediaType.APPLICATION_JSON).delete();
        Assertions.assertEquals(200, respuestaPeticion.getStatus());
        Response comprobacion = target.path(formato).
                request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(404, comprobacion.getStatus());

        //eliminacion con relacion
        formato = String.format("producto/%d", idCreadoConRelacion);
        respuestaPeticion = target.path(String.format(formato))
                .queryParam("idTipoProducto", idTipoProductoCreado).
                request(MediaType.APPLICATION_JSON).delete();
        Assertions.assertEquals(200, respuestaPeticion.getStatus());
        comprobacion = target.path(formato).
                request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(404, comprobacion.getStatus());


//        Assertions.fail("fallo exitosamente");

    }

}
