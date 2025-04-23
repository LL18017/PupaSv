package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.DatosMixtosDTO;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.OrdenDetalleBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.OrdenDetalle;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class OrdenDetalleResourceTest {

    @InjectMocks
    OrdenDetalleResource ordenDetalleResource;
    @Mock
    OrdenDetalleBean odBean;
    @Mock
    private UriInfo uriInfo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindRangeByIdOrden() {
        System.out.println("findRangeByIdOrden");
        Long idOrden = 1L;
        // Caso exitoso
        when(odBean.findRangeByIdOrden(eq(idOrden), anyInt(), anyInt())).thenReturn(List.of(new OrdenDetalle()));
        when(odBean.countByIdOrden(idOrden)).thenReturn(1L);

        Response response = ordenDetalleResource.findRangeByIdOrden(0, 20, idOrden);
        assertEquals(200, response.getStatus());
        // Caso con excepción
        reset(odBean);
        Throwable causa = new IllegalArgumentException("Causa simulada");
        when(odBean.findRangeByIdOrden(eq(idOrden), anyInt(), anyInt())).thenThrow(new RuntimeException("Simulacion de error", causa));

        Response errorResponse = ordenDetalleResource.findRangeByIdOrden(0, 20, idOrden);
        assertNotNull(errorResponse);
        assertTrue(errorResponse.getStatus() >= 400);
        if (errorResponse.getEntity() != null) {
            assertTrue(errorResponse.getEntity().toString().contains("Simulacion de error"));
        }
        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void testFindByIdOrdenAndIdProductoPrecio() {
        System.out.println("findByIdOrdenAndIdProductoPrecio");
        Long idOrden = 1L;
        Long idProducto = 2L;
        when(odBean.findByIdOrdenAndIdPrecioProducto(idOrden, idProducto)).thenReturn(new OrdenDetalle());
        Response response = ordenDetalleResource.findByIdOrdenAndIdProductoPrecio(idOrden, idProducto);
        assertEquals(200, response.getStatus());
        //fail("Esta prueba no pasa quemado");
    }
    @Test
    void testDelete() throws  Exception {
        System.out.println("testDelete");
        doNothing().when(odBean).delete(1L, 2L);
        Response response = ordenDetalleResource.delete(1L, 2L, null);
        assertEquals(200, response.getStatus());
        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void testupdate() {
        System.out.println("testupdate");
        OrdenDetalle ordenDetalle = new OrdenDetalle();
        when(odBean.update(any(OrdenDetalle.class), anyLong(), anyLong()))
                .thenReturn(ordenDetalle);
        Response response = ordenDetalleResource.update(ordenDetalle, null, 1L, 2L);
        assertEquals(200, response.getStatus());
        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void testGenerarOrdenDetalleProducto(){
        System.out.println("testGenerarOrdenDetalleProducto");
        System.out.println("Valores correctos");
        doNothing().when(odBean).generarOrdenDetalleProducto(1L, 2L, 3);
        Response response = ordenDetalleResource.generarOrdenDetalleProducto(1L, 2L, 3);
        assertEquals(200, response.getStatus());

        System.out.println("Cuando ocurra una excepcion");
        RuntimeException exceptionConCausa = new RuntimeException("Error general", new Throwable("Causa de la excepción"));
        doThrow(exceptionConCausa).when(odBean).generarOrdenDetalleProducto(1L, 2L, 3);

        Response responseConExcepcion = ordenDetalleResource.generarOrdenDetalleProducto(1L, 2L, 3);
        assertEquals(500, responseConExcepcion.getStatus());
        assertNotNull(responseConExcepcion.getEntity());
        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void testGenerarOrdenDetalleDesdeCombo_ValoresInvalidos(){
        System.out.println("testGenerarOrdenDetalleDesdeCombo_ValoresInvalidos");
        Response response=ordenDetalleResource.generarOrdenDetalleDesdeCombo(0L,0L,3);
        assertEquals(400, response.getStatus());
        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void testGenerarOrdenDetalleDesdeCombo(){
        System.out.println("testGenerarOrdenDetalleDesdeCombo");
        doNothing().when(odBean).generarOrdenDetalleProducto(1L, 2L, 3);
        Response response = ordenDetalleResource.generarOrdenDetalleDesdeCombo(1L,2L,3);
        assertEquals(200, response.getStatus());
        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void testGenerarOrdenDetalleDesdeCombo_conExcepcion(){
        System.out.println("testGenerarOrdenDetalleDesdeCombo_conExcepcion");

        RuntimeException exceptionConCausa = new RuntimeException("Error general", new Throwable("Causa de la excepción"));
        doThrow(exceptionConCausa).when(odBean).generarOrdenDetalleDesdeCombo(1L, 2L, 3);

        Response responseConExcepcion = ordenDetalleResource.generarOrdenDetalleDesdeCombo(1L, 2L, 3);

        assertEquals(500, responseConExcepcion.getStatus());
        assertNotNull(responseConExcepcion.getEntity());
        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void testGenerarOrdenDetalleMixto(){
        System.out.println("testGenerarOrdenDetalleMixto");
        DatosMixtosDTO dto = new DatosMixtosDTO();
        dto.setIdProductos(1L);
        dto.setCantidadProductos(2);
        dto.setIdCombos(3L);
        dto.setCantidadCombo(1);

        doNothing().when(odBean).generarOrdenDetalleMixto(anyLong(), anyList(), anyList());

        Response response = ordenDetalleResource.generarOrdenDetalleMixto(List.of(dto), 1L);
        assertEquals(200, response.getStatus());
        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void testGenerarOrdenDetalleMixto_valoresInvalidos(){
        System.out.println("testGenerarOrdenDetalleMixto_valoresInvalidos");
        Response response=ordenDetalleResource.generarOrdenDetalleMixto(null,1L);
        assertEquals(400, response.getStatus());
        //fail("Esta prueba no pasa quemado ");
    }

    @Test
    void testGenerarOrdenDetalleMixto_SinIdOrden(){
        System.out.println("testGenerarOrdenDetalleMixto_SinIdOrden");
        DatosMixtosDTO dto = new DatosMixtosDTO();
        dto.setIdProductos(1L);
        dto.setCantidadProductos(2);
        dto.setIdCombos(3L);
        dto.setCantidadCombo(1);

        Response response=ordenDetalleResource.generarOrdenDetalleMixto(List.of(dto),1L);
        assertEquals(200, response.getStatus());
        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void testGenerarOrdenDetalleMixto_conExcepcion() {
        System.out.println("testGenerarOrdenDetalleMixto_conExcepcion");

        DatosMixtosDTO dto = new DatosMixtosDTO();
        dto.setIdProductos(1L);
        dto.setCantidadProductos(1);
        dto.setIdCombos(2L);
        dto.setCantidadCombo(1);

        // Simular una excepción
        doThrow(new RuntimeException("Error simulado", new Throwable("Causa de error"))).when(odBean).generarOrdenDetalleMixto(anyLong(), anyList(), anyList());
        Response response = ordenDetalleResource.generarOrdenDetalleMixto(List.of(dto), 1L);
        assertEquals(500, response.getStatus());
        //fail("Esta prueba no pasa quemado");
    }


}

