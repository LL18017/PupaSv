/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.*;
import org.junit.jupiter.api.function.Executable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.validation.ConstraintViolationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import jakarta.persistence.criteria.Predicate;

import java.util.HashSet;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Combo;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;

/**
 * @author hdz
 */
public class ComboBeanTest {

    @Mock
    private EntityManager mockEm;

    @InjectMocks
    private ComboBean cut;
    List<Combo> LIST_COMBO_TEST = Arrays.asList(
            new Combo(1L),
            new Combo(2L),
            new Combo(3L)
    );

    @BeforeEach
    public void setUp() {
        cut = new ComboBean();
        mockEm = Mockito.mock(EntityManager.class);
        cut.setEntityManager(mockEm);
    }

    @Test
    public void testFindAll() {
        System.out.println("Combo test findAll");
        ComboBean cut = new ComboBean();

        // Caso 1: EntityManager nulo
        Assertions.assertThrows(IllegalStateException.class, () -> cut.findAll());

        EntityManager mockEm = Mockito.mock(EntityManager.class);
        CriteriaBuilder mockCb = Mockito.mock(CriteriaBuilder.class);
        CriteriaQuery<Combo> mockCq = Mockito.mock(CriteriaQuery.class);
        Root<Combo> mockRoot = Mockito.mock(Root.class);
        TypedQuery<Combo> mockTq = Mockito.mock(TypedQuery.class);
        List<Combo> combosMock = Arrays.asList(
                new Combo(1L), new Combo(2L)
        );
        // Caso 2: Flujo normal
        cut.em = mockEm;
        Mockito.when(mockEm.getCriteriaBuilder()).thenReturn(mockCb);
        Mockito.when(mockCb.createQuery(Combo.class)).thenReturn(mockCq);
        Mockito.when(mockCq.from(Combo.class)).thenReturn(mockRoot);
        Mockito.when(mockCq.select(mockRoot)).thenReturn(mockCq);
        Mockito.when(mockCq.orderBy(Mockito.anyList())).thenReturn(mockCq);
        Mockito.when(mockEm.createQuery(mockCq)).thenReturn(mockTq);
        Mockito.when(mockTq.getResultList()).thenReturn(combosMock);
        List<Combo> result = cut.findAll();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        // Caso 3: Error de base de datos
        ComboBean cut2 = new ComboBean();
        EntityManager mockEm2 = Mockito.mock(EntityManager.class);
        cut2.em = mockEm2;
        Mockito.when(mockEm2.getCriteriaBuilder()).thenThrow(new PersistenceException("Error en la base de datos"));
        Assertions.assertThrows(PersistenceException.class, () -> cut2.findAll());

//        Assertions.fail("fallo exitosamente");
    }

    @Test
    public void testFindById() {
        System.out.println("Combo test findById");
        Long idBuscado = 1L;
        Combo buscado = new Combo(1L);
        ComboBean cut = new ComboBean();
        EntityManager mockEm = Mockito.mock(EntityManager.class
        );
        // Caso 1: ID nulo
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.findById(null));
        // Caso 2: EntityManager nulo
        Assertions.assertThrows(IllegalStateException.class, () -> cut.findById(idBuscado));
        // Caso 3: Flujo normal
        cut.em = mockEm;
        Mockito.when(mockEm.find(Combo.class, idBuscado)).thenReturn(buscado);
        Combo respuesta = cut.findById(idBuscado);
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(buscado, respuesta);

        // Caso 4: ID no encontrado
        Mockito.when(mockEm.find(Combo.class, 12345L)).thenReturn(null);
        Assertions.assertThrows(EntityNotFoundException.class, () -> cut.findById(12345L));

