package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.inject.Inject;
import jakarta.json.bind.JsonbException;
import jakarta.persistence.*;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.AbstractDataAccess;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoPrecioBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.TipoProductoBean;

import java.util.logging.Level;
import java.util.logging.Logger;


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
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,e.getMessage(),e);
            return Response.status(400).header(Headers.WRONG_PARAMETER, "id: " + id).build();
        } else if (causa instanceof EntityNotFoundException ) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,e.getMessage(),e);
            return Response.status(404).header(Headers.NOT_FOUND_ID, e.getMessage()).build();
        } else if (causa instanceof NoResultException) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,e.getMessage(),e);
            return Response.status(404).header(Headers.NOT_FOUND_ID, e.getCause()).build();
        } else if (causa instanceof ConstraintViolationException) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,e.getMessage(),e);
            return Response.status(500).header(Headers.PROCESS_ERROR, "error al aceder a la base de datos").build();
        } else if (causa instanceof IllegalStateException) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,e.getMessage(),e);
            return Response.status(500).header(Headers.PROCESS_ERROR, "error en el repositorio").build();
        } else if (causa instanceof PersistenceException) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,e.getMessage(),e);
            return Response.status(500).header(Headers.PROCESS_ERROR, "error en la base  de datos").build();
        }else if (causa instanceof JsonbException) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,e.getMessage(),e);
            return Response.status(500).header(Headers.PROCESS_ERROR, "error al serializar").entity(e).build();
        }
        return Response.status(500).header(Headers.PROCESS_ERROR, causa.toString()).entity(e).build();

    }

}
