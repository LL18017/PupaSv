package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetalle;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductoDetalleResourceIT extends AbstractContainerTest {
    // Configuración de entidades y contenedores
    Long totalEnScript = 4L;
    Integer idTipoProductoPrueba = 1001;
    Long idProductoPrueba = 1001L;

    Integer idTipoProductoCreado = 1003;
    Long idProductoCreado = 1001L;

    @BeforeAll
    public void inicializar() {
        cliente = ClientBuilder.newClient();
        target = cliente.target(String.format("http://localhost:%d/PupaSv-1.0-SNAPSHOT/v1/", servidorDeAplicaion.getMappedPort(9080)));

    }

    @Order(1)
 @Test
    public void testCreate() {
        System.out.println("ProductoDetalle testSI create");
        ProductoDetalle registro = new ProductoDetalle();

        //FLUJO BUENO
        String formatoPath = String.format("productoDetalle/tipoProducto/%d/producto/%d", idTipoProductoCreado, idProductoCreado);
        Response respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).post(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(201, respuesta.getStatus());
        totalEnScript++;

        //probar error de argumento ENTIDAD NULA
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).post(Entity.entity(null, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(500, respuesta.getStatus());

        //probar error de argumento IidTipoProducto INEXISTENTE
        formatoPath = String.format("productoDetalle/tipoProducto/%d/producto/%de", idTipoProductoCreado, 123456);
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).post(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(404, respuesta.getStatus());

        //probar error de argumento IdProducto INEXISTENTE
        formatoPath = String.format("productoDetalle/tipoProducto/%d/producto/%d", 112233, idProductoCreado);
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).post(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(404, respuesta.getStatus());

        //probar error de argumento IdProducto malo
        formatoPath = String.format("productoDetalle/tipoProducto/%d/producto/%d", -90, idProductoCreado);
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).post(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(400, respuesta.getStatus());


//        Assertions.fail("fallo exitosamente");
    }

    @Order(2)
 @Test
    public void testGetBean() {
        System.out.println("ProductoDetalle  testSI getBean");
        Assertions.assertTrue(servidorDeAplicaion.isRunning());
        //CREADO EN LA ANTERIOR PRUEBA

        //todos los registros
        String formatoPath = "productoDetalle";
        Response respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).get();
        assertEquals(200, respuesta.getStatus());
        List<ProductoDetalle> registros = respuesta.readEntity(new GenericType<List<ProductoDetalle>>() {
        });
        assertEquals(totalEnScript, registros.size());

        //flujo normal
        formatoPath = String.format("productoDetalle/tipoProducto/%d/producto/%d", idTipoProductoPrueba, idProductoPrueba);
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertNotNull(respuesta);
        ProductoDetalle registro = respuesta.readEntity(new GenericType<ProductoDetalle>() {
        });
        Assertions.assertEquals(idProductoPrueba, registro.getProductoDetallePK().getIdProducto());
        Assertions.assertEquals(idTipoProductoPrueba, registro.getProductoDetallePK().getIdTipoProducto());

        //fallo de argumentos uno de los ides no existe
        formatoPath = String.format("productoDetalle/tipoProducto/%d/producto/%d", idTipoProductoPrueba, 112233);//no existe este producto
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(404, respuesta.getStatus());

        formatoPath = String.format("productoDetalle/tipoProducto/%d/producto/%d", 112233, idProductoPrueba);//no existe este producto
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(404, respuesta.getStatus());

        //fallo de argumentos uno de los ids es menor a 0
        formatoPath = String.format("productoDetalle/tipoProducto/%d/producto/%d", idTipoProductoPrueba, -90);//no existe este producto
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(400, respuesta.getStatus());


//        Assertions.fail("fallo exitosamente");
    }

    @Order(3)
 @Test
    public void testUpdate() {
        System.out.println("ProductoDetalle  testSI update");
        String observeacion = "observacion de prueba test";
        ProductoDetalle registro = new ProductoDetalle();
        registro.setObservaciones(observeacion);

        //flujo normal
        String formatoPath = String.format("productoDetalle/tipoProducto/%d/producto/%d", idTipoProductoPrueba, idProductoPrueba);
        Response respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).put(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Response respuestaParaComprobar = target.path(formatoPath).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(200, respuesta.getStatus());

        ProductoDetalle registroComprobacion = respuestaParaComprobar.readEntity(ProductoDetalle.class);
        Assertions.assertNotNull(registroComprobacion);
        Assertions.assertEquals(observeacion, registroComprobacion.getObservaciones());
        assertEquals(observeacion, registroComprobacion.getObservaciones());
        Assertions.assertEquals(idProductoPrueba, registroComprobacion.getProductoDetallePK().getIdProducto());
        Assertions.assertEquals(idTipoProductoPrueba, registroComprobacion.getProductoDetallePK().getIdTipoProducto());

        //fallo de argumentos uno de los ids es menor de 0
        formatoPath = String.format("productoDetalle/tipoProducto/%d/producto/%d", idTipoProductoPrueba, -90);
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).put(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(400, respuesta.getStatus());

        //fallo de argumentos uno de los ids es menor de 0
        formatoPath = String.format("productoDetalle/tipoProducto/%d/producto/%d", -9, idProductoPrueba);
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).put(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(400, respuesta.getStatus());

        //fallo de argumentos uno de los ides no existe
        formatoPath = String.format("productoDetalle/tipoProducto/%d/producto/%d", 112233, idProductoPrueba);//no existe este producto
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).get();


        formatoPath = String.format("productoDetalle/tipoProducto/%d/producto/%d", idTipoProductoPrueba, 112233);//no existe este producto
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(404, respuesta.getStatus());


    }

    @Order(4)
 @Test
    public void testDelete() {
        System.out.println("ProductoDetalle  testSIdelete");


        String formato = String.format("productoDetalle/tipoProducto/%d/producto/%d", idTipoProductoCreado, idProductoCreado);
        Response respuesta = target.path(String.format(formato)).
                request(MediaType.APPLICATION_JSON).delete();
        Assertions.assertEquals(200, respuesta.getStatus());

        Response comprobacion = target.path(formato).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(404, comprobacion.getStatus());

        //fallo de argumentos uno de los ides no existe
        formato = String.format("productoDetalle/tipoProducto/%d/producto/%d", idTipoProductoCreado, 112233);//no existe este producto
        respuesta = target.path(formato).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(404, respuesta.getStatus());

        formato = String.format("productoDetalle/tipoProducto/%d/producto/%d", 112233, idProductoCreado);//no existe este producto
        respuesta = target.path(formato).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(404, respuesta.getStatus());
        //ids menores a cero
        formato = String.format("productoDetalle/tipoProducto/%d/producto/%d", -1, idProductoPrueba);//no existe este producto
        respuesta = target.path(formato).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(400, respuesta.getStatus());

        formato = String.format("productoDetalle/tipoProducto/%d/producto/%d", idTipoProductoPrueba, 0);//no existe este producto
        respuesta = target.path(formato).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(400, respuesta.getStatus());


        //fallo de argumentos


    }


}
