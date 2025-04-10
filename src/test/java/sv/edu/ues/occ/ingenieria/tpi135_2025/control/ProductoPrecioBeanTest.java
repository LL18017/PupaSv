//package sv.edu.ues.occ.ingenieria.tpi135_2025.control;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.TypedQuery;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
//import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoPrecio;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class ProductoPrecioBeanTest {
//    List<ProductoPrecio> LIST_Producto_TEST = Arrays.asList(new ProductoPrecio[]{
//            new ProductoPrecio(1L),
//            new ProductoPrecio(2L),
//            new ProductoPrecio(3L),
//            new ProductoPrecio(4L),
//            new ProductoPrecio(5L),
//            new ProductoPrecio(6L),
//    });
//
//    ProductoPrecioBean cut; // Clase bajo prueba
//    EntityManager mockEm;
//    ProductoPrecioBean cut2;
//    TypedQuery mockTp;
//    TypedQuery mockTp2;
//
//    @BeforeEach
//    void setUp() {
//        cut = new ProductoPrecioBean();
//        mockEm = Mockito.mock(EntityManager.class);
//        cut.em = mockEm;
//        mockTp = Mockito.mock(TypedQuery.class);
//        cut2 = Mockito.spy(new ProductoPrecioBean());
//        mockTp2 = Mockito.spy(TypedQuery.class);
//    }
//
//    @Test
//    void orderParameterQuery() {
//        System.out.println("test orderParameterQuery");
//        String esperado = "idProductoPrecio";
//        String respuesta = cut.orderParameterQuery();
//        assertEquals(esperado,respuesta);
//
////        fail("fallo exitosamente");
//    }
//
//    @Test
//    void getEntityManager() {
//        System.out.println("test getEntityManager");
//        Mockito.when(cut2.getEntityManager()).thenReturn(mockEm);
//
//        EntityManager em = cut2.getEntityManager();
//        assertEquals(em,mockEm);
////        fail("fallo exitosamente");
//    }
//
//    @Test
//    void findByIdProducto() {
//        System.out.println("test findByIdProducto");
//        Long idProducto = 1001L;//ya establecido en DB
//        int first = 0;
//        int max = 10;
//        List<ProductoPrecio> esperado = this.LIST_Producto_TEST;
//
//        Mockito.when(mockEm.createNamedQuery("ProductoPrecio.findByIdTipoProductoAndIdProducto",ProductoPrecio.class)).thenReturn(mockTp);
//        Mockito.when(mockTp.setFirstResult(first)).thenReturn(mockTp);
//        Mockito.when(mockTp.setMaxResults(max)).thenReturn(mockTp);
//        Mockito.when(mockTp.setParameter("idProducto", idProducto)).thenReturn(mockTp);
//        Mockito.when(mockTp.getResultList()).thenReturn(this.LIST_Producto_TEST);
//        List<ProductoPrecio> resultados = cut.findByIdProducto(idProducto, first, max);
//        assertNotNull(resultados);
//        assertEquals(esperado.size(), resultados.size());
//
//        //registro nulo
//        assertThrows(IllegalArgumentException.class,()->{
//            cut.findByIdProducto(null, first, max);
//        });
//
//        //fallo de entity
//        cut2.em = mockEm;
//        Mockito.when(mockEm.createNamedQuery("ProductoPrecio.findByIdTipoProductoAndIdProducto",ProductoPrecio.class)).thenReturn(mockTp2);
//        Mockito.doThrow(IllegalStateException.class).when(mockTp2).setParameter("idProducto",idProducto);
//        Assertions.assertThrows(IllegalStateException.class, () -> cut2.findByIdProducto(idProducto,first,max));
//
//
//    }
//
//    @Test
//    void countByIdProducto() {
//        System.out.println("test countByIdProducto");
//        Long idProducto = 1001L;
//        Long esperado=2L;
//
//        Mockito.when(mockEm.createNamedQuery("ProductoPrecio.countByIdTipoProductoAndIdProducto",Long.class)).thenReturn(mockTp);
//        Mockito.when(mockTp.setParameter("idProducto", idProducto)).thenReturn(mockTp);
//        Mockito.when(mockTp.getSingleResult()).thenReturn(esperado);
//        Long resultado = cut.countByIdProducto(idProducto);
//        assertEquals(esperado,resultado);
//
//        //REGISTRO NULL
//        assertThrows(IllegalArgumentException.class,()->{
//            cut.countByIdProducto(null);
//        });
//
//        //fallo de entity
//        cut2.em = mockEm;
//        Mockito.when(mockEm.createNamedQuery("ProductoPrecio.countByIdTipoProductoAndIdProducto",Long.class)).thenReturn(mockTp2);
//        Mockito.doThrow(IllegalStateException.class).when(mockTp2).setParameter("idProducto",idProducto);
//        Assertions.assertThrows(IllegalStateException.class, () -> cut2.countByIdProducto(idProducto));
////        fail("fallo exitosamenet");
//
//
//    }
//}