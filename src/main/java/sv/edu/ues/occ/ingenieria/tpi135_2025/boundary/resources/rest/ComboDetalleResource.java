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

/**
 * @author hdz Recurso REST para gestionar entidades ComboDetalle.
 */
@Path("comboDetalle")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ComboDetalleResource extends GeneralRest implements Serializable {

    @Inject
    ComboDetalleBean comboDetalleBean;
    @Inject
    ComboBean comboBean;
    @Inject
    ProductoBean productoBean;

    /**
     * Obtiene una lista paginada de registros ComboDetalle.
     *
     * @param first la posición del primer registro (por defecto 0).
     * @param max   la cantidad máxima de registros a obtener (por defecto 20).
     * @return 200 con una lista de ComboDetalle y cabecera con el total de
     * registros. 500 si ocurre un error en la lógica o en la base de datos.
     */
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
            return responseExcepcions(e, (Long) null);
        }
    }

    /**
     * Obtiene un detalle específico de combo-producto usando ambos IDs.
     *
     * @param idCombo    ID del combo
     * @param idProducto ID del producto
     * @return detalle ComboDetalle correspondiente
     */
    @GET
    @Path("combo/{idCombo}/producto/{idProducto}")
    public Response findByIDs(@PathParam("idCombo") Long idCombo,
                              @PathParam("idProducto") Long idProducto) {
        try {
            ComboDetalle detalle = comboDetalleBean.findByIdComboAndIdProducto(idCombo, idProducto);
            return Response.ok(detalle).build();
        } catch (Exception e) {
            return responseExcepcions(e, (Long) null);
        }
    }

    /**
     * Crea un nuevo detalle para un combo y producto especificado.
     *
     * @param detalle    entidad ComboDetalle a crear
     * @param idCombo    ID del combo asociado
     * @param idProducto ID del producto asociado
     * @param uriInfo    contexto de la solicitud para construir URI del recurso
     *                   creado
     * @return 201 si se crea correctamente, junto con la ubicación del recurso
     * creado, 400 si hay errores de argumentos, 422 si ya existe, 500 si hay
     * error de servidor.
     */
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
            return responseExcepcions(e, (Long) null);
        }
    }

    /**
     * Actualiza un detalle existente de combo-producto.
     *
     * @param detalle    objeto con los nuevos datos a actualizar.
     * @param idCombo    ID del combo relacionado
     * @param idProducto ID del producto relacionado
     * @param uriInfo    información de la URI de la solicitud.
     * @return respuesta con estado 200 si fue exitoso
     */
    @PUT
    @Path("combo/{idCombo}/producto/{idProducto}")
    public Response update(ComboDetalle detalle,
                           @PathParam("idCombo") Long idCombo,
                           @PathParam("idProducto") Long idProducto,
                           @Context UriInfo uriInfo) {
        try {
            // Validar si el ComboDetalle existe
            ComboDetalle existe = comboDetalleBean.findByIdComboAndIdProducto(idCombo, idProducto);
            if (existe == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("ComboDetalle no encontrado para los IDs proporcionados.")
                        .build();
            }
            comboDetalleBean.updateByComboDetallePK(detalle, idCombo, idProducto);
            URI uri = uriInfo.getAbsolutePathBuilder().build();
            return Response.ok(uri).build();
        } catch (Exception e) {
            return responseExcepcions(e, (Long) null);
        }
    }

    /**
     * Elimina un registro de ComboDetalle identificado por combo y producto.
     *
     * @param idCombo    ID del combo
     * @param idProducto ID del producto
     * @return respuesta con estado 200 si fue exitoso
     */
    @DELETE
    @Path("combo/{idCombo}/producto/{idProducto}")
    public Response delete(@PathParam("idCombo") Long idCombo,
                           @PathParam("idProducto") Long idProducto) {
        try {
            // Validar si el ComboDetalle existe
            ComboDetalle existe = comboDetalleBean.findByIdComboAndIdProducto(idCombo, idProducto);
            if (existe == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("ComboDetalle no encontrado para los IDs proporcionados.")
                        .build();
            }

            comboDetalleBean.deleteByComboDetallePK(idCombo, idProducto);
            return Response.ok().build();
        } catch (Exception e) {
            return responseExcepcions(e, (Long) null);
        }
    }


}
