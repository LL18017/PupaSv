package sv.edu.ues.occ.ingenieria.tpi135_2025.control;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.*;


import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;  // ✅ CORRECTO
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrdenDetalleBeanIT extends AbstractContainerTest {

    @Mock
    private EntityManager em;
    @InjectMocks
    private OrdenDetalleBean ordenDetalleBean;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Order(1)
    @Test
    public void testGenerarOrdenDetalleProducto() {
        System.out.println("generarOrdenDetalleProducto");
        Orden orden = new Orden();
        orden.setIdOrden(1L);
        Producto producto = new Producto();
        producto.setIdProducto(1L);
        ProductoPrecio productoPrecio = new ProductoPrecio();
        productoPrecio.setIdProductoPrecio(10L);

        TypedQuery<ProductoPrecio> query = mock(TypedQuery.class);
        when(em.createNamedQuery("Producto.findByIdProducto", ProductoPrecio.class)).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(productoPrecio);

        OrdenDetalle detalle = ordenDetalleBean.generarOrdenDetalleProducto(orden, producto, 2);

        assertNotNull(detalle);
        assertEquals(orden, detalle.getOrden());
        assertEquals(productoPrecio, detalle.getProductoPrecio());
        assertEquals(2, detalle.getCantidad());
        //Assertions.fail("No pasa ");
    }

    @Order(2)
    @Test
    public void testGenerarOrdenDetalleDesdeCombo() {
        System.out.println("generarOrdenDetalleDesdeCombo");
        Orden orden = new Orden();
        orden.setIdOrden(1L);
        Combo combo = new Combo();
        combo.setIdCombo(1L);
        Integer cantidadCombo = 2;

        List<ComboDetalle> comboDetalles = new ArrayList<>();
        ComboDetalle comboDetalle1 = new ComboDetalle();
        Producto producto1 = new Producto();
        producto1.setIdProducto(1L);
        comboDetalle1.setProducto(producto1);
        comboDetalle1.setCantidad(3);
        comboDetalles.add(comboDetalle1);

        ComboDetalle comboDetalle2 = new ComboDetalle();
        Producto producto2 = new Producto();
        producto2.setIdProducto(2L);
        comboDetalle2.setProducto(producto2);
        comboDetalle2.setCantidad(1);
        comboDetalles.add(comboDetalle2);

        TypedQuery<ComboDetalle> comboDetalleQuery = mock(TypedQuery.class);
        when(em.createNamedQuery("ComboDetalle.findByIdCombo", ComboDetalle.class)).thenReturn(comboDetalleQuery);
        when(comboDetalleQuery.setParameter(anyString(), any())).thenReturn(comboDetalleQuery);
        when(comboDetalleQuery.getResultList()).thenReturn(comboDetalles);

        ProductoPrecio productoPrecio1 = new ProductoPrecio();
        productoPrecio1.setIdProductoPrecio(10L);
        ProductoPrecio productoPrecio2 = new ProductoPrecio();
        productoPrecio2.setIdProductoPrecio(11L);

        TypedQuery<ProductoPrecio> productoPrecioQuery1 = mock(TypedQuery.class);
        when(em.createNamedQuery("Producto.findByIdProducto", ProductoPrecio.class)).thenReturn(productoPrecioQuery1);
        when(productoPrecioQuery1.setParameter(anyString(), any())).thenReturn(productoPrecioQuery1);
        when(productoPrecioQuery1.setMaxResults(anyInt())).thenReturn(productoPrecioQuery1);
        when(productoPrecioQuery1.getSingleResult()).thenReturn(productoPrecio1).thenReturn(productoPrecio2);

        List<OrdenDetalle> ordenDetalles = ordenDetalleBean.generarOrdenDetalleDesdeCombo(orden, combo, cantidadCombo);

        assertNotNull(ordenDetalles);
        assertEquals(2, ordenDetalles.size());
        assertEquals(6, ordenDetalles.get(0).getCantidad());
        assertEquals(2, ordenDetalles.get(1).getCantidad());
        //Assertions.fail("no pasa ");
    }

    @Order(3)
    @Test
    public void testGenerarOrdenDetalleDesdeCombo_comboSinDetalles() {
        System.out.println("generarOrdenDetalleDesdeCombo_comboSinDetalles");
        Orden orden = new Orden();
        orden.setIdOrden(1L);
        Combo combo = new Combo();
        combo.setIdCombo(1L);
        Integer cantidadCombo = 2;

        TypedQuery<ComboDetalle> comboDetalleQuery = mock(TypedQuery.class);
        when(em.createNamedQuery("ComboDetalle.findByIdCombo", ComboDetalle.class)).thenReturn(comboDetalleQuery);
        when(comboDetalleQuery.setParameter(anyString(), any())).thenReturn(comboDetalleQuery);
        when(comboDetalleQuery.getResultList()).thenReturn(new ArrayList<>());

        List<OrdenDetalle> ordenDetalles = ordenDetalleBean.generarOrdenDetalleDesdeCombo(orden, combo, cantidadCombo);

        assertNull(ordenDetalles);
        //Assertions.fail("Esta prueba no pasa quemado");
    }

    @Order(4)
    @Test
    public void testGenerarOrdenDetalleMixto() {
        System.out.println("generarOrdenDetalleMixto");
        Orden orden = new Orden();
        orden.setIdOrden(1L);

        List<Producto> productos = new ArrayList<>();
        Producto producto1 = new Producto();
        producto1.setIdProducto(1L);
        productos.add(producto1);

        List<Combo> combos = new ArrayList<>();
        Combo combo1 = new Combo();
        combo1.setIdCombo(1L);
        combos.add(combo1);

        Integer cantidadProductos = 2;
        Integer cantidadCombo = 3;

        ProductoPrecio productoPrecio1 = new ProductoPrecio();
        productoPrecio1.setIdProductoPrecio(10L);
        TypedQuery<ProductoPrecio> productoPrecioQuery1 = mock(TypedQuery.class);
        when(em.createNamedQuery("Producto.findByIdProducto", ProductoPrecio.class)).thenReturn(productoPrecioQuery1);
        when(productoPrecioQuery1.setParameter(anyString(), any())).thenReturn(productoPrecioQuery1);
        when(productoPrecioQuery1.setMaxResults(anyInt())).thenReturn(productoPrecioQuery1);
        when(productoPrecioQuery1.getSingleResult()).thenReturn(productoPrecio1);

        List<ComboDetalle> comboDetalles = new ArrayList<>();
        ComboDetalle comboDetalle1 = new ComboDetalle();
        Producto productoCombo1 = new Producto();
        productoCombo1.setIdProducto(2L);
        comboDetalle1.setProducto(productoCombo1);
        comboDetalle1.setCantidad(4);
        comboDetalles.add(comboDetalle1);

        TypedQuery<ComboDetalle> comboDetalleQuery = mock(TypedQuery.class);
        when(em.createNamedQuery("ComboDetalle.findByIdCombo", ComboDetalle.class)).thenReturn(comboDetalleQuery);
        when(comboDetalleQuery.setParameter(anyString(), any())).thenReturn(comboDetalleQuery);
        when(comboDetalleQuery.getResultList()).thenReturn(comboDetalles);

        ProductoPrecio productoPrecioCombo1 = new ProductoPrecio();
        productoPrecioCombo1.setIdProductoPrecio(11L);
        TypedQuery<ProductoPrecio> productoPrecioQueryCombo1 = mock(TypedQuery.class);
        when(em.createNamedQuery("Producto.findByIdProducto", ProductoPrecio.class)).thenReturn(productoPrecioQueryCombo1);
        when(productoPrecioQueryCombo1.setParameter(anyString(), any())).thenReturn(productoPrecioQueryCombo1);
        when(productoPrecioQueryCombo1.setMaxResults(anyInt())).thenReturn(productoPrecioQueryCombo1);
        when(productoPrecioQueryCombo1.getSingleResult()).thenReturn(productoPrecioCombo1);

        List<OrdenDetalle> ordenDetalles = ordenDetalleBean.generarOrdenDetalleMixto(orden, productos, combos, cantidadProductos, cantidadCombo);

        assertNotNull(ordenDetalles);
        assertEquals(2, ordenDetalles.size());
        assertEquals(2, ordenDetalles.get(0).getCantidad());
        assertEquals(12, ordenDetalles.get(1).getCantidad());
        //Assertions.fail("Esta prueba mo pasa quemado");
    }
}


