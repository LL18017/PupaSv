package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;


import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest.plantillas.DatosMixtosDTO;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.OrdenDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.OrdenDetallePK;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoPrecio;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrdenDetalleResourceSI extends AbstractContainerTest {

    private WebTarget target;
    private Long idOrdenParaPruebas = 12348L;
    private Long idProductoPrecioParaPrueba = 1001L;
    private Long idComboParaPrueba = 1003L;
    private Long idProductoParaPrueba = 1001L;
    Long totalRegistros = 3L;

    @BeforeAll
    public void inicializar() {
        cliente = ClientBuilder.newClient();
        target = cliente.target(String.format("http://%s:%d/PupaSv-1.0-SNAPSHOT/v1/", servidorDeAplicaion.getHost(),
                servidorDeAplicaion.getMappedPort(9080)));

    }

    @Test
    @Order(1)
    public void testBuscarOrdenDetalle() {
        System.out.println("OrdenDetalle testSI BuscarOrdenDetalle");
        int idOrden = 999;
        int idProducto = 999;
        String pathFormath = String.format("/ordenDetalle/orden/%d/productoPrecio/%d", 12345L, 1001L);

        Response response = target.path(pathFormath)
                .request()
                .get();
        assertEquals(200, response.getStatus());

        pathFormath = String.format("/ordenDetalle/orden/%d/productoPrecio/%d", idOrden, idProducto);
        response = target.path(pathFormath)
                .request()
                .get();
        assertEquals(404, response.getStatus(), "El código de estado debería ser 404");
        //fail("Esta prueba no pasa quemado");
    }



    @Test
    @Order(2)
    public void testUpdateOrdenDetalle() {
        System.out.println("OrdenDetalle testSI Update");
        long idOrden = 123L;
        long idProductoPrecio = 456L;

        OrdenDetalle detalleActualizado = new OrdenDetalle();
        detalleActualizado.setOrdenDetallePK(new OrdenDetallePK(idOrden, idProductoPrecio));
        detalleActualizado.setCantidad(5);
        detalleActualizado.setPrecio(new BigDecimal("12.00"));
        detalleActualizado.setProductoPrecio(new ProductoPrecio(idProductoPrecio)); // Asegúrate de que el ID del producto precio sea el mismo
        String pathFormath = String.format("/ordenDetalle/orden/%d/productoPrecio/%d", idOrdenParaPruebas, idProductoPrecioParaPrueba);

        Response response = target.path(pathFormath)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(detalleActualizado, MediaType.APPLICATION_JSON));


        assertEquals(200, response.getStatus(), "El código de estado debe ser 200 OK para la actualización");
        response.close();

        Response getResponse = target.path(pathFormath)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(200, getResponse.getStatus());
        OrdenDetalle ordenDetalleVerificado = getResponse.readEntity(OrdenDetalle.class);
        assertEquals(5, ordenDetalleVerificado.getCantidad());
        assertEquals(new BigDecimal("12.00"), ordenDetalleVerificado.getPrecio());
        getResponse.close();
        //fail("Esta prueba no pasa quemado");
    }


    @Test
    @Order(3)
    public void testDeleteOrdenDetalle() {
        System.out.println("OrdenDetalle testSI DeleteOrdenDetalle");
        String pathFormath = String.format("/ordenDetalle/orden/%d/productoPrecio/%d", idOrdenParaPruebas, idProductoPrecioParaPrueba);

        // Realiza una solicitud DELETE para eliminar el orden detalle
        Response response = target.path(pathFormath)
                .request()
                .delete();

        assertEquals(200, response.getStatus(), "El código de estado debe ser 204 No Content");
        Response checkResponse = target.path(pathFormath)
                .request()
                .get();
        assertEquals(404, checkResponse.getStatus(), "Después de eliminar, debería ser 404 Not Found");
        //fail("Esta prueba no pasa quemado"); // Removed the forced failure to allow the test to pass if the logic is correct
    }

    @Test
    @Order(4)
    public void testGenerarOrdenDetalleProducto() {
        System.out.println("OrdenDetalle testSI GenerarOrdenDetalleProducto");
        String pathFormath = "/ordenDetalle/producto";


        Response response = target.path(pathFormath)
                .queryParam("cantidad", 5)
                .queryParam("idOrden", idOrdenParaPruebas)
                .queryParam("idProducto", idProductoParaPrueba)
                .request()
                .post(Entity.entity(new OrdenDetalle(), MediaType.APPLICATION_JSON));
        assertEquals(200, response.getStatus());

        // fail("Esta prueba no pasa ");
    }


    private int getCantidadBasePorProducto(long idProductoPrecio) {
        if (idProductoPrecio == 1003L) {
            return 10;
        } else if (idProductoPrecio == 1001L) {
            return 3;
        }
        return 1;
    }


    @Test
    @Order(5)
    public void testGenerarOrdenDetalleDesdeCombo() {
        System.out.println("OrdenDetalle testSI GenerarOrdenDetalleDesdeCombo");

        Response response = target.path("/ordenDetalle/combo")
                .queryParam("idOrden", 12349)
                .queryParam("idCombo", idComboParaPrueba)
                .queryParam("cantidad", 2)
                .request(MediaType.APPLICATION_JSON)
                .post(null);
        assertEquals(200, response.getStatus());

        //MALOS ARGUMENTOS
        response = target.path("/ordenDetalle/combo")
                .queryParam("idOrden", 0)
                .queryParam("idCombo", 0)
                .queryParam("cantidad", 2)
                .request(MediaType.APPLICATION_JSON)
                .post(null);
        assertEquals(400, response.getStatus());
        //NO EXISTE COMBO
        response = target.path("/ordenDetalle/combo")
                .queryParam("idOrden", idOrdenParaPruebas)
                .queryParam("idCombo", 112233L)
                .queryParam("cantidad", 2)
                .request(MediaType.APPLICATION_JSON)
                .post(null);
        assertEquals(404, response.getStatus());
        //NO EXISTE ORDEN
        response = target.path("/ordenDetalle/combo")
                .queryParam("idOrden", 112233L)
                .queryParam("idCombo", idComboParaPrueba)
                .queryParam("cantidad", 2)
                .request(MediaType.APPLICATION_JSON)
                .post(null);
        assertEquals(404, response.getStatus());
        // fail("Esta prueba no pasa quemado");
    }

