package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Pago;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetalle;

import java.util.List;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PagoBeanIT extends AbstractContainerTest {
    PagoBean cut;
    Long totalEnScript = 4l;//cantidad en el script
    Long idPagoPrueba = 1001L;//ya se encuentra en el script
    Long idOrdenPrueba = 12349L;//ya se encuentra en el script
    Long idCreadoEnPrueba = 1001L;//se cambiara luego

    @BeforeEach
    void setUp() {
        cut = new PagoBean();
    }


    @Order(1)
    @Test
    public void createPago() {
        System.out.println("Pago testIT create");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Pago creado = new Pago();
        creado.setIdPago(idOrdenPrueba);
        creado.setMetodoPago("efectivo");
        //creamos
        cut.em.getTransaction().begin();
        cut.create(creado);
        cut.em.getTransaction().commit();
        idCreadoEnPrueba = creado.getIdPago();
        totalEnScript++;

        //fallo de registro
        cut.em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.create(null));
        cut.em.getTransaction().commit();
        ProductoDetalleBean cd = new ProductoDetalleBean();
        em.close();
//        Assertions.fail("fallo exitosamente");
    }

    @Order(2)
    @Test
    void findRange() {
        System.out.println("Pago testIT findRange");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        //flujo normal
        cut.em.getTransaction().begin();
        List<Pago> respuesta = cut.findRange(first, max);
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
        System.out.println("Pago testIT fiand all");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        //flujo normal
        cut.em.getTransaction().begin();
        List<Pago> respuesta = cut.findAll();
        cut.em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(totalEnScript, respuesta.size());

        em.close();
//        Assertions.fail("fallo exitosamente");
    }

    @Order(4)
    @Test
    public void findById() {
        System.out.println("Pago testIT fiandById");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        em.getTransaction().begin();
        Pago respuesta = cut.findById(idPagoPrueba);
        em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(idPagoPrueba, respuesta.getIdPago());
        em.close();
//        Assertions.fail("fallo exitosamente");
    }

    @Order(5)
    @Test
    public void contar() {
        System.out.println("Pago testIT Contar");
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
        System.out.println("Pago testIT update");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        String metodoPAgoCmbio = "bitcoin";
        Pago registroActualizar = new Pago(idCreadoEnPrueba);
        registroActualizar.setMetodoPago(metodoPAgoCmbio);
        //creamos
        cut.em.getTransaction().begin();
        cut.update(registroActualizar, idCreadoEnPrueba);
        cut.em.getTransaction().commit();
        //verificamos
        em.getTransaction().begin();
        Pago respuesta = cut.findById(idCreadoEnPrueba);
        em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(metodoPAgoCmbio, respuesta.getMetodoPago());
        Assertions.assertEquals(idCreadoEnPrueba, respuesta.getIdPago());

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
        System.out.println("Pago testIT delete");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Assertions.assertThrows(PersistenceException.class, () -> {
            cut.delete(idCreadoEnPrueba);
        });

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

    @Order(9)
    @Test
    void findByIdOrden() {
        System.out.println("Pago testIT findByIdOrden");
        EntityManager em = emf.createEntityManager();
        Long idOrden = 12347L;
        Long cantiddaESperada = 2L;
        cut.em = em;

        cut.em.getTransaction().begin();
        List<Pago> respuesta = cut.findByIdOrden(idOrden, 0, 10);
        cut.em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(cantiddaESperada, respuesta.size());

        //FALLO DE ARGUMENTOS

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            List<Pago> byIdOrden = cut.findByIdOrden(idOrden, 0, -1);

        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            List<Pago> byIdOrden = cut.findByIdOrden(idOrden, null, 10);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            List<Pago> byIdOrden = cut.findByIdOrden(-90L, 0, 10);
        });

        em.close();


    }

    @Order(10)
    @Test
    void countByIdOrden() {
        System.out.println("Pago testIT countByIdOrden");
        EntityManager em = emf.createEntityManager();
        Long idOrden = 12347L;
        Long cantiddaESperada = 2L;
        cut.em = em;

        cut.em.getTransaction().begin();
        Long respuesta = cut.countByIdOrden(idOrden);
        cut.em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(cantiddaESperada, respuesta);

        //FALLO DE ARGUMENTOS

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Long byIdOrden = cut.countByIdOrden(null);

        });

        em.close();


    }


}


