package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.*;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetallePK;

import java.util.List;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductoDetalleBeanIT extends AbstractContainerTest {
    ProductoDetalleBean cut;

    Long totalEnScript = 4L;
    Integer idTipoProductoPrueba = 1001;
    Long idProductoPrueba = 1001L;

    Integer idTipoProductoCreado = 1003;
    Long idProductoCreado = 1001L;

    @BeforeEach
    void setUp() {
        cut = new ProductoDetalleBean();
    }


    @Order(1)
    @Test
    public void createProductoDetalle() {
        System.out.println("ProductoDetalle testIT create");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        ProductoDetallePK pk = new ProductoDetallePK(idTipoProductoCreado, idProductoCreado);
        ProductoDetalle creado = new ProductoDetalle();
        creado.setProductoDetallePK(pk);
        cut.em.getTransaction().begin();
        cut.create(creado, idTipoProductoCreado, idProductoCreado);
        cut.em.getTransaction().commit();
        totalEnScript++;

        //fallo de argumentos idTipoProducto nulo
        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.create(creado, null, idProductoCreado);
        });
        em.getTransaction().commit();

        //fallo de argumentos idProducto nulo
        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.create(creado, idTipoProductoPrueba, null);
        });
        em.getTransaction().commit();

        //fallo de argumentos registro nulo
        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.create(null, idTipoProductoPrueba, idProductoCreado);
        });
        em.getTransaction().commit();

        //registros inexistentente
        em.getTransaction().begin();
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            cut.create(creado, 112233, idProductoCreado);
        });
        em.getTransaction().commit();
        //registros inexistentente
        em.getTransaction().begin();
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            cut.create(creado, idTipoProductoCreado, 112233L);
        });
        em.getTransaction().commit();


        em.close();
//        Assertions.fail("fallo exitosamente");
    }

    @Order(2)
    @Test
    void findByIdProductoAndIdProducto() {
        System.out.println("ProductoDetalle testIT findByIdProductoAndIdProducto");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        //flujop normal
        cut.em.getTransaction().begin();
        ProductoDetalle respuesta = cut.findById(idTipoProductoPrueba, idProductoPrueba);
        cut.em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(respuesta.getProductoDetallePK().getIdTipoProducto(), idTipoProductoPrueba);
        Assertions.assertEquals(respuesta.getProductoDetallePK().getIdProducto(), idProductoPrueba);

        //fallo de argumentos idProducto nulo
        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.findById(idTipoProductoPrueba, null);
        });
        em.getTransaction().commit();

        //fallo de argumentos registro nulo
        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.findById(null, idProductoCreado);
        });
        em.getTransaction().commit();

        //registros inexistentente
        em.getTransaction().begin();
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            cut.findById(112233, idProductoCreado);
        });
        em.getTransaction().commit();
        //registros inexistentente
        em.getTransaction().begin();
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            cut.findById(idTipoProductoCreado, 112233L);
        });
        em.getTransaction().commit();

        em.close();

//        Assertions.fail("fallo exitosamente");
    }

    @Order(3)
    @Test
    public void findRange() {
        System.out.println("ProductoDetalle testIT find all");
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        cut.em.getTransaction().begin();
        List<ProductoDetalle> respuesta = cut.findRange(first, max);
        cut.em.getTransaction().commit();
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(totalEnScript, respuesta.size());


        em.close();
//        Assertions.fail("fallo exitosamente");
    }

    @Order(4)
    @Test
    public void update() {
        System.out.println("ProductoDetalle testIT update");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        String observacion = "observado desde test";
        ProductoDetalle actualizado = new ProductoDetalle(idTipoProductoPrueba, idProductoPrueba);
        actualizado.setObservaciones(observacion);
        //creamos
        cut.em.getTransaction().begin();
        cut.update(actualizado, idTipoProductoPrueba, idProductoPrueba);
        cut.em.getTransaction().commit();

        ProductoDetalle respuesta = cut.findById(idTipoProductoPrueba, idProductoPrueba);
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(observacion, respuesta.getObservaciones());


        //fallo de argumentos registro nulo
        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.update(null,idTipoProductoPrueba, idProductoCreado);
        });
        em.getTransaction().commit();
        //fallo de argumentos idProducto nulo
        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.update(actualizado,idTipoProductoPrueba, null);
        });
        em.getTransaction().commit();

        //fallo de argumentos registro nulo
        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.update(actualizado,null, idProductoCreado);
        });
        em.getTransaction().commit();

        //registros inexistentente
        em.getTransaction().begin();
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            cut.update(actualizado ,112233, idProductoCreado);
        });
        em.getTransaction().commit();
        //registros inexistentente
        em.getTransaction().begin();
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            cut.update(actualizado,idTipoProductoCreado, 112233L);
        });
        em.getTransaction().commit();

        em.close();

//        Assertions.fail("fallo exitosamente");
    }

    @Order(5)
    @Test
    public void delete() {
        System.out.println("ProductoDetalle testIT delete");
        ProductoDetalleBean cut = new ProductoDetalleBean();
        EntityManager em = emf.createEntityManager();

        cut.em = em;
        em.getTransaction().begin();
        // Ahora proceder a eliminarla
        cut.deleteByIdTipoProductoAndIdProducto(idTipoProductoCreado, idProductoCreado);
        em.getTransaction().commit();

        em.getTransaction().begin();
        Assertions.assertThrows(NoResultException.class, () -> {cut.findById(idTipoProductoCreado, idProductoCreado);});
        em.getTransaction().commit();
//        Assertions.fail("fallo exitosamente");


        //fallo de argumentos registro nulo
        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.deleteByIdTipoProductoAndIdProducto(null, idProductoCreado);
        });
        em.getTransaction().commit();
        //fallo de argumentos idProducto nulo
        em.getTransaction().begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cut.deleteByIdTipoProductoAndIdProducto(idTipoProductoPrueba, null);
        });
        em.getTransaction().commit();//fallo idTipoProducto malo
    }

}