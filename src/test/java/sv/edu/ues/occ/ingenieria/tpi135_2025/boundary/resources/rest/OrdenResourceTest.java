package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ComboBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Orden;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Combo;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

class OrdenResourceTest {
    List<Combo> TEST_C = Arrays.asList(
            new Combo(1L),
            new Combo(2L),
            new Combo(3L),
            new Combo(4L),
            new Combo(5L),
            new Combo(6L));

    ComboResource cut; // Clase bajo prueba
    ComboBean mockC;
    PersistenceException causaPe = new PersistenceException("eroor desde test");
    PersistenceException persistenceExcepcion = new PersistenceException("Error al consultar", causaPe);

    EntityNotFoundException causaEe = new EntityNotFoundException("eroor desde test");
    EntityNotFoundException entityNotFoundException = new EntityNotFoundException("Error al consultar", causaEe);


    @Test
    void findRange() {
        System.out.println("Combo test findRange");
        cut = new ComboResource();
        mockC = Mockito.mock(ComboBean.class);

        cut.comboBean= mockC;
        Integer first=0;
        Integer max=10;

        //flujo normal
        Mockito.when(mockC.findRange(first, max)).thenReturn(TEST_C);
        Mockito.when(mockC.count()).thenReturn((long) TEST_C.size());
        Response response = cut.findRange(first, max);
        Assertions.assertEquals(200, response.getStatus());
        Mockito.verify(mockC).findRange(first, max);
        Mockito.verify(mockC).count();

        //excepciones

        Mockito.reset(mockC);
        Mockito.when(mockC.findRange(first, max)).thenThrow(persistenceExcepcion);
        response = cut.findRange(first, max);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }


    @Test
    void findById() {
        System.out.println("Combo test findById");
        cut = new ComboResource();
        mockC = Mockito.mock(ComboBean.class);
        Long id = 1L;
        cut.comboBean= mockC;
        Integer first=0;
        Integer max=10;
        //flujo normal
        Mockito.when(mockC.findById(id)).thenReturn(TEST_C.stream().filter(p->p.getIdCombo().equals(id)).findFirst().get());
        Response response = cut.findById(id);
        Assertions.assertEquals(200, response.getStatus());
        //excepciones
        Mockito.reset(mockC);
        Mockito.when(mockC.findById(2L)).thenThrow(entityNotFoundException);
        response = cut.findById(2L);
        Assertions.assertEquals(404, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void create() {
        System.out.println("Combo test create");
        cut = new ComboResource();
        mockC = Mockito.mock(ComboBean.class);
        cut.comboBean= mockC;
        UriInfo mockUriInfo = Mockito.mock(UriInfo.class);
        UriBuilder mockUriBuilder = Mockito.mock(UriBuilder.class);
        Combo tp = new Combo();
        tp.setIdCombo(1L);
        tp.setNombre("Test");
        //flujo normal
        URI uri = URI.create("http://localhost:8080/api/Combo/1");
        Mockito.when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.path(String.valueOf(tp.getIdCombo()))).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.build()).thenReturn(uri);
        Mockito.doNothing().when(mockC).create(tp);
        Response response = cut.create(tp, mockUriInfo);
        Assertions.assertEquals(201, response.getStatus());
        //excepciones
        Mockito.reset(mockC);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockC).create(tp);
        response = cut.create(tp, mockUriInfo);
        Assertions.assertEquals(500, response.getStatus());



        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void delete() {
        System.out.println("Combo test delete");
        cut = new ComboResource();
        mockC = Mockito.mock(ComboBean.class);
        cut.comboBean= mockC;
        Long id=1L;
        //flujo normal
        Mockito.doNothing().when(mockC).delete(id);
        Response response = cut.delete(id);
        Assertions.assertEquals(200, response.getStatus());
        response = cut.delete(id);
        //excepciones
        Mockito.reset(mockC);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockC).delete(id);
        response = cut.delete(id);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void update() {
        System.out.println("Combo test update");
        cut = new ComboResource();
        mockC = Mockito.mock(ComboBean.class);
        cut.comboBean= mockC;
        Long id=1L;
        UriInfo mockUriInfo = Mockito.mock(UriInfo.class);

        UriBuilder mockUriBuilder = Mockito.mock(UriBuilder.class);
        Combo tp = new Combo();
        tp.setIdCombo(1L);
        tp.setNombre("Test");
        //flujo normal
        URI uri = URI.create("http://localhost:8080/api/Combo/1");
        Mockito.when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.path(String.valueOf(tp.getIdCombo()))).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.build()).thenReturn(uri);
        Mockito.when(mockC.update(tp,id)).thenReturn(tp);
        Response response = cut.update(tp,id, mockUriInfo);
        Assertions.assertEquals(200, response.getStatus());
        //excepciones
        Mockito.reset(mockC);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockC).update(tp,id);
        response = cut.update(tp,id, mockUriInfo);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }
}