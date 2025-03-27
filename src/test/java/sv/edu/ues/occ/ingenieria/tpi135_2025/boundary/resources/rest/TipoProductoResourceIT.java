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
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.TipoProducto;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TipoProductoResourceIT {


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
        System.out.println(String.format("http://localhost:%d/PupaSv-1.0-SNAPSHOT/v1/tipoProducto", servidorDeAplicaion.getMappedPort(9080)));
        cliente = ClientBuilder.newClient();
        target = cliente.target(String.format("http://localhost:%d/PupaSv-1.0-SNAPSHOT/v1/", servidorDeAplicaion.getMappedPort(9080)));

    }

    @Order(1)
    @Test
    public void testCreate() throws InterruptedException {
        System.out.println("TipoProdcuto testSI create");
        TipoProducto registro = new TipoProducto();
        registro.setNombre("bebidas test");

        Response respuesta = target.path("tipoProducto").request(MediaType.APPLICATION_JSON).post(Entity.entity(registro, MediaType.APPLICATION_JSON));

        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(201, respuesta.getStatus());
        System.out.println("el recurso se encuentra en : " + respuesta.getLocation());

        //probar error de argumento
        respuesta = target.path("tipoProducto").request(MediaType.APPLICATION_JSON).post(Entity.entity(null, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(500, respuesta.getStatus());


//        Assertions.fail("fallo exitosamente");
    }
    @Order(2)
    @Test
    public void testGetBean() {
        System.out.println("TipoProducto  testSI getBean");
        Assertions.assertTrue(servidorDeAplicaion.isRunning());
        Response respuesta = target.path("tipoProducto").request(MediaType.APPLICATION_JSON).get();

        //buscar por defecto
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertNotNull(respuesta);
        List<TipoProducto> registros = respuesta.readEntity(new GenericType<List<TipoProducto>>() {
        });
        Assertions.assertEquals(4, registros.size());



        //fallo de argumentos
        System.out.println("la url es" +target.path("tipoProducto").queryParam("first",-9).queryParam("max",90).toString());
        respuesta=target.path("tipoProducto").queryParam("first",-9).queryParam("max",90).request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(400, respuesta.getStatus());
//        Assertions.fail("fallo exitosamente");
    }
    @Order(3)
    @Test
    public void testUpdate() {
        System.out.println("TipoProducto  testSI update");
        Integer id = 4;
        String nombre = "nombre de prueba test";
        String observeacion = "observacion de prueba test";

        TipoProducto registro = new TipoProducto();
        registro.setIdTipoProducto(id);
        registro.setNombre(nombre);
        registro.setObservaciones(observeacion);

        Response respuesta = target.path(String.format("tipoProducto/%d", id)).request(MediaType.APPLICATION_JSON).put(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Response respuestaPeticion = target.path(String.format("tipoProducto/%d", id)).request(MediaType.APPLICATION_JSON).get();
        TipoProducto rpp = respuestaPeticion.readEntity(TipoProducto.class);
        Assertions.assertNotNull(rpp);
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertEquals(nombre, rpp.getNombre());
        assertEquals(observeacion, rpp.getObservaciones());
        Assertions.assertEquals(id, rpp.getIdTipoProducto());

        //error de argumentos

        //fallo de argumentos
        respuesta = target.path(String.format("tipoProducto/%d", 12345)).request(MediaType.APPLICATION_JSON).put(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(404, respuesta.getStatus());


    }

    @Order(4)
    @Test
    public void testGetId() {
        System.out.println("TipoProducto  testSI getById");

        Integer idBuscado = 4;//ya existe en el scrip de db
        Response respuesta = target.path(String.format("tipoProducto/%d", idBuscado))  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(idBuscado, respuesta.readEntity(TipoProducto.class).getIdTipoProducto());

        //fallo de argumentos
        respuesta = target.path(String.format("tipoProducto/%d", null))  // Agrega %d para el número
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(404, respuesta.getStatus());

//        Assertions.fail("fallo exitosamente");

    }

    @Order(5)
    @Test
    public void testDelete() {
        // Esta prueba está vacía, así que agregaríamos la lógica de la prueba aquí
        System.out.println("TipoProducto  testSIdelete");

        Integer id = 4;//creado en test create
        TipoProducto registro = new TipoProducto(id);
        Response respuestaPeticion = target.path(String.format("tipoProducto/%d", id)).
                request(MediaType.APPLICATION_JSON).delete();
        Assertions.assertEquals(200, respuestaPeticion.getStatus());
        Response comprobacion = target.path(String.format("tipoProducto/%d", id)).
                request(MediaType.APPLICATION_JSON).get();
        Assertions.assertEquals(404, comprobacion.getStatus());


        //fallo de argumentos
        respuestaPeticion = target.path(String.format("tipoProducto/%d", 0))
                .request(MediaType.APPLICATION_JSON).delete();
        Assertions.assertEquals(500, respuestaPeticion.getStatus());

        respuestaPeticion = target.path(String.format("tipoProducto/%d", 1000))
                .request(MediaType.APPLICATION_JSON).delete();
        Assertions.assertEquals(404, respuestaPeticion.getStatus());


//        Assertions.fail("fallo exitosamente");

    }

    public List<TipoProducto> getLista() {
        TipoProducto registro1 = new TipoProducto();
        registro1.setIdTipoProducto(1);
        registro1.setActivo(true);
        TipoProducto registro2 = new TipoProducto();
        registro2.setIdTipoProducto(2);
        registro2.setActivo(true);
        TipoProducto registro3 = new TipoProducto();
        registro3.setIdTipoProducto(3);
        registro3.setActivo(true);

        List<TipoProducto> lista = new ArrayList<>();
        lista.add(0,registro1);
        lista.add(1,registro2);
        lista.add(2,registro3);

        return lista;
    }
}