/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Combo;

/**
 *
 * @author mjlopez bean para control de entidad Orden
 */
@LocalBean
@Stateless
public class ComboBean extends AbstractDataAccess<Combo> implements Serializable {

    @PersistenceContext(unitName = "PupaSV-PU")
    EntityManager em;

    public ComboBean() {
        super(Combo.class);
    }

    @Override
    public EntityManager getEntityManager() {
        if (em == null) {
            throw new IllegalStateException("EntityManager no ha sido inicializado correctamente.");
        }
        return em;
    }

    @Override
    public String orderParameterQuery() {
        return "idCombo";
    }

    public void update(Combo combo, Long id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido.");
            }

            Combo registroExistente = em.find(Combo.class, id);
            if (registroExistente == null) {
                throw new EntityNotFoundException("Registro no encontrado.");
            }

            registroExistente.setNombre(combo.getNombre());
            em.merge(registroExistente);

        } catch (IllegalStateException e) {
            throw new RuntimeException("Error al acceder al EntityManager.", e);
        }
    }

    public void delete(Long id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("El ID no puede ser null.");
            }
            if (id <= 0) {
                throw new IllegalArgumentException("El ID debe ser mayor que 0.");
            }

            Combo registroExistente = em.find(Combo.class, id);
            if (registroExistente == null) {
                throw new EntityNotFoundException("Registro con ID " + id + " no encontrado en la base de datos.");
            }

            em.remove(em.contains(registroExistente) ? registroExistente : em.merge(registroExistente));

        } catch (IllegalStateException e) {
            throw new RuntimeException("Error al acceder al EntityManager.", e);
        }
    }
}



//prueba de IT
//    @BeforeEach
//    void setUp() {
//        cut = new ComboBean();
//        mockEm = Mockito.mock(EntityManager.class);
//        cut2 = Mockito.spy(new ComboDetalleBean());
//
//        // Asigna IDs únicos temporales para pruebas
//        cut.em = emf.createEntityManager();
//        Mockito.doAnswer(invocation -> {
//            Combo combo = invocation.getArgument(0);
//            if (combo.getIdCombo() == null) {
//                combo.setIdCombo(1004L); // ID simulado
//            }
//            return null;
//        }).when(mockEm).persist(Mockito.any());
//    }
// Flujo normal: Eliminar el registro existente
            // Validar que el ID fue correctamente inicializado
//            Assertions.assertTrue(idCreadoEnPrueba != null && idCreadoEnPrueba > 0,
//                    "El ID del combo creado en prueba no está inicializado correctamente.");
//            em.getTransaction().begin();
//            System.out.println("ID recibido para eliminación: " + idCreadoEnPrueba);
//
//            Assertions.assertDoesNotThrow(() -> cut.delete(idCreadoEnPrueba));
//            em.getTransaction().commit();