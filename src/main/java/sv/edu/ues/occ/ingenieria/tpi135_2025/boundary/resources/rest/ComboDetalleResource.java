package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ComboDetalleBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.*;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ComboBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoBean;

@Path("comboDetalle")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ComboDetalleResource extends GeneralRest implements Serializable {

    @Inject
    private ComboDetalleBean comboDetalleBean;
    @Inject
    private ComboBean comboBean;
    @Inject
    private ProductoBean productoBean;

    @GET
    public Response find(@QueryParam("first") @DefaultValue("0") Integer first,
            @QueryParam("max") @DefaultValue("20") Integer max) {
        try {
            List<ComboDetalle> lista = comboDetalleBean.findRange(first, max);
            Long total = comboDetalleBean.count();
            return Response.ok(lista)
                    .header(Headers.TOTAL_RECORD, total)
                    .build();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return responseExcepcions(e, null);
        }
    }

    @GET
    @Path("combo/{idCombo}/producto/{idProducto}")
    public Response findByIDs(@PathParam("idCombo") Long idCombo,
            @PathParam("idProducto") Long idProducto) {
        try {
            ComboDetalle detalle = comboDetalleBean.findByIdComboAndIdProducto(idCombo, idProducto);
            return Response.ok(detalle).build();
        } catch (Exception e) {
            return responseExcepcions(e, null);
        }
    }
    @POST
    @Path("combo/{idCombo}/producto/{idProducto}")
    public Response create(ComboDetalle detalle,
            @PathParam("idCombo") Long idCombo,
            @PathParam("idProducto") Long idProducto,
            @Context UriInfo uriInfo) {
        try {
            comboDetalleBean.create(detalle, idCombo, idProducto);
            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
            return Response.created(uriBuilder.build()).build();
        } catch (Exception e) {
            return responseExcepcions(e, null);
        }
    }

    @PUT
    @Path("combo/{idCombo}/producto/{idProducto}")
    public Response update(ComboDetalle detalle,
            @PathParam("idCombo") Long idCombo,
            @PathParam("idProducto") Long idProducto,
            @Context UriInfo uriInfo) {
        try {
            comboDetalleBean.updateByComboDetallePK(detalle, idCombo, idProducto);
            URI uri = uriInfo.getAbsolutePathBuilder().build();
            return Response.ok(uri).build();
        } catch (Exception e) {
            return responseExcepcions(e, null);
        }
    }

    
    @DELETE
    @Path("combo/{idCombo}/producto/{idProducto}")
    public Response delete(@PathParam("idCombo") Long idCombo,
            @PathParam("idProducto") Long idProducto,
            @Context UriInfo uriInfo) {
        try {
            comboDetalleBean.deleteByComboDetallePK(idCombo, idProducto);
            return Response.ok().build();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return responseExcepcions(e, null);
        }
    }
}
