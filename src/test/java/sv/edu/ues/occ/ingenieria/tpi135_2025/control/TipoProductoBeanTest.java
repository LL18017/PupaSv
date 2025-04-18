package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class TipoProductoBeanTest {
    TipoProductoBean cut=new TipoProductoBean();
    TipoProductoBean cut2=Mockito.spy(TipoProductoBean.class);
    EntityManager mockEm = Mockito.mock(EntityManager.class);
    @Test
    void orderParameterQuery() {
        System.out.println("TipoProducto test orderParameterQuery");
        String esperado = "idTipoProducto";
        String respuesta = cut.orderParameterQuery();
        assertEquals(esperado, respuesta);

//        fail("fallo exitosamente");
    }


    @Test
    void getEntityManager() {
        System.out.println("TipoProducto test getEntityManager");
        Mockito.when(cut2.getEntityManager()).thenReturn(mockEm);

        EntityManager em = cut2.getEntityManager();
        assertEquals(em, mockEm);
//        fail("fallo exitosamente");
    }
}