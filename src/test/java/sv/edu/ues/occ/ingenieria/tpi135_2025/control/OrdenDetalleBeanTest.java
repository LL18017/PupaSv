package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class OrdenDetalleBeanTest {
    @InjectMocks
    private OrdenDetalleBean ordenDetalleBean;

    @Mock
    private EntityManager em;

    @Mock
    TypedQuery<ProductoPrecio> queryProductoPrecio;
    @Mock
    private TypedQuery<OrdenDetalle> queryMock;

    @Mock
    private TypedQuery<ComboDetalle> comboDetalleQuery;

    @Mock
    private TypedQuery<Long> queryLongMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testConstructor() {
        System.out.println("Test de Constructor");
        assertNotNull(new OrdenDetalleBean());
        //Assertions.fail("Esta prueba no pasa quemado ");
    }
    @Test
    public void testOrderParameterQuery() {
        System.out.println("Test de OrderParameterQuery");
        assertEquals("idOrdenDetalle", ordenDetalleBean.orderParameterQuery());
        //Assertions.fail("Esta prueba no pasa quemado");
    }

    @Test
    public void testGetEntityManager() {
        System.out.println("Test de getEntityManager");
        assertNotNull(ordenDetalleBean.getEntityManager());
        assertEquals(em, ordenDetalleBean.getEntityManager());
        //Assertions.fail("Esta prueba no pasa quemado ");
    }


    @Test
    public void testFindByIdOrdenAndIdPrecioProducto() {
        System.out.println("Test de findByIdOrdenAndIdPrecioProducto");
        OrdenDetalle detalleEsperado = new OrdenDetalle();
        when(em.createNamedQuery("OrdenDetalle.findByPrecioProductoAndIdOrden", OrdenDetalle.class)).thenReturn(queryMock);
        when(queryMock.setParameter(eq("idOrden"), anyLong())).thenReturn(queryMock);
        when(queryMock.setParameter(eq("idProductoPrecio"), anyLong())).thenReturn(queryMock);
        when(queryMock.getSingleResult()).thenReturn(detalleEsperado);

        OrdenDetalle resultado = ordenDetalleBean.findByIdOrdenAndIdPrecioProducto(1L, 1L);
        assertNotNull(resultado);
        assertEquals(detalleEsperado, resultado);
        //Assertions.fail("Esta prueba no pasa quemado");
    }

    @Test
    public void findByIdOrdenAndIdPrecioProducto_noResult_returnsNull() {
        Long idOrden = 1L;
        Long idProductoPrecio = 2L;

        when(em.createNamedQuery(anyString(), org.mockito.ArgumentMatchers.<Class<OrdenDetalle>>any())).thenReturn(queryMock);
        when(queryMock.setParameter("idOrden", idOrden)).thenReturn(queryMock);
        when(queryMock.setParameter("idProductoPrecio", idProductoPrecio)).thenReturn(queryMock);
        when(queryMock.getSingleResult()).thenThrow(new NoResultException());

        OrdenDetalle result = ordenDetalleBean.findByIdOrdenAndIdPrecioProducto(idOrden, idProductoPrecio);
        assertNull(result);
        //Assertions.fail("Esta prueba no pasa quemado");
    }


    @Test
    public void testFindRangeByIdOrden() {
        System.out.println("Test de findRangeByIdOrden");
        List<OrdenDetalle> mockList = List.of(new OrdenDetalle(), new OrdenDetalle());
        when(em.createNamedQuery("OrdenDetalle.findByIdOrden", OrdenDetalle.class)).thenReturn(queryMock);
        when(queryMock.setParameter("idOrden", 1L)).thenReturn(queryMock);
        when(queryMock.setFirstResult(0)).thenReturn(queryMock);
        when(queryMock.setMaxResults(2)).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(mockList);

        List<OrdenDetalle> result = ordenDetalleBean.findRangeByIdOrden(1L, 0, 2);
        assertNotNull(result);
        assertEquals(2, result.size());
        //Assertions.fail("Esta prueba no pasa quemado");
    }
    @Test
    public void findRangeByIdOrden_exceptionThrown_returnsNull() {
        Long idOrden = 1L;
        int first = 0;
        int max = 10;
        when(em.createNamedQuery(anyString(), org.mockito.ArgumentMatchers.<Class<OrdenDetalle>>any()))
                .thenThrow(new RuntimeException("Simulated database error"));
        List<OrdenDetalle> result = ordenDetalleBean.findRangeByIdOrden(idOrden, first, max);
        assertTrue(result.isEmpty()); // Cambiado a assertTrue(result.isEmpty());
        //Assertions.fail("Esta prueba no pasa quemado");
    }

    @Test
    public void testCountByIdOrden() {
        System.out.println("Test de countByIdOrden");
        when(em.createNamedQuery("OrdenDetalle.countByIdOrden", Long.class)).thenReturn(queryLongMock);
        when(queryLongMock.setParameter("idOrden", 1L)).thenReturn(queryLongMock);
        when(queryLongMock.getSingleResult()).thenReturn(5L);

        Long result = ordenDetalleBean.countByIdOrden(1L);
        assertEquals(Long.valueOf(5L), result);
        //Assertions.fail("Esta prueba no pasa quemado");
    }

    @Test
    public void testGenerarOrdenDetalleProducto() {
        System.out.println("Test de generarOrdenDetalleProducto");
        Orden orden = new Orden();
        orden.setIdOrden(1L);

        Producto producto = new Producto();
        producto.setIdProducto(1L);

        ProductoPrecio precio = new ProductoPrecio();

        when(em.createNamedQuery("Producto.findByIdProducto", ProductoPrecio.class)).thenReturn(queryProductoPrecio);
        when(queryProductoPrecio.setParameter("idProducto", 1L)).thenReturn(queryProductoPrecio);
        when(queryProductoPrecio.setMaxResults(1)).thenReturn(queryProductoPrecio);
        when(queryProductoPrecio.getSingleResult()).thenReturn(precio);

        OrdenDetalle result = ordenDetalleBean.generarOrdenDetalleProducto(orden, producto, 3);

        assertNotNull(result);
        assertEquals(orden, result.getOrden());
        assertEquals(precio, result.getProductoPrecio());
        assertEquals(3, result.getCantidad());
        //Assertions.fail("Esta prueba no pasa quemado");
    }

    @Test
    public void testGenerarOrdenDetalleProductoCantidadNula() {
        System.out.println("Test de generarOrdenDetalleProductoCantidadNula");
        Orden orden = new Orden();
        orden.setIdOrden(1L);

        Producto producto = new Producto();
        producto.setIdProducto(1L);

        ProductoPrecio precio = new ProductoPrecio();

        when(em.createNamedQuery("Producto.findByIdProducto", ProductoPrecio.class)).thenReturn(queryProductoPrecio);
        when(queryProductoPrecio.setParameter("idProducto", 1L)).thenReturn(queryProductoPrecio);
        when(queryProductoPrecio.setMaxResults(1)).thenReturn(queryProductoPrecio);
        when(queryProductoPrecio.getSingleResult()).thenReturn(precio);

        OrdenDetalle result = ordenDetalleBean.generarOrdenDetalleProducto(orden, producto, null);

        assertNotNull(result);
        assertEquals(1, result.getCantidad());
        //Assertions.fail("Esta prueba no pasa quemado");
    }

    @Test
    public void testGenerarOrdenDetalleProductoOrdenInvalida() {
        System.out.println("Test de generarOrdenDetalleProductoOrdenInvalida");
        Producto producto = new Producto();
        producto.setIdProducto(1L);
        assertThrows(IllegalArgumentException.class, () -> {
            ordenDetalleBean.generarOrdenDetalleProducto(null, producto, 1);
            //Assertions.fail("Esta prueba no pasa quemado");
        });
    }

    @Test
    public void testGenerarOrdenDetalleProductoProductoInvalido() {
        System.out.println("Test de generarOrdenDetalleProductoProductoProductoInvalido");
        Orden orden = new Orden();
        orden.setIdOrden(1L);
        assertThrows(IllegalArgumentException.class, () -> {
            ordenDetalleBean.generarOrdenDetalleProducto(orden, null, 1);
        });
        //Assertions.fail("Esta prueba no pasa quemado");
    }

    @Test
    public void testGenerarOrdenDetalleDesdeCombo() {
        System.out.println("test de GenerarOrdenDetalleDesdeCombo");
        // Escenario 1: orden inválida
        assertThrows(IllegalArgumentException.class, () -> ordenDetalleBean.generarOrdenDetalleDesdeCombo(null, new Combo(), 1));
        assertThrows(IllegalArgumentException.class, () -> ordenDetalleBean.generarOrdenDetalleDesdeCombo(new Orden(), new Combo(), 1));

        // Escenario 2: combo inválido
        Orden orden = new Orden();
        orden.setIdOrden(1L);
        assertThrows(IllegalArgumentException.class, () -> ordenDetalleBean.generarOrdenDetalleDesdeCombo(orden, null, 1));
        assertThrows(IllegalArgumentException.class, () -> ordenDetalleBean.generarOrdenDetalleDesdeCombo(orden, new Combo(), 1));

        // Escenario 3: cantidadCombo nula
        Combo combo = new Combo();
        combo.setIdCombo(1L);

        when(em.createNamedQuery(eq("ComboDetalle.findByIdCombo"), eq(ComboDetalle.class))).thenReturn(comboDetalleQuery);
        when(comboDetalleQuery.setParameter(eq("idCombo"), eq(1L))).thenReturn(comboDetalleQuery);
        when(comboDetalleQuery.getResultList()).thenReturn(new ArrayList<>());

        List<OrdenDetalle> resultado = ordenDetalleBean.generarOrdenDetalleDesdeCombo(orden, combo, null);
        assertNull(resultado);

        // Escenario 4: combo sin productos
        resultado = ordenDetalleBean.generarOrdenDetalleDesdeCombo(orden, combo, 1);
        assertNull(resultado);

        // Escenario 5: producto inválido
        List<ComboDetalle> comboDetalles = new ArrayList<>();
        ComboDetalle comboDetalle = new ComboDetalle();
        comboDetalle.setProducto(new Producto());
        comboDetalles.add(comboDetalle);

        when(comboDetalleQuery.getResultList()).thenReturn(comboDetalles);

        resultado = ordenDetalleBean.generarOrdenDetalleDesdeCombo(orden, combo, 1);
        assertEquals(0, resultado.size());

        // Escenario 6: producto sin precio
        Producto producto = new Producto();
        producto.setIdProducto(1L);
        comboDetalle.setProducto(producto);
        comboDetalle.setCantidad(2);

        when(em.createNamedQuery(eq("Producto.findByIdProducto"), eq(ProductoPrecio.class))).thenReturn(queryProductoPrecio);
        when(queryProductoPrecio.setParameter(eq("idProducto"), eq(1L))).thenReturn(queryProductoPrecio);
        when(queryProductoPrecio.setMaxResults(1)).thenReturn(queryProductoPrecio);
        when(queryProductoPrecio.getSingleResult()).thenReturn(null);

        resultado = ordenDetalleBean.generarOrdenDetalleDesdeCombo(orden, combo, 1);
        assertEquals(0, resultado.size());

        // Escenario 7: éxito
        ProductoPrecio productoPrecio = new ProductoPrecio();
        productoPrecio.setIdProductoPrecio(1L);

        when(queryProductoPrecio.getSingleResult()).thenReturn(productoPrecio);

        resultado = ordenDetalleBean.generarOrdenDetalleDesdeCombo(orden, combo, 3);
        assertEquals(1, resultado.size());
        assertEquals(6, resultado.get(0).getCantidad());
        //Assertions.fail("Esta prueba no pasa quemado");
    }
    @Test
    void generarOrdenDetalleMixto_todosLosEscenarios() {
        System.out.println("Test de generarOrdenDetalleMixto");
        Orden orden = new Orden();
        orden.setIdOrden(1L);

        Producto producto1 = new Producto();
        producto1.setIdProducto(1L);

        Producto producto2 = new Producto();
        producto2.setIdProducto(2L);

        ProductoPrecio precioProducto1 = new ProductoPrecio();
        precioProducto1.setIdProductoPrecio(10L);

        ProductoPrecio precioProducto2 = new ProductoPrecio();
        precioProducto2.setIdProductoPrecio(20L);

        Combo combo1 = new Combo();
        combo1.setIdCombo(1L);

        ComboDetalle comboDetalle1 = new ComboDetalle();
        comboDetalle1.setProducto(producto1);
        comboDetalle1.setCantidad(2);

        ComboDetalle comboDetalle2 = new ComboDetalle();
        comboDetalle2.setProducto(producto2);
        comboDetalle2.setCantidad(3);

        List<Producto> productos = new ArrayList<>();
        productos.add(producto1);

        List<Combo> combos = new ArrayList<>();
        combos.add(combo1);

        when(em.createNamedQuery(eq("Producto.findByIdProducto"), eq(ProductoPrecio.class))).thenReturn(queryProductoPrecio);
        when(queryProductoPrecio.setParameter(eq("idProducto"), eq(1L))).thenReturn(queryProductoPrecio);
        when(queryProductoPrecio.setMaxResults(1)).thenReturn(queryProductoPrecio);
        when(queryProductoPrecio.getSingleResult()).thenReturn(precioProducto1);

        when(em.createNamedQuery(eq("ComboDetalle.findByIdCombo"), eq(ComboDetalle.class))).thenReturn(comboDetalleQuery);
        when(comboDetalleQuery.setParameter(eq("idCombo"), eq(1L))).thenReturn(comboDetalleQuery);
        when(comboDetalleQuery.getResultList()).thenReturn(List.of(comboDetalle1, comboDetalle2));

        when(queryProductoPrecio.setParameter(eq("idProducto"), eq(2L))).thenReturn(queryProductoPrecio);
        when(queryProductoPrecio.getSingleResult()).thenReturn(precioProducto2);

        List<OrdenDetalle> resultado = ordenDetalleBean.generarOrdenDetalleMixto(orden, productos, combos, 2, 3);
        assertEquals(3, resultado.size());
        assertEquals(2, resultado.get(0).getCantidad());
        assertEquals(6, resultado.get(1).getCantidad());
        assertEquals(9, resultado.get(2).getCantidad());

        // Escenario orden invalida
        assertThrows(IllegalArgumentException.class, () -> ordenDetalleBean.generarOrdenDetalleMixto(null, productos, combos, 2, 3));
        assertThrows(IllegalArgumentException.class, () -> ordenDetalleBean.generarOrdenDetalleMixto(new Orden(), productos, combos, 2, 3));

        // escenarios con listas vacias.
        resultado = ordenDetalleBean.generarOrdenDetalleMixto(orden, new ArrayList<>(), new ArrayList<>(), 2, 3);
        assertEquals(0, resultado.size());

        // escenarios con cantidades nulas.
        resultado = ordenDetalleBean.generarOrdenDetalleMixto(orden, productos, combos, null, null);
        assertEquals(3, resultado.size());
        assertEquals(1, resultado.get(0).getCantidad());
        assertEquals(2, resultado.get(1).getCantidad());
        assertEquals(3, resultado.get(2).getCantidad());

        // escenarios con productos o combos sin precios.
        when(queryProductoPrecio.getSingleResult()).thenReturn(null);
        resultado = ordenDetalleBean.generarOrdenDetalleMixto(orden, productos, combos, 2, 3);
        assertEquals(0, resultado.size());
        //Assertions.fail("Esta prueba no pasa quemado");
    }
}
