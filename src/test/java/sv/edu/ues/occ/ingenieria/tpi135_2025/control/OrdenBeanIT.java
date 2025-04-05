/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.*;

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
        em.getTransaction().begin();
        Long respuesta = cut.count();
        em.getTransaction().commit();
        Assertions.assertEquals(esperado, respuesta);

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
        int max = 10;
        //flujo normal
        em.getTransaction().begin();
        List<Orden> respuesta = cut.findRange(first, max);
        em.getTransaction().commit();

        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(3, respuesta.size());

        //error de argumentos
        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.findRange(null, max);
        });
        em.getTransaction().commit();
        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.findRange(0, 0);
        });
        em.getTransaction().commit();
//        Assertions.fail("fallo exitosamente");
    }

    @Order(3)
    @Test
    public void findAll() {
        System.out.println("testIT fiand all");
        OrdenBean cut = new OrdenBean();
        EntityManager em = emf.createEntityManager();
        long esperado = 3L;
        cut.em = em;
        em.getTransaction().begin();
        List<Orden> respuesta = cut.findAll();
        em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(esperado, respuesta.size());

//        Assertions.fail("fallo exitosamente");
    }

    @Order(4)
    @Test
    public void findById() {
        System.out.println("testIT fiandById");
        OrdenBean cut = new OrdenBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        //datos esperados segun script
        Long id = 12345L;
        LocalDate ld = LocalDate.parse("2025-03-03");
        Date date = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
        String sucursal = "Zarsa";
        boolean activo = true;
        em.getTransaction().begin();
        Orden respuesta = cut.findById(12345L);//id primer elemento
        em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(date, respuesta.getFecha());
        Assertions.assertEquals(sucursal, respuesta.getSucursal());
        Assertions.assertEquals(activo, respuesta.getAnulada());


        //id no encontrado
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            em.getTransaction().begin();
            cut.findById(1025369L);
            em.getTransaction().commit();
        });
        //id Nulo
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.findById(null);
            em.getTransaction().commit();
        });
        //id menor o igual a cero
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.findById(0);
            em.getTransaction().commit();
        });
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
        //flujo normal
        em.getTransaction().begin();
        cut.create(creado);
        em.getTransaction().commit();
        Orden respuesta = cut.findAll().get(0);//obtenemos el ultimo
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(creado, respuesta);

        //entidda nula

        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.create(null);
        });
        em.getTransaction().commit();
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
        Long IdModificar = 12345L;

        //flujo normal
        cut.em.getTransaction().begin();
        Orden modificado = cut.update(ordenPrimera, IdModificar);
        cut.em.getTransaction().commit();
        Orden respuesta = cut.findById(IdModificar);
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(modificado, respuesta);

        //id nulo
        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.update(ordenPrimera, null);
        });
        em.getTransaction().commit();

        //entidad nula
        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.update(null, IdModificar);
        });
        em.getTransaction().commit();

        //registro inexistente
        em.getTransaction().begin();
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            cut.update(ordenPrimera, 1234569L);
        });
        em.getTransaction().commit();


    }

    @Order(7)
    @Test
    public void delete() {
        System.out.println("testIT delete");
        OrdenBean cut = new OrdenBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Object idOrdenPrimera = 12345L;

        //flujo normal
        em.getTransaction().begin();
        cut.delete(idOrdenPrimera);
        em.getTransaction().commit();

        // Verificar que realmente se eliminó
        Assertions.assertThrows(EntityNotFoundException.class, () -> {cut.delete(idOrdenPrimera);});

        //idNullo
        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.delete(null);
        });
        em.getTransaction().commit();
        //id menor o igual, acero
        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.delete(0);
        });
        em.getTransaction().commit();
        //idInexistente
        em.getTransaction().begin();
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            cut.delete(112236L);
        });
        em.getTransaction().commit();

    }

}
