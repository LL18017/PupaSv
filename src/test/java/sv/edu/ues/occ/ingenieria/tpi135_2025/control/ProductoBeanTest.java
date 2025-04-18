package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.*;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;

import java.security.InvalidParameterException;
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
    EntityManager mockEm2;
    ProductoBean cut2;
    TypedQuery mockTq;
    TypedQuery mockTq2;
    Integer first = 0;
    Integer max = 10;

    @BeforeEach
    void setUp() {
        cut = new ProductoBean();
        mockEm = Mockito.mock(EntityManager.class);
        mockEm2 = Mockito.spy(EntityManager.class);
        cut.em = mockEm;
        mockTq = Mockito.mock(TypedQuery.class);
        cut2 = Mockito.spy(new ProductoBean());
        mockTq2 = Mockito.spy(TypedQuery.class);
    }

    @Test
    void findRangeByIdTipoProductosAndActivo() {
        System.out.println("Producto test findRangeByIdTipoProductosAndActivo");
        Integer idTipoProducto = 1;
        boolean activo = true;
        List<Producto> resultadoEsperado = LIST_Producto_TEST;

        //error de argumentos idTipoProdcuto
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.findRangeByIdTipoProductosAndActivo(null, false, first, max);
        });
        //error de argumentos first
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.findRangeByIdTipoProductosAndActivo(idTipoProducto, false, -90, max);
        });
        //error de argumentos max
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.findRangeByIdTipoProductosAndActivo(idTipoProducto, false, first, 0);
        });

        // Configurar el comportamiento de EntityManager y TypedQuery
        Mockito.when(mockEm.find(TipoProducto.class, idTipoProducto)).thenReturn(new TipoProducto());
        Mockito.when(mockEm.createNamedQuery("Producto.findActivosAndIdTipoProducto", Producto.class))
                .thenReturn(mockTq);
        Mockito.when(mockTq.setParameter("idTipoProducto", idTipoProducto)).thenReturn(mockTq);
        Mockito.when(mockTq.setParameter("activo", activo)).thenReturn(mockTq);
        Mockito.when(mockTq.setFirstResult(first)).thenReturn(mockTq);
        Mockito.when(mockTq.setMaxResults(max)).thenReturn(mockTq);
        Mockito.when(mockTq.getResultList()).thenReturn(resultadoEsperado);

        // Ejecutar el método a probar
        List<Producto> resultado = cut.findRangeByIdTipoProductosAndActivo(idTipoProducto, activo, first, max);
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(resultadoEsperado.size(), resultado.size());

        //id Inexistente
        Mockito.when(mockEm.find(TipoProducto.class, 112233)).thenReturn(null);
        Assertions.assertThrows(EntityNotFoundException.class, () -> {cut.findRangeByIdTipoProductosAndActivo(112233, activo, first, max);});

        // Configurar el comportamiento de EntityManager y TypedQuery
        Mockito.when(mockEm.createNamedQuery("Producto.findActivosAndIdTipoProducto", Producto.class))
                .thenReturn(mockTq2);
        Mockito.when(mockTq2.setParameter("idTipoProducto", idTipoProducto)).thenReturn(mockTq2);
        Mockito.when(mockTq2.setParameter("activo", activo)).thenReturn(mockTq2);
        Mockito.when(mockTq2.setFirstResult(first)).thenReturn(mockTq2);
        Mockito.when(mockTq2.setMaxResults(max)).thenReturn(mockTq2);

        // Forzar EntityNotFoundException
        Mockito.doThrow(EntityNotFoundException.class).when(mockTq2).getResultList();
        Assertions.assertThrows(EntityNotFoundException.class, () -> cut.findRangeByIdTipoProductosAndActivo(idTipoProducto, activo, first, max));
        // Forzar PersistenceException
        Mockito.doThrow(PersistenceException.class).when(mockTq2).getResultList();
        Assertions.assertThrows(PersistenceException.class, () -> cut.findRangeByIdTipoProductosAndActivo(idTipoProducto, activo, first, max));

