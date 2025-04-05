package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.AbstractDataAccess;

/**
 *
 * @author mjlopez
 * @param <T> Clase abstracta que implementa el patrón de diseño Fabrica
 * Abstracta. Proporciona métodos genéricos para exponer los datos mediante
 * rest.
 *
 */
public abstract class AbstracDataResource<T> implements Serializable {

    /**
     * metodo para obtener acceso al bean para la persistencia de datos
     *
     * @return un Bean de tipo ABstractDataAccess
     */
    public abstract AbstractDataAccess<T> getBean();

    /**
     * metodo generico para la obtencion del id el cual pued ser integer o Long
     * dependiendo del caso
     *
     * @param registro
     * @return id de la entidad
     */
    public abstract Object getId(T registro);

    /**
     * metodo para obtener el nombre de la entidad
     *
     * @return el nombre de la entidad
     */
    public abstract String getClassName();

    /**
     * metodo que devueleve una rango de datos de tipo T
     *
     * @param first la pocicion del primer dat
     * @param max la cantidad de datos que se desea obtener
     * @return una lista de tipo T si no definel los parametros entonces
     * devuelve los primeros 20 registros
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(
            @QueryParam("first")
            @DefaultValue("0") int first,
            @QueryParam("max")
            @DefaultValue("20") int max
    ) {
        try {
            if (first >= 0 && max >= 0 && max <= 50) {

                List<T> encontrados = getBean().findRange(first, max);
                long total = getBean().count();
                Response.ResponseBuilder builder = Response.ok(encontrados).
                        header("Total-records", total).
                        type(MediaType.APPLICATION_JSON);
                return builder.build();
            } else {
                return Response.status(400).header("wrong parameter, first:", first + ",max: " + max).header("wrong parameter : max", "s").build();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage());
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * metodo para encontrar un registro especifico dado su id
     *
     * @param id del registro a buscar
     * @return un esatatus 200 se se encontro la entidad junto con dicha entidad
     * un estatus 500 en dado caso falle el servidor un estatus 404 si no se
     * encuentra ningun registro con el id especificado 400 si se envia mal una
     * parametro
     */

    @Path("/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("id") Long id) {
        if (id != null) {
            try {
                T encontrado = getBean().findById(id);
                if (encontrado != null) {
                    Response.ResponseBuilder builder = Response.ok(encontrado);
                    return builder.build();
                }
                return Response.status(404).header("not-found-id", id).build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(400).header("wrong-parameter : id", id).build();
    }

    /**
     * registra una entidad T
     *
     * @param registro entidad a persistir
     * @param uriInfo informacion de URl donde se encuantra la peticion
     * @return un estatus 201 si la entidad es creada junto con la url donde se
     * puede encontra dicha entidad 422 en dado caso falle la creacion de la
     * entidad y 500 si por fall el servidor
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response create(T registro, @Context UriInfo uriInfo) {

        System.out.println(registro);
        if (registro != null && getId(registro) == null) {
            try {
                getBean().create(registro);
                if (getId(registro) != null) {
                    UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
                    uriBuilder.path(String.valueOf(getId(registro)));
                    return Response.created(uriBuilder.build()).build();
                }
                return Response.status(422).header("Unprocessable Entity", "Record couldnt be created").build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(500).header("Wrong-parameter", registro).build();
    }

    /**
     * borra un entidad
     *
     * @param id
     * @param uriInfo info de url de donde se esta realizado la peticion
     * @return un status 200 si se borro la entidad , un 422 si hubo un problema
     * y 500 si falla el servdor
     */
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id, @Context UriInfo uriInfo) {
        if (id != null) {
            try {
                getBean().delete(id);
                return Response.status(200).build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(422).header("process-error", "Record couldnt be deleted").build();
            }
        }
        return Response.status(500).header("Wrong-parameter", id).build();
    }

    /**
     * actualiza una entidad de base de datos
     *
     * @param registro entidda a ser actualizada
     * @param uriInfo info de url de donde se esta realizado la peticion
     * @return un status 200 si se actualizo la entidad , un 422 si hubo un
     * problema y 500 si falla el servidor
     */
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(T registro, @Context UriInfo uriInfo) {
        if (registro != null && getId(registro) != null) {
            try {
                getBean().update(registro,getId(registro));
                if (getId(registro) != null) {

                    return Response.status(200).build();
                }
                return Response.status(500).header("process-error", "Record couldnt be updated").build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(500).header("Wrong-parameter", registro).build();
    }
}
