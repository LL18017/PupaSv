package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoDetalleBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.TipoProductoBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetallePK;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("productoDetalle")
public class ProductoDetalleResource extends GeneralRest implements Serializable {


    @Inject
    ProductoDetalleBean pdBean;
    @Inject
    TipoProductoBean tpBean;
    @Inject
    ProductoBean pBean;


    /**
     * metodo que devueleve una lista de todos los ProductoDetalles
     *
     * @param first el primer registro recibido
     * @param max   la cantidda de registros
     * @return una lista de tipo Producto detalle si no definel los parametros first y max  entonces devuelve los primeros 20 registros,
     * retorna 200 si la transacion ocurrio correctamente
     * 400 si los argumentos son erroneos
     * 500 si existe problema al persistir ya sea con el servidor o con la base
     * 404 si no existe el tipoProducto o producto enviado
     */

    @Path("")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response find(@QueryParam("first") @DefaultValue("0") Integer first, @QueryParam("max") @DefaultValue("20") Integer max) {
        try {
            List<ProductoDetalle> resultado = pdBean.findRange(first, max);
            Long total = pdBean.count();
            return Response.ok(resultado).header(Headers.TOTAL_RECORD, total).build();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage());
            return responseExcepcions(e,null);
        }
    }


    /**
     * Metodo para encontrar un registro especifico de producto dado su idTipoProducto e idProducto
     *
     * @param idProducto     idProducto del registro a buscar
     * @param idTipoProducto idTipoProducto del registro a buscar
     * @return un status 200 se se encontro la entidad junto con dicha entidad
     * 500 en dado caso falle el servidor o la base de datos
     * 404 si no se encuentra la entidad o algunos de sus parametros
     * 400 si hay problema con los parametros
     */
    @Path("tipoProducto/{idTipoProducto}/producto/{idProducto}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findByIDs(@PathParam("idTipoProducto") Integer idTipoProducto, @PathParam("idProducto") Long idProducto) {
        try {
            ProductoDetalle registro = pdBean.findById(idTipoProducto, idProducto);
            return Response.ok((registro)).build();
        } catch (Exception e) {
            return responseExcepcions(e, null);
        }
    }

    /**
     * registra una entidad ProductoDetalle
     *
     * @param idTipoProducto id de la entidad tipoProducto Relacionada con producto
     * @param idProducto     id de la entidad Producto Relacionada con producto
     * @param uriInfo        informacion de URl donde se encuantra la peticion
     * @return un estatus 201 si la entidad es creada junto con la url donde se
     * puede encontra dicha entidad 422 en dado caso falle la creacion de la
     * entidad y 500 si por fall el servidor
     */
    @Path("tipoProducto/{idTipoProducto}/producto/{idProducto}")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response create(ProductoDetalle registro, @PathParam("idTipoProducto") Integer idTipoProducto, @PathParam("idProducto") Long idProducto, @Context UriInfo uriInfo) {
        try {
            pdBean.create(registro, idTipoProducto, idProducto);
            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
            return Response.created(uriBuilder.build()).build();
        } catch (Exception e) {
            return responseExcepcions(e, null);
        }
    }

    /**
     * borra un ProdcutoDetalle
     *
     * @param idTipoProducto id de TipoProducto relacionado con el detalle
     * @param idProducto     id de Producto relacionado con el detalle
     * @return un status 200 si se borro la entidad ,
     * un 422 si hubo un problema
     * y 500 si falla el servdor
     * 404 si no encuentra alguno de los parametros
     */
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("tipoProducto/{idTipoProducto}/producto/{idProducto}")
    public Response delete(@PathParam("idTipoProducto") Integer idTipoProducto, @PathParam("idProducto") Long idProducto) {
        try {
            pdBean.deleteByIdTipoProductoAndIdProducto(idTipoProducto, idProducto);
            return Response.status(200).build();
        } catch (Exception e) {
            return responseExcepcions(e, null);
        }
    }

    /**
     * actualiza un detalle en la base de datos
     *
     * @param registro entidda a ser actualizada
     * @param uriInfo  info de url de donde se esta realizado la peticion
     * @return un status 200 si se actualizo la entidad ,
     * un 422 si hubo un problema,
     * 500 si falla el servidor o la base de datos
     * 404 si no existe alguno de los parametros
     * 400 si alguno de los paremtros esta mal
     */
    @Path("tipoProducto/{idTipoProducto}/producto/{idProducto}")
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(ProductoDetalle registro, @PathParam("idProducto") Long idProducto, @PathParam("idTipoProducto") Integer idTipoProducto, @Context UriInfo uriInfo) {
        try {

            pdBean.update(registro, idTipoProducto, idProducto);
            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
            return Response.ok(uriBuilder.build()).build();
        } catch (Exception e) {
            return responseExcepcions(e, null);
        }
    }


}