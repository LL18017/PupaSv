package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetalle;

import java.util.List;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductoBeanIT extends AbstractContainerTest {

    private static final Logger log = LoggerFactory.getLogger(ProductoBeanIT.class);
    ProductoBean cut;

    Long totalEnScript = 20l;//cantidad en el script
    Long idDePrueba = 1001L;//ya se encuentra en el script
    Long idCreadoEnPrueba = 1001L;//se cambiara luego
    Integer idTipoProdcutoCreado = 1002;

    @BeforeEach
    void setUp() {
        cut = new ProductoBean();
    }

    @Order(1)
    @Test
    public void createProducto() {
        System.out.println("Producto testIT create");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Producto creado = new Producto();
        creado.setNombre("nuegados test");
        //creamos
        cut.em.getTransaction().begin();
        cut.createProductoAndDetail(creado, idTipoProdcutoCreado);
        cut.em.getTransaction().commit();
        idCreadoEnPrueba = creado.getIdProducto();
        totalEnScript++;
        //fallo de registro
        cut.em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.create(null));
        cut.em.getTransaction().commit();
        ProductoDetalleBean cd = new ProductoDetalleBean();
        cd.em = emf.createEntityManager();
        ProductoDetalle detalle = cd.findById(idTipoProdcutoCreado, idCreadoEnPrueba);

        em.close();
//        Assertions.fail("fallo exitosamente");
    }

    @Order(2)
    @Test
    void findRange() {
        System.out.println("Producto testIT findRange");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        //flujo normal
        cut.em.getTransaction().begin();
        List<Producto> respuesta = cut.findRange(first, max);
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
        System.out.println("Producto testIT fiand all");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        //flujo normal
        cut.em.getTransaction().begin();
        List<Producto> respuesta = cut.findAll();
        cut.em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(totalEnScript, respuesta.size());

        em.close();
//        Assertions.fail("fallo exitosamente");
    }

    @Order(4)
    @Test
    public void findById() {
        System.out.println("Producto testIT fiandById");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        em.getTransaction().begin();
        Producto respuesta = cut.findById(idDePrueba);
        em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(idDePrueba, respuesta.getIdProducto());
        em.close();
//        Assertions.fail("fallo exitosamente");
    }

    @Order(5)
    @Test
    public void contar() {
        System.out.println("Producto testIT Contar");
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
        System.out.println("Producto testIT update");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        String nombreEsperado = "hace hambruna test";
        Producto registroActualizar = new Producto(idDePrueba);
        registroActualizar.setNombre(nombreEsperado);
        //creamos
        cut.em.getTransaction().begin();
        cut.update(registroActualizar, idDePrueba);
        cut.em.getTransaction().commit();
        //verificamos
        em.getTransaction().begin();
        Producto respuesta = cut.findById(idDePrueba);
        em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(nombreEsperado, respuesta.getNombre());
        Assertions.assertEquals(idDePrueba, respuesta.getIdProducto());

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
        System.out.println("Producto testIT delete");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Assertions.assertThrows(PersistenceException.class, () -> {
            cut.delete(idDePrueba);
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

    @Order(8)
    @Test
    void findRangeByIdTipoProductosAndActivo() {
        System.out.println("Producto testIT findRangeByIdTipoProductosAndActivo");
        EntityManager em = emf.createEntityManager();
        Integer idTipoProducto = 1001;
        boolean activo = true;
        Long cantiddaESperada = 1L;

        cut.em = em;
        //flujo normal
        List<Producto> respuesta = cut.findRangeByIdTipoProductosAndActivo(idTipoProducto, activo, first, max);
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(cantiddaESperada, respuesta.size());
        Assertions.assertEquals(activo, respuesta.get(0).getActivo());

        //erro de argumentos
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.findRangeByIdTipoProductosAndActivo(-96, activo, first, max));
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.findRangeByIdTipoProductosAndActivo(idTipoProducto, activo, -8, max));
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.findRangeByIdTipoProductosAndActivo(idTipoProducto, activo, first, 0));
        //idInexistente
        Assertions.assertThrows(EntityNotFoundException.class, () -> cut.findRangeByIdTipoProductosAndActivo(112233, activo, first, max));
//        Assertions.fail("fallo exitosamente");

    }

    @Order(9)
    @Test
    void countByIdTipoProductosAndActivo() {
        System.out.println("Producto testIT countByIdTipoProductosAndActivo");
        EntityManager em = emf.createEntityManager();
        Integer idTipoProducto = 1001;
        cut.em = em;
        Long cantidadESperada = 1L;
        boolean activo = true;
        Long respuesta = cut.countByIdTipoProductosAndActivo(idTipoProducto, activo);
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(cantidadESperada, respuesta);

        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.countByIdTipoProductosAndActivo(-96, activo));
        Assertions.assertThrows(EntityNotFoundException.class, () -> cut.countByIdTipoProductosAndActivo(112233, activo));
