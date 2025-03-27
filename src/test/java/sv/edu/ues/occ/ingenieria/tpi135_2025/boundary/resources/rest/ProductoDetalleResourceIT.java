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
        Integer idTipoProducto=1002;
        Long idProducto=1004L;
        ProductoDetallePK pk = new ProductoDetallePK();
        registro.setProductoDetallePK(pk);

        String formatoPath=String.format("tipoProducto/%d/producto/%d/detalle",idTipoProducto,idProducto);

        Response respuesta = target.path(formatoPath)
                .request(MediaType.APPLICATION_JSON).
                post(Entity.entity(registro, MediaType.APPLICATION_JSON));

        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(201, respuesta.getStatus());

        System.out.println("el recurso se encuentra en : " + respuesta.getLocation());

        //probar error de argumento
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).post(Entity.entity(null, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(500, respuesta.getStatus());


//        Assertions.fail("fallo exitosamente");
    }
    @Order(2)
    @Test
    public void testGetBean() {
        System.out.println("ProductoDetalle  testSI getBean");
        Assertions.assertTrue(servidorDeAplicaion.isRunning());
        Integer idTipoProducto=1002;
        Long idProducto=1004L;

        String formatoPath=String.format("tipoProducto/%d/producto/%d/detalle",idTipoProducto,idProducto);
        //flujo normal

        Response respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertNotNull(respuesta);

        ProductoDetalle registro = respuesta.readEntity(new GenericType<ProductoDetalle>() {
        });
        Assertions.assertEquals(registro.getProductoDetallePK().getIdProducto(), idProducto);
        Assertions.assertEquals(registro.getProductoDetallePK().getIdTipoProducto(), idTipoProducto);

        //fallo de argumentos

        formatoPath=String.format("tipoProducto/%d/producto/%d/detalle",idTipoProducto,5090);//no existe este producto
        respuesta=target.path(formatoPath).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(500, respuesta.getStatus());
//        Assertions.fail("fallo exitosamente");
    }
    @Order(3)
    @Test
    public void testUpdate() {
        System.out.println("ProductoDetalle  testSI update");
        Integer idTipoProducto=1002;
        Long idProducto=1004L;

        String formatoPath=String.format("tipoProducto/%d/producto/%d/detalle",idTipoProducto,idProducto);
        String observeacion = "observacion de prueba test";

        ProductoDetalle registro = new ProductoDetalle();
        ProductoDetallePK pk= new ProductoDetallePK(idTipoProducto,idProducto);
        registro.setProductoDetallePK(pk);
        registro.setObservaciones(observeacion);

        Response respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).put(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Response respuestaPeticion = target.path(formatoPath).request(MediaType.APPLICATION_JSON).get();
        ProductoDetalle resgistroPeticion = respuestaPeticion.readEntity(ProductoDetalle.class);
        Assertions.assertNotNull(resgistroPeticion);
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertEquals(observeacion, resgistroPeticion.getObservaciones());
        assertEquals(observeacion, resgistroPeticion.getObservaciones());
        Assertions.assertEquals(idProducto, resgistroPeticion.getProductoDetallePK().getIdProducto());
        Assertions.assertEquals(idTipoProducto, resgistroPeticion.getProductoDetallePK().getIdTipoProducto());
    }
    @Order(5)
    @Test
    public void testDelete() {
        System.out.println("ProductoDetalle  testSIdelete");
        Integer idTipoProducto=1002;
        Long idProducto=1004L;
        ProductoDetallePK pk=new ProductoDetallePK(idTipoProducto,idProducto);
        ProductoDetalle registro = new ProductoDetalle(pk);

        String formato=String.format("tipoProducto/%d/producto/%d/detalle",idTipoProducto,idProducto);

        Response respuestaPeticion = target.path(String.format(formato)).
                request(MediaType.APPLICATION_JSON).delete();

        Assertions.assertEquals(200, respuestaPeticion.getStatus());
        Response comprobacion = target.path(formato).
                request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(500, comprobacion.getStatus());


        //fallo de argumentos


        //no existe registro con id  1456
//        formato=String.format("tipoProductoDetalle/%d/ProductoDetalle/%d",idTipoProductoDetalle,1456);
//        respuestaPeticion = target.path(formato)
//                .request(MediaType.APPLICATION_JSON).delete();
//        Assertions.assertEquals(422, respuestaPeticion.getStatus());


//        Assertions.fail("fallo exitosamente");

    }
//
//    @Order(4)
//    @Test
//    public void testGetId() {
//        System.out.println("ProductoDetalle  testSI getById");
//        Long idBuscado = 1001L;//ya existe en el scrip de db
//        Integer idTipoProdcuto = 1;//ya existe en el scrip de db
//        String formato=String.format("tipoProductoDetalle/%d/ProductoDetalle/%d",idTipoProdcuto,idBuscado);
//
//        Response respuesta = target.path(formato)  // Agrega %d para el número
//                .request(MediaType.APPLICATION_JSON).get();
//        Assertions.assertEquals(200, respuesta.getStatus());
//        Assertions.assertNotNull(respuesta);
//        Assertions.assertEquals(idBuscado, respuesta.readEntity(ProductoDetalle.class).getIdProductoDetalle());
//
//        //fallo de argumentos
//        respuesta = target.path(String.format("tipoProductoDetalle/%d/ProductoDetalle/%d",idTipoProdcuto, 0))  // Agrega %d para el número
//                .request(MediaType.APPLICATION_JSON)
//                .get();
//        Assertions.assertEquals(404, respuesta.getStatus());
//
////        Assertions.fail("fallo exitosamente");
//
//    }

}
