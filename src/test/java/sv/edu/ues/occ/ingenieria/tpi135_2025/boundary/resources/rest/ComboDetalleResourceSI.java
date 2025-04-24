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
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ComboDetalle;

/**
 *
 * @author hdz
 */
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ComboDetalleResourceSI extends AbstractContainerTest {

    Long totalEnScript = 6L;

    Long idComboPrueba = 1001L;
    Long idProductoPrueba = 1003L;

    Long idComboCreado = 1003L;
    Long idProductoCreado = 1004L;

    @BeforeAll
    public void inicializar() {
        cliente = ClientBuilder.newClient();
        target = cliente.target(String.format("http://%s:%d/PupaSv-1.0-SNAPSHOT/v1/",
                servidorDeAplicaion.getHost(), servidorDeAplicaion.getMappedPort(9080)));
    }

    @Order(1)
    @Test
    public void testCreate() {
        System.out.println("ComboDetalle testSI create");
        ComboDetalle detalle = new ComboDetalle();

        String path = String.format("comboDetalle/combo/%d/producto/%d",
                idComboCreado, idProductoCreado);
        Response respuesta = target.path(path).request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(detalle, MediaType.APPLICATION_JSON));
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(201, respuesta.getStatus());
        totalEnScript++;

        // entidad nula
        respuesta = target.path(path).request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(null, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(500, respuesta.getStatus());

        // idProducto inexistente
        path = String.format("comboDetalle/combo/%d/producto/%d", idComboCreado, 999999);
        respuesta = target.path(path).request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(detalle, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(404, respuesta.getStatus());

        // idCombo inexistente
        path = String.format("comboDetalle/combo/%d/producto/%d", 999999, idProductoCreado);
        respuesta = target.path(path).request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(detalle, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(404, respuesta.getStatus());

        // idCombo negativo
        path = String.format("comboDetalle/combo/%d/producto/%d", -10, idProductoCreado);
        respuesta = target.path(path).request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(detalle, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(400, respuesta.getStatus());
    }

    @Order(2)
    @Test
    public void testGetBean() {
        System.out.println("ComboDetalle testSI getBean");
        Assertions.assertTrue(servidorDeAplicaion.isRunning());

        // Obtener todos
        String formatoPath = "comboDetalle";
        Response resp = target.path(formatoPath)
                .request(MediaType.APPLICATION_JSON)
                .get();

        Assertions.assertEquals(200, resp.getStatus()); // aqui esta mandando el error 
        List<ComboDetalle> lista = resp.readEntity(new GenericType<List<ComboDetalle>>() {
        });
        Assertions.assertEquals(totalEnScript, lista.size());

        // Obtener uno válido
        String path = String.format("comboDetalle/combo/%d/producto/%d", idComboPrueba, idProductoPrueba);
        resp = target.path(path)
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(200, resp.getStatus());
        ComboDetalle registro = resp.readEntity(ComboDetalle.class);
        Assertions.assertEquals(idComboPrueba, registro.getComboDetallePK().getIdCombo());
        Assertions.assertEquals(idProductoPrueba, registro.getComboDetallePK().getIdProducto());

//         No existente
        path = String.format("comboDetalle/combo/%d/producto/%d", idComboPrueba, 999999);
        resp = target.path(path)
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(404, resp.getStatus());

        // ID negativo
        path = String.format("comboDetalle/combo/%d/producto/%d", idComboPrueba, -1);
        resp = target.path(path)
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(400, resp.getStatus());
    }

    @Order(3)
    @Test
    public void testUpdate() {
        System.out.println("ComboDetalle testSI update");

        ComboDetalle detalle = new ComboDetalle();
        detalle.setCantidad(777);
        detalle.setActivo(true);

        String path = String.format("comboDetalle/combo/%d/producto/%d", idComboPrueba, idProductoPrueba);
        Response respuesta = target.path(path).request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(detalle, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(200, respuesta.getStatus());

        Response comprobacion = target.path(path).request(MediaType.APPLICATION_JSON).get();
        ComboDetalle comprobado = comprobacion.readEntity(ComboDetalle.class);
        Assertions.assertEquals(777, comprobado.getCantidad());

        // id negativo
        path = String.format("comboDetalle/combo/%d/producto/%d", -1, idProductoPrueba);
        respuesta = target.path(path).request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(detalle, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(400, respuesta.getStatus());

        // id inexistente
        path = String.format("comboDetalle/combo/%d/producto/%d", 999999, idProductoPrueba);
        respuesta = target.path(path).request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(detalle, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(404, respuesta.getStatus());
    }

    @Order(4)
    @Test
    public void testDelete() {
        System.out.println("ComboDetalle testSI delete");

        String path = String.format("comboDetalle/combo/%d/producto/%d", idComboCreado, idProductoCreado);
        Response respuesta = target.path(path).request(MediaType.APPLICATION_JSON).delete();
        Assertions.assertEquals(200, respuesta.getStatus());

        Response comprobacion = target.path(path).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(404, comprobacion.getStatus());

        // combo inexistente
        path = String.format("comboDetalle/combo/%d/producto/%d", 99999, idProductoPrueba);
        respuesta = target.path(path).request(MediaType.APPLICATION_JSON).delete();
        Assertions.assertEquals(404, respuesta.getStatus()); // error linea 175

        // producto inexistente
        path = String.format("comboDetalle/combo/%d/producto/%d", idComboPrueba, 99999);
        respuesta = target.path(path).request(MediaType.APPLICATION_JSON).delete();
        Assertions.assertEquals(404, respuesta.getStatus());

        // combo negativo
        path = String.format("comboDetalle/combo/%d/producto/%d", -1, idProductoPrueba);
        respuesta = target.path(path).request(MediaType.APPLICATION_JSON).delete();
        Assertions.assertEquals(400, respuesta.getStatus());
    }
}
