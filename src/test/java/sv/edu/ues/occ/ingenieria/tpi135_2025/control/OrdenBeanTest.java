/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.lang.IllegalArgumentException;
import java.util.Arrays;
import java.util.List;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Orden;

/**
 * @author mjlopez
 */
public class OrdenBeanTest {

    List<Orden> LIST_ORDEN_TEST = Arrays.asList(new Orden[]{
            new Orden(1L),
            new Orden(2L),
            new Orden(3L),
            new Orden(4L),
            new Orden(5L),
            new Orden(6L),
            new Orden(7L),
            new Orden(8L)
    });

    @Test
    public void testFindAll() {
        System.out.println("test findAll");
        OrdenBean cut = new OrdenBean();

        EntityManager mockEm = Mockito.mock(EntityManager.class);
        CriteriaBuilder mockCb = Mockito.mock(CriteriaBuilder.class);
        CriteriaQuery<Orden> mockCq = Mockito.mock(CriteriaQuery.class);
        Root<Orden> mockR = Mockito.mock(Root.class);
        TypedQuery<Orden> mockTq = Mockito.mock(TypedQuery.class);

        // Caso 1: EntityManager nulo
        Assertions.assertThrows(IllegalStateException.class, () -> cut.findAll());


        // Caso 2: Flujo normal
        cut.em = mockEm;
        Mockito.when(mockEm.getCriteriaBuilder()).thenReturn(mockCb);
        Mockito.when(mockCb.createQuery(Orden.class)).thenReturn(mockCq);
        Mockito.when(mockCq.from(Orden.class)).thenReturn(mockR);
        Mockito.when(mockCq.select(mockR)).thenReturn(mockCq);
        Mockito.when(mockEm.createQuery(mockCq)).thenReturn(mockTq);
        Mockito.when(mockTq.getResultList()).thenReturn(LIST_ORDEN_TEST);

        List<Orden> resultados = cut.findAll();

        // Verificación de resultados
        Assertions.assertNotNull(resultados);
        Assertions.assertEquals(LIST_ORDEN_TEST, resultados);

        //Error de base de datos
        OrdenBean cut2 = Mockito.spy(OrdenBean.class);
        cut2.em = mockEm;
        Mockito.when(mockEm.getCriteriaBuilder()).thenReturn(mockCb);
        Mockito.when(mockEm.getCriteriaBuilder()).thenReturn(mockCb);
        Mockito.when(mockCb.createQuery(Orden.class)).thenReturn(mockCq);
        Mockito.when(mockCq.from(Orden.class)).thenReturn(mockR);
        Mockito.when(mockCq.select(mockR)).thenReturn(mockCq);
        Mockito.when(mockEm.createQuery(mockCq)).thenReturn(mockTq);
        Mockito.doThrow(PersistenceException.class).when(mockTq).getResultList();

        Assertions.assertThrows(PersistenceException.class, () -> cut2.findAll());

//        Assertions.fail("fallo exitosamente");
    }

    @Test
    public void testFindById() {
        System.out.println("test findBYId");
        Long idBuscado = 1L;
        Orden Buscado = new Orden(1L);
        OrdenBean cut = new OrdenBean();
        EntityManager mockEm = Mockito.mock(EntityManager.class);

        // Caso 1: id nulo
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.findById(null));
        // Caso 2: EntityManager nulo
        Assertions.assertThrows(IllegalStateException.class, () -> cut.findById(idBuscado));

        // Caso 2: Flujo normal
        cut.em = mockEm;
        Mockito.when(mockEm.find(Orden.class, idBuscado)).thenReturn(Buscado);
        Orden Respuesta = cut.findById(idBuscado);
        Assertions.assertNotNull(Respuesta);
        Assertions.assertEquals(Buscado, Respuesta);

