/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Orden;

/**
 *
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

        // Caso 2: argumentos invalidos
        List<Orden> resultados = cut.findRange(-3, -4);
        Assertions.assertNotNull(resultados);
        Assertions.assertEquals(0, resultados.size());

        // Caso 2 flujo normal
        cut.em = mockEm;
        Mockito.when(mockEm.getCriteriaBuilder()).thenReturn(mockCb);
        Mockito.when(mockCb.createQuery(Orden.class)).thenReturn(mockCq);
        Mockito.when(mockCq.from(Orden.class)).thenReturn(mockR);
        Mockito.when(mockCq.select(mockR)).thenReturn(mockCq);
        Mockito.when(mockEm.createQuery(mockCq)).thenReturn(mockTq);
        Mockito.when(mockTq.getResultList()).thenReturn(LIST_ORDEN_TEST.subList(first, max));

        resultados = cut.findRange(first, max);
        Assertions.assertNotNull(resultados);
        Assertions.assertEquals(LIST_ORDEN_TEST.subList(first, max), resultados);

        // Forzar fallo al acceder al EntityManager
        OrdenBean cut2 = Mockito.spy(new OrdenBean()); // Usar spy para poder interceptar métodos
        Mockito.doThrow(IllegalStateException.class).when(cut2).getEntityManager();
        Assertions.assertThrows(IllegalStateException.class, () -> cut2.findRange(first, max));

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
        try {
            cut.create(ordenCreada);
        } catch (Exception e) {
            Assertions.fail(e);
        }
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
        OrdenBean cut2 = Mockito.spy(new OrdenBean()); // Usar spy para poder interceptar métodos
        Mockito.doThrow(IllegalStateException.class).when(cut2).getEntityManager();
        Assertions.assertThrows(IllegalStateException.class, () -> cut2.count());

    }

    @Test
    public void updateTest() {
        System.out.println("test update");
        OrdenBean cut = new OrdenBean();
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        Orden Ordenmodificar = LIST_ORDEN_TEST.get(0);
        Ordenmodificar.setSucursal("santa Ana");

        //entity nulo
        Assertions.assertThrows(IllegalStateException.class, () -> cut.update(Ordenmodificar));

        //registro nulo
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.update(null));
        //flujo normal

        cut.em = mockEm;
        Mockito.when(mockEm.merge(Ordenmodificar)).thenReturn(LIST_ORDEN_TEST.get(0));
        Orden respuesta = cut.update(Ordenmodificar);
        LIST_ORDEN_TEST.get(0).setSucursal("santa Ana");

        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(LIST_ORDEN_TEST.get(0), respuesta);
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
        Object OrdenEliminadaId = LIST_ORDEN_TEST.get(0).getIdOrden(); // Asegúrate de que LIST_ORDEN_TEST tenga elementos

        // Test para entidad nula
        Assertions.assertThrows(IllegalStateException.class, () -> cut.delete(OrdenEliminadaId));

        // Test para registro nulo
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.delete(null));

        // Flujo normal
        cut.em = mockEm;
        Mockito.when(mockEm.find(Orden.class, OrdenEliminadaId)).thenReturn(LIST_ORDEN_TEST.get(0));
        Mockito.when(mockEm.getCriteriaBuilder()).thenReturn(mockCb);
        Mockito.when(mockCb.createCriteriaDelete(Orden.class)).thenReturn(mockCd);
        Mockito.when(mockCd.from(Orden.class)).thenReturn(mockR);
        Mockito.when(mockEm.createQuery(mockCd)).thenReturn(mockTq);

        // Aseguramos que executeUpdate no haga nada (se ejecute sin lanzar excepciones)
        Mockito.when(mockTq.executeUpdate()).thenReturn(1);

        // Llamamos al método delete y verificamos que no se lance ninguna excepción
        try {
            cut.delete(OrdenEliminadaId); // Llamamos al método delete con el mock
        } catch (Exception e) {
            Assertions.fail("No se esperaba una excepción: " + e.getMessage());
        }

        // Si llegamos aquí, el test ha pasado, ya que no se lanzó ninguna excepción
    }

}
