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
    Long idProductoParaPruebas=1001L;
    @BeforeAll
    void setup() {
        super.inicializar();
        productoPrecioBean = new ProductoPrecioBean();
    }


    @BeforeEach
    void setUpTestData() {
        em = emf.createEntityManager();
        productoPrecioBean.setEntityManager(em);

    }

    @Order(1)
    @Test
    void testFindByIdProducto() {
        System.out.println("findByIdProducto");
        ProductoPrecio precio = productoPrecioBean.findByIdProducto(idProductoParaPruebas);
        assertNotNull(precio);
        assertEquals(new BigDecimal("1.50"), precio.getPrecioSugerido());
        //Assertions.fail("Esta prueba no pasa quemado");

    }

    @Order(2)
    @Test
    public void testCountByIdProducto() {
        System.out.println("countByIdProducto");
        Long count = productoPrecioBean.countByIdProducto(1L);
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