        //id no encontrado
        Mockito.when(mockEm.find(Orden.class, 12345)).thenReturn(null);
        Assertions.assertThrows(EntityNotFoundException.class, () -> cut.findById(12345));
        EntityManager mockEm2 = Mockito.spy(EntityManager.class);
        cut.em = mockEm2;
        Mockito.doThrow(PersistenceException.class).when(mockEm2).find(Orden.class, idBuscado);
        Assertions.assertThrows(PersistenceException.class, () -> cut.findById(idBuscado));
//        Assertions.fail("fallo exitosamente");
    }

    @Test
    public void testFindRange() {
        System.out.println("test findRange");
        OrdenBean cut = new OrdenBean();
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        CriteriaBuilder mockCb = Mockito.mock(CriteriaBuilder.class);
        CriteriaQuery<Orden> mockCq = Mockito.mock(CriteriaQuery.class);
        Root<Orden> mockR = Mockito.mock(Root.class);
        TypedQuery<Orden> mockTq = Mockito.mock(TypedQuery.class);

        int first = 0;
        int max = 3;
        // Caso 1: EntityManager nulo
        Assertions.assertThrows(IllegalStateException.class, () -> cut.findRange(0, 5));

        // Caso 2: argumentos malos
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.findRange(-3, -5));
        //caso 3 argumentops nulos
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.findRange(null, max));

        // Caso 2 flujo normal
        cut.em = mockEm;
        Mockito.when(mockEm.getCriteriaBuilder()).thenReturn(mockCb);
        Mockito.when(mockCb.createQuery(Orden.class)).thenReturn(mockCq);
        Mockito.when(mockCq.from(Orden.class)).thenReturn(mockR);
        Mockito.when(mockCq.select(mockR)).thenReturn(mockCq);
        Mockito.when(mockEm.createQuery(mockCq)).thenReturn(mockTq);
        Mockito.when(mockTq.getResultList()).thenReturn(LIST_ORDEN_TEST.subList(first, max));

        List<Orden> resultados = cut.findRange(first, max);
        Assertions.assertNotNull(resultados);
        Assertions.assertEquals(LIST_ORDEN_TEST.subList(first, max), resultados);

        // Forzar fallo al acceder al EntityManager
        TypedQuery mockTq2 = Mockito.spy(TypedQuery.class); // Usar spy para poder interceptar métodos
        Mockito.when(mockEm.createQuery(mockCq)).thenReturn(mockTq2);
        Mockito.doThrow(PersistenceException.class).when(mockTq2).getResultList();
        Assertions.assertThrows(PersistenceException.class, () -> cut.findRange(first, max));

//        Assertions.fail("fallo exitosamente");
    }

    @Test
    public void testCreate() {
        System.out.println("test create");
        OrdenBean cut = new OrdenBean();
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        Orden ordenCreada = new Orden(9L);

        //entity nulo
        Assertions.assertThrows(IllegalStateException.class, () -> cut.create(ordenCreada));
        //registro nulo
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.create(null));
        //flujo normal
        cut.em = mockEm;
        Mockito.doNothing().when(mockEm).persist(ordenCreada);
        Assertions.assertDoesNotThrow(() -> cut.create(ordenCreada));
        //entidad ya persistida
        EntityManager mockEm2 = Mockito.spy(EntityManager.class);
        cut.em = mockEm2;
        Mockito.doThrow(EntityExistsException.class).when(mockEm2).persist(ordenCreada);
        Assertions.assertThrows(EntityExistsException.class, () -> cut.create(ordenCreada));
        //violacion de reglas
        Mockito.doThrow(ConstraintViolationException.class).when(mockEm2).persist(ordenCreada);
        Assertions.assertThrows(ConstraintViolationException.class, () -> cut.create(ordenCreada));

