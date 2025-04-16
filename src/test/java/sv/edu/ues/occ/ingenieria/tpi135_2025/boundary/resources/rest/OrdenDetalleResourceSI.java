package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;


import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.DatosMixtosDTO;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.OrdenDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.OrdenDetallePK;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoPrecio;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.*;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrdenDetalleResourceSI extends AbstractContainerTest{

    private static final Logger log = LoggerFactory.getLogger(OrdenResourceSI.class);
    private Client client;
    private WebTarget target;

    @BeforeAll
    public void inicializar() {
        cliente = ClientBuilder.newClient();
        target = cliente.target(String.format("http://%s:%d/PupaSv-1.0-SNAPSHOT/v1/",servidorDeAplicaion.getHost(),
                servidorDeAplicaion.getMappedPort(9080)));

        // Insertar datos de prueba para el test de actualización
        OrdenDetalle odParaActualizar = new OrdenDetalle();
        odParaActualizar.setOrdenDetallePK(new OrdenDetallePK(123L, 456L));
        odParaActualizar.setCantidad(2);
        odParaActualizar.setPrecio(new BigDecimal("10.50"));
        odParaActualizar.setProductoPrecio(new ProductoPrecio(456L));

        Response postResponse = target.path("/ordenDetalle")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(odParaActualizar, MediaType.APPLICATION_JSON));
        System.out.println("Setup POST status (update test): " + postResponse.getStatus());
        if (postResponse.getStatus() != 201) {
            System.err.println("Error setting up test data for update: " + postResponse.readEntity(String.class));
        }
        postResponse.close();

        // Insertar datos de prueba para el test de eliminación
        OrdenDetalle odParaEliminar = new OrdenDetalle();
        odParaEliminar.setOrdenDetallePK(new OrdenDetallePK(789L, 101L));
        odParaEliminar.setCantidad(1);
        odParaEliminar.setPrecio(new BigDecimal("5.75"));
        odParaEliminar.setProductoPrecio(new ProductoPrecio(101L));

        postResponse = target.path("/ordenDetalle")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(odParaEliminar, MediaType.APPLICATION_JSON));
        //System.out.println("Setup POST status (delete test): " + postResponse.getStatus());
        if (postResponse.getStatus() != 201) {
            //System.err.println("Error setting up test data for delete: " + postResponse.readEntity(String.class));
        }
        postResponse.close();

    }

    @AfterAll
    public void finalizar() {
        // Limpiar datos de prueba
        Response deleteResponse1 = target.path("/ordenDetalle/orden/123/productoPrecio/456")
                .request()
                .delete();
        System.out.println("Teardown DELETE status (123/456): " + deleteResponse1.getStatus());
        deleteResponse1.close();

        Response deleteResponse2 = target.path("/ordenDetalle/orden/789/productoPrecio/101")
                .request()
                .delete();
        System.out.println("Teardown DELETE status (789/101): " + deleteResponse2.getStatus());
        deleteResponse2.close();

        if (client != null) {
            client.close();
        }
    }
    @Test
    @Order(1)
    public void testBuscarOrdenDetalleNoEncontrado() {
        System.out.println("testBuscarOrdenDetalleNoEncontradoSI");
        int idOrden = 999;
        int idProducto = 999;

        Response response = target.path("/ordenDetalle/orden/{idOrden}/productoPrecio/{idProductoPrecio}")
                .resolveTemplate("idOrden", 999L)
                .resolveTemplate("idProductoPrecio", 999L)
                .request()
                .get();
        assertEquals(404, response.getStatus(), "El código de estado debería ser 404");

        String header = response.getHeaderString(Headers.NOT_FOUND_ID);
        assertNotNull(header, "El header 'X-Not-Found-Id' debería estar presente.");
        assertNotNull(header, "El header personalizado debería estar presente");
        assertEquals("idOrden: 999 , idProducto: 999", header, "El contenido del header no es el esperado");

        String body = response.readEntity(String.class);
        assertTrue(body == null || body.isBlank(), "El cuerpo debería estar vacío o nulo");
        //fail("Esta prueba no pasa quemado ");
    }


  /**  @Test
    @Order(2)
    public void testDeleteOrdenDetalleSuccess() {
        System.out.println("testDeleteOrdenDetalleSuccess");
        long idOrden = 789L;
        long idProductoPrecio = 101L;

        // Realiza una solicitud DELETE para eliminar el orden detalle
        Response response = target.path("/ordenDetalle/orden/{idOrden}/productoPrecio/{idProductoPrecio}")
                .resolveTemplate("idOrden", idOrden)
                .resolveTemplate("idProductoPrecio", idProductoPrecio)
                .request()
                .delete();
        System.out.println("Código de estado: " + response.getStatus());

        assertEquals(204, response.getStatus(), "El código de estado debe ser 204 No Content");
        String responseBody = response.readEntity(String.class);
        System.out.println("Cuerpo de la respuesta: " + responseBody);
        assertTrue(responseBody.isEmpty(), "La respuesta debe estar vacía");
        Response checkResponse = target.path("/ordenDetalle/orden/{idOrden}/productoPrecio/{idProductoPrecio}")
                .resolveTemplate("idOrden", idOrden)
                .resolveTemplate("idProductoPrecio", idProductoPrecio)
                .request()
                .get();
        assertEquals(404, checkResponse.getStatus(), "Después de eliminar, debería ser 404 Not Found");
        checkResponse.close();
         fail("Esta prueba no pasa quemado"); // Removed the forced failure to allow the test to pass if the logic is correct
    }

    @Test
    @Order(3)
    public void testUpdateOrdenDetalleSuccess() {
        System.out.println("testUpdateOrdenDetalleSuccess");
        System.out.println("testUpdateOrdenDetalleSuccess");
        long idOrden = 123L;
        long idProductoPrecio = 456L;

        OrdenDetalle detalleActualizado = new OrdenDetalle();
        detalleActualizado.setOrdenDetallePK(new OrdenDetallePK(idOrden, idProductoPrecio));
        detalleActualizado.setCantidad(5);
        detalleActualizado.setPrecio(new BigDecimal("12.00"));
        detalleActualizado.setProductoPrecio(new ProductoPrecio(idProductoPrecio)); // Asegúrate de que el ID del producto precio sea el mismo

        Response response = target.path("/ordenDetalle")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(detalleActualizado, MediaType.APPLICATION_JSON));

        System.out.println("Código de estado (Update): " + response.getStatus());
        System.out.println("Cuerpo de la respuesta (Update): " + response.readEntity(String.class));

        assertEquals(200, response.getStatus(), "El código de estado debe ser 200 OK para la actualización");
        response.close();

        Response getResponse = target.path("/ordenDetalle/orden/{idOrden}/productoPrecio/{idProductoPrecio}")
                .resolveTemplate("idOrden", idOrden)
                .resolveTemplate("idProductoPrecio", idProductoPrecio)
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(200, getResponse.getStatus(), "Después de actualizar, debería ser 200 OK al obtener");
        OrdenDetalle ordenDetalleVerificado = getResponse.readEntity(OrdenDetalle.class);
        assertEquals(5, ordenDetalleVerificado.getCantidad(), "La cantidad debe haber sido actualizada");
        assertEquals(new BigDecimal("60.00"), ordenDetalleVerificado, "El subtotal debe haber sido actualizado");
        getResponse.close();
    }
**/
    @Test
    @Order(4)
    public void testGenerarOrdenDetalleProducto() {
        System.out.println("testGenerarOrdenDetalleProducto");

        Response response;
        String errorMessage;

        response = target.path("/ordenDetalle/producto")
                .queryParam("orden", 0)
                .queryParam("producto", 5)
                .queryParam("cantidad", 3)
                .request()
                .get();
        System.out.println("Código de estado (BadRequest - Orden 0): " + response.getStatus());
        errorMessage = response.readEntity(String.class);
        System.out.println("Cuerpo de la respuesta (BadRequest - Orden 0): " + errorMessage);
        assertEquals(400, response.getStatus(), "El código de estado debe ser 400 Bad Request");
        assertEquals("Todos los parámetros son obligatorios y deben ser mayores a cero.", errorMessage, "El mensaje de error debe ser correcto");
        response.close();

        response = target.path("/ordenDetalle/producto")
                .queryParam("orden", 1)
                .queryParam("producto", 0)
                .queryParam("cantidad", 3)
                .request()
                .get();
        System.out.println("Código de estado (BadRequest - Producto 0): " + response.getStatus());
        errorMessage = response.readEntity(String.class);
        System.out.println("Cuerpo de la respuesta (BadRequest - Producto 0): " + errorMessage);
        assertEquals(400, response.getStatus(), "El código de estado debe ser 400 Bad Request");
        assertEquals("Todos los parámetros son obligatorios y deben ser mayores a cero.", errorMessage, "El mensaje de error debe ser correcto");
        response.close();

        response = target.path("/ordenDetalle/producto")
                .queryParam("orden", 1)
                .queryParam("producto", 5)
                .queryParam("cantidad", 0)
                .request()
                .get();
        System.out.println("Código de estado (BadRequest - Cantidad 0): " + response.getStatus());
        errorMessage = response.readEntity(String.class);
        System.out.println("Cuerpo de la respuesta (BadRequest - Cantidad 0): " + errorMessage);
        assertEquals(400, response.getStatus(), "El código de estado debe ser 400 Bad Request");
        assertEquals("Todos los parámetros son obligatorios y deben ser mayores a cero.", errorMessage, "El mensaje de error debe ser correcto");
        response.close();
       // fail("Esta prueba no pasa ");
    }

    /**
    @Test
    @Order(5)
    public void testGenerarOrdenDetalleDesdeComboSuccess() throws IOException {
        System.out.println("testGenerarOrdenDetalleDesdeComboSuccess");
        long idOrden = 12345L;
        long idCombo = 1001L;
        int cantidadCombo = 1;

        Response response = target.path("/ordenDetalle/combo")
                .queryParam("orden", idOrden)
                .queryParam("combo", idCombo)
                .queryParam("cantidad", cantidadCombo)
                .request(MediaType.APPLICATION_JSON)
                .get();

        System.out.println("Código de estado (Generar Combo): " + response.getStatus());
        String responseBody = response.readEntity(String.class);
        System.out.println("Cuerpo de la respuesta (Generar Combo): " + responseBody);

        assertEquals(200, response.getStatus(), "El código de estado debe ser 200 OK");

        ObjectMapper mapper = new ObjectMapper();
        List<OrdenDetalle> detallesGenerados = mapper.readValue(responseBody, new TypeReference<List<OrdenDetalle>>() {});
        assertNotNull(detallesGenerados, "La respuesta debe contener una lista de OrdenDetalle");
        assertEquals(2, detallesGenerados.size(), "Deberían generarse 2 OrdenDetalle para este combo");

        boolean hasPupusas = false;
        boolean hasCoca = false;
        BigDecimal precioPupusa = null;
        BigDecimal precioCoca = null;

        for (OrdenDetalle detalle : detallesGenerados) {
            // Ahora la aserción de la orden debería pasar
            assertNotNull(detalle.getOrden(), "La orden asociada al detalle no debe ser nula");
            assertEquals(idOrden, detalle.getOrden().getIdOrden(), "El ID de orden debe coincidir");

            assertNotNull(detalle.getProductoPrecio().getIdProductoPrecio(), "El ID de producto precio no debe ser nulo");

            if (detalle.getProductoPrecio().getIdProductoPrecio() == 1003L) {
                hasPupusas = true;
                assertEquals(10 * cantidadCombo, detalle.getCantidad(), "Cantidad de pupusas incorrecta");
                precioPupusa = detalle.getPrecio();
                assertNotNull(precioPupusa, "El precio de la pupusa no debe ser nulo");
                assertEquals(BigDecimal.valueOf(1.00), precioPupusa, "El precio de la pupusa debe ser 1.00");
            } else if (detalle.getProductoPrecio().getIdProductoPrecio() == 1001L) {
                hasCoca = true;
                assertEquals(3 * cantidadCombo, detalle.getCantidad(), "Cantidad de coca incorrecta");
                precioCoca = detalle.getPrecio();
                assertNotNull(precioCoca, "El precio de la coca no debe ser nulo");
                assertEquals(BigDecimal.valueOf(1.00), precioCoca, "El precio de la coca debe ser 1.00");
            }
        }
        assertTrue(hasPupusas, "Debería incluir pupusas en el combo");
        assertTrue(hasCoca, "Debería incluir coca en el combo");
        response.close();
        fail("Esta prueba no pasa quemao¿do");
    }**/
    private int getCantidadBasePorProducto(long idProductoPrecio) {
        if (idProductoPrecio == 1003L) {
            return 10;
        } else if (idProductoPrecio == 1001L) {
            return 3;
        }
        return 1;
    }

    @Test
    @Order(6)
    public void testGenerarOrdenDetalleDesdeComboBadRequest() {
        System.out.println("testGenerarOrdenDetalleDesdeComboBadRequest");

        Response response;
        String errorMessage;

        response = target.path("/ordenDetalle/combo")
                .queryParam("orden", 0)
                .queryParam("combo", 1)
                .queryParam("cantidad", 2)
                .request()
                .get();
        System.out.println("Código de estado (BadRequest - Orden 0): " + response.getStatus());
        errorMessage = response.readEntity(String.class);
        System.out.println("Cuerpo de la respuesta (BadRequest - Orden 0): " + errorMessage);
        assertEquals(400, response.getStatus(), "El código de estado debe ser 400 Bad Request");
        assertEquals("Los parámetros 'orden' y 'combo' son obligatorios y deben ser mayores a cero.", errorMessage, "El mensaje de error debe ser correcto");
        response.close();

        response = target.path("/ordenDetalle/combo")
                .queryParam("orden", 3)
                .queryParam("combo", 0)
                .queryParam("cantidad", 2)
                .request()
                .get();
        System.out.println("Código de estado (BadRequest - Combo 0): " + response.getStatus());
        errorMessage = response.readEntity(String.class);
        System.out.println("Cuerpo de la respuesta (BadRequest - Combo 0): " + errorMessage);
        assertEquals(400, response.getStatus(), "El código de estado debe ser 400 Bad Request");
        assertEquals("Los parámetros 'orden' y 'combo' son obligatorios y deben ser mayores a cero.", errorMessage, "El mensaje de error debe ser correcto");
        response.close();
        //fail("Esta prueba no pasa queamdo");
    }

    @Test
    @Order(7)
    public void testGenerarOrdenDetalleDesdeComboNoContent() {
        System.out.println("testGenerarOrdenDetalleDesdeComboNoContent");
        long idOrden = 4L; // Reemplaza con un ID de orden que no tenga combos asociados (o el combo ID no genera detalles)
        long idCombo = 2L; // Reemplaza con un ID de combo que no genere detalles para esta orden
        int cantidadCombo = 1;

        Response response = target.path("/ordenDetalle/combo")
                .queryParam("orden", idOrden)
                .queryParam("combo", idCombo)
                .queryParam("cantidad", cantidadCombo)
                .request(MediaType.APPLICATION_JSON)
                .get();

        System.out.println("Código de estado (No Content): " + response.getStatus());
        String responseBody = response.readEntity(String.class);
        System.out.println("Cuerpo de la respuesta (No Content): " + responseBody);

        assertEquals(204, response.getStatus(), "El código de estado debe ser 204 No Content");
        assertTrue(responseBody.isEmpty(), "El cuerpo de la respuesta debe estar vacío");
        response.close();
       // fail("Esta prueba no pasa quemado");
    }
