package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.MockitoAnnotations;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Combo;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ComboDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ComboDetallePK;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * @author hdz
 */
public class ComboDetalleBeanTest {

    ComboDetalleBean cut;
    EntityManager mockEm;
    TypedQuery<ComboDetalle> mockTypedQuery;
    TypedQuery<ComboDetalle> anotherMockQuery;

    @BeforeEach
    void setUp() {
        cut = new ComboDetalleBean();
        mockEm = Mockito.mock(EntityManager.class);
        mockTypedQuery = Mockito.mock(TypedQuery.class);
        cut.em = mockEm;
        anotherMockQuery = Mockito.mock(TypedQuery.class);
    }

    @Test
    void testOrderParameterQuery() {
        String esperado = "cantidad";
        assertEquals(esperado, cut.orderParameterQuery());
    }

    @Test
    void testGetEntityManager() {
        assertEquals(mockEm, cut.getEntityManager());
    }

    @Test
    void testCreateComboDetalle_variosCasos() {
        ComboDetalle detalleValido = new ComboDetalle();
        detalleValido.setCantidad(5);
        detalleValido.setActivo(true);

        // Mocks para Combo y Producto existentes
        Combo comboMock = new Combo();
        Producto productoMock = new Producto();
        when(mockEm.find(Combo.class, 1L)).thenReturn(comboMock);
        when(mockEm.find(Producto.class, 2L)).thenReturn(productoMock);
        // Crear spy para ComboDetalleBean
        ComboDetalleBean spyBean = Mockito.spy(cut);
        spyBean.setEntityManager(mockEm);
        // CASO 1: Flujo exitoso
        assertDoesNotThrow(() -> {
            cut.create(detalleValido, 1L, 2L);
        });

        // CASO 2: detalle = null
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> {
            cut.create(null, 1L, 2L);
        });
        assertEquals("El detalle no puede ser nulo.", ex1.getMessage());

