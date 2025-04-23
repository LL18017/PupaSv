package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.inject.Inject;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.OrdenBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Orden;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.List;

@Path("orden")
public class OrdenResource extends GeneralRest implements Serializable {

    @Inject
    OrdenBean oBean;


    /**
     * metodo que devueleve una rango de datos de tipo Orden
     *
     * @param first la pocicion del primer dat
     * @param max   la cantidad de datos que se desea obtener
     * @return una estatus 200 y una lista de tipo T si no definel los parametros entonces devuelve los primeros 20 registros
     * @return 400 si los argumentos son erroneos
     * 500 si existe problema con el entity, lsi al buscar la cantidad de datos devuelve mas de un resultado o existe problema con la base
     */

    @GET
    @Path("")
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(@QueryParam("first") @DefaultValue("0") Integer first, @QueryParam("max") @DefaultValue("20") Integer max) {
        try {
            List<Orden> encontrados = oBean.findRange(first, max);
            long total = oBean.count();
            Response.ResponseBuilder builder = Response.ok(encontrados).
                    header(Headers.TOTAL_RECORD, total).
                    type(MediaType.APPLICATION_JSON);
            return builder.build();

        } catch (Exception e) {
            return responseExcepcions(e,null);
        }
    }

    /**
     * Metodo para encontrar un registro especifico de Orden dado su id
     * @param id del registro a buscar
     * @return un esatatus 200 si se logro encontrar la entidad junto con dicha entidad
     * 500 en dado caso falle el servidor o la base de datos
     * 404 si no se encuentra la entidad
     * 400 si hay problema con los parametros
     */

    @Path("{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("id") Long id) {
        try {
            Orden registro = oBean.findById(id);
            return Response.ok(registro).build();
        } catch (Exception e) {
            return responseExcepcions(e,id);
        }
    }

    /**
     * Registra una entidad Orden
     * @param uriInfo informacion de URl donde se encuantra la peticion
     * @return un estatus 201 si la entidad es creada junto con la url donde se encuentra
     * 400 si hay error con los argumentos
     * 422 si la entidad ha sido creada previamente
     * 500 si por falla el sercidor o la base
     */

    @Path("")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response create(Orden registro, @Context UriInfo uriInfo) {
        try {
            oBean.create(registro);
            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
            uriBuilder.path(String.valueOf(registro.getIdOrden()));
            return Response.created(uriBuilder.build()).build();
        } catch (Exception e) {
            return responseExcepcions(e,registro.getIdOrden());
        }
    }

    /**
     * Borra un registro de tipo Orden Especifico
     * @param id      id del Orden a eliminar
     * @return un status 200 si se borro la entidad
     * 422 si hubo un problema con las reglas de integridad referencial
     * 500 si falla el servdor o la base d e datos
     * 400 si hubo error con los argumentos
     */
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            oBean.delete(id);
            return Response.status(200).build();
        } catch (Exception e) {
            return responseExcepcions(e,id);
        }
    }

    /**
     * Actualiza una entidad de base de datos
     *
     * @param registro entidda a ser actualizada
     * @param uriInfo  info de url de donde se esta realizado la peticion
     * @param idOrden id de la entidad a ser borrada
     * @return un status 200 si se actualizo la entidad ,
     * 400 si hubo un error con los argumentos
     * problema y 500 si falla el servidor, la base de datos o,
     * 422 si hubo error con las reglas de integridad referencial
     * 404 si no existe dicha entidad
     */
    @Path("{idOrden}")
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(Orden registro, @PathParam("idOrden") @DefaultValue("0") Long idOrden, @Context UriInfo uriInfo) {
        try {
            oBean.update(registro, idOrden);
            return Response.status(200).build();
        } catch (Exception e) {
            return responseExcepcions(e,idOrden);
        }


    }
}
