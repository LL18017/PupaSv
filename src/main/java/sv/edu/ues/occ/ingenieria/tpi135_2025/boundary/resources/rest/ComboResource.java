package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ComboBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Combo;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author hdz Recurso REST para entidad Combo
 */
@Path("combo")
public class ComboResource extends GeneralRest implements Serializable {

    @Inject
    ComboBean comboBean;

    /**
     * Método que devuelve un rango de registros de la entidad Combo.
     *
     * @param first posición del primer registro a devolver (por defecto 0).
     * @param max cantidad máxima de registros a devolver (por defecto 20).
     * @return status 200 con una lista de combos y encabezado con total de
     * registros.
     */
    @GET
    @Path("")
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(@QueryParam("first") @DefaultValue("0") Integer first,
            @QueryParam("max") @DefaultValue("20") Integer max) {

        try {
            List<Combo> combos = comboBean.findRange(first, max);
            long total = comboBean.count();
            return Response.ok(combos)
                    .header(Headers.TOTAL_RECORD, total)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
         return responseExcepcions(e,null);
        }

    }

    /**
     * Método para buscar un Combo por su ID.
     *
     * @param id identificador del combo a buscar.
     * @return status 200 con el combo encontrado. status 404 si no se encuentra
     * el combo. status 400 si el ID es inválido. status 500 si ocurre un error
     * en el servidor.
     */
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("id") Long id) {
        try {
            Combo combo = comboBean.findById(id);
            return Response.ok(combo).build();
        } catch (Exception e) {
         return responseExcepcions(e,id);
        }
    }

    /**
     * Método para registrar un nuevo Combo en la base de datos.
     *
     * @param registro objeto Combo que se desea crear.
     * @param uriInfo información sobre la URI donde se realiza la petición.
     * @return status 201 si el combo fue creado exitosamente, con la URI del
     * nuevo recurso.
     */
    @Path("")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response create(Combo registro, @Context UriInfo uriInfo) {
        try {
            comboBean.create(registro);
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(String.valueOf(registro.getIdCombo()));
            return Response.created(builder.build()).build();
        } catch (Exception e) {
            return responseExcepcions(e, registro != null ? registro.getIdCombo() : null);
        }
    }

    /**
     * Método para actualizar un Combo existente.
     *
     * @param registro objeto Combo actualizado
     * @param idCombo ID del combo a actualizar
     * @param uriInfo contexto URI
     * @return respuesta HTTP 200 si fue exitoso
     */
    @PUT
    @Path("{idCombo}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(Combo registro,
            @PathParam("idCombo") @DefaultValue("0") Long idCombo,
            @Context UriInfo uriInfo) {
        try {
            comboBean.update(registro, idCombo);
            return Response.ok().build();
        } catch (Exception e) {
            // Evitar pasar excepción sin causa
            Exception wrapped = (e.getCause() == null) ? new Exception("Excepción sin causa", e) : e;
            return responseExcepcions(wrapped, idCombo);
        }
    }

    /**
     * Método para eliminar un Combo por su ID.
     *
     * @param id identificador del combo a eliminar.
     * @return status 200 si se eliminó correctamente.
     */
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            comboBean.delete(id);
            return Response.ok().build();
        } catch (Exception e) {
            return responseExcepcions(e, id);
        }
    }

}