        // CASO 3: idCombo <= 0
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> {
            cut.create(new ComboDetalle(), 0L, 2L);
        });
        assertEquals("idCombo no puede ser nulo, menor o igual a cero.", ex2.getMessage());

        // CASO 4: idProducto <= 0
        IllegalArgumentException ex3 = assertThrows(IllegalArgumentException.class, () -> {
            cut.create(new ComboDetalle(), 1L, 0L);
        });
        assertEquals("idProducto no puede ser nulo, menor o igual a cero.", ex3.getMessage());

        // CASO 5: Combo no existe
        when(mockEm.find(Combo.class, 1L)).thenReturn(null); // Combo no encontrado
        EntityNotFoundException ex4 = assertThrows(EntityNotFoundException.class, () -> {
            cut.create(new ComboDetalle(), 1L, 2L);
        });

        // CASO 6: Producto no existe
        when(mockEm.find(Combo.class, 1L)).thenReturn(comboMock); // Ahora sí hay combo
        when(mockEm.find(Producto.class, 2L)).thenReturn(null);   // Pero no hay producto
        EntityNotFoundException ex5 = assertThrows(EntityNotFoundException.class, () -> {
            cut.create(new ComboDetalle(), 1L, 2L);
        });

        // CASO 7: Excepción al persistir (delegado a AbstractDataAccess)
        when(mockEm.find(Combo.class, 1L)).thenReturn(comboMock);
        when(mockEm.find(Producto.class, 2L)).thenReturn(productoMock);
        doThrow(new PersistenceException("Falló persistencia")).when(mockEm).persist(any(ComboDetalle.class));

        ComboDetalle conError = new ComboDetalle();
        PersistenceException ex6 = assertThrows(PersistenceException.class, () -> {
            cut.create(conError, 1L, 2L);
        });
        assertEquals("Error con la base de datos", ex6.getMessage());
        // Caso 8: lanzar IllegalStateException desde super.create
        doThrow(new IllegalStateException("Fallo de estado"))
                .when(spyBean).create(Mockito.argThat(arg -> arg != null && arg.getCantidad() == 3));
        ComboDetalle detalleEstado = new ComboDetalle();
        detalleEstado.setCantidad(3);
        detalleEstado.setActivo(true);

        IllegalStateException ex7 = assertThrows(IllegalStateException.class, () -> {
            spyBean.create(detalleEstado, 1L, 2L);
        });
        assertEquals("Error al persistir el registro", ex7.getMessage());
    }

    @Test
    void testFindByIdComboAndIdProducto_variosCasos() {
        Long idCombo = 1L, idProducto = 2L;
        ComboDetalle esperado = new ComboDetalle();

        // Caso: éxito
        Mockito.when(mockEm.createNamedQuery("ComboDetalle.findByIdComboAndIdProducto", ComboDetalle.class))
                .thenReturn(mockTypedQuery);
        Mockito.when(mockTypedQuery.setParameter(Mockito.eq("idCombo"), Mockito.any()))
                .thenReturn(mockTypedQuery);
        Mockito.when(mockTypedQuery.setParameter(Mockito.eq("idProducto"), Mockito.any()))
                .thenReturn(mockTypedQuery);
        Mockito.when(mockTypedQuery.getSingleResult()).thenReturn(esperado);

        ComboDetalle resultado = cut.findByIdComboAndIdProducto(idCombo, idProducto);
        assertNotNull(resultado);
        assertEquals(esperado, resultado);

        // Caso: NoResultException (Debería lanzar EntityNotFoundException)
        Mockito.when(mockTypedQuery.getSingleResult()).thenThrow(new NoResultException());

        // Verificar que se lanza la excepción EntityNotFoundException
        assertThrows(EntityNotFoundException.class, () ->
                cut.findByIdComboAndIdProducto(idCombo, idProducto)
        );

        // Caso: PersistenceException (Debería lanzar PersistenceException)
        Mockito.doThrow(PersistenceException.class).when(mockTypedQuery).getSingleResult();

        // Verificar que se lanza la excepción PersistenceException
        assertThrows(PersistenceException.class, () ->
                cut.findByIdComboAndIdProducto(idCombo, idProducto)
        );

        // Caso: idCombo nulo o inválido → IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () ->
                cut.findByIdComboAndIdProducto(null, idProducto)
        );

        assertThrows(IllegalArgumentException.class, () ->
                cut.findByIdComboAndIdProducto(0L, idProducto)
        );

        // Caso: idProducto nulo o inválido → IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () ->
                cut.findByIdComboAndIdProducto(idCombo, null)
        );

        assertThrows(IllegalArgumentException.class, () ->
                cut.findByIdComboAndIdProducto(idCombo, 0L)
        );
    }


    @Test
    void testDeleteByComboDetallePK_variosCasos() {
        Long idCombo = 1L, idProducto = 2L;
        Query mockQuery = Mockito.mock(Query.class);
        Combo mockCombo = Mockito.mock(Combo.class);
        Producto mockProducto = Mockito.mock(Producto.class);

        // Caso 1: éxito
        Mockito.when(mockEm.find(Combo.class, idCombo)).thenReturn(mockCombo);
        Mockito.when(mockEm.find(Producto.class, idProducto)).thenReturn(mockProducto);
        // Simular que la query se ejecuta sin problemas
        Mockito.when(mockEm.createNamedQuery("ComboDetalle.deleteByComboDetallePK")).thenReturn(mockQuery);
        Mockito.when(mockQuery.setParameter("idCombo", idCombo)).thenReturn(mockQuery);
        Mockito.when(mockQuery.setParameter("idProducto", idProducto)).thenReturn(mockQuery);
        Mockito.when(mockQuery.executeUpdate()).thenReturn(1); // Simular éxito
        assertDoesNotThrow(() -> cut.deleteByComboDetallePK(idCombo, idProducto));
        // Caso 2: falla por PersistenceException
        Mockito.when(mockQuery.executeUpdate()).thenThrow(new PersistenceException("Fallo de BD"));
        PersistenceException ex = assertThrows(PersistenceException.class, () -> {
            cut.deleteByComboDetallePK(idCombo, idProducto);
        });
        assertTrue(ex.getMessage().contains("Error al eliminar ComboDetalle"));
        // Caso 3: idCombo nulo o inválido → IllegalArgumentException
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, ()
                -> cut.deleteByComboDetallePK(null, idProducto)
        );
        assertEquals("idCombo no puede ser nulo, cero o negativo", ex1.getMessage());
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, ()
                -> cut.deleteByComboDetallePK(0L, idProducto)
        );
        assertEquals("idCombo no puede ser nulo, cero o negativo", ex2.getMessage());
        // Caso 4: idProducto nulo o inválido → IllegalArgumentException
        IllegalArgumentException ex3 = assertThrows(IllegalArgumentException.class, ()
                -> cut.deleteByComboDetallePK(idCombo, null)
        );
        assertEquals("idProducto no puede ser nulo, cero o negativo", ex3.getMessage());
        IllegalArgumentException ex4 = assertThrows(IllegalArgumentException.class, ()
                -> cut.deleteByComboDetallePK(idCombo, 0L)
        );
        assertEquals("idProducto no puede ser nulo, cero o negativo", ex4.getMessage());

        // Caso 7: Combo no encontrado
        Mockito.when(mockEm.find(Combo.class, idCombo)).thenReturn(null);
        EntityNotFoundException ex5 = assertThrows(EntityNotFoundException.class, ()
                -> cut.deleteByComboDetallePK(idCombo, idProducto)
        );

        // Caso 8: Producto no encontrado
        Mockito.when(mockEm.find(Combo.class, idCombo)).thenReturn(mockCombo); // ahora sí existe el combo
        Mockito.when(mockEm.find(Producto.class, idProducto)).thenReturn(null);
        EntityNotFoundException ex6 = assertThrows(EntityNotFoundException.class, ()
                -> cut.deleteByComboDetallePK(idCombo, idProducto)
        );

        // Caso 9: IllegalStateException
        Query mockQuery2 = Mockito.mock(Query.class);
        Mockito.when(mockEm.find(Combo.class, idCombo)).thenReturn(mockCombo);
        Mockito.when(mockEm.find(Producto.class, idProducto)).thenReturn(mockProducto);
        // Reconfigurar para usar mockQuery2
        Mockito.when(mockEm.createNamedQuery("ComboDetalle.deleteByComboDetallePK")).thenReturn(mockQuery2);
        Mockito.when(mockQuery2.setParameter("idCombo", idCombo)).thenReturn(mockQuery2);
        Mockito.when(mockQuery2.setParameter("idProducto", idProducto)).thenReturn(mockQuery2);
        Mockito.when(mockQuery2.executeUpdate()).thenThrow(new IllegalStateException("Illegal state"));

        IllegalStateException ex7 = assertThrows(IllegalStateException.class, ()
                -> cut.deleteByComboDetallePK(idCombo, idProducto)
        );

    }

    @Test
    void testFindRangeByCombo_variosCasos() {
        Long idCombo = 1L;
        int first = 0;
        int max = 10;

        List<ComboDetalle> mockLista = Arrays.asList(new ComboDetalle(), new ComboDetalle());

        // Simular éxito
        Mockito.when(mockEm.createNamedQuery("ComboDetalle.findByIdCombo", ComboDetalle.class)).thenReturn(mockTypedQuery);
        Mockito.when(mockTypedQuery.setParameter("idCombo", idCombo)).thenReturn(mockTypedQuery);
        Mockito.when(mockTypedQuery.setFirstResult(first)).thenReturn(mockTypedQuery);
        Mockito.when(mockTypedQuery.setMaxResults(max)).thenReturn(mockTypedQuery);
        Mockito.when(mockTypedQuery.getResultList()).thenReturn(mockLista);
        assertThrows(IllegalArgumentException.class, () -> cut.findRangeByCombo(null, first, max));

        // Caso válido
        List<ComboDetalle> result = cut.findRangeByCombo(idCombo, first, max);
        assertEquals(mockLista, result);

        // Parámetros inválidos
        Mockito.when(mockTypedQuery.getResultList())
                .thenThrow(new PersistenceException("Error al obtener datos"));
        assertThrows(PersistenceException.class, () -> {
            cut.findRangeByCombo(1L, 0, 10);
        });
    }

    @Test
    void testUpdateByComboDetallePK_casos() {
        Long idCombo = 1L;
        Long idProducto = 2L;
        ComboDetalle existente = new ComboDetalle();
        existente.setCantidad(1);
        existente.setActivo(true);

        ComboDetalle actualizado = new ComboDetalle();
        actualizado.setCantidad(5);
        actualizado.setActivo(false);
        // Caso: existe el ComboDetalle
        Mockito.when(mockEm.createNamedQuery("ComboDetalle.findByIdComboAndIdProducto", ComboDetalle.class))
                .thenReturn(mockTypedQuery);
        Mockito.when(mockTypedQuery.setParameter("idCombo", idCombo)).thenReturn(mockTypedQuery);
        Mockito.when(mockTypedQuery.setParameter("idProducto", idProducto)).thenReturn(mockTypedQuery);
        Mockito.when(mockTypedQuery.getSingleResult()).thenReturn(existente);
        Mockito.when(mockEm.merge(Mockito.any(ComboDetalle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ComboDetalle result = cut.updateByComboDetallePK(actualizado, idCombo, idProducto);
        assertEquals(5, result.getCantidad());
        assertFalse(result.getActivo());
        // Caso: detalleActualizado es nulo
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> {
            cut.updateByComboDetallePK(null, idCombo, idProducto);
        });
        assertEquals("El objeto ComboDetalle no puede ser nulo", ex1.getMessage());
        // Caso: no existe el ComboDetalle
        Mockito.when(mockTypedQuery.getSingleResult()).thenThrow(new NoResultException());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            cut.updateByComboDetallePK(actualizado, idCombo, idProducto);
        });
        // Caso: error en el merge
        // Volvemos a simular que sí existe el registro
        Mockito.when(mockEm.createNamedQuery("ComboDetalle.findByIdComboAndIdProducto", ComboDetalle.class))
                .thenReturn(anotherMockQuery);
        Mockito.when(anotherMockQuery.setParameter("idCombo", idCombo)).thenReturn(anotherMockQuery);
        Mockito.when(anotherMockQuery.setParameter("idProducto", idProducto)).thenReturn(anotherMockQuery);
        Mockito.when(anotherMockQuery.getSingleResult()).thenReturn(existente);
        Mockito.when(mockEm.merge(Mockito.any(ComboDetalle.class))).thenThrow(new PersistenceException("Error en merge"));
        // Ejecutar y verificar que se lanza el nuevo PersistenceException
        PersistenceException ex2 = assertThrows(PersistenceException.class, () -> {
            cut.updateByComboDetallePK(actualizado, idCombo, idProducto);
        });
        assertEquals("Error al actualizar ComboDetalle", ex2.getMessage());
    }
}