//        Assertions.fail("fallo exitosamente productos");
    }

    @Test
    void countByIdTipoProductosAndActivo() {

        System.out.println("Producto test countByIdTipoProductosAndActivo");
        Integer idTipoProducto = 1001;
        Long resultadoEsperado = 2L;
        boolean activo = true;

        cut.em = mockEm;
        //prueba fallo de argumentos
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.countByIdTipoProductosAndActivo(null, activo);
        });
        //configurra comportamiento para buscar por id
        Mockito.when(mockEm.find(TipoProducto.class, idTipoProducto)).thenReturn(null);
        //fallo por id inexistenete
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            cut.countByIdTipoProductosAndActivo(112233, activo);
        });
        // Configurar el comportamiento de EntityManager y TypedQuery para flujo normal
        Mockito.when(mockEm.find(TipoProducto.class, idTipoProducto)).thenReturn(new TipoProducto(idTipoProducto));
        Mockito.when(mockEm.createNamedQuery("Producto.countActivosAndIdTipoProducto", Long.class))
                .thenReturn(mockTq);
        Mockito.when(mockTq.setParameter("idTipoProducto", idTipoProducto)).thenReturn(mockTq);
        Mockito.when(mockTq.setParameter("activo", activo)).thenReturn(mockTq);
        Mockito.when(mockTq.getSingleResult()).thenReturn(resultadoEsperado);
        Long resultado = cut.countByIdTipoProductosAndActivo(idTipoProducto, activo);
        //prueba normal
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(resultadoEsperado, resultado);


        //preparando para tirra excepciones
        Mockito.when(mockEm.createNamedQuery("Producto.countActivosAndIdTipoProducto", Long.class))
                .thenReturn(mockTq2);
        Mockito.when(mockTq2.setParameter("idTipoProducto", idTipoProducto)).thenReturn(mockTq2);
        Mockito.when(mockTq2.setParameter("activo", activo)).thenReturn(mockTq2);


        // Forzar fallo al acceder al PersistenceException
        Mockito.doThrow(PersistenceException.class).when(mockTq2).getSingleResult();
        Assertions.assertThrows(PersistenceException.class, () -> cut.countByIdTipoProductosAndActivo(idTipoProducto, activo));

        // Forzar fallo al acceder al NonUniqueResultException
        Mockito.doThrow(NonUniqueResultException.class).when(mockTq2).getSingleResult();
        Assertions.assertThrows(NonUniqueResultException.class, () -> cut.countByIdTipoProductosAndActivo(idTipoProducto, activo));

//        Assertions.fail("fallo exitosamente contar");
    }

    @Test
    void createProductoAndDetail() throws Exception {

        System.out.println("Producto test createProductoAndDetail");
        Integer idTipoProducto = 1;
        Long idProducto = 1L;

        Producto registro = new Producto();
        registro.setNombre("productoPrueba");
        registro.setActivo(true);

        ProductoDetalle productoDetalle = new ProductoDetalle();
        ProductoDetallePK pkESperadado = new ProductoDetallePK(idTipoProducto, idProducto);
        cut.em = mockEm;

        //fallo de argumentos
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.createProductoAndDetail(null, idTipoProducto);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.createProductoAndDetail(registro,-60);
        });
        //fallo id inexistente
        Mockito.when(mockEm.find(TipoProducto.class, 112233)).thenReturn(null);
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            cut.createProductoAndDetail(registro, 112233);
        });

        //flujo bueno
        Mockito.when(mockEm.find(TipoProducto.class, idTipoProducto)).thenReturn(new TipoProducto(idTipoProducto));
        Mockito.doNothing().when(mockEm).persist(registro);
        Mockito.doNothing().when(mockEm).flush();

        Mockito.doAnswer(methodo -> {
            registro.setIdProducto(idProducto);
            return null;
        }).when(mockEm).refresh(registro);
        Mockito.doAnswer(methodo -> {
            ProductoDetallePK pk = new ProductoDetallePK(idTipoProducto, idProducto);
            productoDetalle.setProductoDetallePK(pk);
            return null;
        }).when(mockEm).persist(Mockito.any(ProductoDetalle.class));
        cut.createProductoAndDetail(registro, idTipoProducto);
        assertEquals(idProducto, registro.getIdProducto());
        assertEquals(pkESperadado, productoDetalle.getProductoDetallePK());


        // Forzar fallo EntityExistsException
        cut.em = mockEm2;
        Mockito.when(mockEm2.find(TipoProducto.class, idTipoProducto)).thenReturn(new TipoProducto(idTipoProducto));
        Mockito.doThrow(EntityExistsException.class).when(mockEm2).persist(registro);
        Assertions.assertThrows(EntityExistsException.class, () -> cut.createProductoAndDetail(registro, idTipoProducto));


        // Forzar fallo PersistenceException
        Mockito.reset(mockEm2);
        Mockito.when(mockEm2.find(TipoProducto.class, idTipoProducto)).thenReturn(new TipoProducto(idTipoProducto));
        Mockito.doThrow(PersistenceException.class).when(mockEm2).persist(registro);
        Assertions.assertThrows(PersistenceException.class, () -> cut.createProductoAndDetail(registro, idTipoProducto));

