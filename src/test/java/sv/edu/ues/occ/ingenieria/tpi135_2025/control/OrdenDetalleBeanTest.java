package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class OrdenDetalleBeanTest {


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConstructor() {
        System.out.println("OrdenDetalle Test de Constructor");
        assertNotNull(new OrdenDetalleBean());
        //Assertions.fail("Esta prueba no pasa quemado ");
    }

    @Test
    public void testOrderParameterQuery() {
        OrdenDetalleBean ordenDetalleBean = new OrdenDetalleBean();
        System.out.println("OrdenDetalle Test de OrderParameterQuery");
        assertEquals("idOrdenDetalle", ordenDetalleBean.orderParameterQuery());
        //Assertions.fail("Esta prueba no pasa quemado");
    }

    @Test
    public void testGetEntityManager() {
        OrdenDetalleBean ordenDetalleBean = new OrdenDetalleBean();
        EntityManager em = Mockito.mock(EntityManager.class);
        ordenDetalleBean.em = em;
        System.out.println("OrdenDetalle Test de getEntityManager");
        assertNotNull(ordenDetalleBean.getEntityManager());
        assertEquals(em, ordenDetalleBean.getEntityManager());
        //Assertions.fail("Esta prueba no pasa quemado ");
    }

    @Test
    public void testSetEntityManager() {
        OrdenDetalleBean ordenDetalleBean = new OrdenDetalleBean();
        EntityManager em = Mockito.mock(EntityManager.class);
        System.out.println("OrdenDetalle Test de setEntityManager");
        ordenDetalleBean.setEntityManager(em);
        assertEquals(em, ordenDetalleBean.getEntityManager());
        //Assertions.fail("Esta prueba no pasa quemado");
    }

    @Test
    public void testFindByIdOrdenAndIdPrecioProducto() {
        OrdenDetalleBean ordenDetalleBean = new OrdenDetalleBean();
        EntityManager em = Mockito.mock(EntityManager.class);
        TypedQuery queryMock = Mockito.mock(TypedQuery.class);
        ordenDetalleBean.em = em;
        System.out.println("OrdenDetalle Test de findByIdOrdenAndIdPrecioProducto");
        OrdenDetalle detalleEsperado = new OrdenDetalle();
        when(em.createNamedQuery("OrdenDetalle.findByPrecioProductoAndIdOrden", OrdenDetalle.class)).thenReturn(queryMock);
        when(queryMock.setParameter(eq("idOrden"), anyLong())).thenReturn(queryMock);
        when(queryMock.setParameter(eq("idProductoPrecio"), anyLong())).thenReturn(queryMock);
        when(queryMock.getSingleResult()).thenReturn(detalleEsperado);

        OrdenDetalle resultado = ordenDetalleBean.findByIdOrdenAndIdPrecioProducto(1L, 1L);
        assertNotNull(resultado);
        assertEquals(detalleEsperado, resultado);

        //errores
        TypedQuery tp = Mockito.spy(TypedQuery.class);
        when(em.createNamedQuery("OrdenDetalle.findByPrecioProductoAndIdOrden", OrdenDetalle.class)).thenReturn(tp);
        when(tp.setParameter(eq("idOrden"), anyLong())).thenReturn(tp);
        when(tp.setParameter(eq("idProductoPrecio"), anyLong())).thenReturn(tp);
        doThrow(NoResultException.class).when(tp).getSingleResult();
        Assertions.assertThrows(NoResultException.class, () -> ordenDetalleBean.findByIdOrdenAndIdPrecioProducto(1L, 1L));
        doThrow(PersistenceException.class).when(tp).getSingleResult();
        Assertions.assertThrows(PersistenceException.class, () -> ordenDetalleBean.findByIdOrdenAndIdPrecioProducto(1L, 1L));
        //Assertions.fail("Esta prueba no pasa quemado");


    }

    @Test
    public void findByIdOrdenAndIdPrecioProducto_noResult_returnsNull() {
        System.out.println("OrdenDetalle Test de findByIdOrdenAndIdPrecioProducto_noResult_returnsNull");
        Long idOrden = 1L;
        Long idProductoPrecio = 2L;

        OrdenDetalleBean ordenDetalleBean = new OrdenDetalleBean();
        EntityManager em = Mockito.mock(EntityManager.class);
        TypedQuery queryMock = Mockito.spy(TypedQuery.class);
        ordenDetalleBean.em = em;
        when(em.createNamedQuery(anyString(), org.mockito.ArgumentMatchers.<Class<OrdenDetalle>>any())).thenReturn(queryMock);
        when(queryMock.setParameter("idOrden", idOrden)).thenReturn(queryMock);
        when(queryMock.setParameter("idProductoPrecio", idProductoPrecio)).thenReturn(queryMock);
        doThrow(NoResultException.class).when(queryMock).getSingleResult();

        Assertions.assertThrows(NoResultException.class, () -> ordenDetalleBean.findByIdOrdenAndIdPrecioProducto(idOrden, idProductoPrecio));
        doThrow(PersistenceException.class).when(queryMock).getSingleResult();
        Assertions.assertThrows(PersistenceException.class, () -> ordenDetalleBean.findByIdOrdenAndIdPrecioProducto(idOrden, idProductoPrecio));


        //Assertions.fail("Esta prueba no pasa quemado");
    }


    @Test
    public void testFindRangeByIdOrden() {
        System.out.println("OrdenDetalle Test de findRangeByIdOrden");
        List<OrdenDetalle> mockList = List.of(new OrdenDetalle(), new OrdenDetalle());
        OrdenDetalleBean ordenDetalleBean = new OrdenDetalleBean();
        EntityManager em = Mockito.mock(EntityManager.class);
        TypedQuery queryMock = Mockito.mock(TypedQuery.class);
        ordenDetalleBean.em = em;
        when(em.createNamedQuery("Orden.findByIdOrden", OrdenDetalle.class)).thenReturn(queryMock);
        when(queryMock.setParameter("idOrden", 1L)).thenReturn(queryMock);
        when(queryMock.setFirstResult(0)).thenReturn(queryMock);
        when(queryMock.setMaxResults(2)).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(mockList);

        List<OrdenDetalle> result = ordenDetalleBean.findRangeByIdOrden(1L, 0, 2);
        assertNotNull(result);
        assertEquals(2, result.size());

        TypedQuery tp = Mockito.mock(TypedQuery.class);
        when(em.createNamedQuery("Orden.findByIdOrden", OrdenDetalle.class)).thenReturn(tp);
        when(tp.setParameter("idOrden", 1L)).thenReturn(tp);
        when(tp.setFirstResult(0)).thenReturn(tp);
        when(tp.setMaxResults(2)).thenReturn(tp);
        Mockito.doThrow(NoResultException.class).when(tp).getResultList();
        Assertions.assertThrows(NoResultException.class, () -> ordenDetalleBean.findRangeByIdOrden(1L, 0, 2));
        Mockito.doThrow(PersistenceException.class).when(tp).getResultList();
        Assertions.assertThrows(PersistenceException.class, () -> ordenDetalleBean.findRangeByIdOrden(1L, 0, 2));
        // fail("Esta prueba no pasa quemado");
    }

    @Test
    public void findRangeByIdOrden_exceptionThrown_returnsNull() {
        System.out.println("OrdenDetalle Test de findRangeByIdOrden_exceptionThrown_returnsNull");
        Long idOrden = 1L;
        int first = 0;
        int max = 10;
        OrdenDetalleBean ordenDetalleBean = new OrdenDetalleBean();
        EntityManager em = Mockito.mock(EntityManager.class);
        TypedQuery queryMock = Mockito.mock(TypedQuery.class);
        ordenDetalleBean.em = em;
        Mockito.when(em.createNamedQuery("Orden.findByIdOrden", OrdenDetalle.class)).thenReturn(queryMock);
        Mockito.when(queryMock.setParameter("idOrden", idOrden)).thenReturn(queryMock);
        Mockito.when(queryMock.setFirstResult(first)).thenReturn(queryMock);
        Mockito.when(queryMock.setMaxResults(max)).thenReturn(queryMock);
        Mockito.doThrow(new NoResultException()).when(queryMock).getResultList();
        Assertions.assertThrows(NoResultException.class, () -> ordenDetalleBean.findRangeByIdOrden(idOrden, first, max));
        //fail("Esta prueba no pasa quemado");;
    }


    @Test
    public void testCountByIdOrden() {
        OrdenDetalleBean ordenDetalleBean = new OrdenDetalleBean();
        EntityManager em = Mockito.mock(EntityManager.class);
        TypedQuery queryMock = Mockito.mock(TypedQuery.class);
        ordenDetalleBean.em = em;
        System.out.println("OrdenDetalle Test de countByIdOrden");

        when(em.createNamedQuery("OrdenDetalle.countByIdOrden", Long.class)).thenReturn(queryMock);
        when(queryMock.setParameter("idOrden", 1L)).thenReturn(queryMock);
        when(queryMock.getSingleResult()).thenReturn(5L);

        Long result = ordenDetalleBean.countByIdOrden(1L);
        assertEquals(Long.valueOf(5L), result);
        TypedQuery tp = Mockito.mock(TypedQuery.class);
        when(em.createNamedQuery("OrdenDetalle.countByIdOrden", Long.class)).thenReturn(tp);
        when(tp.setParameter("idOrden", 1L)).thenReturn(tp);

        doThrow(NoResultException.class).when(tp).getSingleResult();
        Assertions.assertThrows(NoResultException.class, () -> ordenDetalleBean.countByIdOrden(1L));


        doThrow(NonUniqueResultException.class).when(tp).getSingleResult();
        Assertions.assertThrows(NonUniqueResultException.class, () -> ordenDetalleBean.countByIdOrden(1L));

        Mockito.doThrow(PersistenceException.class).when(tp).getSingleResult();
        Assertions.assertThrows(PersistenceException.class, () -> ordenDetalleBean.countByIdOrden(1L));


        //Assertions.fail("Esta prueba no pasa quemado");
    }

    @Test
    public void testGenerarOrdenDetalleProducto() {
        System.out.println("OrdenDetalle test de GenerarOrdenDetalleDesdeCombo");
        Long idProducto = 1L;
        Long idOrden = 1L;
        Integer cantidadCombo = 1;

        ProductoPrecio productoPrecio1 = new ProductoPrecio();
        productoPrecio1.setPrecioSugerido(BigDecimal.valueOf(1.00));
        Object[] detalle = new Object[]{productoPrecio1, 5};

        OrdenDetalleBean ordenDetalleBean = new OrdenDetalleBean();
        EntityManager em = Mockito.mock(EntityManager.class);
        TypedQuery queryMock = Mockito.mock(TypedQuery.class);

        ordenDetalleBean.em = em;

        // fallo de argumentos
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ordenDetalleBean.generarOrdenDetalleProducto(null, idProducto, cantidadCombo);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ordenDetalleBean.generarOrdenDetalleProducto(idOrden, null, cantidadCombo);
        });
        Assertions.assertThrows(NullPointerException.class, () -> {
            ordenDetalleBean.generarOrdenDetalleProducto(idOrden, idProducto, null);
        });

        //flujo normal

        when(em.createNamedQuery("ComboDetalle.findProductoPrecioAndCantidadByIdProducto", Object[].class)).thenReturn(queryMock);
        when(queryMock.setParameter("idProducto", idProducto)).thenReturn(queryMock);
        when(queryMock.getSingleResult()).thenReturn(detalle);

        OrdenDetalle uno = new OrdenDetalle();
        uno.setOrden(new Orden(idOrden));
        ProductoPrecio precio = (ProductoPrecio) detalle[0];
        uno.setPrecio(precio.getPrecioSugerido());
        uno.setCantidad((Integer) detalle[1] * cantidadCombo);

        Mockito.doNothing().when(em).persist(uno);
        Assertions.assertDoesNotThrow(() -> ordenDetalleBean.generarOrdenDetalleProducto(idOrden, idProducto, cantidadCombo));

