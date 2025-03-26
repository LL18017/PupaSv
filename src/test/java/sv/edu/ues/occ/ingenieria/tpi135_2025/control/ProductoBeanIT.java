package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductoBeanIT extends GenericControlIT{
    Producto registro;

    List<Producto> LISTA_Producto = new ArrayList<>();

    EntityManagerFactory emf;
    public ProductoBeanIT() {
    }

    @Container
    static GenericContainer postgres = new PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("TipicoSVtest")
            .withPassword("abc123")
            .withUsername("postgres")
            .withInitScript("tipicos_tpi135_2025.sql")
            .withExposedPorts(5432)
            .withNetworkAliases("db");

    ProductoBean cut;
    EntityManager mockEm;
    ProductoDetalleBean cut2;

    @BeforeEach
    void setUp() {
        cut = new ProductoBean();
        mockEm = Mockito.mock(EntityManager.class);
        cut2 = Mockito.spy(new ProductoDetalleBean());
    }

    @BeforeAll
    public void inicializar() {
        HashMap<String, Object> propiedades = new HashMap<>();
        propiedades.put("jakarta.persistence.jdbc.url", String.format("jdbc:postgresql://localhost:%d/TipicoSVtest", postgres.getMappedPort(5432)));
        emf = Persistence.createEntityManagerFactory("Test-PupaSV-PU", propiedades);

    }

    @Order(1)
    @Test
    public void createProducto() {
        System.out.println("Producto testIT create");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Integer idTipoProducto=1002;
        Producto creado = new Producto();
        creado.setNombre("nuegados test");
        try {
            //creamos
            cut.em.getTransaction().begin();
            cut.createProducto(creado, idTipoProducto);
            cut.em.getTransaction().commit();
            this.registro=creado;
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
    void findRange() {
        System.out.println("Producto testIT findRange");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        int first = 0;
        int max = 2;

        try {
            cut.em.getTransaction().begin();
            List<Producto> respuesta = cut.findRange(first, max);
            cut.em.getTransaction().commit();
            Assertions.assertNotNull(respuesta);
            Assertions.assertEquals(max, respuesta.size());
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
        System.out.println("Producto testIT fiand all");
        EntityManager em = emf.createEntityManager();
        Integer cantidad_esperada = 5;//5 en script + el creado en createProducto
        cut.em = em;

        try {
            cut.em.getTransaction().begin();
            List<Producto> respuesta = cut.findAll();
            cut.em.getTransaction().commit();
            Assertions.assertNotNull(respuesta);
            Assertions.assertEquals(cantidad_esperada, respuesta.size());
            Assertions.assertTrue(respuesta.contains(registro));

            respuesta.forEach(p-> System.out.println(p.toString()));
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
        System.out.println("Producto testIT fiandById");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Producto esperado = registro;
        Long idPerado = 1L;//el ultimo agregado

        try {
            em.getTransaction().begin();
            Producto respuesta = cut.findById(idPerado);//id primer elemento
            em.getTransaction().commit();

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
//    @Test
    public void contar() {
        System.out.println("Producto testIT Contar");
        ProductoBean cut = new ProductoBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Long esperado = 4L;

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

    @Order(6)
//    @Test
    public void update() {
        System.out.println("Producto testIT update");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Long idESperado = 3L;
        String nombreEsperado = "comida test";
        Producto actualizado = new Producto(idESperado);
        actualizado.setNombre(nombreEsperado);
        //creamos
        try {
            cut.em.getTransaction().begin();
            cut.update(actualizado);
            cut.em.getTransaction().commit();

            Producto respuesta = cut.findById(idESperado);
            Assertions.assertNotNull(respuesta);
            Assertions.assertEquals(nombreEsperado, respuesta.getNombre());
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
        System.out.println("Producto testIT delete");
        ProductoBean cut = new ProductoBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Long idProdcutoEliminado = 1L;


        em.getTransaction().begin();
        // Ahora proceder a eliminarla
        cut.delete(idProdcutoEliminado);
        em.getTransaction().commit();

        em.getTransaction().begin();
        Assertions.assertNull( cut.findById(idProdcutoEliminado));
        em.getTransaction().commit();
//        Assertions.fail("fallo exitosamente");
    }

    @Test
    void findRangeProductoActivos() {
        System.out.println("Producto testIT findRangeProductoActivos");
        EntityManager em = emf.createEntityManager();
        Integer first=0;
        Integer max=2;
        Integer idTipoProducto=1001;
        cut.em = em;
        List<Producto> respuesta = cut.findRangeProductoActivos(idTipoProducto,first, max);
        //se deberia devolver los unicos 1 unico valor que tiene true con idTipoProdcuto 1001
        respuesta.forEach(p-> System.out.println(p.toString()));
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(1, respuesta.size());
        Assertions.assertTrue(respuesta.get(0).getActivo());
//        Assertions.fail("fallo exitosamente");

    }
    @Test
    void countRangeProductoActivos() {
        System.out.println("Producto testIT countRangeProductoActivos");
        EntityManager em = emf.createEntityManager();
        Integer idTipoProducto=1001;
        cut.em = em;
        Long respuesta = cut.countProductoActivosByIdTipoProducto(idTipoProducto);
        //se deberia devolver los unicos 1 unico valor que tiene true con idTipoProdcuto 1001
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(1, respuesta);
//        Assertions.fail("fallo exitosamente");

    }


}
