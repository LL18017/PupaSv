package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.inject.Inject;
import jakarta.json.bind.JsonbException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.AbstractDataAccess;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoPrecioBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.TipoProductoBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoPrecio;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.TipoProducto;


public class GeneralRest{


    public GeneralRest() {
    }

    //    abstract AbstractDataAccess<T> getBean();

    @Inject
    ProductoBean pBean;
    @Inject
    TipoProductoBean tpBean;
    @Inject
    ProductoPrecioBean ppBean;


    public Response responseExcepcions(Exception e,Long id) {
        Throwable causa = e.getCause();
        if (causa instanceof IllegalArgumentException) {
            return Response.status(400).header(Headers.WRONG_PARAMETER, "id: " + id).build();
        } else if (causa instanceof EntityNotFoundException) {
            return Response.status(404).header(Headers.NOT_FOUND_ID, "no se ha encontrado el registro con el id: " + id).build();
        } else if (causa instanceof ConstraintViolationException) {
            return Response.status(500).header(Headers.PROCESS_ERROR, "error al aceder a la base de datos").build();
        } else if (causa instanceof IllegalStateException) {
            return Response.status(500).header(Headers.PROCESS_ERROR, "error en el repositorio").build();
        } else if (causa instanceof PersistenceException) {
            return Response.status(500).header(Headers.PROCESS_ERROR, "error en la base  de datos").build();
        }else if (causa instanceof JsonbException) {
            return Response.status(500).header(Headers.PROCESS_ERROR, "error al serializar").entity(e).build();
        }
        return Response.status(500).header(Headers.PROCESS_ERROR, causa.toString()).entity(e).build();

    }

}