        // Caso 5: Error en base de datos
        EntityManager mockEm2 = Mockito.spy(EntityManager.class
        );
        cut.em = mockEm2;
        Mockito.doThrow(PersistenceException.class).when(mockEm2).find(Combo.class, idBuscado);
        Assertions.assertThrows(PersistenceException.class, () -> cut.findById(idBuscado));
//        Assertions.fail("fallo exitosamente");
    }

    @Test
    public void testFindRange() {
        System.out.println("Combo test findRange");

        // Arrange
        ComboBean cut = new ComboBean();
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        CriteriaBuilder mockCb = Mockito.mock(CriteriaBuilder.class);
        CriteriaQuery<Combo> mockCq = Mockito.mock(CriteriaQuery.class);
        Root<Combo> mockRoot = Mockito.mock(Root.class);
        TypedQuery<Combo> mockTypedQuery = Mockito.mock(TypedQuery.class);
        List<Combo> listaEsperada = List.of(new Combo(), new Combo());

        // Caso 1: Parámetros nulos
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.findRange(null, null));

        // Caso 2: Parámetros menores o iguales a cero
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.findRange(-1, 0));

        // Caso 3: EntityManager nulo
        Assertions.assertThrows(IllegalStateException.class, () -> cut.findRange(0, 10));

        // Caso 4: Flujo normal
        cut.em = mockEm;
        Mockito.when(mockEm.getCriteriaBuilder()).thenReturn(mockCb);
        Mockito.when(mockCb.createQuery(Combo.class)).thenReturn(mockCq);
        Mockito.when(mockCq.from(Combo.class)).thenReturn(mockRoot);
        Mockito.when(mockCq.select(mockRoot)).thenReturn(mockCq);
        Mockito.when(mockEm.createQuery(mockCq)).thenReturn(mockTypedQuery);
        Mockito.when(mockTypedQuery.setFirstResult(0)).thenReturn(mockTypedQuery);
        Mockito.when(mockTypedQuery.setMaxResults(10)).thenReturn(mockTypedQuery);
        Mockito.when(mockTypedQuery.getResultList()).thenReturn(listaEsperada);

        List<Combo> resultado = cut.findRange(0, 10);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(2, resultado.size());

        // Caso 5: Error de base de datos
        TypedQuery<Combo> mockQueryConError = Mockito.mock(TypedQuery.class);
        Mockito.when(mockEm.createQuery(Mockito.any(CriteriaQuery.class))).thenReturn(mockQueryConError);
        Mockito.when(mockQueryConError.setFirstResult(Mockito.anyInt())).thenReturn(mockQueryConError);
        Mockito.when(mockQueryConError.setMaxResults(Mockito.anyInt())).thenReturn(mockQueryConError);
        Mockito.when(mockQueryConError.getResultList()).thenThrow(PersistenceException.class);

        Assertions.assertThrows(PersistenceException.class, () -> cut.findRange(0, 10));
