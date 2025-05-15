/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Combo;

/**
 *
 * @author hdz
 */
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ComboResourceSI extends AbstractContainerTest {

    private static final Logger log = LoggerFactory.getLogger(ComboResourceSI.class);

    @BeforeAll
    public void inicializar() {
        cliente = ClientBuilder.newClient();
        target = cliente.target(String.format("http://%s:%d/PupaSv-1.0-SNAPSHOT/v1/", servidorDeAplicaion.getHost(), servidorDeAplicaion.getMappedPort(9080)));
    }

    Integer first = 0;
    Integer max = 30;
    Long cantidadEnScript = 11L;
    Long idBase = 1001L; // ID de referencia de Combo
    Long idCreado=1L;

    @Order(1)
    @Test
    public void testGetBean() {
        System.out.println("Combo testSI get");
        Assertions.assertTrue(servidorDeAplicaion.isRunning());
        Response respuesta = target.path("combo").request(MediaType.APPLICATION_JSON).get();

        // Flujo correcto
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertNotNull(respuesta);
        List<Object[]> registros = respuesta.readEntity(new GenericType<List<Object[]>>() {
        });
        Assertions.assertEquals(10, registros.size());
       
        // Argumentos erróneos
        respuesta = target.path("combo").queryParam("first", first).queryParam("max", -80)
                .request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(400, respuesta.getStatus()); // segun el error es esta liena 63
    }

    @Order(2)
    @Test
    public void testCreate() {
        System.out.println("Combo testSI create");
        Combo registro = new Combo();
        registro.setNombre("nuevoCombo");
        registro.setActivo(true);
        // Flujo correcto
        Response respuesta = target.path("combo").request(MediaType.APPLICATION_JSON).post(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(201, respuesta.getStatus());

        String[] id = respuesta.getLocation().toString().split("/");
        idCreado = Long.valueOf(id[id.length - 1]);
    }

    @Order(3)
    @Test
    public void testUpdateCombo() {
        System.out.println("Combo testSI update");
        Combo registro = new Combo();
        registro.setIdCombo(idBase);
        registro.setNombre("superComboActualizado");
        registro.setActivo(false);

        // Flujo correcto
        Response respuesta = target.path(String.format("combo/%d", idBase)).request(MediaType.APPLICATION_JSON).put(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(200, respuesta.getStatus());

        // Argumentos erróneos
        respuesta = target.path(String.format("combo/%d", 0)).request(MediaType.APPLICATION_JSON).put(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(400, respuesta.getStatus());

        // ID no existente
        respuesta = target.path(String.format("combo/%d", 99999L)).request(MediaType.APPLICATION_JSON).put(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(404, respuesta.getStatus());
    }

    @Order(4)
    @Test
    public void testGetComboById() {
        System.out.println("Combo testSI getById");

        // Flujo correcto
        Response respuestaPeticion = target.path(String.format("combo/%d", idBase)).request(MediaType.APPLICATION_JSON).get();
        Combo respuesta = respuestaPeticion.readEntity(Combo.class);
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(200, respuestaPeticion.getStatus());
        Assertions.assertEquals(idBase, respuesta.getIdCombo());

        // Argumentos erróneos
        respuestaPeticion = target.path(String.format("combo/%d", -90)).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(400, respuestaPeticion.getStatus());

        // ID no existente
        respuestaPeticion = target.path(String.format("combo/%d", 99999L)).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(404, respuestaPeticion.getStatus());
    }

    @Order(5)
    @Test
    public void testDeleteCombo() {
        System.out.println("Combo testSI delete");
        Long id = idCreado; // ID de referencia existente para eliminar

        // Caso exitoso
        Response respuestaPeticion = target.path(String.format("combo/%d", id))
                .request(MediaType.APPLICATION_JSON).delete();
        Assertions.assertEquals(200, respuestaPeticion.getStatus());

        Response comprobacion = target.path(String.format("combo/%d", id))
                .request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(404, comprobacion.getStatus());

        // Caso con ID inválido (0) - debe dar 400
        respuestaPeticion = target.path("combo/0")
                .request(MediaType.APPLICATION_JSON).delete();
        Assertions.assertEquals(400, respuestaPeticion.getStatus());

        // Caso con ID inexistente (10000) - debe dar 404
        respuestaPeticion = target.path("combo/10000")
                .request(MediaType.APPLICATION_JSON).delete();
        Assertions.assertEquals(404, respuestaPeticion.getStatus());
    }

}