//        Assertions.fail("fallo exitosamnete");
    }

    @Test
    public void testCount() {
        System.out.println("test count");
        OrdenBean cut = new OrdenBean();
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        CriteriaBuilder mockCb = Mockito.mock(CriteriaBuilder.class);
        CriteriaQuery<Long> mockCq = Mockito.mock(CriteriaQuery.class);
        Root<Orden> mockR = Mockito.mock(Root.class);
        TypedQuery<Long> mockTq = Mockito.mock(TypedQuery.class);
        // em nulo
        Assertions.assertThrows(IllegalStateException.class, () -> cut.count());

        // Flujo normal
        cut.em = mockEm;
        Mockito.when(mockEm.getCriteriaBuilder()).thenReturn(mockCb);
        Mockito.when(mockCb.createQuery(Long.class)).thenReturn(mockCq);
        Mockito.when(mockCq.from(Orden.class)).thenReturn(mockR);
        Mockito.when(mockCq.select(mockCb.count(mockR))).thenReturn(mockCq);
        Mockito.when(mockEm.createQuery(mockCq)).thenReturn(mockTq);
        Mockito.when(mockTq.getSingleResult()).thenReturn((long) LIST_ORDEN_TEST.size());

        Long resultados = cut.count();
        Assertions.assertNotNull(resultados);
        Assertions.assertEquals(LIST_ORDEN_TEST.size(), resultados);

        // Forzar fallo al acceder al EntityManager
        OrdenBean cut2 = Mockito.spy(new OrdenBean());
        Mockito.when(cut2.getEntityManager()).thenReturn(null);
        Assertions.assertThrows(IllegalStateException.class, () -> cut2.count());
        //fallo de base de datos
        TypedQuery mockTq2 = Mockito.mock(TypedQuery.class);
        Mockito.when(mockEm.getCriteriaBuilder()).thenReturn(mockCb);
        Mockito.when(mockCb.createQuery(Long.class)).thenReturn(mockCq);
        Mockito.when(mockCq.from(Orden.class)).thenReturn(mockR);
        Mockito.when(mockCq.select(mockCb.count(mockR))).thenReturn(mockCq);
        Mockito.when(mockEm.createQuery(mockCq)).thenReturn(mockTq2);
        Mockito.doThrow(PersistenceException.class).when(mockTq2).getSingleResult();
        Assertions.assertThrows(PersistenceException.class, () -> cut.count());
        //fallo de base de nonUniqueResult
        Mockito.doThrow(NonUniqueResultException.class).when(mockTq2).getSingleResult();
        Assertions.assertThrows(PersistenceException.class, () -> cut.count());
    }

    @Test
    public void updateTest() {
        System.out.println("test update");
        OrdenBean cut = new OrdenBean();
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        Orden Ordenmodificar = LIST_ORDEN_TEST.get(0);
        Ordenmodificar.setSucursal("santa Ana");
        Long idModificado = 1L;
        //entity nulo
        Assertions.assertThrows(IllegalStateException.class, () -> cut.update(Ordenmodificar, 1));
        //registro nulo
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.update(null, 1));
        //id nulo

        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.update(Ordenmodificar, null));
        //flujo normal
        cut.em = mockEm;
        Mockito.when(mockEm.find(Orden.class, idModificado)).thenReturn(Ordenmodificar);
        Mockito.when(mockEm.merge(Ordenmodificar)).thenReturn(Ordenmodificar);
        Orden respuesta = cut.update(Ordenmodificar, idModificado);
        LIST_ORDEN_TEST.get(0).setSucursal("santa Ana");
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(LIST_ORDEN_TEST.get(0), respuesta);

        //id Inexistente
        Mockito.when(mockEm.find(Orden.class, 12345)).thenReturn(null);
        Assertions.assertThrows(EntityNotFoundException.class, () -> cut.update(Ordenmodificar, 12345));
        //ConstraintViolationException
        EntityManager mockEm2 = Mockito.spy(EntityManager.class);
        cut.em=mockEm2;
        Mockito.doThrow(ConstraintViolationException.class).when(mockEm2).find(Orden.class, idModificado);
        Assertions.assertThrows(ConstraintViolationException.class, () -> cut.update(Ordenmodificar, idModificado));
        //ConstraintViolationException
        Mockito.doThrow(PersistenceException.class).when(mockEm2).find(Orden.class, idModificado);
        Assertions.assertThrows(PersistenceException.class, () -> cut.update(Ordenmodificar, idModificado));

//        Assertions.fail("la prueba fallo exitosamente");
    }

    @Test
    public void testDelete() {
        System.out.println("test delete");
        OrdenBean cut = new OrdenBean();
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        CriteriaBuilder mockCb = Mockito.mock(CriteriaBuilder.class);
        CriteriaDelete<Orden> mockCd = Mockito.mock(CriteriaDelete.class);
        Root<Orden> mockR = Mockito.mock(Root.class);
        TypedQuery<Orden> mockTq = Mockito.mock(TypedQuery.class);
        Long idEliminar = 1L;
        Object ordenIdEliminado = LIST_ORDEN_TEST.get(0).getIdOrden(); // Asegúrate de que LIST_ORDEN_TEST tenga elementos

        // Test entidad a eliminar inexistente
        Assertions.assertThrows(IllegalStateException.class, () -> cut.delete(idEliminar));

        // Test para registro nulo
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.delete(null));

        // Flujo normal
        cut.em = mockEm;
        Mockito.when(mockEm.find(Orden.class, ordenIdEliminado)).thenReturn(LIST_ORDEN_TEST.get(0));
        Mockito.when(mockEm.getCriteriaBuilder()).thenReturn(mockCb);
        Mockito.when(mockCb.createCriteriaDelete(Orden.class)).thenReturn(mockCd);
        Mockito.when(mockCd.from(Orden.class)).thenReturn(mockR);
        Mockito.when(mockEm.createQuery(mockCd)).thenReturn(mockTq);

        // Aseguramos que executeUpdate no haga nada (se ejecute sin lanzar excepciones)
        Mockito.when(mockTq.executeUpdate()).thenReturn(1);
        Assertions.assertDoesNotThrow(() -> cut.delete(ordenIdEliminado));

        //ConstraintViolationException
        TypedQuery mockTq2 = Mockito.spy(TypedQuery.class);
        Mockito.when(mockEm.createQuery(mockCd)).thenReturn(mockTq2);
        Mockito.doThrow(ConstraintViolationException.class).when(mockTq2).executeUpdate();
        Assertions.assertThrows(ConstraintViolationException.class, () -> cut.delete(idEliminar));
        //PersistenceException
        Mockito.doThrow(PersistenceException.class).when(mockTq2).executeUpdate();
        Assertions.assertThrows(PersistenceException.class, () -> cut.delete(idEliminar));

        // Si llegamos aquí, el test ha pasado, ya que no se lanzó ninguna excepción
    }

}
