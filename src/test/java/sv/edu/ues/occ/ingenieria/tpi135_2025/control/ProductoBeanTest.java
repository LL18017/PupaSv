package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.*;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    TypedQuery mockTp2;

    @BeforeEach
    void setUp() {
        cut = new ProductoBean();
        mockEm = Mockito.mock(EntityManager.class);
        cut.em = mockEm;
        mockTp = Mockito.mock(TypedQuery.class);
        cut2 = Mockito.spy(new ProductoBean());
        mockTp2 = Mockito.spy(TypedQuery.class);
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

    @Test
    void delete() {
        System.out.println("test delete");
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        CriteriaBuilder mockCb = Mockito.mock(CriteriaBuilder.class);
        CriteriaDelete<Producto> mockCd = Mockito.mock(CriteriaDelete.class);
        Root<Producto> mockR = Mockito.mock(Root.class);
        TypedQuery<Producto> mockTq = Mockito.mock(TypedQuery.class);
        Object idProductoEliminado = LIST_Producto_TEST.get(0).getIdProducto(); // Asegúrate de que LIST_Producto_TEST tenga elementos

        // Test para entidda inexistentes
        Assertions.assertThrows(IllegalStateException.class, () -> cut.delete(12345698));

        // Test para registro nulo
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.delete(null));

        // Flujo normal

        cut.em = mockEm;

        Mockito.when(mockEm.createNamedQuery("ProductoDetalle.deleteByIdProducto")).thenReturn(mockTq);
        Mockito.when(mockTq.setParameter("idProducto",idProductoEliminado)).thenReturn(mockTq);
        Mockito.when(mockTq.executeUpdate()).thenReturn(1);

        Mockito.when(mockEm.find(Producto.class, idProductoEliminado)).thenReturn(this.LIST_Producto_TEST.get(0));
        Mockito.when(mockEm.getCriteriaBuilder()).thenReturn(mockCb);
        Mockito.when(mockCb.createCriteriaDelete(Producto.class)).thenReturn(mockCd);
        Mockito.when(mockCd.from(Producto.class)).thenReturn(mockR);
        Mockito.when(mockEm.createQuery(mockCd)).thenReturn(mockTq);
        // Aseguramos que executeUpdate no haga nada (se ejecute sin lanzar excepciones)
        Mockito.when(mockTq.executeUpdate()).thenReturn(1);

        // Llamamos al método delete y verificamos que no se lance ninguna excepción
        try {
            cut.delete(idProductoEliminado); // Llamamos al método delete con el mock
        } catch (Exception e) {
            Assertions.fail("No se esperaba una excepción: " + e.getMessage());
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }

        // Si llegamos aquí, el test ha pasado, ya que no se lanzó ninguna excepción
//        fail("fallo exitosamente");
    }

    @Test
    void findRangeProductoActivos() {
        System.out.println("test countRangeProductoActivosAndIdTipoProducto");
        int first = 0;
        int max = 2;
        int idTipoProducto = 2;
        List<Producto> esperado = LIST_Producto_TEST.subList(first,max);

        //FALLO DE ARGUMENTOS
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.findRangeProductoActivosByIdTipoProducto(idTipoProducto,null,null);
        });

        //flujo normal
        cut.em = mockEm;
        Mockito.when(mockEm.createNamedQuery("Producto.findActivosAndIdTipoProducto",Producto.class)).thenReturn(mockTp);
        Mockito.when(mockTp.setFirstResult(first)).thenReturn(mockTp);
        Mockito.when(mockTp.setMaxResults(max)).thenReturn(mockTp);
        Mockito.when(mockTp.setParameter("idTipoProducto",idTipoProducto)).thenReturn(mockTp);
        Mockito.when(mockTp.getResultList()).thenReturn(esperado);

        List<Producto> resultado = cut.findRangeProductoActivosByIdTipoProducto(idTipoProducto,first,max);
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(esperado.size(), resultado.size());

        //fallo de jecucion

        // Forzar fallo al acceder al EntityManager
        ;
        cut2.em = mockEm;
        Mockito.when(mockEm.createNamedQuery("Producto.findActivosAndIdTipoProducto",Producto.class)).thenReturn(mockTp2);
        Mockito.doThrow(IllegalStateException.class).when(mockTp2).setParameter("idTipoProducto",idTipoProducto);
        Assertions.assertThrows(IllegalStateException.class, () -> cut2.findRangeProductoActivosByIdTipoProducto(idTipoProducto,first,max));

//        fail("fallo exitosamente");

    }
    @Test
    void countRangeProductoActivos() {
        System.out.println("test countRangeProductoActivosAndIdTipoProducto");

        int idTipoProducto = 2;
        Long esperado =1L;

        //FALLO DE ARGUMENTOS
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.findRangeProductoActivosByIdTipoProducto(idTipoProducto,null,null);
        });

        //flujo normal
        cut.em = mockEm;
        Mockito.when(mockEm.createNamedQuery("Producto.countActivosAndIdTipoProducto",Long.class)).thenReturn(mockTp);
        Mockito.when(mockTp.setParameter("idTipoProducto",idTipoProducto)).thenReturn(mockTp);
        Mockito.when(mockTp.getSingleResult()).thenReturn(esperado);

        Long resultado = cut.countProductoActivosByIdTipoProducto(idTipoProducto);
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(esperado, resultado);

        //fallo de jecucion

        // Forzar fallo al acceder al EntityManager
        ;
        cut2.em = mockEm;
        Mockito.when(mockEm.createNamedQuery("Producto.countActivosAndIdTipoProducto",Long.class)).thenReturn(mockTp2);
        Mockito.doThrow(IllegalStateException.class).when(mockTp2).setParameter("idTipoProducto",idTipoProducto);
        Assertions.assertThrows(IllegalStateException.class, () -> cut2.countProductoActivosByIdTipoProducto(idTipoProducto));

//        fail("fallo exitosamente");

    }

}
