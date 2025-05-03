/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.junit.jupiter.Testcontainers;
import static sv.edu.ues.occ.ingenieria.tpi135_2025.control.ComboBeanIT.idCreadoEnPrueba;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Combo;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ComboDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ComboDetallePK;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;

/**
 *
 * @author hdz
 */
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ComboDetalleBeanIT extends AbstractContainerTest {

    ComboDetalleBean cut;

    Long totalEnScript = 25l;//cantidad en el script
    Long idComboPrueba = 1001L;
    Long idProductoPrueba = 1001L;

    Long idComboCreado = 1011L;
    Long idProductoCreado = 1003L;

    ComboDetallePK idCreadoEnPrueba = null;

    @BeforeEach
    void setUp() {
        cut = new ComboDetalleBean();
    }

    @Order(1)
    @Test
    public void create() {
        System.out.println("ComboDetalle testIT create");
        ComboDetalle nuevo = new ComboDetalle();
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        nuevo.setCantidad(7);
        nuevo.setActivo(true);

        try {
            cut.em.getTransaction().begin();
            cut.create(nuevo, idComboCreado, idProductoCreado);
            cut.em.getTransaction().commit();
            totalEnScript++;

            // Validar que fue persistido
            ComboDetallePK pk = new ComboDetallePK(idComboCreado, idProductoCreado);
            ComboDetalle persistido = cut.em.find(ComboDetalle.class, pk);
            Assertions.assertNotNull(persistido, "El ComboDetalle no fue persistido.");
            Assertions.assertEquals(7, persistido.getCantidad());
            System.out.println("ComboDetalle creado con ID: " + pk);

            // registro nulo
            cut.em.getTransaction().begin();
            Assertions.assertThrows(IllegalArgumentException.class, () -> cut.create(null, idComboCreado, idProductoCreado));
            cut.em.getTransaction().commit();

            // idCombo nulo
            ComboDetalle conIdComboNulo = new ComboDetalle();
            conIdComboNulo.setCantidad(5);
            conIdComboNulo.setActivo(true);
            em.getTransaction().begin();
            Assertions.assertThrows(IllegalArgumentException.class, () -> cut.create(conIdComboNulo, null, idProductoCreado));
            em.getTransaction().commit();

            // idProducto nulo
            ComboDetalle conIdProductoNulo = new ComboDetalle();
            conIdProductoNulo.setCantidad(5);
            conIdProductoNulo.setActivo(true);
            em.getTransaction().begin();
            Assertions.assertThrows(IllegalArgumentException.class, () -> cut.create(conIdProductoNulo, idComboCreado, null));
            em.getTransaction().commit();

            // idCombo inexistente
            ComboDetalle comboIdInexistente = new ComboDetalle();
            comboIdInexistente.setCantidad(5);
            comboIdInexistente.setActivo(true);
            em.getTransaction().begin();
            Assertions.assertThrows(EntityNotFoundException.class, () -> cut.create(comboIdInexistente, 9999L, idProductoCreado));
            em.getTransaction().commit();

            // idProducto inexistente
            ComboDetalle productoIdInexistente = new ComboDetalle();
            productoIdInexistente.setCantidad(5);
            productoIdInexistente.setActivo(true);
            em.getTransaction().begin();
            Assertions.assertThrows(EntityNotFoundException.class, () -> cut.create(productoIdInexistente, idComboCreado, 9999L));
            em.getTransaction().commit();

        } catch (Exception e) {
            if (cut.em.getTransaction().isActive()) {
                cut.em.getTransaction().rollback();
            }
            Assertions.fail("Excepción inesperada: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    @Order(2)
    @Test
    void findByIdComboAndIdProducto() {
        System.out.println("ComboDetalle testIT findByIdComboAndIdProducto");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        try {
            // Flujo normal
            em.getTransaction().begin();
            ComboDetalle resultado = cut.findByIdComboAndIdProducto(idComboPrueba, idProductoPrueba);
            em.getTransaction().commit();
            Assertions.assertNotNull(resultado);
            Assertions.assertEquals(idComboPrueba, resultado.getComboDetallePK().getIdCombo());
            Assertions.assertEquals(idProductoPrueba, resultado.getComboDetallePK().getIdProducto());
            // Fallo argumentos nulos o inválidos
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                cut.findByIdComboAndIdProducto(null, idProductoCreado);
            });
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                cut.findByIdComboAndIdProducto(idComboCreado, null);
            });
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                cut.findByIdComboAndIdProducto(0L, idProductoCreado);
            });
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                cut.findByIdComboAndIdProducto(idComboCreado, 0L);
            });
            // Fallo por registro inexistente
            Assertions.assertThrows(EntityNotFoundException.class, () -> {
                cut.findByIdComboAndIdProducto(9999L, idProductoPrueba);
            });
            Assertions.assertThrows(EntityNotFoundException.class, () -> {
                cut.findByIdComboAndIdProducto(idComboCreado, 9999L);
            });
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Order(3)
    @Test
    void findRangeByCombo() {
        System.out.println("ComboDetalle testIT findRangeByCombo");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        try {
            em.getTransaction().begin();

            // Buscar 2 productos existentes
            List<Producto> productos = em.createQuery("SELECT p FROM Producto p WHERE p.activo = true", Producto.class)
                    .setMaxResults(2)
                    .getResultList();

            Assertions.assertTrue(productos.size() >= 2, "Debe haber al menos 2 productos en la base de datos para esta prueba.");
            for (Producto producto : productos) {
                ComboDetallePK pk = new ComboDetallePK(idComboPrueba, producto.getIdProducto());
                // Verificar que no exista previamente
                if (em.find(ComboDetalle.class, pk) == null) {
                    ComboDetalle detalle = new ComboDetalle();
                    detalle.setComboDetallePK(pk);
                    detalle.setCantidad(3);
                    detalle.setActivo(true);
                    em.persist(detalle);
                }
            }
            em.getTransaction().commit();
            // Prueba del método findRangeByCombo
            List<ComboDetalle> resultados = cut.findRangeByCombo(idComboPrueba, 0, 2);
            Assertions.assertNotNull(resultados);
            Assertions.assertFalse(resultados.isEmpty(), "La lista de resultados no debe estar vacía.");
            Assertions.assertTrue(resultados.size() <= 2, "La lista no debe tener más de 2 elementos.");
//            System.out.println("Elementos obtenidos en el rango: " + resultados.size());
            // Pruebas de argumentos inválidos
            Assertions.assertThrows(IllegalArgumentException.class, () -> cut.findRangeByCombo(null, 0, 2));
            Assertions.assertThrows(IllegalArgumentException.class, () -> cut.findRangeByCombo(idComboPrueba, -1, 2));
            Assertions.assertThrows(IllegalArgumentException.class, () -> cut.findRangeByCombo(idComboPrueba, 0, 0));
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace(); // Para ver más detalles en consola
            Assertions.fail("Excepción inesperada: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    @Order(4)
    @Test
    public void update() {
        System.out.println("ComboDetalle testIT update");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        // Datos de prueba
        Long idComboPrueba = 1001L;
        Long idProductoPrueba = 1003L;
        ComboDetalle actualizado = new ComboDetalle();
        actualizado.setCantidad(15);  // Nuevas observaciones de prueba
        actualizado.setActivo(true);
        try {
            // Creamos el ComboDetalle para actualización
            em.getTransaction().begin();
            cut.updateByComboDetallePK(actualizado, idComboPrueba, idProductoPrueba);
            em.getTransaction().commit();

            // Verificamos que la actualización fue exitosa
            ComboDetalle respuesta = cut.findByIdComboAndIdProducto(idComboPrueba, idProductoPrueba);
            Assertions.assertNotNull(respuesta);
            Assertions.assertEquals(15, respuesta.getCantidad());
            Assertions.assertTrue(respuesta.getActivo());

            // Fallo de argumentos: registro nulo
            em.getTransaction().begin();
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                cut.updateByComboDetallePK(null, idComboPrueba, idProductoPrueba);
            });
            em.getTransaction().commit();

            // Fallo de argumentos: idCombo nulo
            em.getTransaction().begin();
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                cut.updateByComboDetallePK(actualizado, null, idProductoPrueba);
            });
            em.getTransaction().commit();

            // Fallo de argumentos: idProducto nulo
            em.getTransaction().begin();
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                cut.updateByComboDetallePK(actualizado, idComboPrueba, null);
            });
            em.getTransaction().commit();

            // Fallo: registros inexistentes (idCombo o idProducto no existen)
            em.getTransaction().begin();
            Assertions.assertThrows(EntityNotFoundException.class, () -> {
                cut.updateByComboDetallePK(actualizado, 112233L, idProductoPrueba);
            });
            em.getTransaction().commit();

            em.getTransaction().begin();
            Assertions.assertThrows(EntityNotFoundException.class, () -> {
                cut.updateByComboDetallePK(actualizado, idComboPrueba, 112233L);
            });
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            Assertions.fail("Excepción inesperada: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    @Order(5)
    @Test
    public void deleteByComboDetallePK() {
        System.out.println("ComboDetalle testIT delete");
        EntityManager em = emf.createEntityManager(); // Usa emf directamente aquí
        cut.setEntityManager(em); // Le asignamos este EntityManager a cut
        em.getTransaction().begin();

        // Verificamos que el registro exista antes
        ComboDetalle existente = cut.findByIdComboAndIdProducto(idComboPrueba, idProductoPrueba);
        Assertions.assertNotNull(existente, "El ComboDetalle debería existir antes de la eliminación");
        try {
            // Eliminamos el registro
            cut.deleteByComboDetallePK(idComboPrueba, idProductoPrueba);

            em.getTransaction().commit();

            // Intentamos obtenerlo nuevamente: debe fallar
            em.getTransaction().begin();
            Assertions.assertThrows(EntityNotFoundException.class, () -> {
                cut.findByIdComboAndIdProducto(idComboPrueba, idProductoPrueba);
            });
            em.getTransaction().commit();

            // Casos negativos
            em.getTransaction().begin();
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                cut.deleteByComboDetallePK(null, idProductoPrueba);
            });
            em.getTransaction().commit();

            em.getTransaction().begin();
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                cut.deleteByComboDetallePK(idComboPrueba, null);
            });
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            Assertions.fail("Excepción inesperada: " + e.getMessage());
        } finally {
            em.close();
        }
    }
}
