package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.TipoProducto;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductoDetalleBeanTest {

    List<ProductoDetalle> LIST_ProductoDetalle_TEST = Arrays.asList(new ProductoDetalle[]{
            new ProductoDetalle(1, 1L),
            new ProductoDetalle(1, 1L),
            new ProductoDetalle(1, 2L),
            new ProductoDetalle(1, 3L),
            new ProductoDetalle(1, 4L),

            new ProductoDetalle(2, 5L),
            new ProductoDetalle(2, 5L),
            new ProductoDetalle(2, 6L),
            new ProductoDetalle(2, 6L),
            new ProductoDetalle(2, 7L),
    });

    ProductoDetalleBean cut;
    EntityManager mockEm;
    ProductoDetalleBean cut2;
    TypedQuery mockTp;
    TypedQuery mockTp2;
    Integer first = 0;
    Integer max = 10;

    @BeforeEach
    void setUp() {
        cut = new ProductoDetalleBean();
        mockEm = Mockito.mock(EntityManager.class);
        cut.em = mockEm;
        mockTp = Mockito.mock(TypedQuery.class);
        mockTp2 = Mockito.spy(TypedQuery.class);
        cut2 = Mockito.spy(ProductoDetalleBean.class);
    }


    @Test
    void orderParameterQuery() {
        System.out.println("test orderParameterQuery");
        String esperado = "idProductoDetalle";
        String respuesta = cut.orderParameterQuery();
        assertEquals(esperado, respuesta);

//        fail("fallo exitosamente");
    }


    @Test
    void getEntityManager() {
        System.out.println("test getEntityManager");
        Mockito.when(cut2.getEntityManager()).thenReturn(mockEm);

        EntityManager em = cut2.getEntityManager();
        assertEquals(em, mockEm);
//        fail("fallo exitosamente");
    }

    @Test
    void findById() {
        System.out.println("test findByIdProductoAndIdProductoDetalle");
        Integer idTipoProducto = 1;
        Long idProducto = 1L;

        ProductoDetalle resultadoEsperado = new ProductoDetalle(idTipoProducto, idProducto);
        cut.em = mockEm;

        //fallo por errores de argumentos
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.findById(null, 2L);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.findById(80, 0L);
        });


        // Configurar el comportamiento de EntityManager y TypedQuery
        Mockito.when(mockEm.createNamedQuery("ProductoDetalle.findByIdTipoProductoAndIdProducto", ProductoDetalle.class))
                .thenReturn(mockTp);
        Mockito.when(mockTp.setParameter("idTipoProducto", idTipoProducto)).thenReturn(mockTp);
        Mockito.when(mockTp.setParameter("idProducto", idProducto)).thenReturn(mockTp);
        Mockito.when(mockTp.getSingleResult()).thenReturn(resultadoEsperado);
        // Configurar el comportamiento de EntityManager y TypedQuery
        Mockito.when(mockEm.find(TipoProducto.class, idTipoProducto)).thenReturn(new TipoProducto());
        Mockito.when(mockEm.find(Producto.class, idProducto)).thenReturn(new Producto());
        //flujo normal
        ProductoDetalle resultado = cut.findById(idTipoProducto, idProducto);
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(resultadoEsperado, resultado);


        //no existe idProducto o idTipoPRODUCTO
        Mockito.when(mockEm.find(TipoProducto.class, 112233)).thenReturn(null);
        Mockito.when(mockEm.find(Producto.class, 112233L)).thenReturn(null);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            cut.findById(112233, idProducto);
        });

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            cut.findById(idTipoProducto, 112233L);
        });

        //suponiendo que no existe el detalle
        Mockito.when(mockTp.getSingleResult()).thenReturn(null);
        Assertions.assertThrows(EntityNotFoundException.class, () -> {cut.findById(idTipoProducto, idProducto);});



        //FALLO EN BASE DE DATOS
        Mockito.when(mockEm.createNamedQuery("ProductoDetalle.findByIdTipoProductoAndIdProducto", ProductoDetalle.class))
                .thenReturn(mockTp2);
        Mockito.when(mockTp2.setParameter("idTipoProducto", idTipoProducto)).thenReturn(mockTp2);
        Mockito.when(mockTp2.setParameter("idProducto", idProducto)).thenReturn(mockTp2);
        Mockito.doThrow(PersistenceException.class).when(mockTp2).getSingleResult();

        //PersistenceException
        Assertions.assertThrows(PersistenceException.class, () -> {
            cut.findById(idTipoProducto, idProducto);
        });
        //IllegalState
        Mockito.doThrow(IllegalStateException.class).when(mockTp2).getSingleResult();
        Assertions.assertThrows(IllegalStateException.class, () -> {
            cut.findById(idTipoProducto, idProducto);
        });


        //        Assertions.fail("fallo exitosamente");
    }

    @Test
    void deleteByPk() {
        System.out.println("test deleteByPk");
        Integer idTipoProducto = 1;
        Long idProducto = 1L;

        //error de argumento
        assertThrows(IllegalArgumentException.class, () -> {
            cut.deleteByIdTipoProductoAndIdProducto(null, idProducto);
        });
        //error de argumento
        assertThrows(IllegalArgumentException.class, () -> {
            cut.deleteByIdTipoProductoAndIdProducto(idTipoProducto, 0L);
        });

        //configuracion
        cut.em = mockEm;
        Mockito.when(mockEm.createNamedQuery("ProductoDetalle.deleteByIdProductoAndIdProducto"))
                .thenReturn(mockTp);
        Mockito.when(mockTp.setParameter("idTipoProducto", idTipoProducto)).thenReturn(mockTp);
        Mockito.when(mockTp.setParameter("idProducto", idProducto)).thenReturn(mockTp);
        Mockito.when(mockTp.executeUpdate()).thenReturn(1);

        Mockito.when(mockEm.find(TipoProducto.class, idTipoProducto)).thenReturn(new TipoProducto());
        Mockito.when(mockEm.find(Producto.class, idProducto)).thenReturn(new Producto());
        Mockito.when(mockEm.find(TipoProducto.class, 112233)).thenReturn(null);
        Mockito.when(mockEm.find(Producto.class, 112233L)).thenReturn(null);

        Assertions.assertDoesNotThrow(() -> {cut.deleteByIdTipoProductoAndIdProducto(idTipoProducto, idProducto);});

        //no existe producto o tipoProducto

        Assertions.assertThrows(EntityNotFoundException.class, () -> {cut.deleteByIdTipoProductoAndIdProducto(idTipoProducto, 112233L);});
        Assertions.assertThrows(EntityNotFoundException.class, () -> {cut.deleteByIdTipoProductoAndIdProducto(112233, idProducto);});

        //forzar error
        Mockito.when(mockEm.createNamedQuery("ProductoDetalle.deleteByIdProductoAndIdProducto"))
                .thenReturn(mockTp2);
        Mockito.when(mockTp2.setParameter("idTipoProducto", idTipoProducto)).thenReturn(mockTp2);
        Mockito.when(mockTp2.setParameter("idProducto", idProducto)).thenReturn(mockTp2);
        Mockito.when(mockTp2.executeUpdate()).thenReturn(1);
        //persistence
        Mockito.doThrow(PersistenceException.class).when(mockTp2).executeUpdate();
        Assertions.assertThrows(PersistenceException.class, () -> {
            cut.deleteByIdTipoProductoAndIdProducto(idTipoProducto, idProducto);
        });
        //IllegalState
        Mockito.doThrow(IllegalStateException.class).when(mockTp2).executeUpdate();
        Assertions.assertThrows(IllegalStateException.class, () -> {
            cut.deleteByIdTipoProductoAndIdProducto(idTipoProducto, idProducto);
        });
//                Assertions.fail("fallo exitosamente");
    }

    @Test
    void findRange() {
        System.out.println("test findAll");
        int first = 1;
        int max = 1;

        List<ProductoDetalle> esperado = this.LIST_ProductoDetalle_TEST;

        //error de argumentos
        cut.em = mockEm;
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findRange(null, null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findRange(0, 0);
        });
        Mockito.when(mockEm.createNamedQuery("ProductoDetalle.findAll", ProductoDetalle.class)).thenReturn(mockTp);
        Mockito.when(mockTp.setFirstResult(first)).thenReturn(mockTp);
        Mockito.when(mockTp.setMaxResults(max)).thenReturn(mockTp);
        Mockito.when(mockTp.getResultList()).thenReturn(esperado);
        List<ProductoDetalle> resultado = cut.findRange(first, max);
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(resultado.size(), esperado.size());

        //forzar error
        Mockito.when(mockEm.createNamedQuery("ProductoDetalle.findAll", ProductoDetalle.class)).thenReturn(mockTp2);
        Mockito.when(mockTp2.setFirstResult(first)).thenReturn(mockTp2);
        Mockito.when(mockTp2.setMaxResults(max)).thenReturn(mockTp2);
        Mockito.doThrow(PersistenceException.class).when(mockTp2).getResultList();
        Assertions.assertThrows(PersistenceException.class, () -> {cut.findRange(first, max);});
//                Assertions.fail("fallo exitosamente");

    }

}
