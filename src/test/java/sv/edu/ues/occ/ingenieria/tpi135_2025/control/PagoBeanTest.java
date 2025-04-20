package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Orden;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Pago;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.PagoDetalle;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PagoBeanTest {
    List<Pago> LIST_Pago = Arrays.asList(new Pago[]{
            new Pago(1L),
            new Pago(2L),
            new Pago(3L),
    });

    @Test
    void findByIdOrden() {
        System.out.println("Pago test findByIdOrden");
        Long idOrden = 2L;
        Integer first=0;
        Integer max=10;
        Pago registro = new Pago();
        registro.setIdOrden(new Orden(idOrden));
        PagoBean cut = new PagoBean();
        EntityManager em= Mockito.mock(EntityManager.class);
        TypedQuery tq = Mockito.mock(TypedQuery.class);
        cut.em = em;
        //flujo normal
        Mockito.when(em.createNamedQuery("Pago.findByIdOrden",Pago.class)).thenReturn(tq);
        Mockito.when(tq.setParameter("idOrden", idOrden)).thenReturn(tq);
        Mockito.when(tq.setFirstResult(first)).thenReturn(tq);
        Mockito.when(tq.setMaxResults(max)).thenReturn(tq);
        Mockito.when(tq.getResultList()).thenReturn(LIST_Pago);
        Assertions.assertDoesNotThrow(() -> {
            List<Pago> registros = cut.findByIdOrden(idOrden, first, max);
            Assertions.assertNotNull(registros);
            Assertions.assertEquals(LIST_Pago.size(), registros.size());
        });
        //no existe id
        Mockito.when(tq.setParameter("idOrden", 112233L)).thenReturn(tq);
        Mockito.when(tq.getResultList()).thenReturn(List.of());
        Assertions.assertThrows(NoResultException.class, () -> {cut.findByIdOrden(112233L, first, max);});
        //error de argumentos
        Assertions.assertThrows(IllegalArgumentException.class, () -> {cut.findByIdOrden(null, first, max);});
        Assertions.assertThrows(IllegalArgumentException.class, () -> {cut.findByIdOrden(idOrden, null, max);});
        Assertions.assertThrows(IllegalArgumentException.class, () -> {cut.findByIdOrden(idOrden, first, null);});

        //excepciones
        TypedQuery tq2 = Mockito.spy(TypedQuery.class);
        Mockito.when(em.createNamedQuery("Pago.findByIdOrden",Pago.class)).thenReturn(tq2);
        Mockito.when(tq2.setParameter("idOrden", idOrden)).thenReturn(tq2);
        Mockito.when(tq2.setFirstResult(first)).thenReturn(tq2);
        Mockito.when(tq2.setMaxResults(max)).thenReturn(tq2);
        Mockito.doThrow(NoResultException.class).when(tq2).getResultList();
        Assertions.assertThrows(NoResultException.class,() -> {cut.findByIdOrden(idOrden, first, max);});
        Mockito.doThrow(PersistenceException.class).when(tq2).getResultList();
        Assertions.assertThrows(PersistenceException.class,() -> {cut.findByIdOrden(idOrden, first, max);});
    }

    @Test
    void countByIdOrden() {
        System.out.println("Pago test findByIdOrden");
        Long idOrden = 2L;
        Integer first=0;
        Integer max=10;
        Pago registro = new Pago();
        registro.setIdOrden(new Orden(idOrden));
        PagoBean cut = new PagoBean();
        EntityManager em= Mockito.mock(EntityManager.class);
        TypedQuery tq = Mockito.mock(TypedQuery.class);
        cut.em = em;

        //flujo normal
        Mockito.when(em.createNamedQuery("Pago.countByIdOrden",Long.class)).thenReturn(tq);
        Mockito.when(tq.setParameter("idOrden", idOrden)).thenReturn(tq);
        Mockito.when(tq.getSingleResult()).thenReturn(Long.valueOf(LIST_Pago.size()));
        Assertions.assertDoesNotThrow(() -> {
            Long total = cut.countByIdOrden(idOrden);
            Assertions.assertNotNull(total);
            Assertions.assertEquals(LIST_Pago.size(), total);
        });

        //error de argumentos
        Assertions.assertThrows(IllegalArgumentException.class, () -> {cut.countByIdOrden(null);});

        //EXcepciones
        TypedQuery tq2 = Mockito.spy(TypedQuery.class);
        Mockito.when(em.createNamedQuery("Pago.countByIdOrden",Long.class)).thenReturn(tq2);
        Mockito.when(tq2.setParameter("idOrden", idOrden)).thenReturn(tq2);

        Mockito.doThrow(NoResultException.class).when(tq2).getSingleResult();
        Assertions.assertThrows(NoResultException.class,() -> {cut.countByIdOrden(idOrden);});

        Mockito.doThrow(NonUniqueResultException.class).when(tq2).getSingleResult();
        Assertions.assertThrows(NonUniqueResultException.class,() -> {cut.countByIdOrden(idOrden);});

        Mockito.doThrow(PersistenceException.class).when(tq2).getSingleResult();
        Assertions.assertThrows(PersistenceException.class,() -> {cut.countByIdOrden(idOrden);});
    }
}