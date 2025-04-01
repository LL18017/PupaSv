package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoPrecio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductoPrecioBeanIT {

    ProductoPrecio registro;

    List<ProductoPrecio> LISTA_Producto = new ArrayList<>();

    EntityManagerFactory emf;

    public ProductoPrecioBeanIT() {
    }

    @Container
    static GenericContainer postgres = new PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("TipicoSVtest")
            .withPassword("abc123")
            .withUsername("postgres")
            .withInitScript("tipicos_tpi135_2025.sql")
            .withExposedPorts(5432)
            .withNetworkAliases("db");

    ProductoPrecioBean cut;
    EntityManager mockEm;
    ProductoDetalleBean cut2;

    @BeforeEach
    void setUp() {
        cut = new ProductoPrecioBean();
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
        Long idProducto = 1004L;
        ProductoPrecio creado = new ProductoPrecio();
        Producto productoPrecio = new Producto(idProducto);
        cut.em.getTransaction().begin();
        creado.setIdProducto(productoPrecio);
        cut.create(creado);
        cut.em.getTransaction().commit();
        this.registro = creado;
        Assertions.assertNotNull(creado.getIdProductoPrecio());
//        Assertions.fail("fallo exitosamente");
    }

    @Order(2)
    @Test
    void findRange() {
        System.out.println("Producto testIT findRange");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        int first = 0;
        int max = 3;
        cut.em.getTransaction().begin();
        List<ProductoPrecio> respuesta = cut.findRange(first, max);
        cut.em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(max, respuesta.size());
//        Assertions.fail("fallo exitosamente");
    }

    @Order(3)
    @Test
    public void findAll() {
        System.out.println("Producto testIT fiand all");
        EntityManager em = emf.createEntityManager();
        Long cantidad_esperada = 4L;//3 en script + el creado en createProducto
        cut.em = em;
        cut.em.getTransaction().begin();
        List<ProductoPrecio> respuesta = cut.findAll();
        cut.em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(cantidad_esperada, respuesta.size());
        Assertions.assertTrue(respuesta.contains(registro));

//        respuesta.forEach(r -> System.out.println(r.toString()));

//        Assertions.fail("fallo exitosamente");
    }

    @Order(4)
    @Test
    public void findById() {
        System.out.println("Producto testIT fiandById");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        ProductoPrecio esperado = registro;
        Long idPerado = 1L;//el ultimo agregado

        em.getTransaction().begin();
        ProductoPrecio respuesta = cut.findById(idPerado);//id primer elemento
        em.getTransaction().commit();
//        Assertions.fail("fallo exitosamente");
    }

    @Order(5)
    @Test
    public void countByIdProducto() {
        System.out.println("Producto testIT countByIdProducto");
        ProductoPrecioBean cut = new ProductoPrecioBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Long idProducto = 1004L;
        Long esperado = 1L;//en metodo create()

        Long respuesta = cut.countByIdProducto(idProducto);
        assertEquals(esperado, respuesta);
//        Assertions.fail("fallo exitosamente");
    }

    @Order(6)
    @Test
    void findRangeByIdProducto() {
        System.out.println("Producto testIT findRangeByIdProdcuto");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Long idProducto = 1004L;
        Long esperado = 1L;//1 por metodo create()

        int first = 0;
        int max = 10;
        cut.em.getTransaction().begin();
        List<ProductoPrecio> respuesta = cut.findByIdProducto(idProducto,first, max);

        cut.em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(esperado, respuesta.size());
//        Assertions.fail("fallo exitosamente");
    }



    @Order(7)
    @Test
    public void update() {
        System.out.println("Producto testIT update");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Long idProductoPrecio = 1L;
        Long idProducto = 1004L;
        BigDecimal precioSugeridoTest=new BigDecimal(1000.00);
        Producto producto = new Producto(idProducto);
        ProductoPrecio registro = new ProductoPrecio();
        registro.setIdProductoPrecio(idProductoPrecio);
        registro.setPrecioSugerido(precioSugeridoTest);

            cut.em.getTransaction().begin();
            cut.update(registro);
            cut.em.getTransaction().commit();

            ProductoPrecio respuesta = cut.findById(idProductoPrecio);
            Assertions.assertNotNull(respuesta);
            Assertions.assertEquals(precioSugeridoTest, respuesta.getPrecioSugerido());
            em.close();
        }
//        Assertions.fail("fallo exitosamente");


    @Order(8)
    @Test
    public void delete() {
        System.out.println("Producto testIT delete");
        ProductoPrecioBean cut = new ProductoPrecioBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Long idProdcutoPrecioEliminado = 1L;


        em.getTransaction().begin();
        // Ahora proceder a eliminarla
        cut.delete(idProdcutoPrecioEliminado);
        em.getTransaction().commit();

        em.getTransaction().begin();
        ProductoPrecio comprobacion = cut.findById(idProdcutoPrecioEliminado);
        em.getTransaction().commit();
        Assertions.assertNull(comprobacion);

//        Assertions.fail("fallo exitosamente");
    }


}