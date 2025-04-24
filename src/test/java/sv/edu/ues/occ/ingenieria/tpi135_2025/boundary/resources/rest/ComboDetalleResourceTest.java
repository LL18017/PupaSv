package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ComboBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ComboDetalleBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ComboDetalleBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Combo;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ComboDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ComboDetalle;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;

class ComboDetalleResourceTest {
    List<ComboDetalle> TEST_C = Arrays.asList(
            new ComboDetalle(1,1L),
            new ComboDetalle(1,2L),
            new ComboDetalle(1,3L),
            new ComboDetalle(1,4L),
            new ComboDetalle(1,5L),
            new ComboDetalle(1,6L));

    ComboDetalleResource cut; // Clase bajo prueba
    ComboDetalleBean mockC;
    PersistenceException causaPe = new PersistenceException("eroor desde test");
    PersistenceException persistenceExcepcion = new PersistenceException("Error al consultar", causaPe);

    EntityNotFoundException causaEe = new EntityNotFoundException("eroor desde test");
    EntityNotFoundException entityNotFoundException = new EntityNotFoundException("Error al consultar", causaEe);


    @Test
    void findRange() {
        System.out.println("ComboDetalle test findRange");
        cut = new ComboDetalleResource();
        mockC = Mockito.mock(ComboDetalleBean.class);

        cut.comboDetalleBean= mockC;
        Integer first=0;
        Integer max=10;

        //flujo normal
        Mockito.when(mockC.findRange(first, max)).thenReturn(TEST_C);
        Mockito.when(mockC.count()).thenReturn((long) TEST_C.size());
        Response response = cut.find(first, max);
        Assertions.assertEquals(200, response.getStatus());
        Mockito.verify(mockC).findRange(first, max);
        Mockito.verify(mockC).count();

        //excepciones

        Mockito.reset(mockC);
        Mockito.when(mockC.findRange(first, max)).thenThrow(persistenceExcepcion);
        response = cut.find(first, max);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }


    @Test
    void findById() {
        System.out.println("ComboDetalle test findById");
        cut = new ComboDetalleResource();
        mockC = Mockito.mock(ComboDetalleBean.class);
        Long idProducto = 1L;
        Long idCombo = 1L;
        cut.comboDetalleBean= mockC;
        //flujo normal
        Mockito.when(mockC.findByIdComboAndIdProducto(idCombo,idProducto)).thenReturn(TEST_C.get(0));
        Response response = cut.findByIDs(idCombo,idProducto);
        Assertions.assertEquals(200, response.getStatus());
        //excepciones
        Mockito.reset(mockC);
        Mockito.when(mockC.findByIdComboAndIdProducto(1L,2L)).thenThrow(entityNotFoundException);
        response = cut.findByIDs(1L,2L);
        Assertions.assertEquals(404, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void create() {
        System.out.println("ComboDetalle test create");
        cut = new ComboDetalleResource();
        mockC = Mockito.mock(ComboDetalleBean.class);
        cut.comboDetalleBean= mockC;
        Long idProducto = 1L;
        Long idCombo = 1L;
        UriInfo mockUriInfo = Mockito.mock(UriInfo.class);
        UriBuilder mockUriBuilder = Mockito.mock(UriBuilder.class);
        ComboDetalle tp = new ComboDetalle();
        tp.setCantidad(99);
        //flujo normal
        URI uri = URI.create("http://localhost:8080/api/ComboDetalle/1");
        Mockito.when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.build()).thenReturn(uri);
        Mockito.doNothing().when(mockC).create(tp);
        Response response = cut.create(tp,idCombo,idProducto,mockUriInfo);
        Assertions.assertEquals(201, response.getStatus());
        //excepciones
        Mockito.reset(mockC);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockC).create(tp,idCombo,idProducto);
        response = cut.create(tp,idCombo,idProducto, mockUriInfo);
        Assertions.assertEquals(500, response.getStatus());



        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void delete() {
        System.out.println("ComboDetalle test delete");
        cut = new ComboDetalleResource();
        mockC = Mockito.mock(ComboDetalleBean.class);
        cut.comboDetalleBean= mockC;
        Long idProducto=1L;
        Long idCombo = 1L;
        //flujo normal
        Mockito.doNothing().when(mockC).deleteByComboDetallePK(idCombo,idProducto);
        Mockito.when(mockC.findByIdComboAndIdProducto(idCombo,idProducto)).thenReturn(TEST_C.get(0));
        Response response = cut.delete(idCombo,idProducto);
        Assertions.assertEquals(200, response.getStatus());
        //no existe
        Mockito.when(mockC.findByIdComboAndIdProducto(112233L,idProducto)).thenReturn(null);
        response=cut.delete(112233L,idProducto);
        Assertions.assertEquals(404, response.getStatus());

        //excepciones
        Mockito.reset(mockC);
        Mockito.when(mockC.findByIdComboAndIdProducto(idCombo,idProducto)).thenReturn(TEST_C.get(0));
        Mockito.doThrow(persistenceExcepcion)
                .when(mockC).deleteByComboDetallePK(idCombo,idProducto);
        response = cut.delete(idCombo,idProducto);

        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void update() {
        System.out.println("ComboDetalle test update");
        cut = new ComboDetalleResource();
        mockC = Mockito.mock(ComboDetalleBean.class);
        cut.comboDetalleBean= mockC;
        Long id=1L;
        Long idCombo = 1L;
        UriInfo mockUriInfo = Mockito.mock(UriInfo.class);

        UriBuilder mockUriBuilder = Mockito.mock(UriBuilder.class);
        ComboDetalle tp = new ComboDetalle();
        tp.setCantidad(9);
        //flujo normal
        URI uri = URI.create("http://localhost:8080/api/ComboDetalle/1");
        Mockito.when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.build()).thenReturn(uri);
        Mockito.when(mockC.updateByComboDetallePK(tp,idCombo,id)).thenReturn(tp);
        Mockito.when(mockC.findByIdComboAndIdProducto(idCombo,id)).thenReturn(TEST_C.get(0));
        Response response = cut.update(tp,idCombo,id, mockUriInfo);
        Assertions.assertEquals(200, response.getStatus());
        //no existe
        Mockito.when(mockC.findByIdComboAndIdProducto(112233L,id)).thenReturn(null);
        response=cut.update(tp,112233L,id,mockUriInfo);
        Assertions.assertEquals(404, response.getStatus());

        //excepciones
        Mockito.reset(mockC);
        Mockito.when(mockC.findByIdComboAndIdProducto(idCombo,id)).thenReturn(TEST_C.get(0));
        Mockito.doThrow(persistenceExcepcion)
                .when(mockC).updateByComboDetallePK(tp,idCombo,id);
        response = cut.update(tp,id,idCombo, mockUriInfo);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }
}