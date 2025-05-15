package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.*;

import java.math.BigDecimal;
import java.util.*;

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
    void orderParameterQuery() {
        PagoDetalleBean cut = new PagoDetalleBean();
        System.out.println("PagoDetalle test orderParameterQuery");
        String esperado = "idPagoDetalle";
        String respuesta = cut.orderParameterQuery();
        assertEquals(esperado, respuesta);

//        fail("fallo exitosamente");
    }


    @Test
    void getEntityManager() {
        PagoDetalleBean cut2 = Mockito.spy(PagoDetalleBean.class);
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        System.out.println("PagoDetalle test getEntityManager");
        Mockito.when(cut2.getEntityManager()).thenReturn(mockEm);

        EntityManager em = cut2.getEntityManager();
        assertEquals(em, mockEm);
//        fail("fallo exitosamente");
    }
    @Test
    void findByIdPago() {
        System.out.println("PagoDetalle test findByIdPago");
        PagoDetalleBean cut = new PagoDetalleBean();
        PagoDetalle pd = new PagoDetalle();
        Long idPago = 2L;
        Pago pago = new Pago(idPago);
        pd.setIdPago(pago);
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        TypedQuery mockTq = Mockito.mock(TypedQuery.class);
        Integer first = 0;
        Integer max = 10;
        cut.em = mockEm;
        //error de argumentos
        assertThrows(IllegalArgumentException.class, () -> cut.findRangeByIdPago(null, first, max));
        assertThrows(IllegalArgumentException.class, () -> cut.findRangeByIdPago(idPago, -60, max));
        assertThrows(IllegalArgumentException.class, () -> cut.findRangeByIdPago(idPago, first, null));
        // no existe pago
        Mockito.when(mockEm.find(Pago.class, 112233L)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> cut.findRangeByIdPago(112233L, first, max));

        //flujo normal
        Mockito.when(mockEm.find(Pago.class, idPago)).thenReturn(pago);

        Mockito.when(mockEm.createNamedQuery("PagoDetalle.findByIdPago", PagoDetalle.class)).thenReturn(mockTq);
        Mockito.when(mockTq.setParameter("idPago", idPago)).thenReturn(mockTq);
        Mockito.when(mockTq.setFirstResult(first)).thenReturn(mockTq);
        Mockito.when(mockTq.setMaxResults(max)).thenReturn(mockTq);
        Mockito.when(mockTq.getResultList()).thenReturn(LIST_PagoDetalle_TEST);

        List<PagoDetalle> resultado = cut.findRangeByIdPago(idPago, first, max);
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(LIST_PagoDetalle_TEST.size(), resultado.size());

        //excepciones
        TypedQuery mockTq2 = Mockito.spy(TypedQuery.class);
        Mockito.when(mockEm.createNamedQuery("PagoDetalle.findByIdPago", PagoDetalle.class)).thenReturn(mockTq2);
        Mockito.when(mockTq2.setParameter("idPago", idPago)).thenReturn(mockTq2);
        Mockito.when(mockTq2.setFirstResult(first)).thenReturn(mockTq2);
        Mockito.when(mockTq2.setMaxResults(max)).thenReturn(mockTq2);
        Mockito.doThrow(PersistenceException.class).when(mockTq2).getResultList();

        Assertions.assertThrows(PersistenceException.class, () -> cut.findRangeByIdPago(idPago, first, max));

//        fail("fallo existosamente");
    }

    @Test
    void countByIdPago() {
        System.out.println("PagoDetalle test countByIdPago");
        PagoDetalleBean cut = new PagoDetalleBean();
        PagoDetalle pd = new PagoDetalle();
        Long idPago = 2L;
        Pago pago = new Pago(idPago);
        pd.setIdPago(pago);
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        TypedQuery mockTq = Mockito.mock(TypedQuery.class);
        Integer first = 0;
        Integer max = 10;
        cut.em = mockEm;


        //error de argumentos
        assertThrows(IllegalArgumentException.class, () -> cut.countByIdPago(null));
        assertThrows(IllegalArgumentException.class, () -> cut.countByIdPago(-60L));
        // no existe pago
        Mockito.when(mockEm.find(Pago.class, 112233)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> cut.countByIdPago(112233L));
        //flujo normal
        Mockito.when(mockEm.find(Pago.class, idPago)).thenReturn(pago);

        Mockito.when(mockEm.createNamedQuery("PagoDetalle.countByIdPago", Long.class)).thenReturn(mockTq);
        Mockito.when(mockTq.setParameter("idPago", idPago)).thenReturn(mockTq);
        Mockito.when(mockTq.getSingleResult()).thenReturn((long) LIST_PagoDetalle_TEST.size());
        Long resultado = cut.countByIdPago(idPago);
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(LIST_PagoDetalle_TEST.size(), resultado);

        //excepciones
        TypedQuery mockTq2 = Mockito.spy(TypedQuery.class);
        Mockito.when(mockEm.createNamedQuery("PagoDetalle.countByIdPago", Long.class)).thenReturn(mockTq2);
        Mockito.when(mockTq2.setParameter("idPago", idPago)).thenReturn(mockTq2);
        Mockito.doThrow(PersistenceException.class).when(mockTq2).getSingleResult();
        Assertions.assertThrows(PersistenceException.class, () -> cut.countByIdPago(idPago));
        Mockito.doThrow(NonUniqueResultException.class).when(mockTq2).getSingleResult();
        Assertions.assertThrows(NonUniqueResultException.class, () -> cut.countByIdPago(idPago));


//        fail("fallo exitosamnete");

    }


    @Test
    void calculoProducto() {
        System.out.println("PagoDetalle test calculoProducto");
        Producto producto = new Producto();
        PagoDetalleBean cut = new PagoDetalleBean();
        TypedQuery mockTp = Mockito.mock(TypedQuery.class);
        producto.setIdProducto(1l);
        Integer cantidad = 10;

        BigDecimal precio = BigDecimal.valueOf(10);
        ProductoPrecio productoPrecio = new ProductoPrecio();
        productoPrecio.setPrecioSugerido(precio);
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        cut.em = mockEm;
        //error de argumentos
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.calculoProducto(null, cantidad));
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.calculoProducto(producto, null));

        //flujo bueno
        Mockito.when(mockEm.createNamedQuery("ProductoPrecio.findByIdProducto", ProductoPrecio.class)).thenReturn(mockTp);
        Mockito.when(mockTp.setParameter("idProducto", producto.getIdProducto())).thenReturn(mockTp);
        Mockito.when(mockTp.getSingleResult()).thenReturn(productoPrecio);
        BigDecimal resultadp = cut.calculoProducto(producto, cantidad);
        Assertions.assertEquals(precio.multiply(BigDecimal.valueOf(cantidad)), resultadp);
        //errores
        TypedQuery mockTp2 = Mockito.spy(TypedQuery.class);
        Mockito.when(mockEm.createNamedQuery("ProductoPrecio.findByIdProducto", ProductoPrecio.class)).thenReturn(mockTp2);
        Mockito.when(mockTp2.setParameter("idProducto", producto.getIdProducto())).thenReturn(mockTp2);

        Mockito.doThrow(EntityNotFoundException.class).when(mockTp2).getSingleResult();
        Assertions.assertThrows(EntityNotFoundException.class, () -> cut.calculoProducto(producto, cantidad));

        Mockito.doThrow(EntityNotFoundException.class).when(mockTp2).getSingleResult();
        Assertions.assertThrows(EntityNotFoundException.class, () -> cut.calculoProducto(producto, cantidad));

        Mockito.doThrow(NonUniqueResultException.class).when(mockTp2).getSingleResult();
        Assertions.assertThrows(NonUniqueResultException.class, () -> cut.calculoProducto(producto, cantidad));

        Mockito.doThrow(NoResultException.class).when(mockTp2).getSingleResult();
        Assertions.assertThrows(NoResultException.class, () -> cut.calculoProducto(producto, cantidad));
        Mockito.doThrow(PersistenceException.class).when(mockTp2).getSingleResult();
        Assertions.assertThrows(PersistenceException.class, () -> cut.calculoProducto(producto, cantidad));


        //flujo bueno


    }

    @Test
    void calculoProductos() {
        System.out.println("PagoDetalle test calculoProductos");
        PagoDetalleBean cut = Mockito.spy(PagoDetalleBean.class);
        // Datos de prueba
        Producto producto1 = new Producto(1L);
        Producto producto2 = new Producto(2L);
        Producto producto3 = new Producto(3L);

        Integer cantidadProducto1 = 10;
        Integer cantidadProducto2 = 5;
        Integer cantidadProducto3 = 4;

        BigDecimal precioProducto1 = BigDecimal.valueOf(10L);
        BigDecimal precioProducto2 = BigDecimal.valueOf(5L);
        BigDecimal precioProducto3 = BigDecimal.valueOf(4L);

        // Esperado
        BigDecimal esperado = precioProducto1.multiply(BigDecimal.valueOf(cantidadProducto1));
        esperado = esperado.add(precioProducto2.multiply(BigDecimal.valueOf(cantidadProducto2)));
        esperado = esperado.add(precioProducto3.multiply(BigDecimal.valueOf(cantidadProducto3)));

        // Crear el mapa de productos
        Map<Producto, Integer> productos = new HashMap<>();
        productos.put(producto1, cantidadProducto1);
        productos.put(producto2, cantidadProducto2);
        productos.put(producto3, cantidadProducto3);

        // Mock de PagoDetalleBean


        // Configuración del mock para el método calculoProducto
        Mockito.doReturn(precioProducto1.multiply(BigDecimal.valueOf(cantidadProducto1))).when(cut).calculoProducto(producto1, cantidadProducto1);
        Mockito.doReturn(precioProducto2.multiply(BigDecimal.valueOf(cantidadProducto2))).when(cut).calculoProducto(producto2, cantidadProducto2);
        Mockito.doReturn(precioProducto3.multiply(BigDecimal.valueOf(cantidadProducto3))).when(cut).calculoProducto(producto3, cantidadProducto3);


        // Llamada al método que estamos testeando
        BigDecimal respuesta = cut.calculoProductos(productos);

        // Verificación de que el resultado es el esperado
        Assertions.assertEquals(esperado, respuesta);

        // Verificar que los métodos fueron llamados las veces correctas (opcional)
        Mockito.verify(cut).calculoProducto(producto1, cantidadProducto1);
        Mockito.verify(cut).calculoProducto(producto2, cantidadProducto2);
        Mockito.verify(cut).calculoProducto(producto3, cantidadProducto3);
    }

    @Test
    void calculoPorOrden() {
        System.out.println("PagoDetalle test calculoPorOrden");
        Orden orden = new Orden();
        PagoDetalleBean cut = new PagoDetalleBean();
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        TypedQuery mockTp = Mockito.mock(TypedQuery.class);
        Long ordenId = 1L;
        orden.setIdOrden(ordenId);
        OrdenDetalle ordenDetalle1 = new OrdenDetalle(ordenId,1L);
        OrdenDetalle ordenDetalle2 = new OrdenDetalle(ordenId,2L);
        ordenDetalle1.setPrecio(BigDecimal.valueOf(10));
        ordenDetalle1.setCantidad(10);
        ordenDetalle2.setPrecio(BigDecimal.valueOf(5));
        ordenDetalle2.setCantidad(5);

        List<OrdenDetalle> detalles = Arrays.asList(ordenDetalle1, ordenDetalle2);
        BigDecimal esperado=ordenDetalle1.getPrecio().multiply(BigDecimal.valueOf(ordenDetalle1.getCantidad()));
        esperado=esperado.add(ordenDetalle2.getPrecio().multiply(BigDecimal.valueOf(ordenDetalle2.getCantidad())));
        //error de argumentos

        Assertions.assertThrows(IllegalArgumentException.class,()->cut.calculoPorOrden(null));

        cut.em=mockEm;
        Mockito.when(mockEm.find(Orden.class, ordenId)).thenReturn(orden);
        Mockito.when(mockEm.createNamedQuery("OrdenDetalle.findByIdOrden",OrdenDetalle.class)).thenReturn(mockTp);
        Mockito.when(mockTp.setParameter("idOrden", ordenId)).thenReturn(mockTp);
        Mockito.when(mockTp.getResultList()).thenReturn(detalles);

        BigDecimal resultado=cut.calculoPorOrden(ordenId);
        Assertions.assertEquals(esperado,resultado);

        //idNo existe

        Mockito.when(mockEm.find(Orden.class, 112233L)).thenReturn(null);
        Assertions.assertThrows(EntityNotFoundException.class,()->cut.calculoPorOrden(112233L));


        TypedQuery mockTp2=Mockito.spy(TypedQuery.class);
        Mockito.when(mockEm.createNamedQuery("OrdenDetalle.findByIdOrden",OrdenDetalle.class)).thenReturn(mockTp2);
        Mockito.when(mockTp2.setParameter("idOrden", ordenId)).thenReturn(mockTp2);

        Mockito.doThrow(NonUniqueResultException.class).when(mockTp2).getResultList();
        Assertions.assertThrows(NonUniqueResultException.class,()->cut.calculoPorOrden(ordenId));

        Mockito.doThrow(NoResultException.class).when(mockTp2).getResultList();
        Assertions.assertThrows(NoResultException.class,()->cut.calculoPorOrden(ordenId));

        Mockito.doThrow(PersistenceException.class).when(mockTp2).getResultList();
        Assertions.assertThrows(PersistenceException.class,()->cut.calculoPorOrden(ordenId));

        //flujo bueno


    }
    @Test
    void createDetalles() {
        System.out.println("PagoDetalle test createDetalles");
        Long ordenId = 1L;
        Orden orden = new Orden();
        PagoDetalleBean cut = Mockito.spy(PagoDetalleBean.class);
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        TypedQuery mockTp = Mockito.mock(TypedQuery.class);
        orden.setIdOrden(ordenId);

        Pago pago1 = new Pago();
        Pago pago2 = new Pago();
        pago1.setIdPago(1L);
        pago1.setIdOrden(new Orden(ordenId));
        pago2.setIdPago(2L);
        pago2.setIdOrden(new Orden(ordenId));
        List<Pago> pagos = Arrays.asList(pago1, pago1);
        BigDecimal esperado=BigDecimal.valueOf(10);

        //error de argumentos
        Assertions.assertThrows(IllegalArgumentException.class,()->cut.createDetalles(null));

        //idNo existe
        cut.em=mockEm;
        Mockito.when(mockEm.find(Orden.class, 112233L)).thenReturn(null);
        Assertions.assertThrows(EntityNotFoundException.class,()->cut.createDetalles(112233L));



        //flujo normal
        Mockito.when(mockEm.find(Orden.class, ordenId)).thenReturn(orden);
        Mockito.when(mockEm.createNamedQuery("Pago.findByIdOrden",Pago.class)).thenReturn(mockTp);
        Mockito.when(mockTp.setParameter("idOrden", ordenId)).thenReturn(mockTp);
        Mockito.when(mockTp.getResultList()).thenReturn(pagos);
        Mockito.doReturn(esperado).when(cut).calculoPorOrden(ordenId);
        Assertions.assertDoesNotThrow(() -> cut.createDetalles(ordenId));

        //excepciones

        Mockito.doThrow(NonUniqueResultException.class).when(cut).calculoPorOrden(ordenId);
        Assertions.assertThrows(NonUniqueResultException.class,()->cut.createDetalles(ordenId));

        Mockito.doThrow(NoResultException.class).when(cut).calculoPorOrden(ordenId);
        Assertions.assertThrows(NoResultException.class,()->cut.createDetalles(ordenId));

        Mockito.doThrow(PersistenceException.class).when(cut).calculoPorOrden(ordenId);
        Assertions.assertThrows(PersistenceException.class,()->cut.createDetalles(ordenId));


    }

}