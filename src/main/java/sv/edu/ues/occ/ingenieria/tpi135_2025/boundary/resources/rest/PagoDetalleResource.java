package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.PagoDetalleBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Pago;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.PagoDetalle;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("pagoDetalle")
public class PagoDetalleResource extends GeneralRest implements Serializable {

    @Inject
    PagoDetalleBean pdBean;

    /**
     * Metodo que devueleve un rango de datos de tipo PagoDetalle
     * param first la pocicion del primer dato
     *
     * @param max    la cantidad de datos que se desea obtener
     * @param idPago opcional para filtrar por idPago
     * @return una lista de tipo T si no definel los parametros entonces
     * devuelve los primeros 20 registros
     */

    @Path("")
    @Produces({MediaType.APPLICATION_JSON})
    @GET
    public Response findRange(@QueryParam("fist") @DefaultValue("0") Integer first, @QueryParam("max") @DefaultValue("20") Integer max, @QueryParam("idPago") @DefaultValue("0") Long idPago) {
        try {
            //flujo normal
            if (idPago != null && idPago == 0) {
                List<PagoDetalle> encontrados = pdBean.findRange(first, max);
                long total = pdBean.count();
                Response.ResponseBuilder builder = Response.ok(encontrados).
                        header(Headers.TOTAL_RECORD, total).
                        type(MediaType.APPLICATION_JSON);
                return builder.build();
            }
            //filtrar por id pago
            List<PagoDetalle> encontrados = pdBean.findRangeByIdPago(idPago, first, max);
            long total = pdBean.countByIdPago(idPago);
            Response.ResponseBuilder builder = Response.ok(encontrados).
                    header(Headers.TOTAL_RECORD, total).
                    type(MediaType.APPLICATION_JSON);
            return builder.build();

        } catch (Exception e) {
            return responseExcepcions(e, null);
        }
    }


    /**
     * Metodo para encontrar un registro especifico de un PagoDetalle dado su id
     *
     * @param id del registro a buscar
     * @return un esatatus 200 se se encontro la entidad junto con dicha entidad
     * un estatus 500 en dado caso falle el servidor un estatus 404 si no se
     * encuentra ningun registro con el id especificado 400 si se envia mal una
     * parametro
     */

    @Path("{idPago}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("idPago") Long id) {
        try {
            PagoDetalle encontrado = pdBean.findById(id);
            Response.ResponseBuilder builder = Response.ok(encontrado);
            return builder.build();
        } catch (Exception e) {
            return responseExcepcions(e, id);
        }
    }

    /**
     * Registra una entidad pagoDetalle
     *
     * @param uriInfo informacion de URl donde se encuantra la peticion
     * @return un estatus 201 si la entidad es creada junto con la url donde se
     * puede encontra dicha entidad 422 en dado caso falle la creacion de la
     * entidad y 500 si por fall el servidor
     */

    @Path("{idPago}")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response create(PagoDetalle registro,
                           @Context UriInfo uriInfo, @PathParam("idPago") Long id) {

        if (id == null || id <= 0) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "pago no puede ser nulo o igual a 0");
            return Response.status(400).header(Headers.WRONG_PARAMETER, "pago es: " + id).build();
        }
        try {
            Pago exite = pdBean.getEntityManager().find(Pago.class, id);
            if (exite == null) {
                return Response.status(404).header(Headers.WRONG_PARAMETER, "no existe el pago con id: " + id).build();
            }
            pdBean.create(registro);
            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
            uriBuilder.path(String.valueOf(registro.getIdPagoDetalle()));
            return Response.created(uriBuilder.build()).build();
        } catch (NullPointerException e) {
            return Response.status(400).header(Headers.WRONG_PARAMETER, e.getMessage()).build();
        } catch (Exception e) {
            return responseExcepcions(e, null);
        }
    }

    /**
     * Borra un registro de tipo PAgo Especifico
     *
     * @param id      id del TipoProducto a eliminar
     * @param uriInfo info de url de donde se esta realizado la peticion
     * @return un status 200 si se borro la entidad , un 422 si hubo un problema
     * y 500 si falla el servdor
     */
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("{id}")
    public Response delete(@PathParam("id") Long id, @Context UriInfo uriInfo) {
        try {
            pdBean.delete(id);
            return Response.status(200).build();
        } catch (Exception e) {
            return responseExcepcions(e, id);
        }

    }

    /**
     * Actualiza un pago detalle de base de datos
     *
     * @param registro entidda a ser actualizada
     * @param uriInfo  info de url de donde se esta realizado la peticion
     * @return un status 200 si se actualizo la entidad , un 422 si hubo un
     * problema y 500 si falla el servidor
     */
    @Path("{idPagoDetalle}")
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(PagoDetalle registro, @Context UriInfo uriInfo, @PathParam("idPagoDetalle") Long idPagoDetalle) {
        try {
            registro.setIdPagoDetalle(idPagoDetalle);
            pdBean.update(registro, idPagoDetalle);
            return Response.status(200).build();
        } catch (Exception e) {
            return responseExcepcions(e, idPagoDetalle);
        }
    }

}