//        Assertions.fail("fallo exitosamente");

    }

    @Order(10)
    @Test
    void findRangeProductoActivos() {
        System.out.println("Producto testIT findRangeProductoActivos");
        EntityManager em = emf.createEntityManager();
        Long cantidadEsperada = 18L;//
        boolean activo = true;
        cut.em = em;
        List<Producto> respuesta = cut.findRangeProductoActivos(first, max, activo);
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(cantidadEsperada, respuesta.size());

        //fallo de argumentos
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.findRangeProductoActivos(-9, max, activo));
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.findRangeProductoActivos(first, 0, activo));
//        Assertions.fail("fallo exitosamente");

    }

    @Order(11)
    @Test
    void countProductoActivos() {
        System.out.println("Producto testIT countProductoActivos");
        EntityManager em = emf.createEntityManager();
        boolean activo = true;
        Long cantiddaEsperada = 18L;//segun script db
        cut.em = em;
        em.getTransaction().begin();
        Long respuesta = cut.countProductoActivos(activo);
        em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(cantiddaEsperada, respuesta);
        em.close();
//        Assertions.fail("fallo exitosamente");

    }

    @Order(12)
    @Test
    void deleteProductoAndDetail() {
        System.out.println("Producto testIT deleteProductoAndDetail");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        //FLUJO NORMAL
        em.getTransaction().begin();
        Assertions.assertDoesNotThrow(() -> cut.deleteProductoAndDetail(idCreadoEnPrueba, idTipoProdcutoCreado));
        em.close();

        //error de argumentos
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.deleteProductoAndDetail(-90L, idTipoProdcutoCreado));
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.deleteProductoAndDetail(idCreadoEnPrueba, null));

    }



    @Order(14)
    @Test
    public void findListByNombre() {
        System.out.println("Producto testIT findByNombre");

        EntityManager em = emf.createEntityManager();
        cut.em = em;

        // Caso válido
        List<Producto> resultado = cut.findByNombre("pepsi",first,max);
        Assertions.assertNotNull(resultado, "Debe encontrar un producto");
        Assertions.assertEquals("pepsi", resultado.get(0).getNombre());

        // Caso con espacios alrededor
        List<Producto> resultadoTrim = cut.findByNombre("tam",first,max);
        Assertions.assertNotNull(resultadoTrim, "Debe encontrar 2 registros de tamales");
        Assertions.assertEquals(2, resultadoTrim.size());

        // Caso con nombre inexistente → debe retornar null
        List<Producto> noExiste = cut.findByNombre("nombre que no existe",first,max);
        Assertions.assertTrue(noExiste.isEmpty(), "Debe retornar null si no encuentra el producto");

        // Casos inválidos (null o vacío)
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.findByNombre(null,first,max));
        em.close();
    }

    @Order(15)
    @Test
    void countProductoByName() {
        System.out.println("Producto testIT countProductoByName");
        EntityManager em = emf.createEntityManager();
        String nombre = "tamal";
        Long cantiddaEsperada = 2L;//segun script db
        cut.em = em;
        em.getTransaction().begin();
        Long respuesta = cut.countProductoByName(nombre);
        em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(cantiddaEsperada, respuesta);
        em.close();
//        Assertions.fail("fallo exitosamente");

    }

}
