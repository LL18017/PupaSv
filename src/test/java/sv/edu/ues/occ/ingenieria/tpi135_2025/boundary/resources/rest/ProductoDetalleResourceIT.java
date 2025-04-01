package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.persistence.EntityManagerFactory;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetallePK;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductoDetalleResourceIT {
    // Configuración de entidades y contenedores
    EntityManagerFactory emf;
    static MountableFile war = MountableFile.forHostPath(Paths.get("target/PupaSv-1.0-SNAPSHOT.war").toAbsolutePath());
    static Network red = Network.newNetwork();

    Client cliente;
    WebTarget target;

    static String ruta = "/home/mjlopez/Escritorio/PupaSv/target/PupaSv-1.0-SNAPSHOT.war";
    static String dbName = "Tipicos";
    static String dbPassword = "12345";
    static String dbUser = "postgres";
    static int dbPort = 5432;

    @Container
    static GenericContainer postgres = new PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName(dbName)
            .withPassword(dbPassword)
            .withUsername(dbUser)
            .withInitScript("tipicos_tpi135_2025.sql")
            .withExposedPorts(dbPort)
            .withNetwork(red)
            .withNetworkAliases("db16_tpi");

    @Container
    static GenericContainer servidorDeAplicaion = new GenericContainer("liberty_app")//nombre de la imagen local
            .withCopyFileToContainer(war, "/config/dropins/PupaSv-1.0-SNAPSHOT.war")//ruta donde debe estar el war en el contenedor
            .withExposedPorts(9080)
            .withNetwork(red)
            .withEnv("DB_PASSWORD", dbPassword)
            .withEnv("DB_USER", dbUser)
            .withEnv("DB_NAME", dbName)
            .withEnv("DB_PORT", String.valueOf(dbPort))
            .withEnv("DB_HOST", "db16_tpi")
            .dependsOn(postgres);

    @BeforeAll
    public void inicializar() {
        cliente = ClientBuilder.newClient();
        target = cliente.target(String.format("http://localhost:%d/PupaSv-1.0-SNAPSHOT/v1/", servidorDeAplicaion.getMappedPort(9080)));

    }

    @Order(1)
    @Test
    public void testCreate() throws InterruptedException {
        System.out.println("ProductoDetalle testSI create");
        ProductoDetalle registro = new ProductoDetalle();
        Integer idTipoProducto = 1002;
        Long idProducto = 1004L;
        ProductoDetallePK pk = new ProductoDetallePK();
        registro.setProductoDetallePK(pk);

        //FLUJO BUENO
        String formatoPath = String.format("productoDetalle/tipoProducto/%d/producto/%d", idTipoProducto, idProducto);
        Response respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).post(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertNotNull(respuesta);
        System.out.println(
                servidorDeAplicaion.getLogs());
        Assertions.assertEquals(201, respuesta.getStatus());

        //probar error de argumento ENTIDAD NULA
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).post(Entity.entity(null, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(500, respuesta.getStatus());

        //probar error de argumento IidTipoProducto INEXISTENTE
        formatoPath = String.format("productoDetalle/tipoProducto/%d/producto/%de", idTipoProducto, 123456);
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).post(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(404, respuesta.getStatus());

        //probar error de argumento IdProducto INEXISTENTE
        formatoPath = String.format("productoDetalle/tipoProducto/%d/producto/%d", 123456, idProducto);
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).post(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(404, respuesta.getStatus());

        //probar error de argumento IdProducto INEXISTENTE
        formatoPath = String.format("productoDetalle/tipoProducto/%d/producto/%d", -90, idProducto);
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
        Integer idTipoProducto = 1002;
        Long idProducto = 1004L;
        Long totaRegistros = 4L;//3 en db + i de create()

        //todos los registros
        String formatoPath = "productoDetalle";
        Response respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).get();
        assertEquals(200, respuesta.getStatus());
        List<ProductoDetalle> registros = respuesta.readEntity(new GenericType<List<ProductoDetalle>>() {
        });
        assertEquals(totaRegistros,registros.size());

        //flujo normal
        formatoPath = String.format("productoDetalle/tipoProducto/%d/producto/%d", idTipoProducto, idProducto);
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertNotNull(respuesta);
        ProductoDetalle registro = respuesta.readEntity(new GenericType<ProductoDetalle>() {
        });
        Assertions.assertEquals(idProducto, registro.getProductoDetallePK().getIdProducto());
        Assertions.assertEquals(idTipoProducto, registro.getProductoDetallePK().getIdTipoProducto());

        //fallo de argumentos uno de los ides no existe
        formatoPath = String.format("productoDetalle/tipoProducto/%d/producto/%d", idTipoProducto, 5090);//no existe este producto
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(404, respuesta.getStatus());

        //fallo de argumentos uno de los ids es menor a 0
        formatoPath = String.format("productoDetalle/tipoProducto/%d/producto/%d", idTipoProducto, -90);//no existe este producto
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(400, respuesta.getStatus());


//        Assertions.fail("fallo exitosamente");
    }

    @Order(3)
    @Test
    public void testUpdate() {
        System.out.println("ProductoDetalle  testSI update");
        Integer idTipoProducto = 1002;
        Long idProducto = 1004L;

        String observeacion = "observacion de prueba test";

        ProductoDetalle registro = new ProductoDetalle();
        ProductoDetallePK pk = new ProductoDetallePK(idTipoProducto, idProducto);
        registro.setProductoDetallePK(pk);
        registro.setObservaciones(observeacion);

        //flujo normal
        String formatoPath = String.format("productoDetalle/tipoProducto/%d/producto/%d", idTipoProducto, idProducto);
        Response respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).put(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Response respuestaParaComprobar = target.path(formatoPath).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(200, respuesta.getStatus());

        ProductoDetalle registroComprobacion = respuestaParaComprobar.readEntity(ProductoDetalle.class);
        Assertions.assertNotNull(registroComprobacion);
        Assertions.assertEquals(observeacion, registroComprobacion.getObservaciones());
        assertEquals(observeacion, registroComprobacion.getObservaciones());
        Assertions.assertEquals(idProducto, registroComprobacion.getProductoDetallePK().getIdProducto());
        Assertions.assertEquals(idTipoProducto, registroComprobacion.getProductoDetallePK().getIdTipoProducto());

        //fallo de argumentos uno de los ids es menor de 0

        formatoPath = String.format("productoDetalle/tipoProducto/%d/producto/%d", idTipoProducto, -90);
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).put(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(400, respuesta.getStatus());

        //fallo de argumentos uno de los ides no existe
        formatoPath = String.format("productoDetalle/tipoProducto/%d/producto/%d", idTipoProducto, 5090);//no existe este producto
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(404, respuesta.getStatus());


    }

    @Order(5)
    @Test
    public void testDelete() {
        System.out.println("ProductoDetalle  testSIdelete");
        Integer idTipoProducto = 1002;
        Long idProducto = 1004L;
        ProductoDetallePK pk = new ProductoDetallePK(idTipoProducto, idProducto);
        ProductoDetalle registro = new ProductoDetalle(pk);

        String formato = String.format("productoDetalle/tipoProducto/%d/producto/%d", idTipoProducto, idProducto);

        Response respuesta = target.path(String.format(formato)).
                request(MediaType.APPLICATION_JSON).delete();
        Assertions.assertEquals(200, respuesta.getStatus());

        Response comprobacion = target.path(formato).request(MediaType.APPLICATION_JSON).get();
        ProductoDetalle registroComprobacion = comprobacion.readEntity(ProductoDetalle.class);
        Assertions.assertEquals(404, comprobacion.getStatus());

        //fallo de argumentos uno de los ides no existe
        formato = String.format("productoDetalle/tipoProducto/%d/producto/%d", idTipoProducto, 5090);//no existe este producto
        respuesta = target.path(formato).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(404, respuesta.getStatus());

        //fallo de argumentos uno de los ids es menor a 0
        formato = String.format("productoDetalle/tipoProducto/%d/producto/%d", idTipoProducto, -90);//no existe este producto
        respuesta = target.path(formato).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(400, respuesta.getStatus());


        //fallo de argumentos


    }


}
