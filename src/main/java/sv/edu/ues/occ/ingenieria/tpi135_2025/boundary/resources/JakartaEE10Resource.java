package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources;



import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 *
 * @author 
 */
public class JakartaEE10Resource {
    
    /**
    * get de prueba que retorna un numero por 13.
    *
    * @param  num paremtro de ingreso cualquiera.
    * @return numero multiplicado por 13.
    */
    
    @GET
    public int ping(int num){
        return 0;
    }
}
