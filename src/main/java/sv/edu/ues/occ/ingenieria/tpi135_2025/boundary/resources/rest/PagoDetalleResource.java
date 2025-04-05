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

@Path("orden")
public class PagoDetalleResource implements Serializable {

    @Inject
    PagoDetalleBean pdBean;

    /**
     * Metodo que devueleve un rango de datos de tipo Pago sin importar el idPago
     * @param first la pocicion del primer dat
     * @param max la cantidad de datos que se desea obtener
     * @return una lista de tipo T si no definel los parametros entonces
     * devuelve los primeros 20 registros
     */

    @Path("pago/pagoDetalle")
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(int first, int max) {
        try {
            if (first >= 0 && max >= 0 && max <= 50) {

                List<PagoDetalle> encontrados = pdBean.findRange(first, max);
                long total = pdBean.count();
                Response.ResponseBuilder builder = Response.ok(encontrados).
                        header(Headers.TOTAL_RECORD, total).
                        type(MediaType.APPLICATION_JSON);
                return builder.build();
            } else {
                return Response.status(400).header(Headers.WRONG_PARAMETER,  "first: "+first + ",max: " + max).header("wrong parameter : max", "s").build();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage());
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * metodo que devueleve una rango de datos de tipo Pago de acuerdo a su idPago
     * @param first la pocicion del primer dat
     * @param max la cantidad de datos que se desea obtener
     * @return una lista de tipo T si no definel los parametros entonces
     * devuelve los primeros 20 registros
     */

    @Path("pago/{idPago}/productoPrecio")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(
            @QueryParam("first")
            @DefaultValue("0") int first,
            @QueryParam("max")
            @DefaultValue("20") int max,
            @PathParam("idPago") Long idPago
    ) {
        try {
            if (first >= 0 && max >= 0 && max <= 50 ) {
                List<PagoDetalle> encontrados = pdBean.findByIdPago(idPago,first, max);
                long total = pdBean.countByIdPago(idPago);
                Response.ResponseBuilder builder = Response.ok(encontrados).
                        header(Headers.TOTAL_RECORD, total).
                        type(MediaType.APPLICATION_JSON);
                return builder.build();
            } else {
                return Response.status(400).header(Headers.WRONG_PARAMETER," first:"+ first + ",max: " + max).header("wrong parameter : max", "s").build();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage());
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * Metodo para encontrar un registro especifico de un PagoDetalle dado su id
     * @param id del registro a buscar
     * @return un esatatus 200 se se encontro la entidad junto con dicha entidad
     * un estatus 500 en dado caso falle el servidor un estatus 404 si no se
     * encuentra ningun registro con el id especificado 400 si se envia mal una
     * parametro
     */

    @Path("/pago/{idPago}/pago/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("id") Long id) {
        if (id != null) {
            try {
                PagoDetalle encontrado = pdBean.findById(id);
                if (encontrado != null) {
                    Response.ResponseBuilder builder = Response.ok(encontrado);
                    return builder.build();
                }
                return Response.status(404).header(Headers.NOT_FOUND_ID, id).build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(400).header(Headers.WRONG_PARAMETER,"id: "+ id).build();
    }

    /**
     * Registra una entidad Pago a un
     * @param uriInfo informacion de URl donde se encuantra la peticion
     * @return un estatus 201 si la entidad es creada junto con la url donde se
     * puede encontra dicha entidad 422 en dado caso falle la creacion de la
     * entidad y 500 si por fall el servidor
     * */

    @Path("pago/{idPago}/pagoDetalle")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response create(PagoDetalle registro,
                           @Context UriInfo uriInfo) {
        if (registro != null && registro.getIdPago() == null && registro.getIdPagoDetalle() != null) {
            try {
                pdBean.create(registro);
                if (registro.getIdPago() != null) {
                    UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
                    uriBuilder.path(String.valueOf(registro.getIdPago()));
                    return Response.created(uriBuilder.build()).build();
                }
                return Response.status(422).header(Headers.UNPROCESSABLE_ENTITY, "Record couldnt be created").build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(500).header(Headers.WRONG_PARAMETER, registro).build();
    }

    /**
     * Borra un registro de tipo PAgo Especifico
     * @param id id del TipoProducto a eliminar
     * @param uriInfo info de url de donde se esta realizado la peticion
     * @return un status 200 si se borro la entidad , un 422 si hubo un problema
     * y 500 si falla el servdor
     */
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("pago/pagoDetalle/{id}")
    public Response delete(@PathParam("id") Integer id, @Context UriInfo uriInfo) {
        if (id != null) {
            try {
                pdBean.delete(id);
                return Response.status(200).build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(422).header(Headers.PROCESS_ERROR, "Record couldnt be deleted").build();
            }
        }
        return Response.status(500).header(Headers.WRONG_PARAMETER, id).build();
    }

    /**
     * Actualiza una entidad de base de datos
     * @param registro entidda a ser actualizada
     * @param uriInfo info de url de donde se esta realizado la peticion
     * @return un status 200 si se actualizo la entidad , un 422 si hubo un
     * problema y 500 si falla el servidor
     */
    @Path("pago")
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(PagoDetalle registro, @Context UriInfo uriInfo) {
        if (registro != null && registro.getIdPago() != null) {
            try {
                pdBean.update(registro,1001);
                if (registro.getIdPago() != null) {
                    return Response.status(200).build();
                }
                return Response.status(500).header(Headers.PROCESS_ERROR, "Record couldnt be updated").build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(500).header(Headers.WRONG_PARAMETER, registro).build();
    }

}
