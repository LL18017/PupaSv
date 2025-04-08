package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Pago;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.PagoDetalle;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PagoDetalleBeanTest {
    List<PagoDetalle> LIST_PagoDetalle_TEST = Arrays.asList(new PagoDetalle[]{
            new PagoDetalle(1L),
            new PagoDetalle(2L),
            new PagoDetalle(3L),
            new PagoDetalle(4L),
            new PagoDetalle(5L),
            new PagoDetalle(6L),
            new PagoDetalle(7L),
            new PagoDetalle(8L)
    });


    @Test
    void findByIdPago() {
        System.out.println("test findByIdPago");
        PagoDetalleBean cut = new PagoDetalleBean();
        PagoDetalle pd = new PagoDetalle();
        Long idPago = 2L;
        Pago pago = new Pago(idPago);
        pd.setIdPago(pago);
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        TypedQuery mockTq = Mockito.mock(TypedQuery.class);
        Integer first=0;
        Integer max=10;
        cut.em = mockEm;
        //error de argumentos
        assertThrows(IllegalArgumentException.class, () -> cut.findRangeByIdPago(null,first,max));
        assertThrows(IllegalArgumentException.class, () -> cut.findRangeByIdPago(idPago,-60,max));
        assertThrows(IllegalArgumentException.class, () -> cut.findRangeByIdPago(idPago,first,null));
        // no existe pago
        Mockito.when(mockEm.find(Pago.class, 112233L)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> cut.findRangeByIdPago(112233L,first,max));

        //flujo normal
        Mockito.when(mockEm.find(Pago.class, idPago)).thenReturn(pago);

        Mockito.when(mockEm.createNamedQuery("PagoDetalle.findByIdPago",PagoDetalle.class)).thenReturn(mockTq);
        Mockito.when(mockTq.setParameter("idPago", idPago)).thenReturn(mockTq);
        Mockito.when(mockTq.setFirstResult(first)).thenReturn(mockTq);
        Mockito.when(mockTq.setMaxResults(max)).thenReturn(mockTq);
        Mockito.when(mockTq.getResultList()).thenReturn(LIST_PagoDetalle_TEST);

        List<PagoDetalle> resultado = cut.findRangeByIdPago(idPago,first,max);
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(LIST_PagoDetalle_TEST.size(), resultado.size());

        //excepciones
        TypedQuery mockTq2 = Mockito.spy(TypedQuery.class);
        Mockito.when(mockEm.createNamedQuery("PagoDetalle.findByIdPago",PagoDetalle.class)).thenReturn(mockTq2);
        Mockito.when(mockTq2.setParameter("idPago", idPago)).thenReturn(mockTq2);
        Mockito.when(mockTq2.setFirstResult(first)).thenReturn(mockTq2);
        Mockito.when(mockTq2.setMaxResults(max)).thenReturn(mockTq2);
        Mockito.doThrow(PersistenceException.class).when(mockTq2).getResultList();

        Assertions.assertThrows(PersistenceException.class, () -> cut.findRangeByIdPago(idPago,first,max));

//        fail("fallo existosamente");
    }

    @Test
    void countByIdPago() {
        System.out.println("test countByIdPago");
        PagoDetalleBean cut = new PagoDetalleBean();
        PagoDetalle pd = new PagoDetalle();
        Long idPago = 2L;
        Pago pago = new Pago(idPago);
        pd.setIdPago(pago);
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        TypedQuery mockTq = Mockito.mock(TypedQuery.class);
        Integer first=0;
        Integer max=10;
        cut.em = mockEm;


        //error de argumentos
        assertThrows(IllegalArgumentException.class, () -> cut.countByIdPago(null));
        assertThrows(IllegalArgumentException.class, () -> cut.countByIdPago(-60L));
        // no existe pago
        Mockito.when(mockEm.find(Pago.class, 112233)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> cut.countByIdPago(112233L));
        //flujo normal
        Mockito.when(mockEm.find(Pago.class, idPago)).thenReturn(pago);

        Mockito.when(mockEm.createNamedQuery("PagoDetalle.countByIdPago",Long.class)).thenReturn(mockTq);
        Mockito.when(mockTq.setParameter("idPago", idPago)).thenReturn(mockTq);
        Mockito.when(mockTq.getSingleResult()).thenReturn((long) LIST_PagoDetalle_TEST.size());
        Long resultado = cut.countByIdPago(idPago);
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(LIST_PagoDetalle_TEST.size(), resultado);

        //excepciones
        TypedQuery mockTq2 = Mockito.spy(TypedQuery.class);
        Mockito.when(mockEm.createNamedQuery("PagoDetalle.countByIdPago",Long.class)).thenReturn(mockTq2);
        Mockito.when(mockTq2.setParameter("idPago", idPago)).thenReturn(mockTq2);
        Mockito.doThrow(PersistenceException.class).when(mockTq2).getSingleResult();
        Assertions.assertThrows(PersistenceException.class, () -> cut.countByIdPago(idPago));
        Mockito.doThrow(NonUniqueResultException.class).when(mockTq2).getSingleResult();
        Assertions.assertThrows(NonUniqueResultException.class, () -> cut.countByIdPago(idPago));


//        fail("fallo exitosamnete");

    }
}