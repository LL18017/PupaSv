//package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;
//
//import jakarta.persistence.EntityManagerFactory;
//import jakarta.ws.rs.client.Client;
//import jakarta.ws.rs.client.ClientBuilder;
//import jakarta.ws.rs.client.Entity;
//import jakarta.ws.rs.client.WebTarget;
//import jakarta.ws.rs.core.GenericType;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.core.Response;
//import org.junit.jupiter.api.*;
//import org.testcontainers.containers.GenericContainer;
//import org.testcontainers.containers.Network;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.testcontainers.utility.MountableFile;
//import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
//import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetalle;
//import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetallePK;
//import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoPrecio;
//
//import java.math.BigDecimal;
//import java.nio.file.Paths;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNull;
//
//@Testcontainers
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//public class ProductoPrecioResourceIT {
//    // Configuración de entidades y contenedores
//    EntityManagerFactory emf;
//    static MountableFile war = MountableFile.forHostPath(Paths.get("target/PupaSv-1.0-SNAPSHOT.war").toAbsolutePath());
//    static Network red = Network.newNetwork();
//
//    Client cliente;
//    WebTarget target;
//
//    static String ruta = "/home/mjlopez/Escritorio/PupaSv/target/PupaSv-1.0-SNAPSHOT.war";
//    static String dbName = "Tipicos";
//    static String dbPassword = "12345";
//    static String dbUser = "postgres";
//    static int dbPort = 5432;
//
//    @Container
//    static GenericContainer postgres = new PostgreSQLContainer("postgres:16-alpine")
//            .withDatabaseName(dbName)
//            .withPassword(dbPassword)
//            .withUsername(dbUser)
//            .withInitScript("tipicos_tpi135_2025.sql")
//            .withExposedPorts(dbPort)
//            .withNetwork(red)
//            .withNetworkAliases("db16_tpi");
//
//    @Container
//    static GenericContainer servidorDeAplicaion = new GenericContainer("liberty_app")//nombre de la imagen local
//            .withCopyFileToContainer(war, "/config/dropins/PupaSv-1.0-SNAPSHOT.war")//ruta donde debe estar el war en el contenedor
//            .withExposedPorts(9080)
//            .withNetwork(red)
//            .withEnv("DB_PASSWORD", dbPassword)
//            .withEnv("DB_USER", dbUser)
//            .withEnv("DB_NAME", dbName)
//            .withEnv("DB_PORT", String.valueOf(dbPort))
//            .withEnv("DB_HOST", "db16_tpi")
//            .dependsOn(postgres);
//
//    @BeforeAll
//    public void inicializar() {
//        cliente = ClientBuilder.newClient();
//        target = cliente.target(String.format("http://localhost:%d/PupaSv-1.0-SNAPSHOT/v1/", servidorDeAplicaion.getMappedPort(9080)));
//
//    }
//
//    @Order(1)
//    @Test
//    public void testCreate() throws InterruptedException {
//        System.out.println("ProductoPresio testSI create");
//        ProductoPrecio registro = new ProductoPrecio();
//        Long idProducto = 1004L;
//        registro.setIdProducto(new Producto(idProducto));
//        registro.setPrecioSugerido(BigDecimal.valueOf(1.50));
//
//        //FLUJO BUENO
//        String formatoPath = String.format("productoPrecio/%d", idProducto);
//        Response respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).post(Entity.entity(registro, MediaType.APPLICATION_JSON));
//        Assertions.assertNotNull(respuesta);
//        Assertions.assertEquals(201, respuesta.getStatus());
//
//
//        //probar error de argumento ENTIDAD NULA
//        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).post(Entity.entity(null, MediaType.APPLICATION_JSON));
//        Assertions.assertEquals(500, respuesta.getStatus());
//
//
//        //probar error de argumento IdProducto INEXISTENTE
//        formatoPath = String.format("productoPrecio/%d", 112233L);
//        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).post(Entity.entity(registro, MediaType.APPLICATION_JSON));
//        Assertions.assertEquals(404, respuesta.getStatus());
//
//        //probar error de argumento IdProducto INEXISTENTE
//        formatoPath = String.format("productoPrecio/%d", -90);
//        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).post(Entity.entity(registro, MediaType.APPLICATION_JSON));
//        Assertions.assertEquals(400, respuesta.getStatus());
//
//
////        Assertions.fail("fallo exitosamente");
//    }
//
//    @Order(2)
//    @Test
//    public void testGetBean() {
//        System.out.println("ProductoDetalle  testSI getBean");
//        Assertions.assertTrue(servidorDeAplicaion.isRunning());
//        //CREADO EN LA ANTERIOR PRUEBA
//        Long idProducto = 1004L;
//        Long totaRegistros = 4L;//3 en db + i de create()
//
//        //todos los registros
//        String formatoPath = "productoPrecio";
//        Response respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).get();
//        assertEquals(200, respuesta.getStatus());
//        List<ProductoPrecio> registros = respuesta.readEntity(new GenericType<List<ProductoPrecio>>() {
//        });
//        assertEquals(totaRegistros, registros.size());
//        registros.forEach(registro -> {
//            System.out.println(registro.toString());
//        });
//        //registros dado un id
//        formatoPath = "productoPrecio";
//        respuesta = target.path(formatoPath).queryParam("idProducto", idProducto).request(MediaType.APPLICATION_JSON).get();
//        Assertions.assertEquals(200, respuesta.getStatus());
//        Assertions.assertNotNull(respuesta);
//        registros = respuesta.readEntity(new GenericType<List<ProductoPrecio>>() {
//        });
//        Assertions.assertEquals(1, registros.size());
//
//        //fallo de argumentos uno de los ides no existe
//        respuesta = target.path(formatoPath).queryParam("idProducto", 12345).request(MediaType.APPLICATION_JSON).get();
//        Assertions.assertEquals(404, respuesta.getStatus());
//
//        //fallo de argumentos uno de los ids es menor a 0
//        respuesta = target.path(formatoPath).queryParam("idProducto", -90).request(MediaType.APPLICATION_JSON).get();
//        Assertions.assertEquals(400, respuesta.getStatus());
//
//
////        Assertions.fail("fallo exitosamente");
//    }
//
//    @Order(3)
//    @Test
//    public void testUpdate() {
//        System.out.println("ProductoDetalle  testSI update");
//        Long idProductoPrecio = 1001L;
//
//        String observeacion = "observacion de prueba test";
//        BigDecimal precioSugerido = new BigDecimal("99.99");
//        ProductoPrecio registro = new ProductoPrecio();
//        registro.setPrecioSugerido(precioSugerido);
//
//        //flujo normal
//        String formatoPath = String.format("productoPrecio/%d", idProductoPrecio);
//        Response respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).put(Entity.entity(registro, MediaType.APPLICATION_JSON));
//        Response respuestaParaComprobar = target.path(formatoPath).request(MediaType.APPLICATION_JSON).get();
//        Assertions.assertEquals(200, respuesta.getStatus());
//
//        ProductoPrecio registroComprobacion = respuestaParaComprobar.readEntity(ProductoPrecio.class);
//        Assertions.assertNotNull(registroComprobacion);
//        Assertions.assertEquals(precioSugerido, registroComprobacion.getPrecioSugerido());
//        Assertions.assertEquals(idProductoPrecio, registroComprobacion.getIdProductoPrecio());
//
//        //fallo de argumentos uno de los ids es menor de 0
//
//        formatoPath = String.format("productoPrecio/%d", -90);
//        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).put(Entity.entity(registro, MediaType.APPLICATION_JSON));
//        Assertions.assertEquals(400, respuesta.getStatus());
//
//        //fallo de argumentos uno de los ides no existe
//        formatoPath = String.format("productoPrecio/%d", 12345);//no existe este producto
//        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).get();
//        Assertions.assertEquals(404, respuesta.getStatus());
//
//
//    }
//
//    @Order(4)
//    @Test
//    public void testGetById() {
//        System.out.println("ProductoPrecio  testSI getById");
//        Long idBuscado = 1001L;//ya existe en el scrip de db
//        String formato = String.format("productoPrecio/%d", idBuscado);
//
//        Response respuesta = target.path(formato)  // Agrega %d para el número
//                .request(MediaType.APPLICATION_JSON).get();
//        Assertions.assertEquals(200, respuesta.getStatus());
//        Assertions.assertNotNull(respuesta);
//        ProductoPrecio registro = respuesta.readEntity(ProductoPrecio.class);
//
//        Assertions.assertEquals(idBuscado,registro.getIdProductoPrecio());
//
//        //fallo de argumentos id inexistentes
//        respuesta = target.path(String.format("productoPrecio/%d", 12345))  // Agrega %d para el número
//                .request(MediaType.APPLICATION_JSON)
//                .get();
//        Assertions.assertEquals(404, respuesta.getStatus());
//        //fallo de argumentos  idProducto menor a 0
//        respuesta = target.path(String.format("productoPrecio/%d", -89))  // Agrega %d para el número
//                .request(MediaType.APPLICATION_JSON)
//                .get();
//        Assertions.assertEquals(400, respuesta.getStatus());
//
////        Assertions.fail("fallo exitosamente");
//
//    }
//
//    @Order(5)
//    @Test
//    public void testDelete() {
//        System.out.println("ProductoDetalle  testSIdelete");
//        Long idProductoPrecio = 1L;
//
//        String formato = String.format("productoPrecio/%d", idProductoPrecio);
//
//        Response respuesta = target.path(String.format(formato)).
//                request(MediaType.APPLICATION_JSON).delete();
//        Assertions.assertEquals(200, respuesta.getStatus());
//
//        Response comprobacion = target.path(formato).request(MediaType.APPLICATION_JSON).get();
//        ProductoPrecio registroComprobacion = comprobacion.readEntity(ProductoPrecio.class);
//        Assertions.assertEquals(404, comprobacion.getStatus());
//        assertNull(registroComprobacion);
//
//        //fallo de argumentos uno de los ides no existe
//        formato = String.format("productoPrecio/%d", 5090);//no existe este producto
//        respuesta = target.path(formato).request(MediaType.APPLICATION_JSON).get();
//        Assertions.assertEquals(404, respuesta.getStatus());
//
//        //fallo de argumentos uno de los ids es menor a 0
//        formato = String.format("productoPrecio/%d", -90);//no existe este producto
//        respuesta = target.path(formato).request(MediaType.APPLICATION_JSON).get();
//        Assertions.assertEquals(400, respuesta.getStatus());
//
//
//        //fallo de argumentos
//
//
//    }
//
//}
