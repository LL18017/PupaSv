package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.persistence.EntityManagerFactory;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
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
    static String dbName = "TipicoSV";
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
            .withNetworkAliases("db");

    @Container
    static GenericContainer payara = new GenericContainer("liberty_app")
            .withExposedPorts(9080)
            .withNetwork(red)
            .withNetworkAliases("db")
            .withEnv("PGPASSWORD","12345")
            .withEnv("PGUSER","postgres")
            .withEnv("PGUSER","postgres")
            .withEnv("PGDBNAME","Tipicos")
            .withEnv("PGPORT","5432")
            .withEnv("PGPORT","5432")
            .dependsOn(postgres)
            .waitingFor(Wait.forLogMessage(".*server is ready to run a smarter planet.*", 1))
            ;


    @BeforeAll
    public void inicializar() {
//        System.out.println("Puerto Mapeado de PostgreSQL: " + postgres.getMappedPort(5432));
        cliente = ClientBuilder.newClient();
        target = cliente.target("http://localhost:9080/PupaSv-1.0-SNAPSHOT/v1/");

    }

    @Test
    public void testGetBean() {
        System.out.println("getBean");
        Assertions.assertTrue(payara.isRunning());
        Response respuesta = target.path("orden").request(MediaType.APPLICATION_JSON).get();

        Assertions.assertEquals(200, respuesta.getStatus());
        Assertions.assertNotNull(respuesta);
        List<Orden> registros = respuesta.readEntity(new GenericType<List<Orden>>(){});
        Assertions.assertEquals(1, registros.size());

//        Assertions.fail("fallo exitosamente");
    }

    @Test
    public void testGetId() {
        // Esta prueba está vacía, así que agregaríamos la lógica de la prueba aquí
        // Por ejemplo, hacer una solicitud de un ID específico
        System.out.println("getId");
        // Aquí va tu lógica de prueba con la API
    }

    @Test
    public void testGetClassName() {
        System.out.println("getClassName");
        // Agregar la lógica de prueba aquí
    }
}
