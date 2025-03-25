package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetallePK;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductoBeanTest {
    List<Producto> LIST_Producto_TEST = Arrays.asList(new Producto[]{
            new Producto(1L),
            new Producto(2L),
            new Producto(3L),
            new Producto(4L),
            new Producto(5L),
            new Producto(6L),
    });

    ProductoBean cut; // Clase bajo prueba
    EntityManager mockEm;
    ProductoBean cut2;
    TypedQuery mockTp;

    @BeforeEach
    void setUp() {
        cut = new ProductoBean();
        mockEm = Mockito.mock(EntityManager.class);
        cut.em = mockEm;
        cut2 = Mockito.spy(new ProductoBean());
        mockTp = Mockito.mock(TypedQuery.class);
    }

    @Test
    void findRangeByIdTipoProductos() {
        System.out.println("test findRangeByIdTipoProductos");
        Integer idTipoProducto = 1;
        int first = 1;
        Integer max = 5;
        List<Producto> resultadoEsperado = LIST_Producto_TEST;

        List<Producto> resultados;
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.findRangeByIdTipoProductos(null,null,null);
        });

        // Configurar el comportamiento de EntityManager y TypedQuery
        Mockito.when(mockEm.createNamedQuery("Producto.findByIdTipoProducto", Producto.class))
                .thenReturn(mockTp);
        Mockito.when(mockTp.setParameter("idTipoProducto", idTipoProducto)).thenReturn(mockTp);
        Mockito.when(mockTp.setFirstResult(first)).thenReturn(mockTp);
        Mockito.when(mockTp.setMaxResults(max)).thenReturn(mockTp);
        Mockito.when(mockTp.getResultList()).thenReturn(resultadoEsperado);

        // Ejecutar el método a probar
        List<Producto> resultado = cut.findRangeByIdTipoProductos(idTipoProducto, first, max);
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(resultadoEsperado.size(), resultado.size());

        // Forzar fallo al acceder al EntityManager
        Mockito.doThrow(IllegalStateException.class).when(cut2).getEntityManager();
        Assertions.assertThrows(IllegalStateException.class, () -> cut2.findRangeByIdTipoProductos(idTipoProducto,first, max));


//        Assertions.fail("fallo exitosamente productos");
    }

    @Test
    void countByIdTipoProductos() {

        System.out.println("test countByIdProductoAndIdProducto");
        Integer idTipoProducto = 1;
        Long idProducto = 1L;

        Long resultadoEsperado = 2L;

        cut.em= mockEm;
        //prueba fallo de argumentos
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.countByIdTipoProductos(null);
        });

        // Configurar el comportamiento de EntityManager y TypedQuery

        Mockito.when(mockEm.createNamedQuery("Producto.countByidTipoProducto", Long.class))
                .thenReturn(mockTp);
        Mockito.when(mockTp.setParameter("idTipoProducto", idTipoProducto)).thenReturn(mockTp);
        Mockito.when(mockTp.getSingleResult()).thenReturn(resultadoEsperado);


        //prueba normal
        Long resultado = cut.countByIdTipoProductos(idTipoProducto);
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(resultado,resultadoEsperado);

        // Forzar fallo al acceder al EntityManager
        Mockito.doThrow(IllegalStateException.class).when(cut2).getEntityManager();
        Assertions.assertThrows(IllegalStateException.class, () -> cut2.countByIdTipoProductos(idTipoProducto));

//        Assertions.fail("fallo exitosamente contar");
    }

    @Test
    void createProducto() throws Exception {

        System.out.println("test createProducto");
        Integer idTipoProducto = 1;
        Long idProducto = 1L;

        Producto registro = new Producto();
        registro.setNombre("productoPrueba");
        registro.setActivo(true);

        ProductoDetalle productoDetalle =new ProductoDetalle();
        ProductoDetallePK pkESperadado = new ProductoDetallePK(idTipoProducto,idProducto);

        //fallo de argumentos
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.createProducto(null,null);
        });

        //flujo bueno
        cut.em= mockEm;
        Mockito.doNothing().when(mockEm).flush();
        Mockito.doAnswer(methodo->{
            registro.setIdProducto(idProducto);
            return null;
        }).when(mockEm).refresh(registro);
        Mockito.doAnswer(methodo->{
            ProductoDetallePK pk=new ProductoDetallePK(idTipoProducto,idProducto);
            productoDetalle.setProductoDetallePK(pk);
            return null;
        }).when(mockEm).persist(Mockito.any(ProductoDetalle.class));

        cut.createProducto(registro,idTipoProducto);
        assertEquals(idProducto, registro.getIdProducto());
        assertEquals(pkESperadado,productoDetalle.getProductoDetallePK());


        // Forzar fallo al acceder al EntityManager
        Mockito.doThrow(IllegalStateException.class).when(cut2).getEntityManager();
        Assertions.assertThrows(IllegalStateException.class, () -> cut2.createProducto(registro,idTipoProducto));


//        Assertions.fail("creacion fallo exitosamente");
    }

    @Test
    void orderParameterQuery() {
        System.out.println("test orderParameterQuery");
        String esperado = "idProducto";
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
}