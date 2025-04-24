/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest.ComboDetalleResource;
import sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest.Headers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Combo;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ComboDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;

/**
 *
 * @author hdz
 */
public class ComboDetalleResourceTest {

    private ComboDetalleResource resource;
    private ComboDetalleBean mockComboDetalleBean;
    private ComboBean mockComboBean;
    private ProductoBean mockProductoBean;
    private UriInfo mockUriInfo;
    private UriBuilder mockUriBuilder;

    @BeforeEach
    void setUp() {
        resource = new ComboDetalleResource();
        mockComboDetalleBean = mock(ComboDetalleBean.class);
        mockComboBean = mock(ComboBean.class);
        mockProductoBean = mock(ProductoBean.class);
        mockUriInfo = mock(UriInfo.class);
        mockUriBuilder = mock(UriBuilder.class);

        // Inyectar dependencias manualmente
        resource.setComboDetalleBean(mockComboDetalleBean);
        resource.setComboBean(mockComboBean);
        resource.setProductoBean(mockProductoBean);

        when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
        when(mockUriBuilder.build()).thenReturn(URI.create("http://localhost/test"));
    }

    @Test
    void testFind() {
        System.out.println("ComboDetalleResource test find");
        // Normal
        List<ComboDetalle> lista = Arrays.asList(new ComboDetalle(), new ComboDetalle());
        when(mockComboDetalleBean.findRange(0, 20)).thenReturn(lista);
        when(mockComboDetalleBean.count()).thenReturn(2L);
        Response response = resource.find(0, 20);
        assertEquals(200, response.getStatus());
        assertEquals(lista, response.getEntity());
        assertEquals("2", response.getHeaderString(Headers.TOTAL_RECORD));
        reset(mockComboDetalleBean);
        // Error simple con causa 
        doThrow(new RuntimeException("Error simple", new Throwable("Causa dummy")))
                .when(mockComboDetalleBean).findRange(0, 20);
        when(mockComboDetalleBean.count()).thenReturn(2L);
        Response errorResponse = resource.find(0, 20);
        assertEquals(500, errorResponse.getStatus());
        assertTrue(errorResponse.getEntity().toString().contains("Error simple"));
        // Reset de nuevo
        reset(mockComboDetalleBean);
        // Error con causa
        RuntimeException inner = new RuntimeException("Causa interna");
        RuntimeException outer = new RuntimeException("Error con causa", inner);
        doThrow(outer).when(mockComboDetalleBean).findRange(0, 20);
        when(mockComboDetalleBean.count()).thenReturn(2L); // de nuevo por seguridad
        Response responseConCausa = resource.find(0, 20);
        assertEquals(500, responseConCausa.getStatus());
    }

    @Test
    void testFindByIDs() {
        System.out.println("ComboDetalleResource test findByIDs");
        // Caso exitoso
        ComboDetalle detalle = new ComboDetalle();
        when(mockComboDetalleBean.findByIdComboAndIdProducto(1L, 2L)).thenReturn(detalle);

        Response response = resource.findByIDs(1L, 2L);
        assertEquals(200, response.getStatus());
        assertEquals(detalle, response.getEntity());

        reset(mockComboDetalleBean);

        // ❌ Caso no encontrado (404)
        when(mockComboDetalleBean.findByIdComboAndIdProducto(1L, 2L)).thenReturn(null);
        Response notFoundResponse = resource.findByIDs(1L, 2L);
        assertEquals(404, notFoundResponse.getStatus());
        assertTrue(notFoundResponse.getEntity().toString().contains("ComboDetalle no encontrado"));

        // 🔄 Reset para el siguiente caso
        reset(mockComboDetalleBean);

        // ⚠️ Caso con excepción (500)
        RuntimeException error = new RuntimeException("Error findByIDs", new Throwable("Causa dummy"));
        when(mockComboDetalleBean.findByIdComboAndIdProducto(1L, 2L)).thenThrow(error);
        Response errorResponse = resource.findByIDs(1L, 2L);
        assertEquals(500, errorResponse.getStatus());
        assertTrue(errorResponse.getEntity().toString().contains("Error findByIDs"));
    }

