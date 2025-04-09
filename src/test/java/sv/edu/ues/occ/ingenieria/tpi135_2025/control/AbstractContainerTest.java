/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;

/**
 * Clase base para pruebas de integración utilizando Testcontainers.
 * <p>
 * Esta clase proporciona un contenedor de PostgreSQL
 * configurados con una red compartida para realizar pruebas de integración.
 */
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractContainerTest {
    EntityManagerFactory emf;
    static String DB_NAME = "tpi135_2025";
    static String DB_PASSWORD = "abc123";
    static String DB_USERNAME = "postgres";
    static String DB_SCRIPT = "tipicos_tpi135_2025.sql";
    static int DB_PORT = 5432;

    Integer first=0;
     Integer max=10;

    @Container
    static GenericContainer postgres = new PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName(DB_NAME)
            .withPassword(DB_PASSWORD)
            .withUsername(DB_USERNAME)
            .withInitScript(DB_SCRIPT)
            .withExposedPorts(DB_PORT)
            .withNetworkAliases("db");


    @BeforeAll
    public void inicializar() {
        HashMap<String, Object> propiedades = new HashMap<>();
        propiedades.put("jakarta.persistence.jdbc.url", String.format("jdbc:postgresql://db:%d/%s", postgres.getMappedPort(5432),DB_NAME));
        emf = Persistence.createEntityManagerFactory("Test-PupaSV-PU", propiedades);
    }


}


