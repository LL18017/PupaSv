/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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

/**
 *
 * @author hdz
 */
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ComboDetalleBeanIT extends AbstractContainerTest {

    ComboDetalleBean cut;

    Long totalEnScript = 3l;//cantidad en el script
    Long idDePrueba = 1001L;//ya se encuentra en el script
    static ComboDetallePK idCreadoEnPrueba = null;

    @BeforeAll
    void init() {
        cut = new ComboDetalleBean();
        cut.em = emf.createEntityManager(); // Solo una vez para todas las pruebas
    }

    @Order(1)
    @Test
    public void create() {
        System.out.println("ComboDetalle testIT create");
        ComboDetalle nuevo = new ComboDetalle();
        ComboDetallePK pk = new ComboDetallePK(1004L, 1002L); // Nuevos valores
        nuevo.setComboDetallePK(pk);
        nuevo.setCantidad(7);
        nuevo.setActivo(true);
        try {
            cut.em.getTransaction().begin();
            Assertions.assertDoesNotThrow(() -> cut.create(nuevo));
            cut.em.getTransaction().commit();
            //ID creado para usar en futuras pruebas
            idCreadoEnPrueba = pk; // aqui me esta generando error
            // Validar que fue persistido
            ComboDetalle persistido = cut.em.find(ComboDetalle.class, pk);
            Assertions.assertNotNull(persistido, "El ComboDetalle no fue persistido.");
            Assertions.assertEquals(7, persistido.getCantidad());
            System.out.println("ComboDetalle creado con ID: " + pk);
            //Intentar persistir null
            cut.em.getTransaction().begin();
            Assertions.assertThrows(IllegalArgumentException.class, () -> cut.create(null));
            cut.em.getTransaction().commit();
        } catch (Exception e) {
            if (cut.em.getTransaction().isActive()) {
                cut.em.getTransaction().rollback();
            }
            Assertions.fail("Excepción inesperada: " + e.getMessage());
        }
    }

}
