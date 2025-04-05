/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Orden;

/**
 * @author mjlopez
 */
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrdenBeanIT extends AbstractContainerTest {

    OrdenBean cut;
    EntityManager mockEm;
    OrdenBean cut2;

    Long totalEnScript = 3l;//cantidad en el script
    Long idDePrueba = 12345L;//ya se encuentra en el script
    Long idCreadoEnPrueba=0L;//se cambiara luego

    @BeforeEach
    void setUp() {
        cut = new OrdenBean();
        mockEm = Mockito.mock(EntityManager.class);
        cut2 = Mockito.spy(new OrdenBean());
    }

    @Order(1)
    @Test
    public void contar() {
        System.out.println("testIT Contar");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        em.getTransaction().begin();
        Long respuesta = cut.count();
        em.getTransaction().commit();
        Assertions.assertEquals(totalEnScript, respuesta);

//        Assertions.fail("fallo exitosamente");
    }

    @Order(2)
    @Test
    public void findRange() {
        System.out.println("testIT findRange");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        //flujo normal
        em.getTransaction().begin();
        List<Orden> respuesta = cut.findRange(first, max);
        em.getTransaction().commit();

        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(totalEnScript, respuesta.size());

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
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        em.getTransaction().begin();
        List<Orden> respuesta = cut.findAll();
        em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(totalEnScript, respuesta.size());

//        Assertions.fail("fallo exitosamente");
    }

    @Order(4)
    @Test
    public void findById() {
        System.out.println("testIT fiandById");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        //datos esperados segun script
        LocalDate ld = LocalDate.parse("2025-03-03");
        Date date = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
        String sucursal = "Zarsa";
        boolean activo = true;
        em.getTransaction().begin();
        Orden respuesta = cut.findById(idDePrueba);//id primer elemento
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
        idCreadoEnPrueba=creado.getIdOrden();
        totalEnScript++;
        Orden respuesta = cut.findById(idCreadoEnPrueba);//obtenemos el ultimo
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
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Orden ordenPrimera = new Orden(12345L);
        ordenPrimera.setFecha(Date.from(LocalDate.parse("2025-03-03").atStartOfDay(ZoneId.systemDefault()).toInstant()));
        ordenPrimera.setSucursal("Zarsa");
        ordenPrimera.setAnulada(false);

        //flujo normal
        cut.em.getTransaction().begin();
        Orden modificado = cut.update(ordenPrimera, idDePrueba);
        cut.em.getTransaction().commit();
        Orden respuesta = cut.findById(idDePrueba);
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
            cut.update(null, idDePrueba);
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

        //flujo normal
        em.getTransaction().begin();
        cut.delete(idCreadoEnPrueba);
        em.getTransaction().commit();

        // Verificar que realmente se eliminó
        Assertions.assertThrows(EntityNotFoundException.class, () -> {cut.delete(idCreadoEnPrueba);});

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
