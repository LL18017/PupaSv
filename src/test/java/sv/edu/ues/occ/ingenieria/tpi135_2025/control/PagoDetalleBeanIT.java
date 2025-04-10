package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Pago;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.PagoDetalle;

import java.util.List;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PagoDetalleBeanIT extends AbstractContainerTest {

    PagoDetalleBean cut;

    Long totalEnScript = 5l;//cantidad en el script
    Long idDePrueba = 1001L;//ya se encuentra en el script
    Long totaRegistrosIdPrueba = 4L;
    Long idCreadoEnPrueba = 1L;//se cambiara luego
    Long idPagoCreado = 1003L;

    @BeforeEach
    void setUp() {
        cut = new PagoDetalleBean();
    }


    @Order(1)
    @Test
    public void createPagoDetalle() {
        System.out.println("PagoDetalle testIT create");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        PagoDetalle creado = new PagoDetalle();
        creado.setIdPago(new Pago(idPagoCreado));
        //creamos
        cut.em.getTransaction().begin();
        cut.create(creado);
        cut.em.getTransaction().commit();
        idCreadoEnPrueba = creado.getIdPagoDetalle();
        totalEnScript++;
        //fallo de registro
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            em.getTransaction().begin();
            em.persist(null);
            em.getTransaction().commit();
        });


        em.close();
//        Assertions.fail("fallo exitosamente");
    }

    @Order(2)
    @Test
    void findRange() {
        System.out.println("PagoDetalle testIT findRange");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        //flujo normal
        cut.em.getTransaction().begin();
        List<PagoDetalle> respuesta = cut.findRange(first, max);
        cut.em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(totalEnScript, respuesta.size());

        //fallo de argumentos
        cut.em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.findRange(null, max));
        cut.em.getTransaction().commit();

        //fallo de 0
        cut.em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.findRange(0, 0));
        cut.em.getTransaction().commit();
        em.close();
//        Assertions.fail("fallo exitosamente");
    }

    @Order(3)
    @Test
    public void findAll() {
        System.out.println("PagoDetalle testIT fiand all");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        //flujo normal
        cut.em.getTransaction().begin();
        List<PagoDetalle> respuesta = cut.findAll();
        cut.em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(totalEnScript, respuesta.size());

        em.close();
//        Assertions.fail("fallo exitosamente");
    }

    @Order(4)
    @Test
    public void findById() {
        System.out.println("PagoDetalle testIT fiandById");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        em.getTransaction().begin();
        PagoDetalle respuesta = cut.findById(idDePrueba);
        em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(idDePrueba, respuesta.getIdPagoDetalle());
        em.close();
//        Assertions.fail("fallo exitosamente");
    }

    @Order(5)
    @Test
    public void contar() {
        System.out.println("PagoDetalle testIT Contar");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        em.getTransaction().begin();
        Long respuesta = cut.count();
        em.getTransaction().commit();
        Assertions.assertEquals(totalEnScript, respuesta);
        em.close();
//        Assertions.fail("fallo exitosamente");
    }

    @Order(6)
    @Test
    public void update() {
        System.out.println("PagoDetalle testIT update");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        String observaciones = "te observo desde test";
        PagoDetalle registroActualizar = new PagoDetalle();
        registroActualizar.setObservaciones(observaciones);
        registroActualizar.setIdPagoDetalle(idDePrueba);
        registroActualizar.setIdPago(new Pago(1001L));
        //creamos
        cut.em.getTransaction().begin();
        cut.update(registroActualizar, idDePrueba);
        cut.em.getTransaction().commit();
        //verificamos
        em.getTransaction().begin();
        PagoDetalle respuesta = cut.findById(idDePrueba);
        em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(observaciones, respuesta.getObservaciones());
        Assertions.assertEquals(idDePrueba, respuesta.getIdPagoDetalle());

        //fallo de argumentos id menor a 0

        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.update(registroActualizar, -63));
        em.getTransaction().commit();

        //id nulo
        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.update(registroActualizar, -63));
        em.getTransaction().commit();

        //registro inexistente
        em.getTransaction().begin();
        Assertions.assertThrows(EntityNotFoundException.class, () -> cut.update(registroActualizar, 112233L));
        em.getTransaction().commit();

        em.close();
    }

    @Order(7)
    @Test
    public void delete() {
        System.out.println("PagoDetalle testIT delete");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        em.getTransaction().begin();
        Assertions.assertDoesNotThrow(() -> cut.delete(idCreadoEnPrueba));
        em.getTransaction().commit();


        //id menor a 0
        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.findById(-90));
        em.getTransaction().commit();
        //ID NULO
        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.findById(null));
        em.getTransaction().commit();
        //no existe registro
        em.getTransaction().begin();
        Assertions.assertThrows(EntityNotFoundException.class, () -> cut.findById(112233L));
        em.getTransaction().commit();
    }

    @Order(8)
    @Test
    public void findRangeByIdPago() {

        System.out.println("PagoDetalle testIT findRangeByIdPago");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

//       flejo normal
        em.getTransaction().begin();
        List<PagoDetalle> resultados=cut.findRangeByIdPago(idDePrueba,first,max);
        cut.em.getTransaction().commit();
        Assertions.assertNotNull(resultados);
        Assertions.assertEquals(totaRegistrosIdPrueba, resultados.size());

        //id menor a 0
        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.findRangeByIdPago(-90L,first,max));
        em.getTransaction().commit();
        //ID NULO
        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.findRangeByIdPago(null,first,max));
        em.getTransaction().commit();
        //no existe registro
        em.getTransaction().begin();
        Assertions.assertThrows(EntityNotFoundException.class, () -> cut.findRangeByIdPago(112233L,first,max));
        em.getTransaction().commit();

//        Assertions.fail("fallo exitosamenet");
    }

    @Order(9)
    @Test
    public void countRangeByIdPago() {

        System.out.println("PagoDetalle testIT countRangeByIdPago");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

//       flejo normal
        em.getTransaction().begin();
        Long resultados=cut.countByIdPago(idDePrueba);
        cut.em.getTransaction().commit();
        Assertions.assertNotNull(resultados);
        Assertions.assertEquals(totaRegistrosIdPrueba, resultados);

        //id menor a 0
        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.countByIdPago(-90L));
        em.getTransaction().commit();
        //ID NULO
        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.countByIdPago(null));
        em.getTransaction().commit();
        //no existe registro
        em.getTransaction().begin();
        Assertions.assertThrows(EntityNotFoundException.class, () -> cut.countByIdPago(112233L));
        em.getTransaction().commit();

//        Assertions.fail("fallo exitosamenet");
    }

}