//    Assertions.fail("fallo con exito");
    }

    @Test
    public void testCreate() {
        System.out.println("ComboBean test create");
        ComboBean cut = new ComboBean();
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        Combo comboCreado = new Combo(4L);

        // Caso 1: EntityManager nulo
        Assertions.assertThrows(IllegalStateException.class, () -> cut.create(comboCreado));

        // Caso 2: Registro nulo
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.create(null));

        // Caso 3: Flujo normal
        cut.em = mockEm;
        Mockito.doNothing().when(mockEm).persist(comboCreado);
        Assertions.assertDoesNotThrow(() -> cut.create(comboCreado));

        // Caso 4: Entidad ya persistida
        EntityManager mockEm2 = Mockito.spy(EntityManager.class);
        cut.em = mockEm2;
        Mockito.doThrow(EntityExistsException.class).when(mockEm2).persist(comboCreado);
        Assertions.assertThrows(EntityExistsException.class, () -> cut.create(comboCreado));

        // Caso 5: Violación de reglas de integridad (ej. ConstraintViolationException)
        Mockito.doThrow(ConstraintViolationException.class).when(mockEm2).persist(comboCreado);
        Assertions.assertThrows(ConstraintViolationException.class, () -> cut.create(comboCreado));
    }

    @Test
    public void testCount() {
        System.out.println("ComboBean test count");

        ComboBean cut = new ComboBean(); // sin spy
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        CriteriaBuilder mockCb = Mockito.mock(CriteriaBuilder.class);
        CriteriaQuery<Long> mockCq = Mockito.mock(CriteriaQuery.class);
        Root<Combo> mockR = Mockito.mock(Root.class);
        TypedQuery<Long> mockTq = Mockito.mock(TypedQuery.class);

        // Asigna el em directamente
        cut.em = mockEm;

        Mockito.when(mockEm.getCriteriaBuilder()).thenReturn(mockCb);
        Mockito.when(mockCb.createQuery(Long.class)).thenReturn(mockCq);
        Mockito.when(mockCq.from(Combo.class)).thenReturn(mockR);
        Mockito.when(mockCb.count(mockR)).thenReturn(Mockito.mock(Expression.class)); // evita null
        Mockito.when(mockCq.select(Mockito.any())).thenReturn(mockCq);
        Mockito.when(mockEm.createQuery(mockCq)).thenReturn(mockTq);
        Mockito.when(mockTq.getSingleResult()).thenReturn((long) LIST_COMBO_TEST.size());

        Long resultado = cut.count();
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(LIST_COMBO_TEST.size(), resultado);

        // EntityManager nulo
        ComboBean cut2 = new ComboBean(); // em sigue siendo null
        Assertions.assertThrows(IllegalStateException.class, () -> cut2.count());

        // excepción de base de datos
        Mockito.when(mockTq.getSingleResult()).thenThrow(new PersistenceException());
        Assertions.assertThrows(PersistenceException.class, () -> cut.count());

//        // resultado no único
        Assertions.assertThrows(PersistenceException.class, () -> cut.count());
    }

    @Test
    public void updateTest() {
        System.out.println("ComboBean test update");

        Combo comboModificar = LIST_COMBO_TEST.get(0);
        comboModificar.setNombre("Nuevo Combo");
        Long idModificado = 1L;

        // entity nulo
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.update(null, (Object) 1L));

        // id nulo
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.update(comboModificar, null));

        // flujo normal
        when(mockEm.find(Combo.class, idModificado)).thenReturn(comboModificar);
        when(mockEm.merge(comboModificar)).thenReturn(comboModificar);
        cut.update(comboModificar, (Object) idModificado);
        Assertions.assertEquals("Nuevo Combo", comboModificar.getNombre());

        // id Inexistente
        when(mockEm.find(Combo.class, 12345L)).thenReturn(null);
        Assertions.assertThrows(EntityNotFoundException.class, () -> cut.update(comboModificar, 12345L));

        // EntityManager nulo (para lanzar RuntimeException)
        ComboBean cut2 = Mockito.spy(new ComboBean());
        Mockito.doReturn(null).when(cut2).getEntityManager();

        Assertions.assertThrows(IllegalStateException.class, ()
                -> cut2.update(comboModificar, (Object) idModificado));

        // ConstraintViolationException
        EntityManager mockEm2 = Mockito.mock(EntityManager.class);
        cut.setEntityManager(mockEm2);
        doThrow(ConstraintViolationException.class).when(mockEm2).find(Combo.class, idModificado);
        Assertions.assertThrows(ConstraintViolationException.class, () -> cut.update(comboModificar, (Object) idModificado));

        // PersistenceException
        doThrow(PersistenceException.class).when(mockEm2).find(Combo.class, idModificado);
        Assertions.assertThrows(PersistenceException.class, () -> cut.update(comboModificar, (Object) idModificado));
    }

    @Test
    public void testDelete() {
        System.out.println("ComboBean test delete");
        ComboBean cut = new ComboBean();
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        CriteriaBuilder mockCb = Mockito.mock(CriteriaBuilder.class);
        CriteriaDelete<Combo> mockCd = Mockito.mock(CriteriaDelete.class);
        Root<Combo> mockR = Mockito.mock(Root.class);
        TypedQuery<Combo> mockTq = Mockito.mock(TypedQuery.class);
        Long idEliminar = 1001L;

        // Simulación de una entidad
        Combo combo = new Combo();
        combo.setIdCombo(idEliminar);
        List<Combo> listaCombo = Arrays.asList(combo);

        // 1. Test: EntityManager no inicializado
        Assertions.assertThrows(IllegalStateException.class, () -> cut.delete(idEliminar));

        // 2. Test: ID nulo
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.delete(null));

        // 3. Flujo normal sin errores
        cut.setEntityManager(mockEm);
        Mockito.when(mockEm.find(Combo.class, idEliminar)).thenReturn(listaCombo.get(0));
        Mockito.when(mockEm.getCriteriaBuilder()).thenReturn(mockCb);
        Mockito.when(mockCb.createCriteriaDelete(Combo.class)).thenReturn(mockCd);
        Mockito.when(mockCd.from(Combo.class)).thenReturn(mockR);
        Predicate mockPredicate = Mockito.mock(Predicate.class);
        Mockito.when(mockCb.equal(Mockito.any(), Mockito.any())).thenReturn(mockPredicate);
        Mockito.when(mockCd.where(mockPredicate)).thenReturn(mockCd);
        Mockito.when(mockEm.createQuery(mockCd)).thenReturn(mockTq);
        Mockito.when(mockTq.executeUpdate()).thenReturn(1);

        Assertions.assertDoesNotThrow(() -> cut.delete(idEliminar));

        // 4. Test: entidad no encontrada
        Mockito.when(mockEm.find(Combo.class, idEliminar)).thenReturn(null);
        Assertions.assertThrows(EntityNotFoundException.class, () -> cut.delete(idEliminar));

        EntityManager mockEm2 = Mockito.mock(EntityManager.class);

        // 5. PersistenceException
        Mockito.doThrow(PersistenceException.class).when(mockEm2).remove(Mockito.any());
        Assertions.assertThrows(PersistenceException.class, () -> cut.delete(idEliminar));

    }

    @Test
    public void testFindAllwithPrice() {
        System.out.println("Combo test findAll");
        ComboBean cut = new ComboBean();
        Integer first=0;
        Integer max=10;
        // Caso 1: EntityManager nulo
        Assertions.assertThrows(IllegalStateException.class, () -> cut.findRangeWithPrice(first,max));

        EntityManager mockEm = Mockito.mock(EntityManager.class);
        TypedQuery mockTq = Mockito.mock(TypedQuery.class);
        Mockito.when(mockEm.createNamedQuery("Combo.findAll", Object[].class)).thenReturn(mockTq);
        Mockito.when(mockTq.setFirstResult(first)).thenReturn(mockTq);
        Mockito.when(mockTq.setMaxResults(max)).thenReturn(mockTq);
        Mockito.when(mockTq.getResultList()).thenReturn(LIST_COMBO_TEST);

        // Caso 2: Flujo normal
        cut.em=mockEm;
        List<Object[]> result = cut.findRangeWithPrice(first,max);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());

        // Caso 3: Error de base de datos
        ComboBean cut2 = new ComboBean();
        EntityManager mockEm2 = Mockito.mock(EntityManager.class);
        cut2.em = mockEm2;

        TypedQuery mockTq2 = Mockito.mock(TypedQuery.class);
        Mockito.when(mockEm2.createNamedQuery("Combo.findAll", Object[].class)).thenReturn(mockTq2);
        Mockito.when(mockTq2.setFirstResult(first)).thenReturn(mockTq2);
        Mockito.when(mockTq2.setMaxResults(max)).thenReturn(mockTq2);
        Mockito.when(mockTq2.getResultList()).thenThrow(PersistenceException.class);
        Assertions.assertThrows(PersistenceException.class, () -> cut2.findRangeWithPrice(first,max));