    @Test
    void testCreate() {
        System.out.println("ComboDetalleResource test create");

        ComboDetalle detalle = new ComboDetalle();
        when(mockComboBean.findById(1L)).thenReturn(new Combo());
        when(mockProductoBean.findById(2L)).thenReturn(new Producto());

        doNothing().when(mockComboDetalleBean).create(detalle, 1L, 2L);
        Response response = resource.create(detalle, 1L, 2L, mockUriInfo);
        assertEquals(201, response.getStatus());
        assertEquals(URI.create("http://localhost/test"), response.getLocation());

        // ❌ Combo no existe
        reset(mockComboBean, mockProductoBean, mockComboDetalleBean);
        when(mockComboBean.findById(1L)).thenReturn(null);
        Response noComboResponse = resource.create(detalle, 1L, 2L, mockUriInfo);
        assertEquals(400, noComboResponse.getStatus());
        assertEquals("Combo no encontrado para el ID proporcionado.", noComboResponse.getEntity());

        // ❌ Producto no existe
        reset(mockComboBean, mockProductoBean, mockComboDetalleBean);
        when(mockComboBean.findById(1L)).thenReturn(new Combo());
        when(mockProductoBean.findById(2L)).thenReturn(null);
        Response noProductoResponse = resource.create(detalle, 1L, 2L, mockUriInfo);
        assertEquals(400, noProductoResponse.getStatus());
        assertEquals("Producto no encontrado para el ID proporcionado.", noProductoResponse.getEntity());

        // ❌ Error inesperado
        reset(mockComboBean, mockProductoBean, mockComboDetalleBean);
        when(mockComboBean.findById(1L)).thenReturn(new Combo());
        when(mockProductoBean.findById(2L)).thenReturn(new Producto());
        RuntimeException conCausa = new RuntimeException("Error create", new Throwable("dummy"));
        doThrow(conCausa).when(mockComboDetalleBean).create(detalle, 1L, 2L);

        Response errorResponse = resource.create(detalle, 1L, 2L, mockUriInfo);
        assertEquals(500, errorResponse.getStatus());
        assertTrue(errorResponse.getEntity().toString().contains("Error create"));

    }

    @Test
    void testUpdate() {
        System.out.println("ComboDetalleResource test update");
        ComboDetalle detalle = new ComboDetalle();
        // Caso exitoso
        when(mockComboDetalleBean.findByIdComboAndIdProducto(1L, 2L)).thenReturn(detalle);
        when(mockComboDetalleBean.updateByComboDetallePK(detalle, 1L, 2L)).thenReturn(detalle);
        Response response = resource.update(detalle, 1L, 2L, mockUriInfo);
        assertEquals(200, response.getStatus());
        assertEquals(URI.create("http://localhost/test"), response.getEntity());

        // ComboDetalle no encontrado (cobertura para el `if (existe == null)`)
        when(mockComboDetalleBean.findByIdComboAndIdProducto(1L, 2L)).thenReturn(null);
        Response notFoundResponse = resource.update(detalle, 1L, 2L, mockUriInfo);
        assertEquals(404, notFoundResponse.getStatus());
        assertTrue(notFoundResponse.getEntity().toString().contains("ComboDetalle no encontrado"));

        // Excepción durante la actualización
        when(mockComboDetalleBean.findByIdComboAndIdProducto(1L, 2L)).thenReturn(detalle);
        when(mockComboDetalleBean.updateByComboDetallePK(detalle, 1L, 2L))
                .thenThrow(new RuntimeException("Error update", new Throwable("dummy")));
        Response errorResponse = resource.update(detalle, 1L, 2L, mockUriInfo);
        assertEquals(500, errorResponse.getStatus());
        assertTrue(errorResponse.getEntity().toString().contains("Error update"));
    }

    @Test
    void testDelete() {
        System.out.println("ComboDetalleResource test delete");
        //caso exitoso
        when(mockComboDetalleBean.findByIdComboAndIdProducto(1L, 2L)).thenReturn(new ComboDetalle());
        Response response = resource.delete(1L, 2L, mockUriInfo);
        assertEquals(200, response.getStatus());
        //Excepción con causa
        RuntimeException conCausa = new RuntimeException("Error delete", new Throwable("dummy"));
        doThrow(conCausa).when(mockComboDetalleBean).deleteByComboDetallePK(1L, 2L);
        Response errorResponse = resource.delete(1L, 2L, mockUriInfo);
        assertEquals(500, errorResponse.getStatus());
        assertTrue(errorResponse.getEntity().toString().contains("Error delete"));
        // ComboDetalle no encontrado
        when(mockComboDetalleBean.findByIdComboAndIdProducto(1L, 2L)).thenReturn(null);
        Response notFoundResponse = resource.delete(1L, 2L, mockUriInfo);
        assertEquals(404, notFoundResponse.getStatus());
        assertTrue(notFoundResponse.getEntity().toString().contains("ComboDetalle no encontrado"));
    }
}
