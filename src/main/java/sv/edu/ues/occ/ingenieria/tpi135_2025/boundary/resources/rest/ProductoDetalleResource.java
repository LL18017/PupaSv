package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.inject.Inject;
import jakarta.persistence.NoResultException;
import jakarta.transaction.UserTransaction;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.AbstractDataAccess;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoDetalleBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.TipoProductoBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetallePK;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.TipoProducto;

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
     * metodo que devueleve una registro de tipo ProductoDetalle
     *
     * @return una lista de tipo T si no definel los parametros entonces
     * devuelve los primeros 20 registros
     */

//    @Path("")
//    @GET
//    @Produces({MediaType.APPLICATION_JSON})
//    public Response find(@QueryParam("first") @DefaultValue("0") Integer first, @QueryParam("max") @DefaultValue("20") Integer max) {
//        try {
//            List<ProductoDetalle> resultado = pdBean.findRange(first, max);
//            Long total = pdBean.count();
//            return Response.ok(resultado).header(Headers.TOTAL_RECORD, total).build();
//        } catch (Exception e) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage());
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }

    @Path("tipoProducto/{idTipoProducto}/producto/{idProducto}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findByIDs(@PathParam("idTipoProducto") Integer idTipoProducto, @PathParam("idProducto") Long idProducto) {
        try {
            ProductoDetalle registro = pdBean.findById(idTipoProducto, idProducto);
            return Response.ok((registro)).build();

        } catch (Exception e) {
            return responseExcepcions(e,idProducto);
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
    @Path("tipoProducto/{idTipoProducto}/producto/{idProducto}")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response create(ProductoDetalle registro, @PathParam("idTipoProducto") Integer idTipoProducto, @PathParam("idProducto") Long idProducto, @Context UriInfo uriInfo) {
        try {
            Response comprobacion = verificarPathParams(idTipoProducto, idProducto);
            if (comprobacion.getStatus() != 200) return comprobacion;
            Response existeTp = verificarTipoProductoExiste(idTipoProducto);
            Response existeP = verificarProductoExiste(idProducto);

            if (existeP.getStatus() != 200) return existeP;
            if (existeTp.getStatus() != 200) return existeTp;

            registro.setProductoDetallePK(new ProductoDetallePK(idTipoProducto, idProducto));
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
    @Path("tipoProducto/{idTipoProducto}/producto/{idProducto}")
    public Response delete(@PathParam("idTipoProducto") Integer idTipoProducto, @PathParam("idProducto") Long idProducto, @Context UriInfo uriInfo) {
        try {
            Response comprobacion = verificarPathParams(idTipoProducto, idProducto);
            if (comprobacion.getStatus() != 200) return comprobacion;
            Response existe = verificarPathParams(idTipoProducto, idProducto);
            if (comprobacion.getStatus() != 200) return existe;

            ProductoDetallePK pk = new ProductoDetallePK(idTipoProducto, idProducto);
            pdBean.deleteByPk(pk);
            return Response.status(200).build();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(5000).header(Headers.PROCESS_ERROR, "Record couldnt be deleted").build();
        }
    }

    /**
     * actualiza una entidad de base de datos
     *
     * @param registro entidda a ser actualizada
     * @param uriInfo  info de url de donde se esta realizado la peticion
     * @return un status 200 si se actualizo la entidad , un 422 si hubo un
     * problema y 500 si falla el servidor
     */
    @Path("tipoProducto/{idTipoProducto}/producto/{idProducto}")
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(ProductoDetalle registro, @PathParam("idProducto") Long idProducto, @PathParam("idTipoProducto") Integer idTipoProducto, @Context UriInfo uriInfo) {
        try {
            Response comprobacion = verificarPathParams(idTipoProducto, idProducto);
            if (comprobacion.getStatus() != 200) return comprobacion;
            Response existe = verificarPathParams(idTipoProducto, idProducto);
            if (comprobacion.getStatus() != 200) return existe;

            ProductoDetallePK pk = new ProductoDetallePK(idTipoProducto, idProducto);
            registro.setProductoDetallePK(pk);
            pdBean.update(registro);
            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
            return Response.ok(uriBuilder.build()).build();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(500).entity(e.getMessage()).build();
        }
    }


    /**
     * verifica la existencia y el correcto asignacion de valores d
     *
     * @param idProducto idProducto del productoDetalle
     * @param idTipoProducto idProducto del productoDetalle
     * @return un status 200 si tod esta bien
     */
    public Response verificarPathParams(Integer idTipoProducto, Long idProducto) {
        try {
            Response verificarIdTipoProducto = verificarId(idTipoProducto, "idTipoProdcuto");
            Response verificarIdProducto = verificarId(idProducto, "idProdcuto");

            if (verificarIdTipoProducto.getStatus() != 200) return verificarIdTipoProducto;
            if (verificarIdProducto.getStatus() != 200) return verificarIdProducto;

            Response existeTp = verificarTipoProductoExiste(idTipoProducto);
            Response existep = verificarProductoExiste(idProducto);

            if (existeTp.getStatus() != 200) return existeTp;
            if (existep.getStatus() != 200) return existep;

            return Response.status(200).build();
        } catch (Exception e) {
            return Response.status(500).build();
        }
    }

    /**
     * verifica la existencia de productoDetalle
     * @param idProducto idProducto del productoDetalle
     * @param idTipoProducto idProducto del productoDetalle
     * @return un status 200 si tod esta bien
     */
    public Response verificarExistencia(Integer idTipoProducto, Long idProducto) {
        try {
            ProductoDetalle existePd = pdBean.findById(idTipoProducto, idProducto);
            if (existePd == null)
                return Response.status(404).header(Headers.NOT_FOUND_ID, "NO EXISTE DETALLE CON ESA CLAVE").build();
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(500).build();
        }
    }

}
