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
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Orden;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Combo;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ComboResourceTest {

    List<Combo> TEST_TP = Arrays.asList(
            new Combo(1L),
            new Combo(2L),
            new Combo(3L),
            new Combo(4L),
            new Combo(5L),
            new Combo(6L));

    ComboResource cut; // Clase bajo prueba
    ComboBean mockP;
    PersistenceException causaPe = new PersistenceException("eroor desde test");
    PersistenceException persistenceExcepcion = new PersistenceException("Error al consultar", causaPe);

    EntityNotFoundException causaEe = new EntityNotFoundException("eroor desde test");
    EntityNotFoundException entityNotFoundException = new EntityNotFoundException("Error al consultar", causaEe);


//    @Test
    void findRange() {
        System.out.println("Combo test findRange");
        cut = new ComboResource();
        mockP = Mockito.mock(ComboBean.class);

        cut.comboBean= mockP;
        Integer first=0;
        Integer max=10;
        List<Object[]> lista = new ArrayList<>();

        // Simulando fila 1: idCombo=1, activo=true, nombre="Combo1", descripcion="Desc1", url="url1", suma=100.0
        lista.add(new Object[] {1L, true, "Combo1", "Desc1", "url1", 100.0});

        // Simulando fila 2: idCombo=2, activo=false, nombre="Combo2", descripcion="Desc2", url="url2", suma=250.5
        lista.add(new Object[] {2L, false, "Combo2", "Desc2", "url2", 250.5});

        //id cero y activo null
        Mockito.when(mockP.findRangeWithPrice(first, max)).thenReturn(lista);
        Mockito.when(mockP.count()).thenReturn((long) TEST_TP.size());
        Response response = cut.findRange(first, max);
        Assertions.assertEquals(200, response.getStatus());
        Mockito.verify(mockP).findRangeWithPrice(first, max);
        Mockito.verify(mockP).count();

        // excepciones
        ComboBean mockComboBean = Mockito.mock(ComboBean.class);
        cut.comboBean= mockComboBean;
        Mockito.when(mockComboBean.findRangeWithPrice(first, max)).thenThrow(persistenceExcepcion);
        response = cut.findRange(first, max);
        Assertions.assertEquals(500, response.getStatus());

        // Forzar fallo
//        Assertions.fail("Esta prueba no pasa quemado");
    }


    @Test
    void findById() {
        System.out.println("Combo test findById");
        cut = new ComboResource();
        mockP = Mockito.mock(ComboBean.class);
        Long id = 1L;
        cut.comboBean= mockP;
        Integer first=0;
        Integer max=10;
        //flujo normal
        Mockito.when(mockP.findById(id)).thenReturn(TEST_TP.stream().filter(p->p.getIdCombo().equals(id)).findFirst().get());
        Response response = cut.findById(id);
        Assertions.assertEquals(200, response.getStatus());
        //excepciones
        Mockito.reset(mockP);
        Mockito.when(mockP.findById(2L)).thenThrow(entityNotFoundException);
        response = cut.findById(2L);
        Assertions.assertEquals(404, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void create() {
        System.out.println("Combo test create");
        cut = new ComboResource();
        mockP = Mockito.mock(ComboBean.class);
        cut.comboBean= mockP;
        UriInfo mockUriInfo = Mockito.mock(UriInfo.class);
        UriBuilder mockUriBuilder = Mockito.mock(UriBuilder.class);
        Combo tp = new Combo();
        tp.setIdCombo(1L);
        tp.setNombre("test");
        //flujo normal
        URI uri = URI.create("http://localhost:8080/api/Combo/1");
        Mockito.when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.path(String.valueOf(tp.getIdCombo()))).thenReturn(mockUriBuilder);
        Mockito.when(mockUriBuilder.build()).thenReturn(uri);
        Mockito.doNothing().when(mockP).create(tp);
        Response response = cut.create(tp, mockUriInfo);
        Assertions.assertEquals(201, response.getStatus());
        //excepciones
        Mockito.reset(mockP);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockP).create(tp);
        response = cut.create(tp, mockUriInfo);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void delete() {
        System.out.println("Combo test delete");
        cut = new ComboResource();
        mockP = Mockito.mock(ComboBean.class);
        cut.comboBean= mockP;
        Long id=1L;
        //flujo normal
        Mockito.doNothing().when(mockP).delete(id);
        Response response = cut.delete(id);
        Assertions.assertEquals(200, response.getStatus());
        response = cut.delete(id);
        //excepciones
        Mockito.reset(mockP);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockP).delete(id);
        response = cut.delete(id);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }

    @Test
    void update() {
        System.out.println("Combo test update");
        cut = new ComboResource();
        mockP = Mockito.mock(ComboBean.class);
        cut.comboBean= mockP;
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
        Mockito.when(mockP.update(tp,id)).thenReturn(tp);
        Response response = cut.update(tp,id, mockUriInfo);
        Assertions.assertEquals(200, response.getStatus());
        //excepciones
        Mockito.reset(mockP);
        Mockito.doThrow(persistenceExcepcion)
                .when(mockP).update(tp,id);
        response = cut.update(tp,id, mockUriInfo);
        Assertions.assertEquals(500, response.getStatus());

        //fail("Esta prueba no pasa quemado");
    }
    @Test
    void findRangeByName() {
        System.out.println("Producto test findRangeByName");

        ComboBean mockP = Mockito.mock(ComboBean.class);
        ComboResource cut = new ComboResource(); // No moquear el recurso, instanciarlo
        cut.comboBean = mockP;

        Integer first = 0;
        Integer max = 10;
        String nombre = "test";

        // Lista simulada
        List<Object[]> combos = new ArrayList<>();
        combos.add(new Object[]{1L, true, "Combo Pepsi", "Incluye Pepsi y snacks", "https://imagen.com/combo1.jpg", BigDecimal.valueOf(5.99)});
        combos.add(new Object[]{2L, true, "Combo Nachos", "Nachos + Pepsi", "https://imagen.com/combo2.jpg", BigDecimal.valueOf(6.49)});
        combos.add(new Object[]{3L, false, "Combo Hotdog", "Hotdog grande + soda", "https://imagen.com/combo3.jpg", BigDecimal.valueOf(4.99)});

        // Caso exitoso
        Mockito.when(mockP.findByNombre(nombre, first, max)).thenReturn(combos);
        Mockito.when(mockP.countProductoByName(nombre)).thenReturn((long) combos.size());

        Response response = cut.findRangeByName(first, max, nombre);
        Assertions.assertEquals(200, response.getStatus());

        Mockito.verify(mockP).findByNombre(nombre, first, max);
        Mockito.verify(mockP).countProductoByName(nombre);

        // Caso con excepción
        Mockito.reset(mockP);
        Mockito.when(mockP.findByNombre(nombre, first, max)).thenThrow(new RuntimeException("Fallo simulado"));

        response = cut.findRangeByName(first, max, nombre);
        Assertions.assertEquals(500, response.getStatus());
    }

}