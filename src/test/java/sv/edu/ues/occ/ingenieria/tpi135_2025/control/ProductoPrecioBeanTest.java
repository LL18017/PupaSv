package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoPrecio;

import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductoPrecioBeanTest {
    List<ProductoPrecio> LIST_Producto_TEST = Arrays.asList(new ProductoPrecio[]{
            new ProductoPrecio(1L),
            new ProductoPrecio(2L),
            new ProductoPrecio(3L),
            new ProductoPrecio(4L),
            new ProductoPrecio(5L),
            new ProductoPrecio(6L),
    });

    ProductoPrecioBean cut; // Clase bajo prueba
    EntityManager mockEm;
    ProductoPrecioBean cut2;
    TypedQuery mockTp;
    TypedQuery mockTp2;

    @BeforeEach
    void setUp() {
        cut = new ProductoPrecioBean();
        mockEm = Mockito.mock(EntityManager.class);
        cut.em = mockEm;
        mockTp = Mockito.mock(TypedQuery.class);
        cut2 = Mockito.spy(new ProductoPrecioBean());
        mockTp2 = Mockito.spy(TypedQuery.class);
    }

    @Test
    void orderParameterQuery() {
        System.out.println("test orderParameterQuery");
        String esperado = "idProductoPrecio";
        String respuesta = cut.orderParameterQuery();
        assertEquals(esperado,respuesta);

////        fail("fallo exitosamente");
    }

    @Test
    void getEntityManager() {
        System.out.println("test getEntityManager");
        Mockito.when(cut2.getEntityManager()).thenReturn(mockEm);

        EntityManager em = cut2.getEntityManager();
        assertEquals(em,mockEm);
////        fail("fallo exitosamente");
    }

    @Test
    void testSetEntityManager() {
        System.out.println("test setEntityManager");
        ProductoPrecioBean bean = new ProductoPrecioBean();
        EntityManager emMock = Mockito.mock(EntityManager.class);
        bean.setEntityManager(emMock);
        assertEquals(emMock, bean.getEntityManager());
        //fail("Esta prueba no pasa quemado");
    }




    @Test
    void findByIdProducto() {
        System.out.println("test findByIdProducto");
        Long idProducto = 1001L;//ya establecido en DB
        int first = 0;
        int max = 10;
        Mockito.when(mockEm.createNamedQuery("ProductoPrecio.findByIdTipoProductoAndIdProducto",ProductoPrecio.class)).thenReturn(mockTp);
        Mockito.when(mockTp.setParameter("idProducto", idProducto)).thenReturn(mockTp);
        Mockito.when(mockTp.getSingleResult()).thenReturn(this.LIST_Producto_TEST.get(0));
        ProductoPrecio resultados = cut.findByIdProducto(idProducto);
        assertNotNull(resultados);

        //registro nulo
        assertThrows(IllegalArgumentException.class,()->{
            cut.findByIdProducto(null);
        });

        //fallo de entity
        cut2.em = mockEm;
        Mockito.when(mockEm.createNamedQuery("ProductoPrecio.findByIdTipoProductoAndIdProducto",ProductoPrecio.class)).thenReturn(mockTp2);
        Mockito.doThrow(IllegalStateException.class).when(mockTp2).setParameter("idProducto",idProducto);
        Assertions.assertThrows(IllegalStateException.class, () -> cut2.findByIdProducto(idProducto));


    }

    @Test
    void countByIdProducto() {
        System.out.println("test countByIdProducto");
        Long idProducto = 1001L;
        Long esperado=2L;

        Mockito.when(mockEm.createNamedQuery("ProductoPrecio.countByIdTipoProductoAndIdProducto",Long.class)).thenReturn(mockTp);
        Mockito.when(mockTp.setParameter("idProducto", idProducto)).thenReturn(mockTp);
        Mockito.when(mockTp.getSingleResult()).thenReturn(esperado);
        Long resultado = cut.countByIdProducto(idProducto);
        assertEquals(esperado,resultado);

        //REGISTRO NULL
        assertThrows(IllegalArgumentException.class,()->{
            cut.countByIdProducto(null);
        });

        //fallo de entity
        cut2.em = mockEm;
        Mockito.when(mockEm.createNamedQuery("ProductoPrecio.countByIdTipoProductoAndIdProducto",Long.class)).thenReturn(mockTp2);
        Mockito.doThrow(IllegalStateException.class).when(mockTp2).setParameter("idProducto",idProducto);
        Assertions.assertThrows(IllegalStateException.class, () -> cut2.countByIdProducto(idProducto));
////        fail("fallo exitosamenet");
    }

    @Test
    void tetsCreate(){
        System.out.println("test create");
        // Caso 1: El idProducto es null
        ProductoPrecio registro = new ProductoPrecio();
        assertThrows(IllegalArgumentException.class, () -> cut.create(registro, null));

        // Caso 2: El idProducto es menor que 0
        assertThrows(IllegalArgumentException.class, () -> cut.create(registro, -1L));

        // Caso 3: El producto no existe (em.find devuelve null)
        Long idProductoInexistente = 999L;
        when(mockEm.find(Producto.class, idProductoInexistente)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> cut.create(registro, idProductoInexistente));

        // Caso 4: El producto existe y la creación se realiza correctamente
        Long idProductoValido = 1L;
        ProductoPrecio registroValido = new ProductoPrecio();
        Producto productoExistente = new Producto(idProductoValido);
        when(mockEm.find(Producto.class, idProductoValido)).thenReturn(productoExistente);

        ProductoPrecioBean spyCut = spy(cut);
        doNothing().when(spyCut).create(registroValido);  // Especificamos que no haga nada cuando se invoque create.

        spyCut.create(registroValido, idProductoValido);
        verify(spyCut, times(1)).create(registroValido);

        // Caso 5: Se lanza una excepción inesperada (cualquier otra excepción en el bloque try)
        when(mockEm.find(Producto.class, idProductoValido)).thenThrow(new RuntimeException("Error inesperado"));
        assertThrows(RuntimeException.class, () -> cut.create(registroValido, idProductoValido));
        //fail("Esta prueba no pasa quemado");
    }


}