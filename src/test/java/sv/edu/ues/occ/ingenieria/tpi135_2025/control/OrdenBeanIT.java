/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Orden;

/**
 *
 * @author mjlopez
 */
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrdenBeanIT {

    String nombreDb = "TipicosSV";
    String password = "abc123";
    String username = "postgres";
    int puerto = 9080;

    List<Orden> LISTA_ORDEN = new ArrayList<>();

    EntityManagerFactory emf;

    public OrdenBeanIT() {
    }

    @Container
    static GenericContainer postgres = new PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("TipicoSV")
            .withPassword("abc123")
            .withUsername("postgres")
            .withInitScript("tipicos_tpi135_2025.sql")
            .withExposedPorts(5432)
            .withNetworkAliases("db");

    @BeforeAll
    public void inicializar() {
        crearLista();
        System.out.println("Puerto Mapeado: " + postgres.getMappedPort(5432));
        HashMap<String, Object> propiedades = new HashMap<>();
        propiedades.put("jakarta.persistence.jdbc.url", String.format("jdbc:postgresql://localhost:%d/TipicoSV", postgres.getMappedPort(5432)));
        emf = Persistence.createEntityManagerFactory("Test-PupaSV-PU", propiedades);
    }

    @Order(1)
    @Test
    public void contar() {
        System.out.println("testIT Contar");
        OrdenBean cut = new OrdenBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Long esperado = 3L;

        try {
            Long respuesta = cut.count();
            Assertions.assertEquals(esperado, respuesta);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Rollback en caso de error
            }
            Assertions.fail("Excepción inesperada: " + e.getMessage());
        } finally {
            em.close();
        }
//        Assertions.fail("fallo exitosamente");
    }

    @Order(2)
    @Test
    public void findRange() {
        System.out.println("testIT findRange");
        OrdenBean cut = new OrdenBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        int first = 0;
        int max = 2;
        List<Orden> esperado = LISTA_ORDEN.subList(first, max);

        try {
            List<Orden> respuesta = cut.findRange(first, max);

            Assertions.assertNotNull(respuesta);
            Assertions.assertEquals(esperado, respuesta);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Rollback en caso de error
            }
            Assertions.fail("Excepción inesperada: " + e.getMessage());
        } finally {
            em.close();
        }
//        Assertions.fail("fallo exitosamente");
    }

    @Order(3)
    @Test
    public void findAll() {
        System.out.println("testIT fiand all");
        OrdenBean cut = new OrdenBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        List<Orden> esperado = LISTA_ORDEN;

        try {
            List<Orden> respuesta = cut.findAll();
            Assertions.assertNotNull(respuesta);
            Assertions.assertEquals(esperado, respuesta);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Rollback en caso de error
            }
            Assertions.fail("Excepción inesperada: " + e.getMessage());
        } finally {
            em.close();
        }
//        Assertions.fail("fallo exitosamente");
    }

    @Order(4)
    @Test
    public void findById() {
        System.out.println("testIT fiandById");
        OrdenBean cut = new OrdenBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Orden esperado = LISTA_ORDEN.get(0);

        try {
            Orden respuesta = cut.findById(12345L);//id primer elemento
            Assertions.assertNotNull(respuesta);
            Assertions.assertEquals(esperado, respuesta);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Rollback en caso de error
            }
            Assertions.fail("Excepción inesperada: " + e.getMessage());
        } finally {
            em.close();
        }
//        Assertions.fail("fallo exitosamente");
    }

    @Order(5)
    @Test
    public void create() {
        System.out.println("testIT create");
        OrdenBean cut = new OrdenBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Orden creado = new Orden();
        creado.setFecha(Date.from(LocalDate.parse("2025-03-06").atStartOfDay(ZoneId.systemDefault()).toInstant()));
        creado.setSucursal("Zarsa");
        creado.setAnulada(true);
        try {
            //creamos
            cut.em.getTransaction().begin();
            cut.create(creado);
            cut.em.getTransaction().commit();
            Orden respuesta = cut.findAll().get(0);//obtenemos el ultimo
            Assertions.assertNotNull(respuesta);
            Assertions.assertEquals(creado, respuesta);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Rollback en caso de error
            }
            Assertions.fail("Excepción inesperada: " + e.getMessage());
        } finally {
            em.close();
        }
//        Assertions.fail("fallo exitosamente");
    }

    @Order(6)
    @Test
    public void update() {
        System.out.println("testIT update");
        OrdenBean cut = new OrdenBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Orden ordenPrimera = new Orden(12345L);
        ordenPrimera.setFecha(Date.from(LocalDate.parse("2025-03-03").atStartOfDay(ZoneId.systemDefault()).toInstant()));
        ordenPrimera.setSucursal("Zarsa");
        ordenPrimera.setAnulada(false);

        //creamos
        try {
            cut.em.getTransaction().begin();
            cut.update(ordenPrimera);
            cut.em.getTransaction().commit();
            Orden respuesta = cut.findById(12345L);
            Assertions.assertNotNull(respuesta);
            Assertions.assertEquals(ordenPrimera.getAnulada(), false);//solo la propiedad que cambio
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Rollback en caso de error
            }
            Assertions.fail("Excepción inesperada: " + e.getMessage());
        } finally {
            em.close();
        }

//        Assertions.fail("fallo exitosamente");
    }

    @Order(7)
    @Test
    public void delete() {
        System.out.println("testIT delete");
        OrdenBean cut = new OrdenBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Object idOrdenPrimera = 12345L;

        try {
            // Ahora proceder a eliminarla
            em.getTransaction().begin();
            cut.delete(idOrdenPrimera);
            em.getTransaction().commit();

            // Verificar que realmente se eliminó
            Orden respuesta = cut.findById(12345L);
            Assertions.assertNull(respuesta);

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Rollback en caso de error
            }
            Assertions.fail("Excepción inesperada: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public void crearLista() {
        Orden ordenPrimera = new Orden(12345L);
        ordenPrimera.setFecha(Date.from(LocalDate.parse("2025-03-03").atStartOfDay(ZoneId.systemDefault()).toInstant()));
        ordenPrimera.setSucursal("Zarsa");
        ordenPrimera.setAnulada(true);

        Orden ordenSegunda = new Orden(12346L);
        ordenSegunda.setFecha(Date.from(LocalDate.parse("2025-03-04").atStartOfDay(ZoneId.systemDefault()).toInstant()));
        ordenSegunda.setSucursal("Zarsa");
        ordenSegunda.setAnulada(true);

        Orden ordenTercera = new Orden(12347L);
        ordenTercera.setFecha(Date.from(LocalDate.parse("2025-03-05").atStartOfDay(ZoneId.systemDefault()).toInstant()));
        ordenTercera.setSucursal("Zarsa");
        ordenTercera.setAnulada(true);

        LISTA_ORDEN.add(ordenPrimera);
        LISTA_ORDEN.add(ordenSegunda);
        LISTA_ORDEN.add(ordenTercera);
    }
}
