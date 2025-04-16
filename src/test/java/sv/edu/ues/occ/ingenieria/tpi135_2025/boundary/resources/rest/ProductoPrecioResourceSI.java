
/**
package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoPrecio;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.TipoProducto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static org.junit.Assert.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductoPrecioResourceSI extends AbstractContainerTest{
    private static Client cliente;
    private static Jsonb jsonb;
    private static jakarta.ws.rs.client.WebTarget target;

    private static Long idCreado;
    private static Integer idTipoProductoCreado;


    OffsetDateTime offsetDateTime = OffsetDateTime.now();  // Este es tu OffsetDateTime
    Date date = Date.from(offsetDateTime.toInstant());

    @BeforeAll
    public static void inicializar() {
        cliente = ClientBuilder.newClient();
        target = cliente.target(String.format("http://%s:%d/PupaSv-1.0-SNAPSHOT/v1/",servidorDeAplicaion.getHost(),
                servidorDeAplicaion.getMappedPort(9080)));
        jsonb = JsonbBuilder.create();

        // Crear TipoProducto para relacionar
        var tipoProducto = new TipoProducto();
        tipoProducto.setNombre("TipoTest");
        tipoProducto.setActivo(true);
        tipoProducto.setObservaciones("Observación test");

        Response r = target.path("tipo-producto")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(jsonb.toJson(tipoProducto)));

        assertEquals(201, r.getStatus());
        TipoProducto creado = r.readEntity(TipoProducto.class);
        idTipoProductoCreado = creado.getIdTipoProducto();
        assertNotNull(idTipoProductoCreado);
    }

    @Test
    public void testCrearProductoPrecio() {
        System.out.println("testCrearProductoPrecio");
        ProductoPrecio precio = new ProductoPrecio();
        precio.setIdProducto(new Producto(1001L));
        precio.setPrecioSugerido(BigDecimal.valueOf(100.0));
        Date fechaDesde = Date.from(OffsetDateTime.now().toInstant());
        Date fechaHasta = Date.from(OffsetDateTime.now().plusDays(30).toInstant());

        precio.setFechaDesde(fechaDesde);
        precio.setFechaHasta(fechaHasta);
        Response response = target
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(precio, MediaType.APPLICATION_JSON));
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getLocation());
        fail("Esta prueba no pasa quemado");
    }


    @Test
    @Order(2)
    public void testObtenerPorId() {
        System.out.println("");
        Response r = target.path("producto-precio/{id}")
                .resolveTemplate("id", idCreado)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(200, r.getStatus());
        ProductoPrecio obtenido = r.readEntity(ProductoPrecio.class);
        assertEquals(idCreado, obtenido.getIdProductoPrecio());
    }

    @Test
    @Order(3)
    public void testActualizar() {
        Response rGet = target.path("producto-precio/{id}")
                .resolveTemplate("id", idCreado)
                .request(MediaType.APPLICATION_JSON)
                .get();
        ProductoPrecio original = rGet.readEntity(ProductoPrecio.class);
        original.setPrecioSugerido(BigDecimal.valueOf(3.99));

        Response rPut = target.path("producto-precio/{id}")
                .resolveTemplate("id", idCreado)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(jsonb.toJson(original)));

        assertEquals(200, rPut.getStatus());
    }

    @Test
    @Order(4)
    public void testEliminar() {
        Response r = target.path("producto-precio/{id}")
                .resolveTemplate("id", idCreado)
                .request(MediaType.APPLICATION_JSON)
                .delete();

        assertEquals(204, r.getStatus());
    }

    @Test
    @Order(5)
    public void testNoEncontrado() {
        Response r = target.path("producto-precio/{id}")
                .resolveTemplate("id", 999999)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(404, r.getStatus());
    }

    @Test
    @Order(6)
    public void testContarPorProducto() {
        Response r = target.path("producto-precio/contar-por-producto")
                .queryParam("idTipoProducto", idTipoProductoCreado)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(200, r.getStatus());
        Long cantidad = r.readEntity(Long.class);
        assertNotNull(cantidad);
    }
}
**/