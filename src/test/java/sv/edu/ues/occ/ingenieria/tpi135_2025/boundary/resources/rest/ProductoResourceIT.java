package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.persistence.EntityManagerFactory;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.xml.bind.SchemaOutputResolver;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductoResourceIT {

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
        System.out.println("Producto testSI create");
        Producto registro = new Producto();
        registro.setNombre("bebidas test");
        Integer idTipoProducto=1003;//este id no es usado
        String formatoPath=String.format("tipoProducto/%d/producto",idTipoProducto);


        //flujo bueno
        Response respuesta = target.path(formatoPath)
                .request(MediaType.APPLICATION_JSON).
                post(Entity.entity(registro, MediaType.APPLICATION_JSON));

        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(201, respuesta.getStatus());

        System.out.println("el recurso se encuentra en : " + respuesta.getLocation());

        //probar error de argumento malo
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).post(Entity.entity(null, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(500, respuesta.getStatus());

        //probar error de argumento idTipoProducto malo
        formatoPath=String.format("tipoProducto/%d/producto",-90);
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).post(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(400, respuesta.getStatus());


        //probar error de argumento idTipoProducto inexistenete
        formatoPath=String.format("tipoProducto/%d/producto",12345);
        respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).post(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(404, respuesta.getStatus());


//        Assertions.fail("fallo exitosamente");
    }
    @Order(2)
    @Test
    public void testGetRange() {
        System.out.println("Producto  testSI getRange");
        Assertions.assertTrue(servidorDeAplicaion.isRunning());
        Integer idTipoProducto=1001;//dos registros en db
        Integer cantidadEsperadas=2;
        Integer cantidadEsperadasActicos=1;

        String formatoPath=String.format("tipoProducto/%d/producto",idTipoProducto);
        //flujo normal

        Response respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertNotNull(respuesta);

        List<Producto> registros = respuesta.readEntity(new GenericType<List<Producto>>() {
        });
        Assertions.assertEquals(cantidadEsperadas, registros.size());

        //flujo para buscar activos
        formatoPath=String.format("tipoProducto/%d/producto",idTipoProducto);
        respuesta=target.path(formatoPath).queryParam("first",0).queryParam("max",5).queryParam("activo",true).request(MediaType.APPLICATION_JSON).get();
        registros = respuesta.readEntity(new GenericType<List<Producto>>() {
        });
        Assertions.assertEquals(cantidadEsperadasActicos, registros.size());


        //fallo de argumentos first max
        respuesta=target.path(formatoPath).queryParam("first",3).queryParam("max",90).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(400, respuesta.getStatus());

        //fallo de argumentos id Inexistente
        formatoPath=String.format("tipoProducto/%d/producto",123456);
        respuesta=target.path(formatoPath).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(404, respuesta.getStatus());
//        Assertions.fail("fallo exitosamente");
    }
    @Order(3)
    @Test
    public void testUpdate() {
        System.out.println("Producto  testSI update");
        Long id = 1L;
        Integer idTipoProducto=1001;
        String formatoPath=String.format("tipoProducto/%d/producto/%d",idTipoProducto,id);
        String nombre = "producto de prueba test";
        String observeacion = "observacion de prueba test";

        Producto registro = new Producto();
        registro.setNombre(nombre);
        registro.setObservaciones(observeacion);

        Response respuesta = target.path(formatoPath).request(MediaType.APPLICATION_JSON).put(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Response respuestaPeticion = target.path(formatoPath).request(MediaType.APPLICATION_JSON).get();
        Producto rpp = respuestaPeticion.readEntity(Producto.class);
        Assertions.assertNotNull(rpp);
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertEquals(nombre, rpp.getNombre());
        assertEquals(observeacion, rpp.getObservaciones());
        Assertions.assertEquals(id, rpp.getIdProducto());

        //error de argumentos
        respuesta = target.path(String.format("tipoProducto/%d/producto/%d", idTipoProducto,12345)).request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(404, respuesta.getStatus());

        //fallo de argumentos id inexistentes
        respuestaPeticion = target.path(String.format("tipoProducto/%d/producto/%d",idTipoProducto, 12345))
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(404, respuestaPeticion.getStatus());
        //fallo de argumentos  idProducto menor a 0
        respuestaPeticion = target.path(String.format("tipoProducto/%d/producto/%d",idTipoProducto, -89))
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(400, respuestaPeticion.getStatus());

        //fallo de argumentos  idTipoProducto menor a 0
        respuestaPeticion = target.path(String.format("tipoProducto/%d/producto/%d",-89, idTipoProducto))
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(400, respuestaPeticion.getStatus());
    }

    @Order(4)
    @Test
    public void testGetById() {
        System.out.println("Producto  testSI getById");
        Long idBuscado = 1001L;//ya existe en el scrip de db
        Integer idTipoProdcuto = 1001;//ya existe en el scrip de db
        String formato=String.format("tipoProducto/%d/producto/%d",idTipoProdcuto,idBuscado);

        Response respuesta = target.path(formato)  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(idBuscado, respuesta.readEntity(Producto.class).getIdProducto());

        //fallo de argumentos id inexistentes
        respuesta = target.path(String.format("tipoProducto/%d/producto/%d",idTipoProdcuto, 12345))  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(404, respuesta.getStatus());
        //fallo de argumentos  idProducto menor a 0
        respuesta = target.path(String.format("tipoProducto/%d/producto/%d",idTipoProdcuto, -89))  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(400, respuesta.getStatus());

        //fallo de argumentos  idTipoProducto menor a 0
        respuesta = target.path(String.format("tipoProducto/%d/producto/%d",-89, idBuscado))  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(400, respuesta.getStatus());

//        Assertions.fail("fallo exitosamente");

    }

    @Order(5)
    @Test
    public void testDelete() {
        // Esta prueba está vacía, así que agregaríamos la lógica de la prueba aquí
        System.out.println("Producto  testSIdelete");

        Integer idTipoProducto = 4;//creado en test create
        Long idProducto = 1L;//creado en test create
        Producto registro = new Producto(idProducto);

        String formato=String.format("tipoProducto/%d/producto/%d",idTipoProducto,idProducto);

        Response respuestaPeticion = target.path(String.format(formato)).
                request(MediaType.APPLICATION_JSON).delete();

        Assertions.assertEquals(200, respuestaPeticion.getStatus());
        Response comprobacion = target.path(formato).
                request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(404, comprobacion.getStatus());


        //fallo de argumentos id inexistentes
        respuestaPeticion = target.path(String.format("tipoProducto/%d/producto/%d",idTipoProducto, 12345))  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(404, respuestaPeticion.getStatus());
        //fallo de argumentos  idProducto menor a 0
        respuestaPeticion = target.path(String.format("tipoProducto/%d/producto/%d",idTipoProducto, -89))  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(400, respuestaPeticion.getStatus());

        //fallo de argumentos  idTipoProducto menor a 0
        respuestaPeticion = target.path(String.format("tipoProducto/%d/producto/%d",-89, idTipoProducto))  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(400, respuestaPeticion.getStatus());

//        Assertions.fail("fallo exitosamente");

    }

}
