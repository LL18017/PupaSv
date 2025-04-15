package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.inject.Inject;
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

@Path("combo")
public class ComboResource extends GeneralRest implements Serializable {

    @Inject
    ComboBean comboBean;

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
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Collections.singletonMap("error", e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            e.printStackTrace(); // para ver el tipo exacto
            Throwable cause = e.getCause();
            System.out.println("Causa: " + cause);
            if (cause instanceof IllegalArgumentException) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Collections.singletonMap("error", cause.getMessage()))
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
            return responseExcepcions(e, null);
        }

    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("id") Long id) {
        try {
            Combo combo = comboBean.findById(id);
            return Response.ok(combo).build();
        } catch (Exception e) {
            return responseExcepcions(e, id);
        }
    }

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
            return responseExcepcions(e, registro.getIdCombo());
        }
    }

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
            return responseExcepcions(e, idCombo);
        }
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("{id}")
    public Response delete(@PathParam("id") Long id, @Context UriInfo uriInfo) {
        try {
            comboBean.delete(id);
            return Response.ok().build();
        } catch (Exception e) {
            return responseExcepcions(e, id);
        }
    }
}