package sv.edu.ues.occ.ingenieria.tpi135_2025.control;


import jakarta.persistence.*;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.*;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;  // ✅ CORRECTO
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrdenDetalleBeanIT extends AbstractContainerTest {

    OrdenDetalleBean cut;

    Long totalEnScript = 4l;//cantidad en el script
    Long idDePrueba = 1001L;//ya se encuentra en el script
    Long idCreadoEnPrueba = 1001L;//se cambiara luego
    Integer idTipoProdcutoCreado = 1002;

    Long idOrdenParPruebas = 12348L;
    Long idComboParPruebas = 1001L;
    Long idProductoPrecioPAraPruebas = 1001L;
    Long idProductoParaPruebas = 1003L;

    @BeforeEach
    void setUp() {
        cut = new OrdenDetalleBean();
    }


    @Order(1)
    @Test
    public void testGenerarOrdenDetalleProducto() {
        System.out.println("OrdenDetalle testIt generarOrdenDetalleProducto");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        assertDoesNotThrow(() -> cut.generarOrdenDetalleProducto(idOrdenParPruebas, idProductoParaPruebas, 2));

        //falo de argumentos
        assertThrows(IllegalArgumentException.class, () -> cut.generarOrdenDetalleProducto(null, idProductoParaPruebas, 2));
        assertThrows(IllegalArgumentException.class, () -> cut.generarOrdenDetalleProducto(idOrdenParPruebas, null, 2));
        assertThrows(NoResultException.class, () -> cut.generarOrdenDetalleProducto(idOrdenParPruebas, 112233L, 2));

//        Assertions.fail("No pasa ");
    }

    @Order(2)
    @Test
    public void testGenerarOrdenDetalleDesdeCombo() {
        System.out.println("OrdenDetalle testIt generarOrdenDetalleDesdeCombo");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        //flujo normal
        assertDoesNotThrow(() -> cut.generarOrdenDetalleDesdeCombo(idOrdenParPruebas, idComboParPruebas, 2));
        //error de argumentos
        assertThrows(IllegalArgumentException.class, () -> {
            cut.generarOrdenDetalleDesdeCombo(idOrdenParPruebas, null, 2);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            cut.generarOrdenDetalleDesdeCombo(null, idComboParPruebas, 2);
        });
        assertThrows(NoResultException.class, () -> {
            cut.generarOrdenDetalleDesdeCombo(idOrdenParPruebas, 112233L, 2);
        });

        //Assertions.fail("no pasa ");
    }


    @Order(4)
    @Test
    public void testGenerarOrdenDetalleMixto() {
        System.out.println("OrdenDetalle testIt generarOrdenDetalleMixto");

        EntityManager em = emf.createEntityManager();
        cut.em = em;

        // Crear la lista de productos con cantidad
        List<Object[]> productosList = new ArrayList<>();
        productosList.add(new Object[]{1001L, 2});  // Coca x2
        productosList.add(new Object[]{1002L, 4});  // Pepsi x4
        productosList.add(new Object[]{1003L, 2});  // pupusas x2
        // Crear la lista de productos con cantidad
        List<Object[]> comboList = new ArrayList<>();
        comboList.add(new Object[]{1001L, 2});
        comboList.add(new Object[]{1002L, 4});

        // Lista de combos (IDs existentes en la base de datos)

        em.getTransaction().begin();
        assertDoesNotThrow(() -> cut.generarOrdenDetalleMixto(idOrdenParPruebas, productosList, comboList));
        em.getTransaction().commit();

        // Mostrar resultados

        em.close();
    }

    @Order(5)
    @Test
    public void update() {
        System.out.println("OrdenDetalle testIt update");

        EntityManager em = emf.createEntityManager();
        cut.em = em;
        OrdenDetalle registro = new OrdenDetalle();
        Integer cantidadModificada = 99;
        BigDecimal precioModificado = BigDecimal.valueOf(99.00);
        registro.setCantidad(cantidadModificada);
        registro.setPrecio(precioModificado);


        // Lista de combos (IDs existentes en la base de datos)
        Assertions.assertDoesNotThrow(() -> {
            em.getTransaction().begin();
            OrdenDetalle resultado = cut.update(registro, idOrdenParPruebas, idProductoPrecioPAraPruebas);
            em.getTransaction().commit();
            assertNotNull(resultado);
            assertEquals(registro.getCantidad(), resultado.getCantidad());
            assertEquals(registro.getPrecio(), resultado.getPrecio());
        });

        em.close();
    }

    @Order(6)
    @Test
    public void delete() {
        System.out.println("OrdenDetalle testIt delete");

        EntityManager em = emf.createEntityManager();
        cut.em = em;

        // Lista de combos (IDs existentes en la base de datos)
        Assertions.assertDoesNotThrow(() -> {
            em.getTransaction().begin();
            cut.delete( idOrdenParPruebas, idProductoPrecioPAraPruebas);
            em.getTransaction().commit();
        });

        Assertions.assertThrows(NoResultException.class,()->{
            em.getTransaction().begin();
            cut.findByIdOrdenAndIdPrecioProducto(idOrdenParPruebas, idProductoPrecioPAraPruebas);
            em.getTransaction().commit();
        });

        em.close();
    }


}


