package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetallePK;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductoDetalleBeanTest {

    List<ProductoDetalle> LIST_ProductoDetalle_TEST = Arrays.asList(new ProductoDetalle[]{
            new ProductoDetalle(1,1L),
            new ProductoDetalle(1,1L),
            new ProductoDetalle(1,2L),
            new ProductoDetalle(1,3L),
            new ProductoDetalle(1,4L),

            new ProductoDetalle(2,5L),
            new ProductoDetalle(2,5L),
            new ProductoDetalle(2,6L),
            new ProductoDetalle(2,6L),
            new ProductoDetalle(2,7L),
    });

    ProductoDetalleBean cut;
    EntityManager mockEm;
    ProductoDetalleBean cut2;
    TypedQuery mockTp;

    @BeforeEach
    void setUp() {
        cut = new ProductoDetalleBean();
        mockEm = Mockito.mock(EntityManager.class);
        cut.em = mockEm;
        cut2 = Mockito.spy(new ProductoDetalleBean());
        mockTp = Mockito.mock(TypedQuery.class);
    }


    @Test
    void orderParameterQuery() {
        System.out.println("test orderParameterQuery");
        String esperado = "idProductoDetalle";
        String respuesta = cut.orderParameterQuery();
        assertEquals(esperado,respuesta);

//        fail("fallo exitosamente");
    }


    @Test
    void getEntityManager() {
        System.out.println("test getEntityManager");
        Mockito.when(cut2.getEntityManager()).thenReturn(mockEm);

        EntityManager em = cut2.getEntityManager();
        assertEquals(em,mockEm);
//        fail("fallo exitosamente");
    }
    @Test
    void findByIdProductoAndIdProducto() {
        System.out.println("test findByIdProductoAndIdProductoDetalle");
        Integer idTipoProducto = 1;
        Long idProducto = 1L;
        Integer first = 1;
        Integer max = 2;
        List<ProductoDetalle> resultadoEsperado = LIST_ProductoDetalle_TEST.stream().filter(pd ->pd.getProductoDetallePK().equals(new ProductoDetallePK(idTipoProducto,idProducto))).toList();

        List<ProductoDetalle> resultados;
        cut.em = mockEm;
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.findByIdProductoAndIdProducto(null,null,null,null);
        });

        // Configurar el comportamiento de EntityManager y TypedQuery
        Mockito.when(mockEm.createNamedQuery("ProductoDetalle.findByIdTipoProductoAndIdProducto", ProductoDetalle.class))
                .thenReturn(mockTp);
        Mockito.when(mockTp.setParameter("idTipoProducto", idTipoProducto)).thenReturn(mockTp);
        Mockito.when(mockTp.setParameter("idProducto", idProducto)).thenReturn(mockTp);
        Mockito.when(mockTp.setFirstResult(first)).thenReturn(mockTp);
        Mockito.when(mockTp.setMaxResults(max)).thenReturn(mockTp);
        Mockito.when(mockTp.getResultList()).thenReturn(resultadoEsperado);

        // Ejecutar el método a probar
        List<ProductoDetalle> resultado = cut.findByIdProductoAndIdProducto(idTipoProducto, idProducto, first, max);
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(resultadoEsperado.size(), resultado.size());

        // Forzar fallo al acceder al EntityManager
        Mockito.doThrow(IllegalStateException.class).when(cut2).getEntityManager();
        Assertions.assertThrows(IllegalStateException.class, () -> cut2.findByIdProductoAndIdProducto(idTipoProducto, idProducto,first, max));


//        Assertions.fail("fallo exitosamente");
    }

    @Test
    void countByIdProductoAndIdProducto() {
        System.out.println("test countByIdProductoAndIdProductoDetalle");
        Integer idTipoProducto = 1;
        Long idProducto = 1L;

        Long resultadoEsperado = 2L;

        cut.em= mockEm;
        //prueba fallo de argumentos
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.countByIdProductoAndIdProducto(null,null);
        });

        // Configurar el comportamiento de EntityManager y TypedQuery
        Mockito.when(mockEm.createNamedQuery("ProductoDetalle.countByIdTipoProductoAndIdProducto", Long.class))
                .thenReturn(mockTp);
        Mockito.when(mockTp.setParameter("idTipoProducto", idTipoProducto)).thenReturn(mockTp);
        Mockito.when(mockTp.setParameter("idProducto", idProducto)).thenReturn(mockTp);
        Mockito.when(mockTp.getSingleResult()).thenReturn(resultadoEsperado);


        //prueba normal
        Long resultado = cut.countByIdProductoAndIdProducto(idTipoProducto, idProducto);
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(resultado,resultadoEsperado);

        // Forzar fallo al acceder al EntityManager
        Mockito.doThrow(IllegalStateException.class).when(cut2).getEntityManager();
        Assertions.assertThrows(IllegalStateException.class, () -> cut2.countByIdProductoAndIdProducto(idTipoProducto, idProducto));

//        Assertions.fail("fallo exitosamente contar");
    }

    @Test
    void deleteByPk() {
        System.out.println("test deleteByPk");
        Integer idTipoProducto = 1;
        Long idProducto = 1L;
        ProductoDetalle registro = new ProductoDetalle();
        ProductoDetallePK pk = new ProductoDetallePK(idTipoProducto,idProducto);

        //error de argumento

        assertThrows(IllegalArgumentException.class, () -> {
            cut.deleteByPk(null);
        });

        //flujo normal
        cut.em= mockEm;
        Mockito.when(mockEm.createNamedQuery("ProductoDetalle.deleteByIdProductoAndIdProducto"))
                .thenReturn(mockTp);
        Mockito.when(mockTp.setParameter("idTipoProducto", pk.getIdTipoProducto())).thenReturn(mockTp);
        Mockito.when(mockTp.setParameter("idProducto", pk.getIdProducto())).thenReturn(mockTp);
        Mockito.when(mockTp.executeUpdate()).thenReturn(1);

        cut.deleteByPk(pk);

        //forzar error

        Mockito.doThrow(IllegalStateException.class).when(cut2).getEntityManager();
        Assertions.assertThrows(IllegalStateException.class, () -> cut2.deleteByPk(pk));
//                Assertions.fail("fallo exitosamente");
    }

    @Test
    void findAll() {
        System.out.println("test findAll");
        int first = 1;
        int max = 1;

        List<ProductoDetalle> esperado=this.LIST_ProductoDetalle_TEST;

        //error de argumentos
        cut.em= mockEm;
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findAll(null,null);
        });
        Mockito.when(mockEm.createNamedQuery("ProductoDetalle.findAll", ProductoDetalle.class)).thenReturn(mockTp);
        Mockito.when(mockTp.setFirstResult(first)).thenReturn(mockTp);
        Mockito.when(mockTp.setMaxResults(max)).thenReturn(mockTp);
        Mockito.when(mockTp.getResultList()).thenReturn(esperado);
        List<ProductoDetalle> resultado = cut.findAll(first,max);
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(resultado.size(), esperado.size());

        //forzar error
        Mockito.doThrow(IllegalStateException.class).when(cut2).getEntityManager();
        Assertions.assertThrows(IllegalStateException.class, () -> cut2.findAll(first,max));
//                Assertions.fail("fallo exitosamente");
    }
}
