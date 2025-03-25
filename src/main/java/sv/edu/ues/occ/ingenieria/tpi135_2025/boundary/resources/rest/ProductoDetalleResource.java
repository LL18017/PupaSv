package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.transaction.UserTransaction;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoDetalleBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetallePK;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("tipoProducto/{idTipoProducto}/producto/{idProducto}/detalle")
public class ProductoDetalleResource implements Serializable {
    @Inject
    ProductoDetalleBean pdBean;


    /**
     * metodo que devueleve una rango de datos de tipo ProductoDetalle dados sus ids
     *
     * @param idTipoProducto id de tipo TipoProducto Relacionado al detalle
     * @param idProducto     id de tipo Producto Relacionado al detalle
     * @return una lista de tipo T si no definel los parametros entonces
     * devuelve los primeros 20 registros
     */

    @Path("")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(
            @PathParam("idTipoProducto") Integer idTipoProducto,
            @PathParam("idProducto") Long idProducto,
            @QueryParam("first")
            @DefaultValue("0") int first,
            @QueryParam("max")
            @DefaultValue("20") int max

    ) {
        try {
            if (idTipoProducto != null && idProducto != null) {

                List<ProductoDetalle> encontrados = pdBean.findByIdProductoAndIdProducto(idTipoProducto, idProducto, first, max);
                long total = pdBean.countByIdProductoAndIdProducto(idTipoProducto, idProducto);
                Response.ResponseBuilder builder = Response.ok(encontrados).
                        header(Headers.TOTAL_RECORD, total).
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
     * registra una entidad ProductoDetalle ademas de establecer la relacion producto detalle
     *
     * @param idTipoProducto id de la entidad tipoProductoRelacionada con producto
     * @param uriInfo        informacion de URl donde se encuantra la peticion
     * @return un estatus 201 si la entidad es creada junto con la url donde se
     * puede encontra dicha entidad 422 en dado caso falle la creacion de la
     * entidad y 500 si por fall el servidor
     */
    @Path("")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})

    public Response create(
                            ProductoDetalle registro,
                           @PathParam("idTipoProducto") Integer idTipoProducto,
                           @PathParam("idProducto") Long idProducto,
                           @Context UriInfo uriInfo) {
        if (idProducto != null && idTipoProducto!= null) {
            registro.setProductoDetallePK(new ProductoDetallePK(idTipoProducto, idProducto));
            try {
                pdBean.create(registro);
                if (registro.getProductoDetallePK() != null) {
                    UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
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
     * borra un REGISTRO Producto Especifico
     *
     * @param idTipoProducto id de TipoProducto para PK
     * @param idProducto     id de Producto para PK
     * @param uriInfo        info de url de donde se esta realizado la peticion
     * @return un status 200 si se borro la entidad , un 422 si hubo un problema
     * y 500 si falla el servdor
     */
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("")
    public Response delete(
            @PathParam("idTipoProducto") Integer idTipoProducto,
            @PathParam("idProducto") Long idProducto,
            @Context UriInfo uriInfo) {
        if (idTipoProducto != null && idProducto != null) {

            ProductoDetallePK pk = new ProductoDetallePK(idTipoProducto, idProducto);
            try {
                pdBean.deleteByPk(pk);
                return Response.status(200).build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(422).header(Headers.PROCESS_ERROR, "Record couldnt be deleted").build();
            }
        }
        return Response.status(500).header(Headers.WRONG_PARAMETER, idProducto).build();
    }

    /**
     * actualiza una entidad de base de datos
     * @param registro entidda a ser actualizada
     * @param uriInfo  info de url de donde se esta realizado la peticion
     * @return un status 200 si se actualizo la entidad , un 422 si hubo un
     * problema y 500 si falla el servidor
     */
    @Path("")
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(ProductoDetalle registro,
                           @PathParam("idProducto") Long idProducto,
                           @PathParam("idTipoProducto") Integer idTipoProducto,
                           @Context UriInfo uriInfo) {
        if (registro != null && idTipoProducto != null && idProducto != null) {
            ProductoDetallePK pk = new ProductoDetallePK(idTipoProducto, idProducto);
            try {
                registro.setProductoDetallePK(pk);
                pdBean.update(registro);
                UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
                return Response.created(uriBuilder.build()).build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(500).header(Headers.WRONG_PARAMETER, registro).build();
    }
}
