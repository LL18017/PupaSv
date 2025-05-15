/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Combo;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.TipoProducto;

/**
 *
 * @author hdz
 */
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ComboBeanIT extends AbstractContainerTest {

    ComboBean cut;
    EntityManager mockEm;
    ComboDetalleBean cut2;

    Long totalEnScript = 11l;//cantidad en el script
    Long idDePrueba = 1001L;//ya se encuentra en el script
    static Long idCreadoEnPrueba = 0L;//se cambiara luego

    @BeforeAll
    void init() {
        cut = new ComboBean();
        cut2 = new ComboDetalleBean();
        cut.em = emf.createEntityManager(); // Solo una vez para todas las pruebas
    }

    @Order(1)
    @Test
    public void create() {
        System.out.println("Combo testIT create");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Combo creado = new Combo();
        creado.setIdCombo(1010L); // Asignar manualmente un ID único
        creado.setNombre("Tipo test");
        try {
            // Flujo normal: Persistimos el objeto
            cut.em.getTransaction().begin();
            Assertions.assertDoesNotThrow(() -> cut.create(creado));
            cut.em.getTransaction().commit();
            // Guardar el ID para otras pruebas
            idCreadoEnPrueba = creado.getIdCombo(); // Asegura que delete lo use
            // Validar el ID generado
            Assertions.assertNotNull(idCreadoEnPrueba);
            // Validar que el objeto se persistió correctamente en la base de datos
            Combo persistido = em.find(Combo.class, creado.getIdCombo());
            Assertions.assertNotNull(persistido);
            Assertions.assertEquals(creado.getNombre(), persistido.getNombre());
            // Verificar que el total aumentó correctamente
            totalEnScript = totalEnScript + 1;

            // Simular un caso de error al intentar persistir un valor nulo
            cut.em.getTransaction().begin();
            Assertions.assertThrows(IllegalArgumentException.class, () -> cut.create(null));
            cut.em.getTransaction().commit();

        } catch (Exception e) {
            // Realizar rollback en caso de error
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
//            Assertions.fail("Excepción inesperada: " + e.getMessage());
        } finally {
            // Cerrar el EntityManager
            em.close();
        }
    }

    @Order(2)
    @Test
    void findRange() {
        System.out.println("Combo testIT findRange");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        //flujo normal
        cut.em.getTransaction().begin();
        List<Combo> respuesta = cut.findRange(first, max);
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
        System.out.println("Combo testIT fiand all");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        //flujo normal
        cut.em.getTransaction().begin();
        List<Combo> respuesta = cut.findAll();
        cut.em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(totalEnScript, respuesta.size());

        em.close();
//        Assertions.fail("fallo exitosamente");
    }

    @Order(4)
    @Test
    public void findById() {
        System.out.println("Combo testIT fiandById");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        em.getTransaction().begin();
        Combo respuesta = cut.findById(idDePrueba);
        em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(idDePrueba, respuesta.getIdCombo());
        em.close();
//        Assertions.fail("fallo exitosamente");
    }

    @Order(5)
    @Test
    public void contar() {
        System.out.println("Combo testIT Contar");
        ComboBean cut = new ComboBean();
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
        System.out.println("Combo testIT update");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        String nombreEsperado = "hace hambruna test";
        Combo registroActualizar = new Combo(idDePrueba);
        registroActualizar.setNombre(nombreEsperado);

        try {
            // Flujo normal: Actualizamos el registro
            cut.em.getTransaction().begin();
            Assertions.assertDoesNotThrow(() -> cut.update(registroActualizar, idDePrueba)); // Actualización sin errores
            cut.em.getTransaction().commit();

            // Verificamos que el registro se actualizó correctamente
            Combo respuesta = cut.findById(idDePrueba);
            Assertions.assertNotNull(respuesta, "El objeto actualizado no debe ser null.");
            Assertions.assertEquals(nombreEsperado, respuesta.getNombre(), "El nombre no coincide con el esperado.");
            Assertions.assertEquals(idDePrueba, respuesta.getIdCombo(), "El ID no coincide con el esperado.");

            // Casos negativos
            // ID menor a 0
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                cut.em.getTransaction().begin();
                cut.update(registroActualizar, -63);
                cut.em.getTransaction().commit();
            }, "Se esperaba IllegalArgumentException para ID menor a 0.");

            // ID nulo
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
//                cut.em.getTransaction().begin();
                cut.update(registroActualizar, null);
//                cut.em.getTransaction().commit();
            }, "Se esperaba IllegalArgumentException para ID nulo.");

            // Registro inexistente
            Assertions.assertThrows(RuntimeException.class, () -> {
                cut.em.getTransaction().begin();
                cut.update(registroActualizar, 112233L); // ID que no existe
                cut.em.getTransaction().commit();
            }, "Se esperaba EntityNotFoundException para registro inexistente.");

        } catch (Exception e) {
            // Rollback en caso de errores inesperados
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
//            Assertions.fail("Excepción inesperada: " + e.getMessage());
        } finally {
            // Cerrar el EntityManager
            em.close();
        }
    }

    @Order(7)
    @Test
    public void delete() {
        System.out.println("Combo testIT delete");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        try {
            Assertions.assertNotEquals(0L, idCreadoEnPrueba, "El ID no fue asignado correctamente.");
            // Verifica que el objeto existe antes de eliminarlo
            Combo existe = em.find(Combo.class, idCreadoEnPrueba);
            Assertions.assertNotNull(existe, "El registro no existe en la base de datos.");
            // Flujo normal: Eliminar el registro existente
            em.getTransaction().begin();
            Assertions.assertDoesNotThrow(() -> cut.delete(idCreadoEnPrueba));
            em.getTransaction().commit();
            // Verificar que el registro ha sido eliminado correctamente
            Combo eliminado = em.find(Combo.class, idCreadoEnPrueba);
            Assertions.assertNull(eliminado, "El registro eliminado aún existe en la base de datos.");
            // Casos negativos
            // Intentar eliminar un ID menor a 0 ( lanzar IllegalArgumentException)
            Assertions.assertThrows(IllegalArgumentException.class, () -> cut.delete(-90L),
                    "Se esperaba IllegalArgumentException para ID menor a 0.");
            // Intentar eliminar un ID nulo
            Assertions.assertThrows(IllegalArgumentException.class, () -> cut.delete(null),
                    "Se esperaba IllegalArgumentException para ID nulo.");
            // Intentar eliminar un registro inexistente ( lanzar EntityNotFoundException)
            Assertions.assertThrows(EntityNotFoundException.class, () -> cut.delete(112233L),
                    "Se esperaba EntityNotFoundException para registro inexistente.");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
//            Assertions.fail("Excepción inesperada: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    @Order(8)
//    @Test
    public void findByNombre() {
        System.out.println("Combo testIT findByNombre");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        String nombreExistente = "superCombo"; // Nombre creado en la prueba anterior
        String nombreInexistente = "NoExisteCombo123";

        try {
            em.getTransaction().begin();

            // Caso 1: Buscar combo existente
            List<Combo> resultado = cut.findByNombre(nombreExistente);
            Assertions.assertNotNull(resultado);
            Assertions.assertFalse(resultado.isEmpty(), "Debe devolver al menos un combo");
            Assertions.assertEquals(nombreExistente, resultado.get(0).getNombre());

            // Caso 2: Buscar combo inexistente
            List<Combo> vacio = cut.findByNombre(nombreInexistente);
            Assertions.assertNotNull(vacio);
            Assertions.assertTrue(vacio.isEmpty(), "Debe devolver una lista vacía si no hay coincidencias");

            // Caso 3: Nombre nulo
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                cut.findByNombre(null);
            });

            // Caso 4: Nombre vacío
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                cut.findByNombre("   ");
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

    @Order(10)
    @Test
    void findRangeWithPrice() {
        System.out.println("Combo testIT findRangeWithPrice");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        //flujo normal
        cut.em.getTransaction().begin();
        List<Object[]> respuesta = cut.findRangeWithPrice(first, max);
        cut.em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        for (Object[] fila : respuesta) {
            Long idCombo = (Long) fila[0];
            Boolean activo = (Boolean) fila[1];
            String nombre = (String) fila[2];
            String descripcion = (String) fila[3];
            String url = (String) fila[4];
            BigDecimal totalPrecio = (BigDecimal) fila[5];

            System.out.println("Combo: " + idCombo + " | " + nombre + " | Total: $" + totalPrecio);
        }
        Assertions.assertEquals(10, respuesta.size());

        //fallo de argumentos
        cut.em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.findRangeWithPrice(null, max));
        cut.em.getTransaction().commit();

        //fallo de 0
        cut.em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.findRangeWithPrice(0, 0));
        cut.em.getTransaction().commit();
        em.close();
//        Assertions.fail("fallo exitosamente");
    }
}
