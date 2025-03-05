package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.persistence.EntityManagerFactory;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Orden;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrdenResourceIT {

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
    static GenericContainer payara = new GenericContainer("liberty_app")
            .withCopyFileToContainer(war, "/config/dropins/PupaSv-1.0-SNAPSHOT.war")
            .withExposedPorts(9080)
            .withNetwork(red)
            .withEnv("PGPASSWORD", dbPassword)
            .withEnv("PGUSER", dbUser)
            .withEnv("PGDBNAME", dbName)
            .withEnv("PGPORT", String.valueOf(dbPort))
            .withEnv("PGSERVER", "db16_tpi")
            .dependsOn(postgres);

    @BeforeAll
    public void inicializar() {
//        System.out.println(String.format("http://localhost:%d/PupaSv-1.0-SNAPSHOT/v1/orden", payara.getMappedPort(9080)));
        cliente = ClientBuilder.newClient();
        target = cliente.target(String.format("http://localhost:%d/PupaSv-1.0-SNAPSHOT/v1/", payara.getMappedPort(9080)));

    }

    @Order(1)
    @Test
    public void testGetBean() {
        System.out.println("testSI get");
        Assertions.assertTrue(payara.isRunning());
        Response respuesta = target.path("orden").request(MediaType.APPLICATION_JSON).get();

        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertNotNull(respuesta);
        List<Orden> registros = respuesta.readEntity(new GenericType<List<Orden>>() {
        });
        Assertions.assertEquals(3, registros.size());

//        Assertions.fail("fallo exitosamente");
    }

    @Order(2)
    @Test
    public void testCreate() throws InterruptedException {
        System.out.println("testSI create");
        Orden registro = new Orden();
        registro.setFecha(new Date());
        registro.setSucursal("Zarsa");
        registro.setAnulada(false);

        Response respuesta = target.path("orden").request(MediaType.APPLICATION_JSON).post(Entity.entity(registro, MediaType.APPLICATION_JSON));

        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(201, respuesta.getStatus());
        System.out.println("el recurso se encuentra en : " + respuesta.getLocation());
//        Assertions.fail("fallo exitosamente");
    }

    @Order(3)
    @Test
    public void testUpdate() {
        System.out.println("testSI update");
        String sucursal = "sa";
        boolean anulada = true;
        Long id = 1L;

        Orden registro = new Orden();
        registro.setIdOrden(id);
        registro.setSucursal(sucursal);
        registro.setAnulada(anulada);

        Response respuesta = target.path("orden").request(MediaType.APPLICATION_JSON).put(Entity.entity(registro, MediaType.APPLICATION_JSON));
        Response respuestaPeticion = target.path(String.format("orden/%d", id.intValue())).request(MediaType.APPLICATION_JSON).get();
        Orden rpp = respuestaPeticion.readEntity(Orden.class);
        Assertions.assertNotNull(rpp);
        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertEquals(sucursal, rpp.getSucursal());
        Assertions.assertTrue(anulada && rpp.getAnulada());
        Assertions.assertEquals(id, rpp.getIdOrden());
//        Assertions.fail("fallo exitosamente");
    }

    @Order(4)
    @Test
    public void testGetId() {
        // Esta prueba está vacía, así que agregaríamos la lógica de la prueba aquí
        System.out.println("testSI getById");
        Long id = 1L;
        Response respuestaPeticion = target.path(String.format("orden/%d", id.intValue())).request(MediaType.APPLICATION_JSON).get();
        Orden respuesta = respuestaPeticion.readEntity(Orden.class);
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(200, respuestaPeticion.getStatus());
        Assertions.assertEquals(id, respuesta.getIdOrden());
//        Assertions.fail("fallo exitosamente");

    }

    @Order(5)
    @Test
    public void testDelete() {
        // Esta prueba está vacía, así que agregaríamos la lógica de la prueba aquí
        System.out.println("testSI delete");
        Long id = 1L;
        Orden registro = new Orden(id);
        Response respuestaPeticion = target.path(String.format("orden/%d", id.intValue())).
                request(MediaType.APPLICATION_JSON).delete();
        Assertions.assertEquals(200, respuestaPeticion.getStatus());
        Response comprobacion = target.path(String.format("orden/%d", id.intValue())).
                request(MediaType.APPLICATION_JSON).get();        
        Assertions.assertEquals(404, comprobacion.getStatus());
//        Assertions.fail("fallo exitosamente");

    }

}
