package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.*;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.TipoProducto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.TipoProducto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.TipoProducto;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertTrue;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TipoProductoBeanIT {
    String nombreDb = "TipicosSVtest";
    String password = "abc123";
    String username = "postgres";
    int puerto = 9080;

    List<TipoProducto> LISTA_TipoProducto = new ArrayList<>();

    EntityManagerFactory emf;

    public TipoProductoBeanIT() {
    }

    @Container
    static GenericContainer postgres = new PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("TipicoSVtest")
            .withPassword("abc123")
            .withUsername("postgres")
            .withInitScript("tipicos_tpi135_2025.sql")
            .withExposedPorts(5432)
            .withNetworkAliases("db");

    TipoProductoBean cut;
    EntityManager mockEm;
    ProductoDetalleBean cut2;

    @BeforeEach
    void setUp() {
        cut = new TipoProductoBean();
        mockEm = Mockito.mock(EntityManager.class);
        cut2 = Mockito.spy(new ProductoDetalleBean());
    }

    @BeforeAll
    public void inicializar() {
        crearLista();
        System.out.println("Puerto Mapeado: " + postgres.getMappedPort(5432));
        HashMap<String, Object> propiedades = new HashMap<>();
        propiedades.put("jakarta.persistence.jdbc.url", String.format("jdbc:postgresql://localhost:%d/TipicoSVtest", postgres.getMappedPort(5432)));
        emf = Persistence.createEntityManagerFactory("Test-PupaSV-PU", propiedades);
    }

    @Order(1)
    @Test
    public void create() {
        System.out.println("TipoProducto testIT create");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        TipoProducto creado = new TipoProducto();
        creado.setNombre("Tipo test");
        try {
            //creamos
            cut.em.getTransaction().begin();
            cut.create(creado);
            cut.em.getTransaction().commit();
            LISTA_TipoProducto.add(creado);

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
        System.out.println("TipoProducto testIT findRange");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        int first = 0;
        int max = 2;
        List<TipoProducto> esperados = LISTA_TipoProducto.subList(first, max);

        try {
            cut.em.getTransaction().begin();
            List<TipoProducto> respuesta = cut.findRange(first, max);
            cut.em.getTransaction().commit();
            Assertions.assertNotNull(respuesta);
            Assertions.assertEquals(esperados, respuesta);
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
        System.out.println("TipoProducto testIT fiand all");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        List<TipoProducto> esperados = LISTA_TipoProducto;

        try {
            cut.em.getTransaction().begin();
            List<TipoProducto> respuesta = cut.findAll();
            cut.em.getTransaction().commit();
            Assertions.assertNotNull(respuesta);
            Assertions.assertTrue(esperados.containsAll(respuesta) && respuesta.containsAll(esperados));
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
        System.out.println("TipoProducto testIT fiandById");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        TipoProducto esperado = LISTA_TipoProducto.get(0);
        Integer idPerado = 1;

        try {
            em.getTransaction().begin();
            TipoProducto respuesta = cut.findById(idPerado);//id primer elemento
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
    @Test
    public void contar() {
        System.out.println("TipoProducto testIT Contar");
        TipoProductoBean cut = new TipoProductoBean();
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
    @Test
    public void update() {
        System.out.println("TipoProducto testIT update");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Integer idESperado = 3;
        String nombreEsperado = "hace hambruna test";
        TipoProducto tpPrimeraACtualizado = new TipoProducto(idESperado);
        tpPrimeraACtualizado.setNombre(nombreEsperado);
        //creamos
        try {
            cut.em.getTransaction().begin();
            cut.update(tpPrimeraACtualizado);
            cut.em.getTransaction().commit();
            TipoProducto respuesta = cut.findById(idESperado);
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

    public void crearLista() {
        TipoProducto TipoProductoPrimera = new TipoProducto(1);
        TipoProductoPrimera.setNombre("bebida ric");
        TipoProductoPrimera.setActivo(true);
        TipoProducto TipoProductoSegunda = new TipoProducto(2);
        TipoProductoSegunda.setNombre("comida");
        TipoProducto TipoProductoTercera = new TipoProducto(3);
        TipoProductoTercera.setNombre("tipicos");
        LISTA_TipoProducto.add(0, TipoProductoPrimera);
        LISTA_TipoProducto.add(1, TipoProductoSegunda);
        LISTA_TipoProducto.add(2, TipoProductoTercera);
    }

    @Order(7)
    @Test
    public void delete() {
        System.out.println("TipoProducto testIT delete");
        TipoProductoBean cut = new TipoProductoBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Integer idProdcutoEliminado = 4;


        em.getTransaction().begin();
        // Ahora proceder a eliminarla
        cut.delete(idProdcutoEliminado);
        em.getTransaction().commit();

        em.getTransaction().begin();
        Assertions.assertNull( cut.findById(idProdcutoEliminado));
        em.getTransaction().commit();


//        Assertions.fail("fallo exitosamente");
    }

}
