package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoPrecio;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductoPrecioBeanIT extends AbstractContainerTest {
    private EntityManager em;
    private ProductoPrecioBean productoPrecioBean;

    @BeforeAll
    void setup() {
        super.inicializar();
        productoPrecioBean = new ProductoPrecioBean();
    }

    @AfterAll
    void tearDown() {
        if (em != null) em.close();
        if (emf != null) emf.close();
    }

    @BeforeEach
    void setUpTestData() {
        em = emf.createEntityManager();
        productoPrecioBean.setEntityManager(em);

        em.getTransaction().begin();

        em.createQuery("DELETE FROM OrdenDetalle").executeUpdate();
        em.createQuery("DELETE FROM ProductoPrecio").executeUpdate();
        em.createQuery("DELETE FROM ComboDetalle").executeUpdate();
        em.createQuery("DELETE FROM ProductoDetalle").executeUpdate();
        em.createQuery("DELETE FROM Producto").executeUpdate();

        Producto producto1 = new Producto();
        producto1.setIdProducto(1L);
        producto1.setNombre("Producto 1");
        em.persist(producto1);
        System.out.println("EL ID DEL PRODUCTO: " + producto1.getIdProducto());

        Producto producto2 = new Producto();
        producto2.setIdProducto(2L);
        producto2.setNombre("Producto 2");
        em.persist(producto2);

        ProductoPrecio precio1 = new ProductoPrecio();
        precio1.setIdProductoPrecio(1L);
        precio1.setIdProducto(producto1);
        precio1.setPrecioSugerido(new BigDecimal("10.50"));
        em.persist(precio1);

        ProductoPrecio precio2 = new ProductoPrecio();
        precio2.setIdProductoPrecio(2L);
        precio2.setIdProducto(producto1);
        precio2.setPrecioSugerido(new BigDecimal("12.75"));
        em.persist(precio2);

        em.flush();
        em.getTransaction().commit();
    }

    @Order(1)
    @Test
    void testFindByIdProducto() {
        System.out.println("findByIdProducto");
        List<ProductoPrecio> precios = productoPrecioBean.findByIdProducto(1L, 0, 10);
        assertNotNull(precios);
        precios.forEach(p -> System.out.println("Precio: " + p.getPrecioSugerido()));

        precios.sort(Comparator.comparing(ProductoPrecio::getPrecioSugerido));
        System.out.println(precios.size());
        assertFalse(precios.isEmpty());
        assertEquals(new BigDecimal("10.50"), precios.get(0).getPrecioSugerido());
        assertEquals(new BigDecimal("12.75"), precios.get(1).getPrecioSugerido());

        /**
         if (!precios.isEmpty()) {
         assertEquals(new BigDecimal("10.50"), precios.get(0).getPrecioSugerido());
         if (precios.size() > 1) {
         assertEquals(new BigDecimal("12.75"), precios.get(1).getPrecioSugerido());
         } else {
         fail("Se esperaba al menos dos precios para el producto con ID 1");
         }
         } else {
         fail("No se encontraron precios para el producto con ID 1");
         }
         **/
        //Assertions.fail("Esta prueba no pasa quemado");

    }

    @Order(2)
    @Test
    public void testCountByIdProducto() {
        System.out.println("countByIdProducto");
        Long count = productoPrecioBean.countByIdProducto(1L);
        System.out.println("Conteo retornado: " + count);
        assertEquals(0L, count);
    }

    @Order(3)
    @Test
    public void testCountByIdProducto_NullId() {
        System.out.println("countByIdProducto con ID nulo");
        assertThrows(IllegalArgumentException.class, () -> {
            productoPrecioBean.countByIdProducto(null);
        });
    }

    @Order(4)
    @Test
    public void testCountByIdProducto_NegativeId() {
        System.out.println("countByIdProducto con ID negativo");
        assertThrows(IllegalArgumentException.class, () -> {
            productoPrecioBean.countByIdProducto(-5L);
        });
    }

}