//        Assertions.fail("creacion fallo exitosamente");
    }

    @Test
    void orderParameterQuery() {
        System.out.println("Producto test orderParameterQuery");
        String esperado = "idProducto";
        String respuesta = cut.orderParameterQuery();
        assertEquals(esperado, respuesta);

//        fail("fallo exitosamente");
    }


    @Test
    void getEntityManager() {
        System.out.println("Producto test getEntityManager");
        Mockito.when(cut2.getEntityManager()).thenReturn(mockEm);

        EntityManager em = cut2.getEntityManager();
        assertEquals(em, mockEm);
//        fail("fallo exitosamente");
    }

    @Test
    void deleteProductoAndDetail() {
        System.out.println("Producto test deleteProductoAndDetail");
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        CriteriaBuilder mockCb = Mockito.mock(CriteriaBuilder.class);
        CriteriaDelete<Producto> mockCd = Mockito.mock(CriteriaDelete.class);
        Root<Producto> mockR = Mockito.mock(Root.class);
        TypedQuery<Producto> mockTq = Mockito.mock(TypedQuery.class);
        Long idProductoEliminado = LIST_Producto_TEST.get(0).getIdProducto();
        Integer idTipoProdcuto=1;

        // Test para idProducto nulo
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.deleteProductoAndDetail(null,idTipoProdcuto));
        // Test para idTipoProducto nulo
        Assertions.assertThrows(IllegalArgumentException.class, () -> cut.deleteProductoAndDetail(idProductoEliminado,null));
        // Test para entidda inexistentes
        cut.em = mockEm;
        Mockito.when(mockEm.createNamedQuery("ProductoDetalle.deleteByIdProductoAndIdProducto")).thenReturn(mockTq);
        Mockito.when(mockTq.setParameter("idProducto", 112233L)).thenReturn(mockTq);
        Mockito.when(mockTq.setParameter("idTipoProducto", idTipoProdcuto)).thenReturn(mockTq);
        Mockito.when(mockTq.executeUpdate()).thenReturn(0);

        Assertions.assertThrows(EntityNotFoundException.class, () -> cut.deleteProductoAndDetail(112233L,idTipoProdcuto));

        // Flujo normal

        cut.em = mockEm;
        Mockito.when(mockTq.setParameter("idProducto", idProductoEliminado)).thenReturn(mockTq);
        Mockito.when(mockTq.setParameter("idTipoProducto", idTipoProdcuto)).thenReturn(mockTq);
        Mockito.when(mockTq.executeUpdate()).thenReturn(1);

        Mockito.when(mockEm.find(Producto.class, idProductoEliminado)).thenReturn(this.LIST_Producto_TEST.get(0));
        Mockito.when(mockEm.getCriteriaBuilder()).thenReturn(mockCb);
        Mockito.when(mockCb.createCriteriaDelete(Producto.class)).thenReturn(mockCd);
        Mockito.when(mockCd.from(Producto.class)).thenReturn(mockR);
        Mockito.when(mockEm.createQuery(mockCd)).thenReturn(mockTq);
        // Aseguramos que executeUpdate no haga nada (se ejecute sin lanzar excepciones)
        Mockito.when(mockTq.executeUpdate()).thenReturn(1);
        cut.deleteProductoAndDetail(idProductoEliminado,idTipoProdcuto);
        Assertions.assertDoesNotThrow(() -> cut.deleteProductoAndDetail(idProductoEliminado,idTipoProdcuto));

        //PersistenceException
        Mockito.when(mockEm.createNamedQuery("ProductoDetalle.deleteByIdProductoAndIdProducto")).thenReturn(mockTq2);
        Mockito.when(mockTq2.setParameter("idProducto", idProductoEliminado)).thenReturn(mockTq2);
        Mockito.when(mockTq2.setParameter("idTipoProducto", idTipoProdcuto)).thenReturn(mockTq2);

        Mockito.doThrow(PersistenceException.class).when(mockTq2).executeUpdate();
        Assertions.assertThrows(PersistenceException.class, () -> cut.deleteProductoAndDetail(idProductoEliminado,idTipoProdcuto));
