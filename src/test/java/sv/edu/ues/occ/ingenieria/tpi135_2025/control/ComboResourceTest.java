/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityNotFoundException;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest.ComboResource;
import sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest.Headers;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Combo;

/**
 *
 * @author hdz
 */
public class ComboResourceTest {

    private ComboResource comboResource;
    private ComboBean mockComboBean;

    @BeforeEach
    void setUp() {
        comboResource = new ComboResource();
        mockComboBean = mock(ComboBean.class);
        comboResource.setComboBean(mockComboBean);
    }

    @Test
    void testFindRange() {
        System.out.println("ComboResource test FindRange");
        List<Combo> lista = Arrays.asList(new Combo(), new Combo());
        when(mockComboBean.findRange(0, 20)).thenReturn(lista);
        when(mockComboBean.count()).thenReturn(2L);

        Response response = comboResource.findRange(0, 20);

        assertEquals(200, response.getStatus());
        assertEquals(lista, response.getEntity());
        assertEquals("2", response.getHeaderString(Headers.TOTAL_RECORD));
        // Caso con excepción
        when(mockComboBean.findRange(0, 20)).thenThrow(new RuntimeException("Error al consultar"));
        Response responseError = comboResource.findRange(0, 20);
        assertEquals(500, responseError.getStatus());
        assertTrue(responseError.getEntity().toString().contains("Error al consultar"));
        // Caso con excepción CON causa
        RuntimeException inner = new RuntimeException("Causa interna");
        RuntimeException outer = new RuntimeException("Error con causa", inner);
        doThrow(outer).when(mockComboBean).findRange(0, 20);
        Response responseConCausa = comboResource.findRange(0, 20);
        assertEquals(500, responseConCausa.getStatus());

    }

    @Test
    void testFindById() {
        System.out.println("ComboResource test FindById");
        //Caso valido
        Combo combo = new Combo();
        combo.setIdCombo(1L);
        when(mockComboBean.findById(1L)).thenReturn(combo);

        Response response = comboResource.findById(1L);

        assertEquals(200, response.getStatus());
        assertEquals(combo, response.getEntity());
        // Caso ID inexistente
        when(mockComboBean.findById(999L)).thenReturn(null);

        Response responseNotFound = comboResource.findById(999L);
        assertEquals(404, responseNotFound.getStatus());
        // Caso con excepción
        when(mockComboBean.findById(2L)).thenThrow(new RuntimeException("Error interno"));
        Response responseError = comboResource.findById(2L);
        assertEquals(500, responseError.getStatus());

        // Caso con excepción CON causa (para cubrir responseExcepcions)
        RuntimeException inner = new RuntimeException("Causa profunda");
        RuntimeException outer = new RuntimeException("Error con causa", inner);
        doThrow(outer).when(mockComboBean).findById(3L);
        Response responseConCausa = comboResource.findById(3L);
        assertEquals(500, responseConCausa.getStatus()); // o ajusta según lo que devuelva responseExcepcions

    }

    @Test
    void testCreate() {
        System.out.println("ComboResource test Create");
        // Create válido
        Combo nuevoCombo = new Combo();
        nuevoCombo.setIdCombo(5L);

        UriInfo mockUriInfo = mock(UriInfo.class);
        UriBuilder mockBuilder = mock(UriBuilder.class);

        when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockBuilder);
        when(mockBuilder.path("5")).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(URI.create("http://localhost/combo/5"));

        Response response = comboResource.create(nuevoCombo, mockUriInfo);
        assertEquals(201, response.getStatus());

        verify(mockComboBean).create(nuevoCombo);
        assertEquals(201, response.getStatus());
        assertEquals(URI.create("http://localhost/combo/5"), response.getLocation());
        // Create inválido (null)
        Response responseInvalid = comboResource.create(null, mockUriInfo);
        assertEquals(400, responseInvalid.getStatus());
        // Create con excepción
        Combo comboExcepcion = new Combo();
        comboExcepcion.setIdCombo(10L);
        doThrow(new RuntimeException("Error al crear")).when(mockComboBean).create(comboExcepcion);
        Response responseException = comboResource.create(comboExcepcion, mockUriInfo);
        assertEquals(500, responseException.getStatus());
    }

    @Test
    void testUpdate() {
        System.out.println("ComboResource test Update");
        // Caso exitoso
        Combo comboActualizado = new Combo();
        comboActualizado.setNombre("Nuevo nombre");

        UriInfo uriInfo = mock(UriInfo.class);

        Response response = comboResource.update(comboActualizado, 1L, uriInfo);

        verify(mockComboBean).update(comboActualizado, 1L);
        assertEquals(200, response.getStatus());
        // Caso con ID que no coincide
        Combo comboError = new Combo();
        comboError.setIdCombo(99L); // ID distinto

        doThrow(new IllegalArgumentException("ID no coincide"))
                .when(mockComboBean).update(comboError, 1L);
        Response responseError = comboResource.update(comboError, 1L, uriInfo);

        assertEquals(400, responseError.getStatus());
    }

    @Test
    void testDelete() {
        System.out.println("ComboResource test Delete");
        UriInfo uriInfo = mock(UriInfo.class);
        // Caso exitoso
        Response response = comboResource.delete(1L);
        verify(mockComboBean).delete(1L);
        assertEquals(200, response.getStatus());

        // Caso con excepción simulada
        doThrow(new EntityNotFoundException("No se encontró combo con ID 999"))
                .when(mockComboBean).delete(999L);
        Response responseError = comboResource.delete(999L);
        assertEquals(404, responseError.getStatus());
        assertTrue(responseError.getEntity().toString().contains("No se encontró combo"));
        // Caso con una excepción genérica: Simula una excepción inesperada
        RuntimeException runtimeException = new RuntimeException("Error inesperado");
        doThrow(runtimeException).when(mockComboBean).delete(1L);  // Simula error al eliminar
        Response responseGenericError = comboResource.delete(1L);
        assertEquals(500, responseGenericError.getStatus()); // Se espera un 500
        assertTrue(responseGenericError.getEntity().toString().contains("Excepción sin causa"));
    }
}