//    @Test
//    @Order(8)
//    public void testGenerarOrdeDetalleMixto() throws IOException {
//        System.out.println("OrdenDetalle testSI GenerarOrdeDetalleMixto");
//        Long idOrden = 123451L;
//        List<DatosMixtosDTO> datos = new ArrayList<>();
//        datos.add(new DatosMixtosDTO(1001L,5,1001L,1));
//        datos.add(new DatosMixtosDTO(1002L,8,1002L,2));
//
//        String pathFormath = "/ordenDetalle/mixto";
//        Response response = target.path(pathFormath)
//                .queryParam("idOrden", idOrden)
//                .request(MediaType.APPLICATION_JSON)
//                .post(Entity.entity(datos, MediaType.APPLICATION_JSON));
//        assertEquals(200,response.getStatus());
//        //lista vacia
//        response = target.path(pathFormath)
//                .queryParam("idOrden", idOrden)
//                .request(MediaType.APPLICATION_JSON)
//                .post(Entity.entity(List.of(), MediaType.APPLICATION_JSON));
//        assertEquals(400,response.getStatus());
//        //mal id
//        response = target.path(pathFormath)
//                .queryParam("idOrden", -96)
//                .request(MediaType.APPLICATION_JSON)
//                .post(Entity.entity(List.of(), MediaType.APPLICATION_JSON));
//        assertEquals(400,response.getStatus());
//
//
//    }

}
