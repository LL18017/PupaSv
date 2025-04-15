package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;


import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;
import org.junit.platform.engine.TestExecutionResult;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Orden;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.OrdenDetalle;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrdenDetalleResourceSI extends AbstractContainerTest{

    @BeforeAll
    public void setup() {
        cliente = ClientBuilder.newClient();
        target = cliente.target(String.format("http://%s:%d/PupaSv-1.0-SNAPSHOT/v1/",
                servidorDeAplicaion.getHost(), servidorDeAplicaion.getMappedPort(9080)));
    }

    @Test
    @Order(1)
    void findRange_sinParametros_devuelvePrimeros20() {
        Response response = target.path("productoPrecio/ordenDetalle")
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<OrdenDetalle> detalles = response.readEntity(new GenericType<List<OrdenDetalle>>() {});
        assertEquals(20, detalles.size());
        assertNotNull(response.getHeaderString("Total-Record"));

        System.out.println("Logs del contenedor de OpenLiberty después de la prueba findRange_sinParametros:");
        System.out.println(servidorDeAplicaion.getLogs());
    }

    @AfterAll
    static void printAllContainerLogs() {
        System.out.println("Logs del contenedor de OpenLiberty al final de todas las pruebas:");
        System.out.println(servidorDeAplicaion.getLogs());
    }

    @Test
    @Order(2)
    void findRange_conParametrosValidos_devuelveRangoEspecifico() {
        int first = 5;
        int max = 10;
        Response response = target.path("productoPrecio/ordenDetalle")
                .queryParam("first", first)
                .queryParam("max", max)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<OrdenDetalle> detalles = response.readEntity(new GenericType<List<OrdenDetalle>>() {});
        assertEquals(max, detalles.size());
        assertNotNull(response.getHeaderString("Total-Record"));
    }

    @Test
    @Order(3)
    void findRange_conFirstNegativo_devuelveBadRequest() {
        Response response = target.path("productoPrecio/ordenDetalle")
                .queryParam("first", -5)
                .queryParam("max", 10)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(" first: -5,max: 10", response.getHeaderString("Wrong-Parameter"));
        assertEquals("s", response.getHeaderString("wrong parameter : max"));
    }

    @Test
    @Order(4)
    void findRange_conMaxNegativo_devuelveBadRequest() {
        Response response = target.path("productoPrecio/ordenDetalle")
                .queryParam("first", 0)
                .queryParam("max", -10)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(" first: 0,max: -10", response.getHeaderString("Wrong-Parameter"));
        assertEquals("s", response.getHeaderString("wrong parameter : max"));
    }

    @Test
    @Order(5)
    void findRange_conMaxMayorA50_devuelveBadRequest() {
        Response response = target.path("productoPrecio/ordenDetalle")
                .queryParam("first", 0)
                .queryParam("max", 51)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(" first: 0,max: 51", response.getHeaderString("Wrong-Parameter"));
        assertEquals("s", response.getHeaderString("wrong parameter : max"));
    }

}