//        Assertions.fail("fallo exitosamente");
    }


    @Test
    void testFindByNombre() {
        System.out.println("Combo test findByNombre");
        Integer first=0;
        Integer max=10;
        // Caso 1: nombre nulo o vacío
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.findByNombre(null,first,max));
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.findByNombre("",first,max));

        String nombreValido = "Combo válido";

        // Caso 2: resultado único correcto
        List<Combo> productosMock = new ArrayList<>();
        productosMock.add(new Combo(1L));
        TypedQuery queryOk = Mockito.mock(TypedQuery.class);
        Mockito.when(mockEm.createNamedQuery("Combo.findByNombre", Object[].class)).thenReturn(queryOk);
        Mockito.when(queryOk.setParameter("nombre", "%"+nombreValido+"%")).thenReturn(queryOk);
        Mockito.when(queryOk.setFirstResult(first)).thenReturn(queryOk);
        Mockito.when(queryOk.setMaxResults(max)).thenReturn(queryOk);
        Mockito.when(queryOk.getResultList()).thenReturn(productosMock);
        List result = cut.findByNombre(nombreValido,first,max);
        Assertions.assertNotNull(result);

        // Caso 3: no se encuentra el productos
        TypedQuery queryNoResult = Mockito.mock(TypedQuery.class);
        Mockito.when(mockEm.createNamedQuery("Combo.findByNombre", Object[].class)).thenReturn(queryNoResult);
        Mockito.when(queryNoResult.setParameter("nombre", "%"+nombreValido+"%")).thenReturn(queryNoResult);
        Mockito.when(queryNoResult.setFirstResult(first)).thenReturn(queryNoResult);
        Mockito.when(queryNoResult.setMaxResults(max)).thenReturn(queryNoResult);
        Mockito.when(queryNoResult.getResultList()).thenThrow(new NoResultException());

        Assertions.assertThrows(NoResultException.class,()-> cut.findByNombre(nombreValido,first,max));


        // Caso 5: error de persistencia
        TypedQuery<Combo> queryError = Mockito.mock(TypedQuery.class);
        Mockito.when(mockEm.createNamedQuery("Combo.findByNombre", Combo.class)).thenReturn(queryError);
        Mockito.when(queryError.setParameter("nombre", "%"+nombreValido+"%")).thenReturn(queryError);
        Mockito.when(queryError.setFirstResult(first)).thenReturn(queryError);
        Mockito.when(queryError.setMaxResults(max)).thenReturn(queryError);
        Mockito.when(queryError.getResultList()).thenThrow(new PersistenceException("Error BD"));
        Assertions.assertThrows(PersistenceException.class, () -> cut.findByNombre(nombreValido,first,max));
    }

    @Test
    void countProductoByName() {
        System.out.println("Combo test countCountByName");
        Long esperado = 1L;
        String nombre = "test";

        //flujo normal
        cut.em = mockEm;
        TypedQuery mockTq= Mockito.mock(TypedQuery.class);
        Mockito.when(mockEm.createNamedQuery("Combo.countByNombre", Long.class)).thenReturn(mockTq);
        Mockito.when(mockTq.setParameter("nombre", "%"+nombre+"%")).thenReturn(mockTq);
        Mockito.when(mockTq.getSingleResult()).thenReturn(esperado);

        Long resultado = cut.countProductoByName(nombre);
        Mockito.when(mockTq.setParameter("nombre", nombre)).thenReturn(mockTq);
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(esperado, resultado);

        // Forzar fallo al acceder al EntityManager
        TypedQuery mockTq2 = Mockito.mock(TypedQuery.class);
        Mockito.when(mockEm.createNamedQuery("Combo.countByNombre", Long.class)).thenReturn(mockTq2);
        Mockito.when(mockTq2.setParameter("nombre","%"+ nombre+"%")).thenReturn(mockTq2);
        //PersistenceException
        Mockito.doThrow(PersistenceException.class).when(mockTq2).getSingleResult();

        Assertions.assertThrows(PersistenceException.class, () -> cut.countProductoByName(nombre));
        //NonUniqueResultException
        Mockito.doThrow(NonUniqueResultException.class).when(mockTq2).getSingleResult();
        Assertions.assertThrows(NonUniqueResultException.class, () -> cut.countProductoByName(nombre));

//        fail("fallo exitosamente");
    }


}
