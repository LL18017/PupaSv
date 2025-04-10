package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.TipoProducto;

import java.util.List;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TipoProductoBeanIT extends AbstractContainerTest {
    TipoProductoBean cut;
    EntityManager mockEm;
    ProductoDetalleBean cut2;

    Long totalEnScript = 3l;//cantidad en el script
    Integer idDePrueba = 1001;//ya se encuentra en el script
    Integer idCreadoEnPrueba=0;//se cambiara luego

    @BeforeEach
    void setUp() {
        cut = new TipoProductoBean();
        mockEm = Mockito.mock(EntityManager.class);
        cut2 = Mockito.spy(new ProductoDetalleBean());
    }

    @Order(1)
    @Test

    public void create() {
        System.out.println("TipoProducto testIT create");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        TipoProducto creado = new TipoProducto();
        creado.setNombre("Tipo test");

        //flujo normal
        cut.em.getTransaction().begin();
        Assertions.assertDoesNotThrow(() -> cut.create(creado));
        cut.em.getTransaction().commit();
        totalEnScript = totalEnScript + 1;
        idCreadoEnPrueba=creado.getIdTipoProducto();
        //fallo de registro
        cut.em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.create(null));
        cut.em.getTransaction().commit();

        em.close();
//        Assertions.fail("fallo exitosamente");
    }

    @Order(2)
    @Test
    void findRange() {
        System.out.println("TipoProducto testIT findRange");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        //flujo normal
        cut.em.getTransaction().begin();
        List<TipoProducto> respuesta = cut.findRange(first, max);
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
        System.out.println("TipoProducto testIT findAll");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        //flujo normal
        cut.em.getTransaction().begin();
        List<TipoProducto> respuesta = cut.findAll();
        cut.em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(totalEnScript, respuesta.size());

        em.close();
//        Assertions.fail("fallo exitosamente");
    }

    @Order(4)
    @Test
    public void findById() {
        System.out.println("TipoProducto testIT findById");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        em.getTransaction().begin();
        TipoProducto respuesta = cut.findById(idDePrueba);
        em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(idDePrueba, respuesta.getIdTipoProducto());
        em.close();
//        Assertions.fail("fallo exitosamente");
    }

    @Order(5)
    @Test
    public void contar() {
        System.out.println("TipoProducto testIT Contar");
        TipoProductoBean cut = new TipoProductoBean();
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
        System.out.println("TipoProducto testIT update");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        String nombreEsperado = "hace hambruna test";
        TipoProducto registroActualizar = new TipoProducto(idDePrueba);
        registroActualizar.setNombre(nombreEsperado);
        //creamos
        cut.em.getTransaction().begin();
        cut.update(registroActualizar, idDePrueba);
        cut.em.getTransaction().commit();
        //verificamos
        em.getTransaction().begin();
        TipoProducto respuesta = cut.findById(idDePrueba);
        em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(nombreEsperado, respuesta.getNombre());
        Assertions.assertEquals(idDePrueba, respuesta.getIdTipoProducto());

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
        Assertions.assertThrows(EntityNotFoundException.class, () -> cut.update(registroActualizar, 112233));
        em.getTransaction().commit();

        em.close();

//        Assertions.fail("fallo exitosamente");
    }

    @Order(7)
    @Test
    public void delete() {
        System.out.println("TipoProducto testIT delete");
        TipoProductoBean cut = new TipoProductoBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;


        em.getTransaction().begin();
        cut.delete(idCreadoEnPrueba);
        em.getTransaction().commit();

        em.getTransaction().begin();
        Assertions.assertThrows(EntityNotFoundException.class,()->cut.findById(idCreadoEnPrueba));
        em.getTransaction().commit();


        //id menor a 0
        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.delete(-90));
        em.getTransaction().commit();
        //ID NULO
        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.delete(null));
        em.getTransaction().commit();
        //no existe registro
        em.getTransaction().begin();
        Assertions.assertThrows(EntityNotFoundException.class, () -> cut.delete(112233));
        em.getTransaction().commit();



//        Assertions.fail("fallo exitosamente");
    }

}
