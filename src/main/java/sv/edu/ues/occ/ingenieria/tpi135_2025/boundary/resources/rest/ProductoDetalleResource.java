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

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("tipoproducto/{idtipoproducto}/producto/{idProducto}/productoDetalle")
public class ProductoDetalleResource implements Serializable {
    @Inject
    ProductoDetalleBean pdBean;

    /**
     * metodo que devueleve una rango de datos de tipo ProductoDetalle
     * @param idTipoProducto id de tipo TipoProducto Relacionado al detalle
     * @param idProducto id de tipo Producto Relacionado al detalle
      * @return una lista de tipo T si no definel los parametros entonces
     * devuelve los primeros 20 registros
     */

    @Path("")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(
            @PathParam("idtipoproducto") Integer idTipoProducto,
            @PathParam("idProducto") Long idProducto,
            @QueryParam("first")
            @DefaultValue("0") int first,
            @QueryParam("max")
            @DefaultValue("20") int max

    ) {
        try {
            if (idTipoProducto!=null && idProducto!=null) {

                List<ProductoDetalle> encontrados = pdBean.findByIdProductoAndIdProductoDetalle(idTipoProducto,idProducto,first, max);
                long total = pdBean.countByIdProductoAndIdProductoDetalle(idTipoProducto,idProducto);
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
     * metodo para encontrar un registro especifico de producto dado su id
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
                ProductoDetalle encontrado = pdBean.findById(id);
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
        return Response.status(400).header("wrong-parameter : id", id).build();
    }


    /**
     * registra una entidad ProductoDetalle ademas de establecer la relacion producto detalle
     *
     * @param idTipoProducto id de la entidad tipoProductoRelacionada con producto
     * @param uriInfo informacion de URl donde se encuantra la peticion
     * @return un estatus 201 si la entidad es creada junto con la url donde se
     * puede encontra dicha entidad 422 en dado caso falle la creacion de la
     * entidad y 500 si por fall el servidor
     */
    @Path("")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})

    public Response create(ProductoDetalle registro, @PathParam("idtipoproducto") Integer idTipoProducto, @Context UriInfo uriInfo) {
        if (registro != null && registro.getProductoDetallePK() == null) {
            try {
                pdBean.create(registro);
                if (registro.getProductoDetallePK() != null) {

                    return Response.ok().build();
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
     * @param idProducto id de Producto para PK
     * @param uriInfo info de url de donde se esta realizado la peticion
     * @return un status 200 si se borro la entidad , un 422 si hubo un problema
     * y 500 si falla el servdor
     */
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("")
    public Response delete( @PathParam("idtipoproducto") Integer idTipoProducto,@PathParam("idProducto") Long idProducto , @Context UriInfo uriInfo) {
        ProductoDetalle registro = new ProductoDetalle(idTipoProducto,idTipoProducto);
        if (idTipoProducto != null && idProducto != null) {
            try {
                pdBean.delete(registro);
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
     *
     * @param registro entidda a ser actualizada
     * @param uriInfo info de url de donde se esta realizado la peticion
     * @return un status 200 si se actualizo la entidad , un 422 si hubo un
     * problema y 500 si falla el servidor
     */
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(ProductoDetalle registro, @Context UriInfo uriInfo) {
        if (registro != null && registro.getProductoDetallePK() != null) {
            try {
                pdBean.update(registro);
                if (registro.getProductoDetallePK() != null) {

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
