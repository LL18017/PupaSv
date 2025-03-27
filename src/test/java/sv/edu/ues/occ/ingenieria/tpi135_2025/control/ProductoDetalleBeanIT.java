package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetallePK;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductoDetalleBeanIT {
    ProductoDetalle registro;
    List<ProductoDetalle> LISTA_ProductoDetalle = new ArrayList<>();
    EntityManagerFactory emf;
    @Container
    static GenericContainer postgres = new PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("TipicoSVtest")
            .withPassword("abc123")
            .withUsername("postgres")
            .withInitScript("tipicos_tpi135_2025.sql")
            .withExposedPorts(5432)
            .withNetworkAliases("db");

    ProductoDetalleBean cut;
    EntityManager mockEm;
    ProductoDetalleBean cut2;
    @BeforeEach
    void setUp() {
        cut = new ProductoDetalleBean();
        mockEm = Mockito.mock(EntityManager.class);
        cut2 = Mockito.spy(new ProductoDetalleBean());
    }

    @BeforeAll
    public void inicializar() {
        HashMap<String, Object> propiedades = new HashMap<>();
        propiedades.put("jakarta.persistence.jdbc.url", String.format("jdbc:postgresql://localhost:%d/TipicoSVtest", postgres.getMappedPort(5432)));
        emf = Persistence.createEntityManagerFactory("Test-PupaSV-PU", propiedades);

    }

    @Order(1)
    @Test
    public void createProductoDetalle() {
        System.out.println("ProductoDetalle testIT create");
        EntityManager em = emf.createEntityManager();
        Integer idTipoProducto=1002;
        Long idProducto=1004L;

        ProductoDetallePK pk = new ProductoDetallePK(idTipoProducto, idProducto);
        cut.em = em;
        ProductoDetalle creado = new ProductoDetalle();
        creado.setProductoDetallePK(pk);
        try {
            //creamos
            cut.em.getTransaction().begin();
            cut.create(creado);
            cut.em.getTransaction().commit();
            this.registro=creado;
            System.out.println(creado.toString());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Rollback en caso de error
            }
            Assertions.fail("Excepción inesperada: " + e.getMessage());
        } finally {
            em.close();
        }
//        Assertions.fail("fallo exitosamente");
    }

    @Order(2)
    @Test
    void findByIdProductoAndIdProducto() {
        System.out.println("ProductoDetalle testIT findByIdProductoAndIdProducto");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        int first = 0;
        int max = 2;
        //ya existente en el script
        Integer idTipoProducto=1001;
        Long idProducto=1001L;

        try {
            cut.em.getTransaction().begin();
            ProductoDetalle respuesta = cut.findById(idTipoProducto,idProducto);
            cut.em.getTransaction().commit();
            Assertions.assertNotNull(respuesta);
            Assertions.assertEquals(respuesta.getProductoDetallePK().getIdTipoProducto(), idTipoProducto);
            Assertions.assertEquals(respuesta.getProductoDetallePK().getIdProducto(), idProducto);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Rollback en caso de error
            }
            Assertions.fail("Excepción inesperada: " + e);
        } finally {
            em.close();
        }
//        Assertions.fail("fallo exitosamente");
    }

    @Order(3)
    @Test
    public void findAll() {
        System.out.println("ProductoDetalle testIT fiand all");
        EntityManager em = emf.createEntityManager();
        Integer cantidad_esperada = 4;//3 en script + el creado en createProductoDetalle
        cut.em = em;

        try {
            cut.em.getTransaction().begin();
            List<ProductoDetalle> respuesta = cut.findAll(0, cantidad_esperada);
            cut.em.getTransaction().commit();
            Assertions.assertNotNull(respuesta);
//            Assertions.assertEquals(cantidad_esperada, respuesta.size());
//            Assertions.assertTrue(respuesta.contains(registro));
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Rollback en caso de error
            }
            Assertions.fail("Excepción inesperada: " + e);
        } finally {
            em.close();
        }
//        Assertions.fail("fallo exitosamente");
    }
    @Order(4)
    @Test
    public void update() {
        System.out.println("ProductoDetalle testIT update");
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Long idProducto = 1004L;
        Integer idTipoProducto = 1002;
        String observacion = "observado desde test";
        ProductoDetalle actualizado = new ProductoDetalle(idTipoProducto, idProducto);
        actualizado.setObservaciones(observacion);
        //creamos
        try {
            cut.em.getTransaction().begin();
            cut.update(actualizado);
            cut.em.getTransaction().commit();

            ProductoDetalle respuesta = cut.findById(idTipoProducto,idProducto);
            Assertions.assertNotNull(respuesta);
            Assertions.assertEquals(observacion, respuesta.getObservaciones());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Rollback en caso de error
            }
            Assertions.fail("Excepción inesperada: " + e.getMessage());
        } finally {
            em.close();
        }

//        Assertions.fail("fallo exitosamente");
    }

    @Order(5)
    @Test
    public void delete() {
        System.out.println("ProductoDetalle testIT delete");
        ProductoDetalleBean cut = new ProductoDetalleBean();
        EntityManager em = emf.createEntityManager();
        //creado en create
        Integer idTipoProducto=1002;
        Long idProducto=1004L;

        ProductoDetallePK pk=new ProductoDetallePK(idTipoProducto, idProducto);

        cut.em = em;
        Long idProdcutoEliminado = 1L;
        em.getTransaction().begin();
        // Ahora proceder a eliminarla
        cut.deleteByPk(pk);
        em.getTransaction().commit();

        em.getTransaction().begin();
        Assertions.assertThrows(IllegalStateException.class,()-> cut.findById(idTipoProducto,idProducto));
        em.getTransaction().commit();
//        Assertions.fail("fallo exitosamente");
    }

//    @Test
//    void findRangeProductoDetalleActivos() {
//        System.out.println("ProductoDetalle testIT findRangeProductoDetalleActivos");
//        EntityManager em = emf.createEntityManager();
//        Integer first=0;
//        Integer max=2;
//        Integer idTipoProductoDetalle=1001;
//        cut.em = em;
//        List<ProductoDetalle> respuesta = cut.findRangeProductoDetalleActivos(idTipoProductoDetalle,first, max);
//        //se deberia devolver los unicos 1 unico valor que tiene true con idTipoProdcuto 1001
//        respuesta.forEach(p-> System.out.println(p.toString()));
//        Assertions.assertNotNull(respuesta);
//        Assertions.assertEquals(1, respuesta.size());
//        Assertions.assertTrue(respuesta.get(0).getActivo());
////        Assertions.fail("fallo exitosamente");
//
//    }
//    @Test
//    void countRangeProductoDetalleActivos() {
//        System.out.println("ProductoDetalle testIT countRangeProductoDetalleActivos");
//        EntityManager em = emf.createEntityManager();
//        Integer idTipoProductoDetalle=1001;
//        cut.em = em;
//        Long respuesta = cut.countProductoDetalleActivosByIdTipoProductoDetalle(idTipoProductoDetalle);
//        //se deberia devolver los unicos 1 unico valor que tiene true con idTipoProdcuto 1001
//        Assertions.assertNotNull(respuesta);
//        Assertions.assertEquals(1, respuesta);
////        Assertions.fail("fallo exitosamente");
//
//    }
//
//    @Test
//    void findByIdProductoDetalleAndIdProductoDetalle() {
//    }
//
//    @Test
//    void countByIdProductoDetalleAndIdProductoDetalle() {
//    }
//
//    @Test
//    void deleteByPk() {
//    }
//
//    @Test
//    void findAll() {
//    }
}