/**
    @Test
    @Order(8)
    public void testGenerarOrdeDetalleMixto() throws IOException {
        System.out.println("testGenerarOrdeDetalleMixto");
        DatosMixtosDTO datos = new DatosMixtosDTO();
        datos.setIdOrden(12345L);
        datos.setIdProductos(asList(1001L, 1002L));
        datos.setCantidadProductos(asList(2, 3));
        datos.setIdCombos(asList(2001L));
        datos.setCantidadCombo(asList(1));

        Response response = target.path("/ordenDetalle/mixto")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(datos, MediaType.APPLICATION_JSON));

        assertEquals(200, response.getStatus(), "El código de estado debe ser 200 OK");

        String responseBody = response.readEntity(String.class);
        System.out.println("Cuerpo de la respuesta: " + responseBody);

        ObjectMapper mapper = new ObjectMapper();
        List<OrdenDetalle> detallesGenerados = mapper.readValue(responseBody, new TypeReference<List<OrdenDetalle>>() {});

        assertNotNull(detallesGenerados, "La respuesta debe contener una lista de OrdenDetalle");
        assertTrue(detallesGenerados.size() > 0, "La lista de OrdenDetalle no debe estar vacía");

        assertEquals(datos.getIdProductos().size() + datos.getIdCombos().size(), detallesGenerados.size(),
                "La cantidad de detalles debe coincidir con la suma de productos y combos");
        for (int i = 0; i < detallesGenerados.size(); i++) {
            OrdenDetalle detalle = detallesGenerados.get(i);
            assertNotNull(detalle.getOrden(), "La orden asociada al detalle no debe ser nula");
            assertEquals(12345L, detalle.getOrden().getIdOrden(), "El ID de la orden debe coincidir");
            assertNotNull(detalle.getProductoPrecio().getIdProductoPrecio(), "El ID de producto precio no debe ser nulo");

            if (i < datos.getIdProductos().size()) {
                assertEquals(datos.getCantidadProductos().get(i), detalle.getCantidad(), "La cantidad del producto debe coincidir");
            } else {
                assertEquals(datos.getCantidadCombo().get(i - datos.getIdProductos().size()), detalle.getCantidad(), "La cantidad del combo debe coincidir");
            }
        }
        response.close();
    }
**/

}