//        fail("fallo exitosamente");
    }

    @Test
    void findRangeProductoActivos() {
        System.out.println("Producto test findRangeProductoActivos");
        int first = 0;
        int max = 2;
        int idTipoProducto = 2;
        boolean activo = true;
        List<Producto> esperado = LIST_Producto_TEST.subList(first, max);

        //FALLO DE ARGUMENTOS
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.findRangeProductoActivos(null, null, activo);
        });

        //flujo normal
        cut.em = mockEm;
        Mockito.when(mockEm.createNamedQuery("Producto.findByAnyActivo", Producto.class)).thenReturn(mockTq);

        Mockito.when(mockTq.setParameter("activo", activo)).thenReturn(mockTq);
        Mockito.when(mockTq.setFirstResult(first)).thenReturn(mockTq);
        Mockito.when(mockTq.setMaxResults(max)).thenReturn(mockTq);
        Mockito.when(mockTq.getResultList()).thenReturn(esperado);

        List<Producto> resultado = cut.findRangeProductoActivos(first, max, activo);
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(esperado.size(), resultado.size());

        // Forzar fallo al acceder al EntityManager
        Mockito.when(mockEm.createNamedQuery("Producto.findByAnyActivo", Producto.class)).thenReturn(mockTq2);
        Mockito.when(mockTq2.setParameter("activo", activo)).thenReturn(mockTq2);
        Mockito.when(mockTq2.setFirstResult(first)).thenReturn(mockTq2);
        Mockito.when(mockTq2.setMaxResults(max)).thenReturn(mockTq2);

        //PersistenceException
        Mockito.doThrow(PersistenceException.class).when(mockTq2).getResultList();
        Assertions.assertThrows(PersistenceException.class, () -> cut.findRangeProductoActivos(first, max, activo));

//        fail("fallo exitosamente");

    }

    @Test
    void countProductoActivos() {
        System.out.println("Producto test countProductoActivos");
        Long esperado = 1L;
        boolean activo = true;

        //flujo normal
        cut.em = mockEm;
        Mockito.when(mockEm.createNamedQuery("Producto.countByAnyActivo", Long.class)).thenReturn(mockTq);
        Mockito.when(mockTq.setParameter("activo", activo)).thenReturn(mockTq);
        Mockito.when(mockTq.getSingleResult()).thenReturn(esperado);

        Long resultado = cut.countProductoActivos(true);
        Mockito.when(mockTq.setParameter("activo", activo)).thenReturn(mockTq);
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(esperado, resultado);

        // Forzar fallo al acceder al EntityManager

        Mockito.when(mockEm.createNamedQuery("Producto.countByAnyActivo", Long.class)).thenReturn(mockTq2);
        Mockito.when(mockTq2.setParameter("activo", activo)).thenReturn(mockTq2);
        //PersistenceException
        Mockito.doThrow(PersistenceException.class).when(mockTq2).getSingleResult();

        Assertions.assertThrows(PersistenceException.class, () -> cut.countProductoActivos(activo));
        //NonUniqueResultException
        Mockito.doThrow(NonUniqueResultException.class).when(mockTq2).getSingleResult();
        Assertions.assertThrows(NonUniqueResultException.class, () -> cut.countProductoActivos(activo));


//        fail("fallo exitosamente");

    }

}
