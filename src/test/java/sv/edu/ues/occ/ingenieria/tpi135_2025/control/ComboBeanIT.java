/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
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

    Long totalEnScript = 3l;//cantidad en el script
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
        creado.setIdCombo(1004L); // Asignar manualmente un ID único
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
            System.out.println("Total actualizado en script: " + totalEnScript);
            System.out.println("ID creado en create(): " + idCreadoEnPrueba);

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
            System.out.println("Registro actualizado correctamente: " + respuesta);

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
            System.out.println("ID para eliminación: " + idCreadoEnPrueba);
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
            System.out.println("Registro eliminado correctamente: " + idCreadoEnPrueba);
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

}