//        //precio invalidos desde db

        detalle[0] = null;
        when(queryMock.getSingleResult()).thenReturn(detalle);
        Assertions.assertThrows(NullPointerException.class, () -> ordenDetalleBean.generarOrdenDetalleProducto(idOrden, idProducto, 2));
//
        //otros errores
        detalle[0] = productoPrecio1;
        doThrow(PersistenceException.class).when(em).persist(uno);
        Assertions.assertThrows(PersistenceException.class, () -> ordenDetalleBean.generarOrdenDetalleProducto(idOrden, idProducto, 2));
        doThrow(EntityNotFoundException.class).when(em).persist(uno);
        Assertions.assertThrows(EntityNotFoundException.class, () -> ordenDetalleBean.generarOrdenDetalleProducto(idOrden, idProducto, 2));

        //detalle no exxiste

        when(em.createNamedQuery("ComboDetalle.findProductoPrecioAndCantidadByIdProducto", Object[].class)).thenReturn(queryMock);
        when(queryMock.setParameter("idProducto", 112233L)).thenReturn(queryMock);
        when(queryMock.getSingleResult()).thenReturn(null);
        Assertions.assertThrows(NoResultException.class, () -> ordenDetalleBean.generarOrdenDetalleProducto(idOrden, 112233L, 2));


    }


    @Test
    public void testGenerarOrdenDetalleDesdeCombo() {
        System.out.println("OrdenDetalle test de GenerarOrdenDetalleDesdeCombo");
        Long idCombo = 1L;
        Long idOrden = 1L;
        Integer cantidadCombo = 1;

        List<Object[]> detalles = new ArrayList<>();
        ProductoPrecio productoPrecio1 = new ProductoPrecio();
        productoPrecio1.setPrecioSugerido(BigDecimal.valueOf(1.00));
        ProductoPrecio productoPrecio2 = new ProductoPrecio();
        productoPrecio1.setPrecioSugerido(BigDecimal.valueOf(0.80));
        detalles.add(new Object[]{productoPrecio1, 20});
        detalles.add(new Object[]{productoPrecio2, 3});

        OrdenDetalleBean ordenDetalleBean = new OrdenDetalleBean();
        EntityManager em = Mockito.mock(EntityManager.class);
        TypedQuery queryMock = Mockito.mock(TypedQuery.class);

        ordenDetalleBean.em = em;

        // fallo de argumentos
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ordenDetalleBean.generarOrdenDetalleDesdeCombo(null, idCombo, cantidadCombo);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ordenDetalleBean.generarOrdenDetalleDesdeCombo(idOrden, null, cantidadCombo);
        });
        Assertions.assertThrows(NullPointerException.class, () -> {
            ordenDetalleBean.generarOrdenDetalleDesdeCombo(idOrden, idCombo, null);
        });

        //flujo normal

        when(em.createNamedQuery("ComboDetalle.findProductoPrecioAndCantidadByIdCombo", Object[].class)).thenReturn(queryMock);
        when(queryMock.setParameter("idCombo", idCombo)).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(detalles);

        OrdenDetalle uno = new OrdenDetalle();
        uno.setOrden(new Orden(idOrden));
        ProductoPrecio precio = (ProductoPrecio) detalles.get(0)[0];
        uno.setPrecio(precio.getPrecioSugerido());
        uno.setCantidad((Integer) detalles.get(0)[1] * cantidadCombo);

        OrdenDetalle dos = new OrdenDetalle();
        dos.setOrden(new Orden(idOrden));
        precio = (ProductoPrecio) detalles.get(1)[0];
        dos.setPrecio(precio.getPrecioSugerido());
        dos.setCantidad((Integer) detalles.get(1)[1] * cantidadCombo);

        Mockito.doNothing().when(em).persist(uno);
        Mockito.doNothing().when(em).persist(dos);
        Assertions.assertDoesNotThrow(() -> ordenDetalleBean.generarOrdenDetalleDesdeCombo(idOrden, idCombo, cantidadCombo));

        //precio invalidos desde db

        detalles.set(0, new Object[]{productoPrecio1, null});
        when(queryMock.getResultList()).thenReturn(detalles);
        Assertions.assertThrows(NullPointerException.class, () -> ordenDetalleBean.generarOrdenDetalleDesdeCombo(idOrden, idCombo, 2));

        //otros errores
        doThrow(PersistenceException.class).when(em).persist(uno);
        detalles.set(0, new Object[]{productoPrecio1, 10});
        Assertions.assertThrows(PersistenceException.class, () -> ordenDetalleBean.generarOrdenDetalleDesdeCombo(idOrden, idCombo, 2));
        doThrow(EntityNotFoundException.class).when(em).persist(uno);
        Assertions.assertThrows(EntityNotFoundException.class, () -> ordenDetalleBean.generarOrdenDetalleDesdeCombo(idOrden, idCombo, 2));

        //detalle no exxiste

        when(em.createNamedQuery("ComboDetalle.findProductoPrecioAndCantidadByIdCombo", Object[].class)).thenReturn(queryMock);
        when(queryMock.setParameter("idCombo", 112233L)).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(null);
        Assertions.assertThrows(NoResultException.class, () -> ordenDetalleBean.generarOrdenDetalleDesdeCombo(idOrden, 112233L, 2));


    }

    @Test
    void generarOrdenDetalleMixto() {
        System.out.println("OrdenDetalle Test de generarOrdenDetalleMixto");

        OrdenDetalleBean ordenDetalleBean = new OrdenDetalleBean();
        EntityManager em = Mockito.mock(EntityManager.class);
        ordenDetalleBean.em = em;

        TypedQuery<Object[]> queryMock = Mockito.mock(TypedQuery.class);

        Long idOrden = 12348L;

        List<Object[]> productosList = new ArrayList<>();
        productosList.add(new Object[]{1001L, -9});  // Coca x2
        productosList.add(new Object[]{1002L, 4});  // Pepsi x4

        // Simulación de combos (comboId, cantidad de combos)
        List<Object[]> comboList = new ArrayList<>();
        comboList.add(new Object[]{1001L, 2});  // Combo x2
        comboList.add(new Object[]{1002L, 3});  // Combo x2

        Producto coca = new Producto(1001L);
        Producto pepsi = new Producto(1002L);
        Producto pupusa = new Producto(1003L);

        ProductoPrecio precioCoca = new ProductoPrecio();
        precioCoca.setPrecioSugerido(BigDecimal.valueOf(1.00));
        precioCoca.setIdProducto(coca);
        ProductoPrecio precioPepsi = new ProductoPrecio();
        precioPepsi.setPrecioSugerido(BigDecimal.valueOf(0.80));
        precioPepsi.setIdProducto(pepsi);
        ProductoPrecio precioPupusa = new ProductoPrecio();
        precioPupusa.setIdProducto(pupusa);
        precioPupusa.setPrecioSugerido(BigDecimal.valueOf(1.50));

        //error de argumentos
        Assertions.assertThrows(IllegalArgumentException.class, () -> ordenDetalleBean.generarOrdenDetalleMixto(null, productosList, comboList));
        Assertions.assertThrows(IllegalArgumentException.class, () -> ordenDetalleBean.generarOrdenDetalleMixto(idOrden, null, null));

        // flujo normal
        when(em.createNamedQuery("ProductoPrecio.findProductoProductoProductoByIdProducto", Object[].class)).thenReturn(queryMock);
        when(queryMock.setParameter("idProducto", 1001L)).thenReturn(queryMock);
        when(queryMock.getSingleResult()).thenReturn(new Object[]{precioCoca, coca});

        when(queryMock.setParameter("idProducto", 1002L)).thenReturn(queryMock);
        when(queryMock.getSingleResult()).thenReturn(new Object[]{precioPepsi, pepsi});

        when(em.createNamedQuery("ComboDetalle.findProductoPrecioProductoAndCantidadByIdCombo", Object[].class)).thenReturn(queryMock);
        when(queryMock.setParameter("idCombo", 1001L)).thenReturn(queryMock);
        when(queryMock.setParameter("idCombo", 1002L)).thenReturn(queryMock);


        List<Object[]> comboProductos = new ArrayList<>();
        comboProductos.add(new Object[]{precioCoca, coca, 1});   // Coca x1 en combo
        comboProductos.add(new Object[]{precioPepsi, pepsi, 1}); // Pepsi x1 en combo
        comboProductos.add(new Object[]{precioPupusa, pupusa, 3}); // Pupusa x3 en combo

        when(queryMock.getResultList()).thenReturn(comboProductos);
        Assertions.assertDoesNotThrow(() -> ordenDetalleBean.generarOrdenDetalleMixto(idOrden, productosList, comboList));

        //errores

        TypedQuery tp = Mockito.spy(TypedQuery.class);
        when(em.createNamedQuery("ComboDetalle.findProductoPrecioProductoAndCantidadByIdCombo", Object[].class)).thenReturn(tp);
        when(tp.setParameter("idCombo", 1001L)).thenReturn(tp);
        when(tp.setParameter("idCombo", 1002L)).thenReturn(tp);

        doThrow(NoResultException.class).when(tp).getResultList();
        assertThrows(NoResultException.class, () -> ordenDetalleBean.generarOrdenDetalleMixto(idOrden, productosList, comboList));
        doThrow(PersistenceException.class).when(tp).getResultList();
        assertThrows(PersistenceException.class, () -> ordenDetalleBean.generarOrdenDetalleMixto(idOrden, productosList, comboList));


    }

}
