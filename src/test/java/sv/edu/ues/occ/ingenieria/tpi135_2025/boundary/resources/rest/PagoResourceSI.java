package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;


import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Orden;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Pago;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PagoResourceSI extends AbstractContainerTest {

    Long totalEnScript = 4L;
    Long idParaTest = 1001L;
    Long idPagoCreado = 1001L;
    Long idOrden = 12347L;

    @BeforeAll
    public void inicializar() {
        cliente = ClientBuilder.newClient();
        target = cliente.target(String.format("http://%s:%d/PupaSv-1.0-SNAPSHOT/v1/", servidorDeAplicaion.getHost(), servidorDeAplicaion.getMappedPort(9080)));

    }

    @Order(1)
    @Test

    public void testCreate() throws InterruptedException {
        System.out.println("Pago testSI create");
        Pago registro = new Pago();
        registro.setIdOrden(new Orden(idOrden));
        registro.setMetodoPago("cash");
        String path = String.format("pago");

        //flujo bueno con idTipoPago
        Response respuesta = target.path(path)
                .request(MediaType.APPLICATION_JSON).
                post(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(201, respuesta.getStatus());

        String[] id = respuesta.getLocation().toString().split("/");
        idPagoCreado = Long.valueOf(id[id.length - 1]);
        totalEnScript++;


        //probar error de argumento malo
        respuesta = target.path(path).request(MediaType.APPLICATION_JSON).post(Entity.entity(null, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(500, respuesta.getStatus());


//        Assertions.fail("fallo exitosamente");
    }

    @Order(2)
    @Test
    public void testGetRange() {
        System.out.println("Pago  testSI getRange");
        Assertions.assertTrue(servidorDeAplicaion.isRunning());

        String path = "pago";
        //todos los registros
        Response respuesta = target.path(path).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertNotNull(respuesta);
        List<Pago> registros = respuesta.readEntity(new GenericType<List<Pago>>() {
        });
        Assertions.assertEquals(totalEnScript, registros.size());

        //fallo de argumentos first max
        respuesta = target.path(path).queryParam("first", first).queryParam("max", 0).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(400, respuesta.getStatus());
//

    }

    @Order(3)
    @Test
    public void testUpdate() {
        System.out.println("pago  testSI update");
        String path = String.format("pago/%d", idParaTest);
        String metodoDePagoModificado = "bitcoin";
        String referenciaModificado = "referencia de prueba test";

        Pago registro = new Pago();
        registro.setMetodoPago(metodoDePagoModificado);
        registro.setReferencia(referenciaModificado);

        Response respuesta = target.path(path).request(MediaType.APPLICATION_JSON).put(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Response respuestaPeticion = target.path(path).request(MediaType.APPLICATION_JSON).get();

        Assertions.assertEquals(200, respuesta.getStatus());
        Pago rpp = respuestaPeticion.readEntity(Pago.class);
        Assertions.assertNotNull(rpp);
        Assertions.assertEquals(metodoDePagoModificado, rpp.getMetodoPago());
        assertEquals(referenciaModificado, rpp.getReferencia());
        Assertions.assertEquals(idParaTest, rpp.getIdPago());

        //fallo de argumentos id inexistentes
        path = String.format("pago/%d", 12345);
        respuestaPeticion = target.path(path)
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(404, respuestaPeticion.getStatus());

        //fallo de argumentos  idPago menor a 0
        path = String.format("pago/%d", -90);
        respuestaPeticion = target.path(path)
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(400, respuestaPeticion.getStatus());

    }

    @Order(4)
    @Test
    public void testGetById() {
        System.out.println("Pago  testSI getById");
        String formato = String.format("pago/%d", idParaTest);

        Response respuesta = target.path(formato)  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(idParaTest, respuesta.readEntity(Pago.class).getIdPago());

        //fallo de argumentos id inexistentes
        respuesta = target.path(String.format("pago/%d", 12345))  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(404, respuesta.getStatus());
        //fallo de argumentos  idPago menor a 0
        respuesta = target.path(String.format("pago/%d", -89))  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(400, respuesta.getStatus());

//        Assertions.fail("fallo exitosamente");

    }

    @Order(5)
    @Test
    public void testDelete() {
        System.out.println("Pago  testSI delete");

        //fallo de argumentos id inexistentes
        Response respuestaPeticion = target.path(String.format("pago/%d", 12345))  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(404, respuestaPeticion.getStatus());
        //fallo de argumentos  idPago menor a 0
        respuestaPeticion = target.path(String.format("pago/%d", -89))  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(400, respuestaPeticion.getStatus());

        //eliminacion sin relacion
        String formato = String.format("pago/%d", idPagoCreado);
        formato = String.format("pago/%d", idPagoCreado);
        respuestaPeticion = target.path(String.format(formato)).
                request(MediaType.APPLICATION_JSON).delete();
        Assertions.assertEquals(200, respuestaPeticion.getStatus());
        Response comprobacion = target.path(formato).
                request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(404, comprobacion.getStatus());


//        Assertions.fail("fallo exitosamente");

    }


    @Order(7)
    @Test
    public void testFindByIdOrden() {
        System.out.println("Pago  testSI findByIdOrden");
        Assertions.assertTrue(servidorDeAplicaion.isRunning());

        String path = String.format("pago/orden/%d", idOrden);
        Long cantidad = 2L;
        //todos los registros
        Response respuesta = target.path(path).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertNotNull(respuesta);
        List<Pago> registros = respuesta.readEntity(new GenericType<List<Pago>>() {
        });
        Assertions.assertEquals(cantidad, registros.size());

        //fallo de argumentos first max
        respuesta = target.path(path).queryParam("first", first).queryParam("max", 0).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(400, respuesta.getStatus());
        //id Inexistente
        path = String.format("pago/orden/%d", 112233);
        respuesta = target.path(path).queryParam("first", first).queryParam("max", max).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(404, respuesta.getStatus());
//

    }

}
