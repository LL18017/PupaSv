package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.PagoDetalle;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PagoDetalleResourceSI extends AbstractContainerTest{
    Long totalEnScript = 5L;
    Long idParaTest = 1001L;
    Long idPagoDetalleCreado = 1006L;
    Long totalEnScriptParaIdTest = 4L;
    @BeforeAll
    public void inicializar() {
        cliente = ClientBuilder.newClient();
        target = cliente.target(String.format("http://%s:%d/PupaSv-1.0-SNAPSHOT/v1/", servidorDeAplicaion.getHost(),servidorDeAplicaion.getMappedPort(9080)));

    }

    @Order(1)
   @Test
//ljnaejbaéjbcáe
    public void testCreate() throws InterruptedException {
        System.out.println("PagoDetalle testSI create");
        PagoDetalle registro = new PagoDetalle();
        Long idPago=1003L;
        String path =String.format("pagoDetalle/%d",idPago);

        //flujo bueno con idTipoProducto
        Response respuesta = target.path(path)
                .request(MediaType.APPLICATION_JSON).
                post(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(201, respuesta.getStatus());

        String[] id = respuesta.getLocation().toString().split("/");
        idPagoDetalleCreado = Long.valueOf(id[id.length - 1]);
        totalEnScript++;

        //probar error de argumento malo
        respuesta = target.path(path).request(MediaType.APPLICATION_JSON).post(Entity.entity(null, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(500, respuesta.getStatus());

        //probar error de argumento idPago no existe
        path =String.format("pagoDetalle/%d",112233);
        respuesta = target.path(path).request(MediaType.APPLICATION_JSON).post(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(404, respuesta.getStatus());



//        Assertions.fail("fallo exitosamente");
    }

    @Order(2)
   @Test
    public void testGetRange() {
        System.out.println("pagoDetalle  testSI getRange");
        Assertions.assertTrue(servidorDeAplicaion.isRunning());

        String path = "pagoDetalle";

        //todos los registros
        Response respuesta = target.path(path).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertNotNull(respuesta);
        List<PagoDetalle> registros = respuesta.readEntity(new GenericType<List<PagoDetalle>>() {
        });

//        Assertions.assertEquals(totalEnScript, registros.size());

        //todos con idParaTest
        respuesta = target.path(path).queryParam("idPago", idParaTest).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertNotNull(respuesta);
        registros = respuesta.readEntity(new GenericType<List<PagoDetalle>>() {
        });
        Assertions.assertEquals(totalEnScriptParaIdTest, registros.size());



        //fallo de argumentos first max
        respuesta = target.path(path).queryParam("first", first).queryParam("max", 0).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(400, respuesta.getStatus());
//
//        //fallo de argumentos id Inexistente
        respuesta = target.path(path).queryParam("idPago", 112233).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(404, respuesta.getStatus());
//        Assertions.fail("fallo exitosamente");
    }

    @Order(3)
   @Test
    public void testUpdate() {
        System.out.println("pagoDetalle  testSI update");
        String path = String.format("pagoDetalle/%d", idParaTest);
        BigDecimal monto=BigDecimal.valueOf(99.99);
        PagoDetalle registro = new PagoDetalle();
        registro.setMonto(monto);

        Response respuesta = target.path(path).request(MediaType.APPLICATION_JSON).put(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Response respuestaPeticion = target.path(path).request(MediaType.APPLICATION_JSON).get();

        Assertions.assertEquals(200, respuesta.getStatus());
        PagoDetalle rpp = respuestaPeticion.readEntity(PagoDetalle.class);
        Assertions.assertNotNull(rpp);
        assertEquals(idParaTest, rpp.getIdPagoDetalle());
        Assertions.assertEquals(monto , rpp.getMonto());

        //fallo de argumentos id inexistentes
        path = String.format("pagoDetalle/%d", 12345);
        respuestaPeticion = target.path(path)
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(404, respuestaPeticion.getStatus());

        //fallo de argumentos  idProducto menor a 0
        path = String.format("pagoDetalle/%d", -90);
        respuestaPeticion = target.path(path)
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(400, respuestaPeticion.getStatus());

    }

    @Order(4)
   @Test
    public void testGetById() {
        System.out.println("pagoDetalle  testSI getById");
        String formato = String.format("pagoDetalle/%d", idParaTest);

        Response respuesta = target.path(formato)  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(idParaTest, respuesta.readEntity(PagoDetalle.class).getIdPagoDetalle());

        //fallo de argumentos id inexistentes
        respuesta = target.path(String.format("pagoDetalle/%d", 12345))  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(404, respuesta.getStatus());
        //fallo de argumentos  idProducto menor a 0
        respuesta = target.path(String.format("pagoDetalle/%d", -89))  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(400, respuesta.getStatus());

//        Assertions.fail("fallo exitosamente");

    }

    @Order(5)
   @Test
    public void testDelete() {
        // Esta prueba está vacía, así que agregaríamos la lógica de la prueba aquí
        System.out.println("pagoDetalle  testSI delete");


        //fallo de argumentos id inexistentes
        Response respuestaPeticion = target.path(String.format("pagoDetalle/%d", 12345))  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(404, respuestaPeticion.getStatus());
        //fallo de argumentos  idProducto menor a 0
        respuestaPeticion = target.path(String.format("/pagoDetalle/%d", -89))  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(400, respuestaPeticion.getStatus());

        //eliminacion con relacion
        String formato = String.format("pagoDetalle/%d", idPagoDetalleCreado);
        respuestaPeticion = target.path(String.format(formato))
                .request(MediaType.APPLICATION_JSON).delete();
        Assertions.assertEquals(200, respuestaPeticion.getStatus());

        Response comprobacion = target.path(formato).
                request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(404, comprobacion.getStatus());


//        Assertions.fail("fallo exitosamente");

    }
}
